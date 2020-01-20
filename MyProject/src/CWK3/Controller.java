/**
 * Sample Skeleton for 'userInterface.fxml' Controller Class
 */

package CWK3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
/**
 * Controller
 * @author Administrator
 */
public class Controller {

    // data model
    private MeasurementData md = new MeasurementData();
    
    @FXML
    private Label label_log;
    
    
    @FXML
    private ChoiceBox<String> bin_type;
    
    void initialize() {

    }
    
     @FXML
    void plot(ActionEvent event) {
        if (!md.getIsLoaded()) {
            return;
        }
        
        md.bin(bin_type.getValue());
        md.fit();
        
        Plot p = new Plot();
        p.setMeasuremenData(md);
        Stage secondStage = new Stage();
        p.start(secondStage);
    }

    @FXML
    void Calculate(ActionEvent event) {
        if (!md.getIsLoaded()) {
            return;
        }
        double mean = 0.0;
        double variance = 0.0;
        double median = 0.0;
        double std = 0.0;
        
        mean = md.getMean();
        variance = md.getVariance();
        median = md.getMedian();
        std = md.getStd();
        
        label_log.setText(label_log.getText() + "\nmean: \t\t\t\t" + mean + "\nvariance: \t\t\t\t" + variance + "\nmedian: \t\t\t\t" + median + "\nstandard deviation: \t" + std);
    }
    
    @FXML
    void openDataFile(ActionEvent event) throws FileNotFoundException, IOException {
        Stage stage = new Stage();
 
        // file chooser
        FileChooser chooser=new FileChooser();
        // set default directory ? how about mac? 
        // chooser.setInitialDirectory(new File("C:\\")); 
        
        // set window title
        chooser.setTitle("open data file"); 
        
        // set file extension filter
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"),
                                             new FileChooser.ExtensionFilter("CSV", "*.csv"));
        String filePath = "";
        try {
            // get filePath if user select a file.
            filePath = chooser.showOpenDialog(stage).getAbsolutePath();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        System.out.println("loading " + filePath);
        if (filePath.isEmpty()) {
            return;
        }
        
        // read file line by line
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        int cnt = 0;

        md.reset();
        while ((line = br.readLine()) != null) {
            // add to data model
            md.addOriginalData(line);
            cnt ++;
        }
        br.close();
        isr.close();
        fis.close();
        
        // set status
        md.setIsLoaded(true);
        label_log.setText("data file " + filePath + " loaded, \n" + cnt + " lines.\n\n");
    }
    
    
}
