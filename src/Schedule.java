import java.util.ArrayList;

public class Schedule {

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
    public boolean attemptToAddEvent(Event event) {
        for (Event otherEvent : events) {
            if (event.isOverlapping(otherEvent)) {
                return false;
            }
        }
        events.add(event);
        sortEvents();
        return true;
    }
}