import java.time.LocalDateTime;

public class Event {

    /** the name of the Event */
    private String name;
    /** the date and time when the Event starts */
    private LocalDateTime start;
    /** the date and time when the Event ends */
    private LocalDateTime end;
    /** whether or not the Event repeats every week */
    private boolean repeatingWeekly;

    /**
     * Constructor for an Event given the name, starting time, ending time, but not if it repeats weekly.
     * In this case, it creates an Event with the specified information and fills {@link #repeatingWeekly} as false
     * @param name {@link #name}
     * @param start {@link #start}
     * @param end {@link #end}
     */
    public Event(String name, LocalDateTime start, LocalDateTime end) {
        this(name, start, end, false);
    }

    /**
     * Constructor for an Event given the name, starting time, ending time, and if it repeats weekly
     * @param name {@link #name}
     * @param start {@link #start}
     * @param end {@link #end}
     * @param repeatingWeekly {@link #repeatingWeekly}
     */
    public Event(String name, LocalDateTime start, LocalDateTime end, boolean repeatingWeekly) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.repeatingWeekly = repeatingWeekly;
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

    public boolean isRepeatingWeekly() {
        return repeatingWeekly;
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

    /**
     * Duplicates the Event a week later. Only works if the Event is repeating weekly.
     */
    public Event duplicateEvent() {
        if (repeatingWeekly) {
            return new Event(name, start.plusWeeks(1), end.plusWeeks(1), repeatingWeekly);
        } else {
            return null;
        }
    }
}