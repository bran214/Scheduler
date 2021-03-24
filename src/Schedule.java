import java.util.ArrayList;

public class Schedule {

    private ArrayList<Event> events;

    public void display() {
        System.out.println(events);
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}