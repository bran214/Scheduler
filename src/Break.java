import java.time.LocalDateTime;

/**
 * Class for a Break, which functions like {@link Event} except {@link Schedule} will detect that it's a {@link Break} and not issue notifications for it.
 * @author Brandon Winters
 */
public class Break extends Event {

    public Break(LocalDateTime start, LocalDateTime end) {
        super("Break", start, end);
    }
}