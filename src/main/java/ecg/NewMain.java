package ecg;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewMain extends Application {

    private static final int MAX_DATA_POINTS = 100;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private InputSignalStub input;
    private EcgHandler output;
    private UpdateLineChart measureChart;
    private UpdateLineChart measureDerivativeChart;

    public static void main(String[] args) {
        launch(args);
    }

    private void init(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400);

        VBox vbox = new VBox(3);
        vbox.setPadding(new Insets(5));

        // scroll bar for app
        final ScrollBar sc = new ScrollBar();
        sc.setLayoutX(scene.getWidth() - sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(400);
        sc.setMax(800);

        sc.valueProperty().addListener((observable, oldValue, newValue) -> {
            vbox.setLayoutY(-newValue.doubleValue());
        });

        measureChart = new UpdateLineChart(MAX_DATA_POINTS, "Value");
        measureDerivativeChart = new UpdateLineChart(MAX_DATA_POINTS, "Value derivative");


        ListView<String> list = new ListView<>();
        String[] portNames = {"aa", "bb", "cc", "bb", "cc", "bb", "cc", "bb", "cc"};
        ObservableList<String> items = FXCollections.observableArrayList(portNames);
        list.setItems(items);
        list.setMaxHeight(100);

        vbox.getChildren().addAll(measureChart.getLineChart(), measureDerivativeChart.getLineChart(), list);
        root.getChildren().addAll(vbox, sc);


        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            running.set(false);
            Platform.exit();
            System.exit(0);
        });
    }

    @Override
    public void start(Stage stage) {
        init(stage);

        BlockingQueue<Double> queue = new LinkedBlockingQueue<>();
        ConcurrentLinkedQueue<Double> measureValues = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Double> measureDerivativeValues = new ConcurrentLinkedQueue<>();
        input = new InputSignalStub(queue, running);
        output = new EcgHandler(queue, measureValues, measureDerivativeValues, running);

        Thread inp = new Thread(input);
        Thread out = new Thread(output);

        inp.start();
        out.start();

        prepareTimeline(measureChart, measureValues);
        prepareTimeline(measureDerivativeChart, measureDerivativeValues);

        stage.show();
    }

    private void prepareTimeline(UpdateLineChart lineChart, ConcurrentLinkedQueue<Double> queue) {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries(lineChart, queue);
            }
        }.start();
    }

    private void addDataToSeries(UpdateLineChart lineChart, ConcurrentLinkedQueue<Double> queue) {
        if (!queue.isEmpty()) {
            lineChart.addDataToSeries(queue.remove());
        }
    }
}