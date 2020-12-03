/*
 * Whiteboard Clone Application Test 2
 * By: Ian Skelskey
 */
package whiteboard.clone.pkg2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
/**
 *
 * @author Ian A Skelskey
 */
public class BrushTool extends Tool{
    private double initialX;
    private double initialY;
    
    private double finalX;
    private double finalY;
    
    private double previousX;
    private double previousY;
    
    private double size = 0;
    private double strokeSize = 0;
    
    GraphicsContext brushTool;
    GraphicsContext toolPreview;
    
    @FXML
    Canvas canvasPrev;
    
    @FXML
    TextField bsize;
    
    @FXML
    ColorPicker colorPicker;
    
    public BrushTool(Canvas p){
        super(p);
        toolPreview = canvasPrev.getGraphicsContext2D();
        brushTool = p.getGraphicsContext2D();
    }
    public void onMousePressed() {
        canvasPrev.setOnMousePressed(e -> {
            previousX = e.getX();
            previousY = e.getY();
        });

        canvasPrev.setOnMouseDragged(e -> {
            double size = Double.parseDouble(bsize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            //Change the endings of lines from squared to rounded
            brushTool.setLineCap(StrokeLineCap.ROUND);
            brushTool.setLineJoin(StrokeLineJoin.ROUND);
            //Set line with (want to implement pressure sensitivity)
            brushTool.setLineWidth(size);
            brushTool.setStroke(colorPicker.getValue());
            brushTool.strokeLine(previousX, previousY, x, y);
            previousX = x;
            previousY = y;

        });
        canvasPrev.setOnMouseReleased(e -> {

        });
    }
}