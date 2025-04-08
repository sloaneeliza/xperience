package xperience;

import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test XPerience Server
 * @version 1.1
 */
public class XPerienceServerTest {
  /**
   * Default character encoding
   */
  private static final Charset ENC = StandardCharsets.US_ASCII;
  
  public static void main(String[] args) {
    // Test and set arguments
    if (args.length < 2 || args.length > 3) {
      System.err.println("Parameter(s): <server> <port> [-eol]");
      System.exit(1);
    }
    String server = args[0];
    int port = Integer.parseInt(args[1]);
    boolean eol = (args.length == 3);
    
    // Run tests
    interact(server, port, "Danoke#02/12/2025#8pm#Fusion of Karaoke and Dance#", eol);
    interact(server, port, "Dona Dance#02/14/2025#8pm#Dance like you don’t care#", eol);
    interact(server, port, "Dona Dance Dance#02/14/2025#9pm#Light the night#", eol);
    interact(server, port, "Danoke#02/12/2025#8pm#Fusion of Karaoke and Dance#", eol);
    interact(server, port, "Dona Dance#03/14/2025#8am#Dance dance like you don’t care#", eol);
    interact(server, port, "Dona Dance Dance#03/14/2025#8pm#Light the night#", eol);
    interact(server, port, "Safety#02/16/2025#8pm#Dance#Leave your friends#", eol);
    interact(server, port, "Safety#02/16/2025#8pm#Dance#", eol);
  }
  
  /**
   * Create connection to server:port, send string, and optionally EoL
   * 
   * @param server ip/name to connect to
   * @param port port to connect to
   * @param send string to send
   * @param eol true if should send \n
   */
  private static void interact(String server, int port, String send, boolean eol) {
    // Connect to server:port
    try (Socket clntSock = new Socket(server, port);
        PrintWriter out = new PrintWriter(clntSock.getOutputStream(), true, ENC)) {
      // Send string
      out.write(send + (eol ? "\n" : ""));
      out.flush();
      // Print response
      print(clntSock.getInputStream().readAllBytes());
    } catch (Exception ex) {
      System.err.println("Client communication failed: " + ex.getMessage());
    }
  }
  
  /**
   * Print characters (ASCII) to console.  Print non-printable characters as decimal
   * 
   * @param buf bytes to print
   */
  private static void print(byte[] buf) {
    for (int i=0; i < buf.length; i++) {
      // If printable
      if (buf[i] >= 32 && buf[i] < 127) {
        System.out.print((char) buf[i]);
      } else {
        // not printable (so show [decimal]
        System.out.print("[" + buf[i] + "]");
      }
    }
    System.out.println();
  }
}
