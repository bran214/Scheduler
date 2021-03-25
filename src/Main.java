import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Schedule schedule = new Schedule();
        for (int i = 12; i >= 1; i--) {
            for (int j = 28; j >= 1; j--) {
                System.out.println(schedule.attemptToAddEvent(new Event(2021 + " " + i + " " + j, LocalDateTime.of(2021, i, j, 9, 0), LocalDateTime.of(2021, i, j, 10, 0))));
            }
        }
    }
}