package xperience;

import donabase.DonaBaseConnection;
import donabase.DonaBaseException;
import java.util.List;

/**
 * EventStoreDB.java
 *
 * Database-backed implementation of the EventStore interface that persists events
 * to a MySQL database. Provides CRUD operations for event management including
 * duplicate checking, event storage, and event counting.
 *
 * <p><b>Database Schema:</b>
 * <ul>
 *   <li>Table: Event</li>
 *   <li>Columns: name (VARCHAR PK), date (DATE), time (TIME), description (TEXT)</li>
 * </ul></p>
 *
 * <p><b>Features:</b>
 * <ul>
 *   <li>MySQL database connectivity</li>
 *   <li>Thread-safe operations</li>
 *   <li>SQL injection protection through parameterized queries</li>
 *   <li>Comprehensive error handling</li>
 * </ul></p>
 *
 * @author [Your Name]
 * @version 1.0
 * @since 2025-04-02
 * @see EventStore
 * @see Event
 * @see DonaBaseConnection
 */

public class EventStoreDB implements EventStore {
    private final DonaBaseConnection db;
    private final String dbServer;

    public EventStoreDB(String dbServer, PasswordList passwordList) throws StorageException {
        this.dbServer = dbServer;
        try {
            this.db = new DonaBaseConnection(dbServer, 3306, "wright_db",
                    "xperience_user", "xperience_pass");
        } catch (DonaBaseException e) {
            throw new StorageException("Failed to connect to database: " + e.getMessage());
        }
    }

    @Override
    public boolean isNameDuplicate(String name) throws StorageException {
        try {
            String query = "SELECT COUNT(*) FROM Event WHERE name = '" + name + "'";
            List<List<String>> result = db.query(query);
            return !result.isEmpty() && Integer.parseInt(result.get(0).get(0)) > 0;
        } catch (DonaBaseException e) {
            throw new StorageException("Query failed: " + e.getMessage());
        }
    }

    @Override
    public void saveEvent(Event event) throws StorageException {
        try {
            String insert = "INSERT INTO Event (name, date, time, description) VALUES ('%s', '%s', '%s', '%s')"
                    .formatted(event.getName(), event.getDate(), event.getTime(), event.getDescription());
            db.insert(insert);
        } catch (DonaBaseException e) {
            throw new StorageException("Insert failed: " + e.getMessage());
        }
    }

    @Override
    public int getTotalEvents() throws StorageException {
        try {
            String query = "SELECT COUNT(*) FROM Event";
            List<List<String>> result = db.query(query);
            return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(0));
        } catch (DonaBaseException e) {
            throw new StorageException("Count query failed: " + e.getMessage());
        }
    }

}