package xperience;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
 * Multi-threaded server implementing the XPerience protocol
 * @version 2.0
 */
public class XPerienceServer {

    public static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);

    private final EventStore eventStore;
    private final int port;
    private final PasswordList passwordList;

    public XPerienceServer(EventStore eventStore, int port, PasswordList passwordList) {
        this.eventStore = eventStore;
        this.port = port;
        this.passwordList = passwordList;
        setupLogger();
    }

    private void setupLogger() {
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
            @Override
            public String format(LogRecord rec) {
                return String.format(format, new Date(rec.getMillis()),
                        rec.getLevel().getLocalizedName(), rec.getMessage());
            }
        });
        root.addHandler(handler);
        root.setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
    }

    public static void checkParameters(String[] args) {
        if (args.length != 2) {
            logger.severe("Incorrect parameters. Expected: <Port> <PasswordFile>");
            System.exit(1);
        }
    }

    public static void checkIntPort(String port) {
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            logger.severe("Invalid port number: " + port);
            System.exit(1);
        }
    }

    public void start() {
        try (ServerSocket servSock = new ServerSocket(port);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            logger.info("XPerience server started on port " + port);

            while (true) {
                Socket clntSock = servSock.accept();
                executor.submit(new ClientHandler(clntSock, eventStore, passwordList));
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server error", ex);
        }
    }

    public static void main(String[] args) {
        checkParameters(args);
        checkIntPort(args[0]);

        try {
            int port = Integer.parseInt(args[0]);
            String passwordFile = args[1];
            PasswordList passwordList = new PasswordList(passwordFile);
            EventStore store = new EventStoreMemory();
            new XPerienceServer(store, port, passwordList).start();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load password file", ex);
            System.exit(1);
        }
    }
}