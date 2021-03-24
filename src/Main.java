import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Schedule schedule = new Schedule();
        schedule.attemptToAddEvent(new Event("1", LocalDateTime.of(2021, 1, 1, 9, 0), LocalDateTime.of(2021, 1, 1, 10, 0)));
        schedule.attemptToAddEvent(new Event("1.1", LocalDateTime.of(2021, 1, 1, 9, 30), LocalDateTime.of(2021, 1, 1, 10, 30)));
        schedule.attemptToAddEvent(new Event("0", LocalDateTime.of(2021, 1, 1, 7, 0), LocalDateTime.of(2021, 1, 1, 8, 0)));
        schedule.attemptToAddEvent(new Event("2", LocalDateTime.of(2021, 1, 2, 7, 0), LocalDateTime.of(2021, 1, 2, 8, 0)));
        schedule.display();
    }
}