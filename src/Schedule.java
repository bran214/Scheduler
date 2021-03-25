import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Schedule implements Serializable {

    private ArrayList<Event> events = new ArrayList<>();

    @Override
    public String toString() {
        String returnValue = "";
        for (Event event : events) {
            returnValue += event.getStart().toString() + "\n\t" + event.getName() + "\n" + event.getEnd().toString() + "\n";
        }
        return returnValue;
    }

    public ArrayList<Event> getEvents() {
        ArrayList<Event> copyOfEvents = new ArrayList<>();
        copyOfEvents.addAll(events);
        return copyOfEvents;
    }

    /**
     * Sorts all of the stored Events in the Schedule
     * @deprecated
     */
    public void sortEvents() {
        sortEvents(0);
    }

    /**
     * Recursive helper method to sortEvents()
     * @param startIndex the index to start sorting from
     */
    private void sortEvents(int startIndex) {
        int chronologicalFirstEventIndex = startIndex;
        for (int i = startIndex; i < events.size(); i++) {
            if (events.get(i).getStart().isBefore(events.get(chronologicalFirstEventIndex).getStart())) {
                chronologicalFirstEventIndex = i;
            }
        }
        // After the chronologicalFirstEvent is determined, swap it with the first Event in the ArrayList
        Event temp = events.get(startIndex);
        events.set(startIndex, events.get(chronologicalFirstEventIndex));
        events.set(chronologicalFirstEventIndex, temp);
        if (!(startIndex + 1 >= events.size())) {
            sortEvents(startIndex + 1); // everything up to the startIndex is guaranteed to be sorted, so sort everything after
        }
    }

//    /**
//     * Iterative method to sort all of the stored Events in the Schedule.
//     * This was deemed to be similarly efficient in terms of CPU usage and run time to the recursive method,
//     * so the recursive method will be used due to better legibility.
//     */
//    public void sortEvents() {
//        for (int i = 0; i < events.size(); i++) {
//            int chronologicalFirstEventIndex = i;
//            for (int j = i; j < events.size(); j++) {
//                if (events.get(j).getStart().isBefore(events.get(chronologicalFirstEventIndex).getStart()))
//                    chronologicalFirstEventIndex = j;
//            }
//            Event temp = events.get(i);
//            events.set(i, events.get(chronologicalFirstEventIndex));
//            events.set(chronologicalFirstEventIndex, temp);
//        }
//    }

    /**
     * Attempts to add an Event and returns if adding was successful.
     * Adding an event is unsuccessful if it overlaps with any other events.
     * @param event the Event to add to the Schedule
     * @return if the operation was successful
     */
    /*
        This is an optimized version of the attemptToAddEvent(Event) method which automatically finds where
        the Event should go, rather than depending on the sortEvents() method after adding.
        This is much more efficient in terms of CPU usage and run time when dealing with a large amount of Events.
        For example, when dealing with ~40,000 Events, adding these with the optimized method takes ~13 seconds, while
        the unoptimized version still didn't complete even after 5 minutes.
     */
    public boolean attemptToAddEvent(Event event) {
        if (events.size() == 0) { // if there are no elements in events, automatically add it as there are no possible conflicts
            events.add(event);
            return true;
        } else if (events.size() == 1) { // if there is only one element, this must be handled specially as events.size() - 1 is also equal to 0, which causes problems without this block
            if (!event.isOverlapping(events.get(0))) {
                if (event.getStart().isBefore(events.get(0).getStart())) {
                    events.add(0, event);
                    return true;
                } else {
                    events.add(event);
                    return true;
                }
            }
        }
        for (int i = 0; i < events.size(); i++) {
            if (i == events.size() - 1) { // if i is last in the array, going to the next block will cause errors so it must be handled specially
                if (event.getStart().isAfter(events.get(i).getStart())) {
                    if (!(event.isOverlapping(events.get(i)))) {
                        events.add(event);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if ((event.getStart().isBefore(events.get(i + 1).getStart()) && event.getStart().isAfter(events.get(i).getStart())) || i == 0) { // if the event to be added is before the next event but after the current event or i is 0
                if (i == 0 && event.getStart().isBefore(events.get(i).getStart())) { // if this is before any other event
                    if (!(event.isOverlapping(events.get(i)))) {
                        events.add(i, event);
                        return true;
                    }
                }
                if (!(event.isOverlapping(events.get(i + 1)) || event.isOverlapping(events.get(i)))) { // if the event doesn't overlap either of the other two events
                    events.add(i + 1, event); // add the event between the other two events
                    return true; // and return that the operation was successful
                } else { // otherwise the event does overlap one of the other two events
                    return false; // so the operation was unable to be completed
                }
            }
        }
        return false;
    }

//    /**
//     * Attempts to add an Event and returns if adding was successful.
//     * Adding an event is unsuccessful if it overlaps with any other events.
//     * @param event the Event to add to the Schedule
//     * @return if the operation was successful
//     */
//    public boolean attemptToAddEvent(Event event) { // older version which depended on the sortEvents() method after adding an Event.
//        for (Event otherEvent : events) {
//            if (event.isOverlapping(otherEvent)) {
//                return false;
//            }
//        }
//        events.add(event);
//        sortEvents();
//        return true;
//    }

    /**
     * Gives the positions at which a Duration can fit within the Schedule.
     * @param duration the duration to fit in the Schedule
     * @return an array of all the positions where the Duration fits
     */
    public ArrayList<Integer> availableSlots(Duration duration) {
        duration = duration.truncatedTo(ChronoUnit.MINUTES);
        ArrayList<Integer> slots = new ArrayList<>();
        if (events.size() == 0) {
            slots.add(0);
        }
        for (int i = 0; i < events.size(); i++) {
            if (i == 0) {
                slots.add(i);
            }
            if (i == events.size() - 1) {
                slots.add(i + 1);
            } else {
                if (Duration.between(events.get(i).getEnd(), events.get(i + 1).getStart()).compareTo(duration) >= 0) {
                    slots.add(i + 1);
                }
            }
        }
        return slots;
    }

    public Event issueNotification() {
        for (Event event : events) {
            if (LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).isEqual(event.getStart()) && !(event instanceof Break)) {
                return event;
            }
        }
        return null;
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, ClassCastException, IOException {
        events = (ArrayList<Event>) in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(events);
    }
}