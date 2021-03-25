import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Event implements Serializable {

    /** the name of the Event */
    private String name;
    /** the date and time when the Event starts */
    private LocalDateTime start;
    /** the date and time when the Event ends */
    private LocalDateTime end;

    /**
     * Constructor for an Event given the name, starting time, ending time, and if it repeats weekly
     * @param name {@link #name}
     * @param start {@link #start}
     * @param end {@link #end}
     */
    public Event(String name, LocalDateTime start, LocalDateTime end) {
        this.name = name;
        this.start = start.truncatedTo(ChronoUnit.MINUTES);
        this.end = end.truncatedTo(ChronoUnit.MINUTES);
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

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        this.name = in.readUTF();
        this.start = (LocalDateTime) in.readObject();
        this.end = (LocalDateTime) in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeObject(start);
        out.writeObject(end);
    }
}