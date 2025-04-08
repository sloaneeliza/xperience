package xperience;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class to manage password list
 *
 * @version 1.0
 */
public class PasswordList {
    private final List<String> passwords;
    private final Path passwordFilePath;
    private static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);

    /**
     * Create password list from file
     *
     * @param passwordFilePath Path to password file
     * @throws IOException if I/O problem
     */
    public PasswordList(String passwordFilePath) throws IOException {
        this.passwordFilePath = Path.of(passwordFilePath);
        this.passwords = new ArrayList<>(Files.readAllLines(this.passwordFilePath));
    }

    /**
     * If password in list, remove password from list and return true;
     * otherwise (not in list), return false
     *
     * @param password password to use
     * @return true if password in list; false otherwise
     */
    public synchronized boolean use(String password) {
        if (passwords.contains(password)) {
            passwords.remove(password);
            logger.fine("Password used: " + password);
            return true;
        }
        logger.warning("Invaliad password: " + password);
        return false;
    }

    /**
     * Gets the first available password without removing it
     * @return first password or null if empty
     */
    public synchronized String getFirstPassword() {
        return passwords.isEmpty() ? null : passwords.get(0);
    }

    /**
     * Returns the number of available passwords
     * @return current count of remaining passwords
     */
    public synchronized int size() {
        return passwords.size();
    }
}