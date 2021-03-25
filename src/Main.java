import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Schedule schedule = new Schedule();
        for (int k = 2010; k <= 2021; k++) {
            for (int i = 12; i >= 1; i--) {
                for (int j = 1; j <= 28; j++) {
                    schedule.attemptToAddEvent(new Event(k + " " + i + " " + j, LocalDateTime.of(k, i, j, 9, 0), LocalDateTime.of(k, i, j, 10, 0)));
                }
            }
        }
        schedule.display();
    }
}