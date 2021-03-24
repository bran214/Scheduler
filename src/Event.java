import java.time.LocalDateTime;

public class Event {

    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean isRepeating;

    public Event(String name, LocalDateTime start, LocalDateTime end, boolean isRepeating) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.isRepeating = isRepeating;
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