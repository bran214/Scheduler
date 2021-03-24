import java.time.LocalDateTime;

public class Event {

    /** the name of the Event */
    private String name;
    /** the date and time when the Event starts */
    private LocalDateTime start;
    /** the date and time when the Event ends */
    private LocalDateTime end;

    private boolean isRepeating;

    /**
     * Constructor for an Event given the name, starting time, ending time, and
     * @param name {@link #name}
     * @param start {@link #start}
     * @param end {@link #end}
     * @param isRepeating
     */
    public Event(String name, LocalDateTime start, LocalDateTime end, boolean isRepeating) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.isRepeating = isRepeating;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Determines if this event is overlapping a different event.
     * @param otherEvent a different event
     * @return true if the events overlap, false if they don't
     */
    public boolean isOverlapping(Event otherEvent) {
        if (this.start.isBefore(otherEvent.start) || this.start.isEqual(otherEvent.start)) {
            // determine if otherEvent.start is either before or after this.end
            if (otherEvent.start.isBefore(this.end)) {
                return true;
            } else { // otherEvent.start is after or equal to this.end (equal doesn't qualify as overlapping)
                return false;
            }
        } else { // this.start is after other.start
            // determine if other.end is before or after this.start
            if (otherEvent.end.isAfter(this.start)) {
                return true;
            } else { // other.end is before or equal to this.start
                return false;
            }
        }
    }
}