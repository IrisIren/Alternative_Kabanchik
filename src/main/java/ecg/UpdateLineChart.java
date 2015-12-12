package ecg;


import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class UpdateLineChart {

    private final NumberAxis xAxis;
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private int max_data_points;
    private int xSeriesData = 0;

    public UpdateLineChart(int max_data_points, String title) {
        this.max_data_points = max_data_points;

        xAxis = new NumberAxis(0, max_data_points, max_data_points / 10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis();

        lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setAnimated(false);
        lineChart.setTitle(title);
        lineChart.setHorizontalGridLinesVisible(true);
        lineChart.getData().add(series);
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    public void addDataToSeries(Double d) {
        series.getData().add(new XYChart.Data<>(xSeriesData++, d));
        if (series.getData().size() > this.max_data_points) {
            series.getData().remove(0, series.getData().size() - this.max_data_points);
        }
        xAxis.setLowerBound(xSeriesData - this.max_data_points);
        xAxis.setUpperBound(xSeriesData - 1);
    }


}
