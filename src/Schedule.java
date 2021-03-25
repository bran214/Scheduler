import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable {

    private ArrayList<Event> events = new ArrayList<>();

    public void display() {
        for (Event event : events) {
            System.out.println(event.getStart().toString());
            System.out.println("\t" + event.getName());
            System.out.println(event.getEnd().toString());
            System.out.println();
        }
    }

    /**
     * Sorts all of the stored Events in the Schedule
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
        System.out.println(events.size());
        if (events.size() == 0) {
            events.add(event);
            return true;
        }
        for (int i = 0; i < events.size(); i++) {
            if (i == 0 || i == events.size() - 1) { // if i is an endpoint, then it must be handled specially
                if (i == 0 && event.getStart().isBefore(events.get(i).getStart())) { // if the event is before i and i is 0, it must go first
                    if (!(event.isOverlapping(events.get(i)))) {
                        events.add(i, event);
                        return true;
                    } else {
                        return false;
                    }
                } else if (i == events.size() - 1 && event.getStart().isAfter(events.get(i).getStart())) { // if the event is after i and i is the last in events, it must go last
                    if (!(event.isOverlapping(events.get(i)))) {
                        events.add(event);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (event.getStart().isBefore(events.get(i + 1).getStart()) && event.getStart().isAfter(events.get(i).getStart())) { // if the event to be added is before the next event but after the current event
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

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        events = (ArrayList<Event>) in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(events);
    }
}