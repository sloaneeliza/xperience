package xperience;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EventStoreMemory.java
 *
 * In-memory implementation of the EventStore interface that stores events in a synchronized list.
 * Provides thread-safe operations for checking duplicate event names, saving events,
 * and tracking total event count using atomic operations.
 *
 * Features:
 * - Thread-safe event storage using synchronized list
 * - Duplicate name checking
 * - Atomic ID generation
 * - Basic input validation
 *
 * @author Sloane Wright
 * @since [2025-03-28]
 */

public class EventStoreMemory implements EventStore {
    private final List<Event> events = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public boolean isNameDuplicate(String name) throws StorageException {
        if (name == null || name.trim().isEmpty()) {
            throw new StorageException("Event name cannot be null/empty");
        }
        synchronized (events) {
            return events.stream().anyMatch(e -> e.getName().equals(name));
        }
    }

    @Override
    public void saveEvent(Event event) throws StorageException {
        if (event == null) {
            throw new StorageException("Event cannot be null");
        }
        synchronized (events) {
            events.add(event);
        }
    }

    @Override
    public int getTotalEvents() throws StorageException {
        return idCounter.getAndIncrement();
    }
}