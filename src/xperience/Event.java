package xperience;
/**
 * Represents an event with name, date, time, and description.
 *
 * <p>Used by {@link XPerienceServer} to store and validate event submissions.
 * Event names must be unique within the server instance.</p>
 *
 * @author Sloane Wright
 * @version 1.0
 * @since 2025-03-26
 */

public class Event {
    private String name;
    private String date;
    private String time;
    private String description;

    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}