import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Main extends Application {

    Schedule schedule = new Schedule();
    public final static String SAVEFILE = "schedule.ser";
    public final static int WIDTH = 1600;
    public final static int HEIGHT = 800;

    Label firstEventDate = new Label();
    Label lastEventDate = new Label();
    LocalDateTime firstEventStart;
    LocalDateTime lastEventEnd;

    TextField eventName = new TextField();
    TextField eventTime = new TextField();
    Button confirmEventButton = new Button("Confirm time and view possible slots");
    Label timeSliderLabel = new Label("Adjust the slider to move the positioning of the event in an empty slot");
    Slider timeSlider = new Slider();

    Button addButton = new Button();
    boolean adding = false;

    HBox eventsDisplay = new HBox();
    VBox eventDetails = new VBox();

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane rootNode = new BorderPane();
        Scene scene = new Scene(rootNode, WIDTH, HEIGHT);
        scene.getStylesheets().add("style.css");

        try {
            ObjectInputStream fileIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SAVEFILE)));
            schedule = (Schedule) fileIn.readObject();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add everything to the rootNode
        rootNode.setTop(eventsDisplay);
        rootNode.setLeft(firstEventDate);
        rootNode.setRight(lastEventDate);
        rootNode.setCenter(eventDetails);
        eventDetails.getChildren().add(eventName);
        eventDetails.getChildren().add(eventTime);
        eventDetails.getChildren().add(confirmEventButton);
        confirmEventButton.setOnAction(new ConfirmButtonHandler());
        eventDetails.getChildren().add(timeSliderLabel);
        eventDetails.getChildren().add(timeSlider);
        timeSlider.setMin(0);
        timeSlider.setMax(1);
        eventDetails.setAlignment(Pos.CENTER);
        rootNode.setBottom(addButton);
        addButton.setOnAction(new AddButtonHandler());

        updateView();

        stage.setScene(scene);
        stage.setTitle("Scheduler");
        stage.setResizable(false);
        stage.show();
    }

    public void updateView() { // called upon launch and after an event is added
        if (schedule.getEvents().size() > 0) {
            firstEventStart = schedule.getEvents().get(0).getStart();
            lastEventEnd = schedule.getEvents().get(schedule.getEvents().size() - 1).getEnd();
            firstEventDate.setText(firstEventStart.toLocalDate().toString());
            lastEventDate.setText(lastEventEnd.toLocalDate().toString());
        }

        eventsDisplay.setSpacing(0);
        eventsDisplay.setAlignment(Pos.CENTER);
        eventsDisplay.setPrefWidth(WIDTH * 0.8);
        eventsDisplay.getChildren().clear();
        for (int i = 0; i < schedule.getEvents().size(); i++) {
            if (i == 0) {
                Button firstEmptyButton = new Button();
                firstEmptyButton.setPrefSize(WIDTH * 0.1, eventsDisplay.getHeight());
                firstEmptyButton.setDisable(true);
                firstEmptyButton.setVisible(false);
                firstEmptyButton.setText("Before...");
                firstEmptyButton.setOnAction(new CreateEventButtonHandler());
                eventsDisplay.getChildren().add(firstEmptyButton);
            }
            Event event = schedule.getEvents().get(i);
            int width = (int) (eventsDisplay.getPrefWidth() * ((double) Duration.between(event.getStart(), event.getEnd()).toMillis() / (double) Duration.between(firstEventStart, lastEventEnd).toMillis()));
            Button eventButton = new Button();
            eventButton.setPrefSize(width, eventsDisplay.getHeight());
            eventButton.getStyleClass().add("event-button");
            eventButton.setUserData(event);
            eventButton.setOnAction(new EventButtonHandler());
            eventsDisplay.getChildren().add(eventButton);
            if (i < schedule.getEvents().size() - 1) {
                Button emptyButton = new Button();
                int emptyWidth = (int) (eventsDisplay.getPrefWidth() * ((double) Duration.between(event.getEnd(), schedule.getEvents().get(i + 1).getStart()).toMillis() / (double) Duration.between(firstEventStart, lastEventEnd).toMillis()));
                emptyButton.setPrefSize(emptyWidth, eventsDisplay.getHeight());
                emptyButton.setDisable(true);
                emptyButton.setVisible(false);
                emptyButton.setMinWidth(1);
                emptyButton.setOnAction(new CreateEventButtonHandler());
                eventsDisplay.getChildren().add(emptyButton);
            }
        }

        // add the last "empty" button after the for loop so that there is a way to add events even when there are no existing ones
        Button lastEmptyButton = new Button();
        lastEmptyButton.setPrefSize(WIDTH * 0.1, eventsDisplay.getHeight());
        lastEmptyButton.setDisable(true);
        lastEmptyButton.setVisible(false);
        lastEmptyButton.setText("After...");
        lastEmptyButton.setOnAction(new CreateEventButtonHandler());
        eventsDisplay.getChildren().add(lastEmptyButton);

        // ensure everything that shouldn't be visible isn't and that all fields are sufficiently cleared
        eventName.setEditable(false);
        eventTime.setEditable(false);
        eventName.setPromptText("Event Name");
        eventTime.setPromptText("Event Time");
        eventName.clear();
        eventTime.clear();
        confirmEventButton.setVisible(false);
        confirmEventButton.setDisable(true);
        timeSliderLabel.setVisible(false);
        timeSlider.setVisible(false);
        timeSlider.setDisable(true);
        addButton.setText("Start adding");
        adding = false;
    }

    @Override
    public void stop() throws Exception {
        try {
            ObjectOutputStream fileOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(SAVEFILE)));
            fileOut.writeObject(schedule);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.stop();
    }

    class EventButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            Button eventButton = (Button) e.getSource();
            Event event = (Event) eventButton.getUserData();
            eventName.setText(event.getName());
            eventTime.setText(event.getStart().toLocalDate().toString() + " " + event.getStart().toLocalTime().toString() + " to " + event.getEnd().toLocalDate().toString() + " " + event.getEnd().toLocalTime().toString());
        }
    }

    class AddButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            if (!adding) { // toggle to adding mode
                for (int i = 0; i < eventsDisplay.getChildren().size(); i++) { // disable clicking on existing events
                    Node node = eventsDisplay.getChildren().get(i);
                    if (node.getUserData() instanceof Event) {
                        node.setDisable(true);
                    }
                }
                // clear fields, set things visible
                eventName.clear();
                eventTime.clear();
                eventName.setEditable(true);
                eventTime.setEditable(true);
                confirmEventButton.setVisible(true);
                confirmEventButton.setDisable(false);
                timeSliderLabel.setVisible(true);
                timeSlider.setVisible(true);
                timeSlider.setDisable(false);
                addButton.setText("Stop adding");
                adding = true;
            } else { // toggle adding mode off
                for (int i = 0; i < eventsDisplay.getChildren().size(); i++) { // re-enable clicking on existing events and hide empty spaces
                    Node node = eventsDisplay.getChildren().get(i);
                    if (!(node.getUserData() instanceof Event)) {
                        node.setVisible(false);
                        node.setDisable(true);
                    } else {
                        node.setDisable(false);
                    }
                }
                // clear fields and set things invisible
                eventName.clear();
                eventTime.clear();
                eventName.setEditable(false);
                eventTime.setEditable(false);
                confirmEventButton.setVisible(false);
                confirmEventButton.setDisable(true);
                timeSliderLabel.setVisible(false);
                timeSlider.setVisible(false);
                timeSlider.setDisable(true);
                addButton.setText("Start adding");
                adding = false;
            }
        }
    }

    class ConfirmButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            int days;
            int hours;
            int minutes;
            Duration duration = Duration.between(LocalDateTime.MIN, LocalDateTime.MAX);
            try { // a valid input is given
                days = Integer.parseInt(eventTime.getText().substring(0, 2));
                hours = Integer.parseInt(eventTime.getText().substring(3, 5));
                minutes = Integer.parseInt(eventTime.getText().substring(6));
                duration = Duration.ofDays(days).plus(Duration.ofHours(hours).plus(Duration.ofMinutes(minutes))); // convert that input to a duration
            } catch (Exception exception) { // otherwise duration will be left at the maximum possible value, ensuring an event can only be added before or after, or when a valid input is given
                eventTime.clear();
                eventTime.setPromptText("Please enter a time in the format DD:HH mm");
            }
            ArrayList<Integer> possibleLocations = schedule.availableSlots(duration); // determine all possible locations for the event
            ArrayList<Button> emptySlots = new ArrayList<>();
            for (int i = 0; i < eventsDisplay.getChildren().size(); i++) { // collect all of the "empty" buttons into an array
                Node node = eventsDisplay.getChildren().get(i);
                if (!(node.getUserData() instanceof Event)) {
                    emptySlots.add((Button) node);
                }
            }
            for (int i = 0; i < emptySlots.size(); i++) { // looping through those empty buttons, which match a possible location?
                if (possibleLocations.contains(i)) { // if it matches, enable it
                    emptySlots.get(i).setDisable(false);
                    emptySlots.get(i).setVisible(true);
                } else { // if it doesn't, disable it
                    emptySlots.get(i).setDisable(true);
                    emptySlots.get(i).setVisible(false);
                }
            }
        }
    }

    class CreateEventButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {
            Button eventButton = (Button) e.getSource();
            if (eventButton.getText().equals("Before...") || eventButton.getText().equals("After...")) { // if it is a special "Before..." or "After..." button
                try {
                    String eventTimeText = eventTime.getText();
                    // obtain the start and ending times
                    int startYear = Integer.parseInt(eventTimeText.substring(0, 4));
                    int startMonth = Integer.parseInt(eventTimeText.substring(5, 7));
                    int startDay = Integer.parseInt(eventTimeText.substring(8, 10));
                    int startHour = Integer.parseInt(eventTimeText.substring(11, 13));
                    int startMinute = Integer.parseInt(eventTimeText.substring(14, 16));
                    int endYear = Integer.parseInt(eventTimeText.substring(20, 24));
                    int endMonth = Integer.parseInt(eventTimeText.substring(25, 27));
                    int endDay = Integer.parseInt(eventTimeText.substring(28, 30));
                    int endHour = Integer.parseInt(eventTimeText.substring(31, 33));
                    int endMinute = Integer.parseInt(eventTimeText.substring(34));
                    schedule.attemptToAddEvent(new Event(eventName.getText(), LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute), LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute)));
                    updateView();
                } catch (Exception exception) {
                    eventTime.clear();
                    eventTime.setPromptText("Please enter the start and end of the event (YYYY-MM-DD HH:mm to YYYY-MM-DD HH:mm) and click Before... or After... again.");
                }
            } else {
                try {
                    int i = eventsDisplay.getChildren().indexOf(eventButton);
                    Event previousEvent = (Event) eventsDisplay.getChildren().get(i - 1).getUserData();
                    Event nextEvent = (Event) eventsDisplay.getChildren().get(i + 1).getUserData();
                    int days = Integer.parseInt(eventTime.getText().substring(0, 2));
                    int hours = Integer.parseInt(eventTime.getText().substring(3, 5));
                    int minutes = Integer.parseInt(eventTime.getText().substring(6));
                    Duration duration = Duration.ofDays(days).plus(Duration.ofHours(hours).plus(Duration.ofMinutes(minutes)));
                    Duration movableSpace = Duration.between(previousEvent.getEnd(), nextEvent.getStart()).minus(duration);
                    Duration sliderOffset = Duration.of((long) (timeSlider.getValue() * movableSpace.toMinutes()), ChronoUnit.MINUTES);
                    schedule.attemptToAddEvent(new Event(eventName.getText(), previousEvent.getEnd().plus(sliderOffset), previousEvent.getEnd().plus(duration).plus(sliderOffset)));
                    updateView();
                } catch (Exception exception) {
                    eventTime.clear();
                    eventTime.setPromptText("Please enter a time in the format DD HH:mm");
                }
            }
        }
    }
}