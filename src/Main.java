import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Schedule schedule = new Schedule();
        for (int k = 1900; k <= 2021; k++) {
            for (int i = 12; i >= 1; i--) {
                for (int j = 28; j >= 1; j--) {
                    System.out.println(schedule.attemptToAddEvent(new Event(k + " " + i + " " + j, LocalDateTime.of(k, i, j, 9, 0), LocalDateTime.of(k, i, j, 10, 0))));
                }
            }
        }
        schedule.display();
    }
}