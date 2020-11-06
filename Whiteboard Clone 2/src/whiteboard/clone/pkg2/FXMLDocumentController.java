/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package whiteboard.clone.pkg2;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Ian A Skelskey
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private ColorPicker colorPicker;
    
    @FXML
    private TextField bsize;
    
    @FXML
    private Canvas canvas;
    
    
    boolean toolSelected = true;
    
    
    
    GraphicsContext brushTool;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        brushTool = canvas.getGraphicsContext2D();
        canvas.setOnMouseDragged(e-> {
            double size = Double.parseDouble(bsize.getText());
            double x = e.getX() - size/2;
            double y = e.getY() - size/2;
            
            if(toolSelected && !bsize.getText().isEmpty()){
                brushTool.setFill(colorPicker.getValue());
                brushTool.fillRoundRect(x, y, size, size, size, size);
            }
        });
    }    
    
    @FXML
    public void toolSelected(ActionEvent e){
        toolSelected = true;
    }
}