package xperience;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * XPeriencePasswordTester.java
 *
 * Comprehensive password testing client for XPerienceServer
 * Tests valid/invalid passwords and password reuse scenarios
 *
 * @version 1.0
 */
public class XPeriencePasswordTest {
    private static final String DELIM = "#";
    private static final String SERVER = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=== XPerience Password Test Client ===");

        testValidPassword("TechConf", "2025-08-15", "14:00", "Annual tech conference", "mypass123");
        testInvalidPassword("MusicFest", "2025-09-20", "18:00", "Live music event", "wrongpass");
        testPasswordReuse("ArtShow", "2025-10-05", "10:00", "Local artists exhibition", "securePass");
        testEmptyPassword("Workshop", "2025-11-12", "13:30", "Programming workshop", "");
        testMissingPasswordField("Meetup", "2025-12-24", "19:00", "Community gathering");
    }

    private static void testValidPassword(String name, String date, String time, String desc, String password) {
        System.out.println("\n[TEST 1] Valid Password (" + password + ")");
        sendRequest(name, date, time, desc, password);
    }

    private static void testInvalidPassword(String name, String date, String time, String desc, String password) {
        System.out.println("\n[TEST 2] Invalid Password (" + password + ")");
        sendRequest(name, date, time, desc, password);
    }

    private static void testPasswordReuse(String name, String date, String time, String desc, String password) {
        System.out.println("\n[TEST 3] Password Reuse (" + password + ")");
        System.out.println("First attempt:");
        sendRequest(name, date, time, desc, password);
        System.out.println("Second attempt:");
        sendRequest(name + "2", date, time, desc, password);
    }

    private static void testEmptyPassword(String name, String date, String time, String desc, String password) {
        System.out.println("\n[TEST 4] Empty Password");
        sendRequest(name, date, time, desc, password);
    }

    private static void testMissingPasswordField(String name, String date, String time, String desc) {
        System.out.println("\n[TEST 5] Missing Password Field");
        try (Socket socket = new Socket(SERVER, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = name + DELIM + date + DELIM + time + DELIM + desc + DELIM;
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }

    private static void sendRequest(String name, String date, String time, String desc, String password) {
        try (Socket socket = new Socket(SERVER, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = name + DELIM + date + DELIM + time + DELIM + desc + DELIM + password + DELIM;
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
}