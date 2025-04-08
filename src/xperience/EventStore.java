package xperience;

/**
 * EventStore.java
 *
 * Interface defining the contract for event storage operations in the XPerience system.
 * Implementations must provide thread-safe methods for checking duplicates, saving events,
 * and retrieving event counts.
 *
 * <p><b>Key Responsibilities:</b>
 * <ul>
 *   <li>Validate event name uniqueness</li>
 *   <li>Persist event data</li>
 *   <li>Maintain event count statistics</li>
 * </ul></p>
 *
 * @author Sloane Wright
 * @version 2.0
 * @since 2025-04-02
 */
public interface EventStore {
    boolean isNameDuplicate(String name) throws StorageException;
    void saveEvent(Event event) throws StorageException;
    int getTotalEvents() throws StorageException;
}

class StorageException extends Exception {
    public StorageException(String message) { super(message); }
}