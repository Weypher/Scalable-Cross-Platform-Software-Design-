/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CWK3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * ref: HelloWorldWithGUI.
 * @author Pengbo Liu
 */
public class Main extends Application{
    
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        // load fxml
        Parent root = FXMLLoader.load(getClass().getResource("userInterface.fxml"));
        
        // window title
        primaryStage.setTitle("CWK3");
        
        primaryStage.setScene(new Scene(root));
        
        // set default value for Choice box: bin_type
        ChoiceBox<String> bin_type = (ChoiceBox<String>)root.lookup("#bin_type");
        bin_type.getItems().addAll("Square root choice", "Sturge’s formula", "Rice rule");
        bin_type.getSelectionModel().selectFirst();
        
        // do not support window resize.
        primaryStage.setResizable(false);
        
        // show
        primaryStage.show();
    }
    

    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }
    

    
    
    
}
