package xperience;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

/**
 * ClientHandler.java
 *
 * Handles individual client connections for the XPerience event management system.
 * Processes incoming event submissions and coordinates with the EventStore and PasswordList
 * to validate and persist events.
 *
 * <p><b>Protocol Handling:</b>
 * <ul>
 *   <li>Validates message format: {@code <Name>#<Date>#<Time>#<Description>#<Password>#}</li>
 *   <li>Performs comprehensive input validation</li>
 *   <li>Manages thread-safe event storage operations</li>
 * </ul></p>
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Names: 1-100 characters</li>
 *   <li>Dates: ISO format (YYYY-MM-DD)</li>
 *   <li>Times: HH:MM format</li>
 *   <li>Descriptions: 1-1000 characters</li>
 * </ul></p>
 *
 * @author Sloane Wright
 * @version 2.1
 * @since 2025-04-02
 * @see Event
 * @see EventStore
 * @see PasswordList
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final EventStore eventStore;
    private final PasswordList passwordList;

    public ClientHandler(Socket clientSocket, EventStore eventStore, PasswordList passwordList) {
        this.clientSocket = clientSocket;
        this.eventStore = eventStore;
        this.passwordList = passwordList;
    }

    @Override
    public void run() {
        String clientAddress = clientSocket.getRemoteSocketAddress().toString();
        logger.info("New client connected: " + clientAddress);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.US_ASCII));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.US_ASCII), true)) {

            String message = in.readLine();
            if (message == null) {
                out.print("Reject#");
                return;
            }

            String response = processMessage(message);
            out.print(response);
            logger.info("Response sent: " + response);

        } catch (IOException e) {
            logger.warning("Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.finer("Error closing socket: " + e.getMessage());
            }
        }
    }

    private String processMessage(String message) {
        String[] parts = message.split("#", -1);
        if (parts.length < 5) {
            return "Reject#";
        }

        String password = parts[4];
        if (!passwordList.use(password)) {
            return "Reject#";
        }

        Event event = new Event(parts[0], parts[1], parts[2], parts[3]);
        if (!isValidEvent(event)) {
            return "Reject#";
        }

        try {
            synchronized (eventStore) {
                if (eventStore.isNameDuplicate(event.getName())) {
                    return "Reject#";
                }
                eventStore.saveEvent(event);
                return "Accept#" + eventStore.getTotalEvents() + "#";
            }
        } catch (StorageException e) {
            logger.severe("Storage error: " + e.getMessage());
            return "Reject#";
        }
    }

    private boolean isValidEvent(Event event) {
        return isValidName(event.getName()) &&
                isValidDate(event.getDate()) &&
                isValidTime(event.getTime()) &&
                isValidDescription(event.getDescription());
    }

    private boolean isValidName(String name) {
        return !name.isEmpty() && name.length() <= 100;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        if (time == null || time.length() < 4 || time.length() > 5) {
            return false;
        }
        return time.contains(":");
    }

    private boolean isValidDescription(String description) {
        return !description.isEmpty() && description.length() <= 1000;
    }
}