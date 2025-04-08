package xperience;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
 * Database-backed server implementing the XPerience protocol with password authentication
 * @version 2.0
 */
public class XPerienceServerDB {
    public static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);

    private final EventStore eventStore;
    private final int port;
    private final PasswordList passwordList;
    private final String dbServer;

    public XPerienceServerDB(EventStore eventStore, int port, PasswordList passwordList, String dbServer) {
        this.eventStore = eventStore;
        this.port = port;
        this.passwordList = passwordList;
        this.dbServer = dbServer;
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
        if (args.length != 3) {
            logger.severe("Incorrect parameters. Expected: <Port> <PasswordFile> <DBServer>");
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

            logger.info("XPerienceDB server started on port " + port);
            logger.info("Using database server: " + dbServer);

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
            String dbServer = args[2];
            PasswordList passwordList = new PasswordList(passwordFile);
            logger.info("Loaded " + passwordList.size() + " passwords");

            EventStore store = new EventStoreDB(dbServer, passwordList);
            new XPerienceServerDB(store, port, passwordList, dbServer).start();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Server initialization failed", ex);
            System.exit(1);
        }
    }
}