/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CWK3;

import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Administrator
 */
public class Plot extends Application {

    // the data model
    private MeasurementData md = null;
    
    Scene scene;
    
    /**
     * setMeasuremenData
     * @param m 
     */
    public void setMeasuremenData(MeasurementData m) {
        this.md = m;
    }
    
    @Override
    public void start(Stage stage) {

        // ref: https://stackoverflow.com/questions/28788117/stacking-charts-in-javafx
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        // base chart
        final BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        
        // add data to bar chart
        barChart.getData().add( createBarChartSeries());

        // barChart.setBarGap(13);
        // barChart.setCategoryGap(50);
        
        // add css to bar chart
        barChart.getStylesheets().addAll(getClass().getResource("chart.css").toExternalForm());
        
        // set labels
        xAxis.setLabel("Measurement data ");
        yAxis.setLabel("Normalised data count");

        // overlay chart
        // xAxis.setTickLength(10);
        
        // line chart
        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        // lineChart.setCreateSymbols(true);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAlternativeColumnFillVisible(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.getXAxis().setVisible(false);
        lineChart.getYAxis().setVisible(false);
        // set css
        lineChart.getStylesheets().addAll(getClass().getResource("chart.css").toExternalForm());

        // add data to line chart
        lineChart.getData().add( createLineChartSeries());
        
        
        StackPane sp = new StackPane();
        // sp.getChildren().addAll( barChart, lineChart);
        
        // label to show fitting parameters
        Label label_params = new Label();
        
        // get fitting parameters from data model
        double[] ps = md.getParamaters();
        label_params.setText("          factor:\t" + ps[0] + " \n          Mean:\t" + ps[1] + " \n          Sigma:\t" + ps[2]);
        
        // a button
        Button btn = new Button("Export to png file");
        // button1.getStyleClass().add("button1");
        // when the button is clicked, take a snapshot of the scene.
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                // hide label and button.
                label_params.setVisible(false);
                btn.setVisible(false);
                
                // export scene to png file
                exportPngFile();
                
                // show label and button
                btn.setVisible(true);
                label_params.setVisible(true);
                
                // pop a message box
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("export");
                alert.setHeaderText(null);
                alert.setContentText("./chart.png saved.");

                alert.showAndWait();

             }
        });
                
        HBox vbox = new HBox();
        vbox.setLayoutX(0);
        vbox.setLayoutY(0);
        vbox.getChildren().add(btn);
        vbox.getChildren().add(label_params);
        vbox.setMaxHeight(100);
        
        sp.getChildren().addAll(barChart,lineChart);
        
        GridPane gp = new GridPane();
        gp.add(vbox, 0, 0);
        gp.add(sp, 0, 1);
        scene = new Scene(gp, 500, 500);

        // scene  = new Scene(barChart,800,600);

        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }
    
    /**
     * exportPngFile
     */
    public void exportPngFile() {
        WritableImage wim = scene.snapshot(null);
        File fileA = new File("./chart.png");
        try {
             ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", fileA);
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * createBarChartSeries
     * @return XYChart.Series<String,Number> 
     */
    private XYChart.Series<String,Number> createBarChartSeries() {

        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        // get binned data from model
        ArrayList<TwoTuple<Double, Integer>> arr = md.getArrBinnedData();
        for (TwoTuple<Double, Integer>tt : arr) {
            // System.out.println(String.format("%.4f", tt.first * 100000) + ":" + tt.second);
            series.getData().add(new XYChart.Data<String,Number>(String.format("%.4f", tt.first * 100000), tt.second));
        }
        return series;
    }
    
    /**
     * createLineChartSeries
     * @return XYChart.Series<String,Number> 
     */
    private XYChart.Series<String,Number> createLineChartSeries() {

        XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
        // get fitted data from data model
        ArrayList<TwoTuple<Double, Double>> arr = md.getArrFittedData();
        for (TwoTuple<Double, Double>tt : arr) {
            // System.out.println(String.format("%.4f", tt.first * 100000) + "====" + tt.second);
            series.getData().add(new XYChart.Data<String,Number>(String.format("%.4f", tt.first * 100000), tt.second));
        }
        return series;
    }

}
