package xperience;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class XPerienceTestClient {
    private static final String DELIM = "#";
    private static final String PASSWORDS_FILE = "src/passwords.txt";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java XPerienceTestClient <Server> <Port>");
            System.exit(1);
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);

        testWithNewPassword(server, port, "Danoke1", "2025-02-12", "20:00", "Fusion of Karaoke and Dance");
        testWithNewPassword(server, port, "Danoke2", "2025-02-14", "20:00", "Fusion of Dance and Karaoke");
        testWithNewPassword(server, port, "Mimoetry", "2025-02-14", "19:00", "Poetry \"read\" by a mime");


        //debugging
        String[] PASSWORDS = {"mypass123", "securePass", "eventPoster"};
        testEvent(server, port, "Danoke1", "2025-02-12", "20:00",
                "Fusion of Karaoke and Dance", PASSWORDS[0]);
        testEvent(server, port, "Danoke2", "2025-02-14", "20:00",
                "Fusion of Dance and Karaoke", PASSWORDS[1]);
        testEvent(server, port, "Mimoetry", "2025-02-14", "19:00",
                "Poetry \"read\" by a mime", PASSWORDS[2]);


    }

    /**
     * This method is used for debugging purposes. I use manually
     * inserted passwords in the array  PASSWORDS (see line 9) rather than grabbing the
     * passwords from the txt file.
     *
     * This method is to ensure that if we were rotating passwords, that
     * XPerienceServerDB can still manage to work
     * @param server
     * @param port
     * @param name
     * @param date
     * @param time
     * @param description
     * @param password
     */
    private static void testEvent(String server, int port, String name,
                                  String date, String time,
                                  String description, String password) {
        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = name + DELIM + date + DELIM + time + DELIM +
                    description + DELIM + password + DELIM;
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void testWithNewPassword(String server, int port,
                                            String name, String date,
                                            String time, String desc) throws IOException {
        PasswordList passwordList = new PasswordList(PASSWORDS_FILE);
        String password = passwordList.getFirstPassword();

        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = name + DELIM + date + DELIM + time + DELIM + desc + DELIM + password + DELIM;
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Server response: " + response);
        }
    }
    private static void resetPasswordList() throws IOException {
        PasswordList passwordList = new PasswordList(PASSWORDS_FILE);
    }
}