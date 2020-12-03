package whiteboard.clone.pkg2;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.imageio.ImageIO;

/**
 *
 * @author Ian A Skelskey
 *
 * Raster Paint Application Version 1.0
 *
 * I created this as a preliminary exercise for making a vector-based graphical
 * whiteboard application for education.
 *
 */
public class FXMLDocumentController implements Initializable {

    //FXML Containers and Controls
    //Containers
    @FXML
    private AnchorPane window; //Anchor pane with size bound to window size
    @FXML
    private VBox master; //VBox to organize application
    @FXML
    private HBox toolBar; //Contains the draw tool toggle buttons.
    @FXML
    private HBox transformToolBar; // contains pan & zoom etc as toggle buttons
    @FXML
    private StackPane artboard; //This is where the canvases are.        
    @FXML
    private HBox botBar1;  //I'd like to combine the two botBars into a gridpane.
    @FXML
    private HBox botBar2;

    //Canvases
    @FXML
    private Canvas background; //This stores the background.
    @FXML
    private Canvas canvas; //This is where the user's input is drawn.
    @FXML
    private Canvas canvasPrev; //This is the top-most layer for user interaction.

    //Menus
    @FXML
    private MenuBar menu;

    //Controls
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ColorPicker strokePicker;
    @FXML
    private ColorPicker backgroundPicker;
    @FXML
    private TextField bsize;
    @FXML
    private Slider brushHardness;
    @FXML
    private Label lblStroke;
    @FXML
    private TextField txtStroke;

    //Graphics Contexts
    //Do I even need more than one? -_-
    GraphicsContext drawTool;
    GraphicsContext eraserTool;
    GraphicsContext toolPreview;

    //Variables
    //Desparately in need of organization.
    //Cursor Locations
    private double initialX;
    private double initialY;
    private double finalX;
    private double finalY;
    private double lastX;
    private double lastY;

    //Tool Parameters
    private double size = 0;
    private double strokeSize = 0;
    private int hardness = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupForm();
    }

    //Tool Methods
    @FXML
    public void brushToggle(ActionEvent p) {
        resetTools();
        drawTool = canvasPrev.getGraphicsContext2D();
        drawTool = canvas.getGraphicsContext2D();
        canvasPrev.setOnMousePressed(e -> {
            //onPress is where I want to save a snapshot of the current canvases for undo feature
            lastX = e.getX();
            lastY = e.getY();
        });
        canvasPrev.setOnMouseDragged(e -> {
            drawBrush(e.getX(), e.getY());
        });
        canvasPrev.setOnMouseReleased(e -> {

        });
    }

    @FXML
    public void rectangleToggle(ActionEvent p) {
        resetTools();
        drawTool = canvas.getGraphicsContext2D();
        toolPreview = canvasPrev.getGraphicsContext2D();

        canvasPrev.setOnMousePressed(e -> {
            initialX = e.getX();
            initialY = e.getY();
        });

        canvasPrev.setOnMouseDragged(e -> {
            clearCanvas(canvasPrev);
            previewShape("rect", e.getX(), e.getY());
        });
        canvasPrev.setOnMouseReleased(e -> {
            clearCanvas(canvasPrev);
            if (txtStroke.getText().isEmpty()) {
                drawRect(canvas, initialX, initialY, e.getX(), e.getY(), colorPicker.getValue(), strokePicker.getValue(), 0);
            } else {
                drawRect(canvas, initialX, initialY, e.getX(), e.getY(), colorPicker.getValue(), strokePicker.getValue(), Double.valueOf(txtStroke.getText()));
            }
        });
    }

    @FXML
    public void ellipseToggle(ActionEvent p) {
        resetTools();
        toolPreview = canvasPrev.getGraphicsContext2D();
        drawTool = canvas.getGraphicsContext2D();

        canvasPrev.setOnMousePressed(e -> {
            initialX = e.getX();
            initialY = e.getY();
        });

        canvasPrev.setOnMouseDragged(e -> {
            clearCanvas(canvasPrev);
            //rectangleToolPreview = canvasPrev.getGraphicsContext2D();
            //Change the endings of lines from squared to rounded
            toolPreview.setLineCap(StrokeLineCap.ROUND);
            toolPreview.setLineJoin(StrokeLineJoin.ROUND);
            //This is supposed to be the guide that shows where the rectangle
            //will be before it is placed.
            toolPreview.setLineWidth(1);
            toolPreview.setStroke(Color.GRAY);
            if (e.getX() > initialX) {
                if (e.getY() > initialY) {
                    toolPreview.strokeOval(initialX, initialY, e.getX() - initialX, e.getY() - initialY);
                } else {
                    toolPreview.strokeOval(initialX, e.getY(), e.getX() - initialX, initialY - e.getY());
                }
            } else {
                if (e.getY() > initialY) {
                    toolPreview.strokeOval(e.getX(), initialY, initialX - e.getX(), e.getY() - initialY);
                } else {
                    toolPreview.strokeOval(e.getX(), e.getY(), initialX - e.getX(), initialY - e.getY());
                }
            }
        });

        canvasPrev.setOnMouseReleased(e -> {
            clearCanvas(canvasPrev);
            if (txtStroke.getText().isEmpty()) {
                drawEllipse(canvas, initialX, initialY, e.getX(), e.getY(), colorPicker.getValue(), strokePicker.getValue(), 0);
            } else {
                drawEllipse(canvas, initialX, initialY, e.getX(), e.getY(), colorPicker.getValue(), strokePicker.getValue(), Double.valueOf(txtStroke.getText()));
            }
        });
    }

    @FXML
    public void triangleToggle(ActionEvent p) {
        resetTools();
    }

    @FXML
    public void lineToggle(ActionEvent p) {
        resetTools();
        toolPreview = canvasPrev.getGraphicsContext2D();
        drawTool = canvas.getGraphicsContext2D();

        canvasPrev.setOnMousePressed(e -> {
            initialX = e.getX();
            initialY = e.getY();
        });
        canvasPrev.setOnMouseDragged(e -> {
            clearCanvas(canvasPrev);
            strokeSize = Double.parseDouble(txtStroke.getText());
            toolPreview.setLineCap(StrokeLineCap.ROUND);
            toolPreview.setLineJoin(StrokeLineJoin.ROUND);
            //Set line with (want to implement pressure sensitivity)
            toolPreview.setLineWidth(strokeSize);
            toolPreview.setStroke(Color.GRAY);
            toolPreview.strokeLine(initialX, initialY, e.getX(), e.getY());
        });
        canvasPrev.setOnMouseReleased(e -> {
            clearCanvas(canvasPrev);
            strokeSize = Double.parseDouble(txtStroke.getText());
            drawTool.setLineCap(StrokeLineCap.ROUND);
            drawTool.setLineJoin(StrokeLineJoin.ROUND);
            //Set line with (want to implement pressure sensitivity)
            drawTool.setLineWidth(strokeSize);
            drawTool.setStroke(strokePicker.getValue());
            drawTool.strokeLine(initialX, initialY, e.getX(), e.getY());
        });
    }
    
    int counter;

    @FXML
    public void eraserToggle(ActionEvent p) {
        resetTools();
        
        canvasPrev.setOnMousePressed(e -> {
            size = Double.parseDouble(bsize.getText());
            counter = 0;
            lastX = e.getX();
            lastY = e.getY();
        });
        canvasPrev.setOnMouseDragged(e -> {
            
            double avgX = (e.getX() + lastX) / 2;
            double avgY = (e.getY() + lastY) / 2;

            drawErase(e.getX(), e.getY());

            //If you've already been dragging...
            //if (counter != 0) {
            //    drawErase(avgX, avgY);
                //attempts at smoothing and filling in blank space
            //}
            if (calcDistance(e.getX(), 0, lastX, 0) >= size) {
                double distance = calcDistance(e.getX(), 0, lastX, 0);
                int iterations = (int) Math.floor(distance/size);
                System.out.println("Iterations: " + iterations);
                System.out.println("X: " + e.getX());
                System.out.println("Y: " + e.getY());
                double slope = (lastY - e.getY())/(lastX - e.getX());
                double xUnit = Math.floor(distance/iterations);
                System.out.println("xUnit: " + xUnit);
                
                for (double i = lastX; i < e.getX(); i++){
                    drawErase(lastX + xUnit*i, lastY + i*slope);
                }
                
                //drawErase((lastX + avgX) / 2, (lastY + avgY) / 2);
                //drawErase((e.getX() + avgX) / 2, (e.getY() + avgY) / 2);
                //further filling in as distance between mouse locations increases
            }
            lastX = e.getX();
            lastY = e.getY();
            counter++;
        });
    }

    @FXML
    public void undoPressed() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error: Work in progress!");
        alert.setHeaderText("This button doesn't do anything yet...");
        alert.setContentText("Sorry for the inconvenience. I will fix that soon.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }

    @FXML
    public void clearPressed() {
        clearCanvas(canvas);
    }

    @FXML
    public void switchColors() {
        Color strokeColor = strokePicker.getValue();
        Color fillColor = colorPicker.getValue();
        colorPicker.setValue(strokeColor);
        strokePicker.setValue(fillColor);
    }

    public void resetTools() {
        drawTool = null;
        eraserTool = null;
        toolPreview = null;
    }

    //Menu Methods
    @FXML
    public void saveCanvas() {
        int CANVAS_WIDTH = (int) canvas.getWidth();
        int CANVAS_HEIGHT = (int) canvas.getHeight();
        FileChooser fileChooser = new FileChooser();
        Stage saveWin = new Stage();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(saveWin);

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage(CANVAS_WIDTH, CANVAS_HEIGHT);
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                //
            }
        }
    }

    @FXML
    public void editCanvasSize() {
        Dialog editCanvasDialog = new Dialog<>();
        editCanvasDialog.setTitle("Edit Canvas Size");
        editCanvasDialog.setHeaderText("Enter desired dimensions for canvas in pixels.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField height = new TextField();
        TextField width = new TextField();

        height.setPromptText("Height");
        width.setPromptText("Width");

        grid.add(new Label("Height: "), 0, 0);
        grid.add(new Label("Width: "), 0, 1);
        grid.add(height, 1, 0);
        grid.add(width, 1, 1);
        editCanvasDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        editCanvasDialog.getDialogPane().setContent(grid);

        //editCanvasDialog.show();
        //How to use user input to control functionality?
        //Seems like I got it...
        editCanvasDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                background.setHeight(Double.valueOf(height.getText()));
                background.setWidth(Double.valueOf(width.getText()));
                canvas.setHeight(Double.valueOf(height.getText()));
                canvas.setWidth(Double.valueOf(width.getText()));
                canvasPrev.setHeight(Double.valueOf(height.getText()));
                canvasPrev.setWidth(Double.valueOf(width.getText()));
                fillCanvas(background, backgroundPicker.getValue());
            }
        });

    }

    //Preview Methods
    //Code a previewShape fucntion that calls the correct draw function to make
    //a 1 px gray outline of whichever shape selected.
    public void previewShape(String pShape, double x, double y) {
        switch (pShape) {
            case "rect":
                double height = Math.abs(y - initialY);
                double width = Math.abs(x - initialX);
                //This is the guide that shows where the rectangle
                //will be before it is placed.
                drawRect(canvasPrev, initialX, initialY, x, y, Color.TRANSPARENT, Color.GREY, 1);
                break;
            case "ellipse":
                break;
            case "triangle":
                break;
            //consider simplifying to one polygon tool
            //instead of separate triangle, rectangle, etc
        }
    }

    //Draw Methods
    public void drawBrush(double x, double y) {
        double size = Double.parseDouble(bsize.getText());
        double xMid = x - size / 2;
        double yMid = y - size / 2;
        //Change the endings of lines from squared to rounded
        drawTool.setLineCap(StrokeLineCap.ROUND);
        drawTool.setLineJoin(StrokeLineJoin.ROUND);
        //Set line with (want to implement pressure sensitivity)
        drawTool.setLineWidth(size);
        drawTool.setStroke(colorPicker.getValue());
        drawTool.strokeLine(lastX, lastY, xMid, yMid);
        lastX = xMid;
        lastY = yMid;
    }

    public void drawRect(Canvas pCanvas, double initialX, double initialY, double finalX, double finalY, Color pFill, Color pStroke, double pStrokeSize) {

        drawTool = pCanvas.getGraphicsContext2D();
        double height = Math.abs(finalY - initialY);
        double width = Math.abs(finalX - initialX);

        boolean movedDown = finalY > initialY;
        boolean movedRight = finalX > initialX;

        drawTool.setStroke(pStroke);
        drawTool.setFill(pFill);
        drawTool.setLineWidth(pStrokeSize);

        if (movedRight) {
            if (movedDown) {
                drawTool.fillRect(initialX, initialY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeRect(initialX - pStrokeSize / 2, initialY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            } else { //If moved up
                drawTool.fillRect(initialX, finalY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeRect(initialX - pStrokeSize / 2, finalY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            }
        } else { //If moved left
            if (movedDown) {
                drawTool.fillRect(finalX, initialY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeRect(finalX - pStrokeSize / 2, initialY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            } else { //If moved up
                drawTool.fillRect(finalX, finalY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeRect(finalX - pStrokeSize / 2, finalY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            }
        }
    }

    public void drawEllipse(Canvas pCanvas, double initialX, double initialY, double finalX, double finalY, Color pFill, Color pStroke, double pStrokeSize) {
        drawTool = pCanvas.getGraphicsContext2D();
        double height = Math.abs(finalY - initialY);
        double width = Math.abs(finalX - initialX);

        boolean movedDown = finalY > initialY;
        boolean movedRight = finalX > initialX;
        drawTool.setLineWidth(pStrokeSize);
        drawTool.setStroke(pStroke);
        drawTool.setFill(pFill);

        if (movedRight) {
            if (movedDown) {
                drawTool.fillOval(initialX, initialY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeOval(initialX - pStrokeSize / 2, initialY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            } else {
                drawTool.fillOval(initialX, finalY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeOval(initialX - pStrokeSize / 2, finalY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            }
        } else {
            if (movedDown) {
                drawTool.fillOval(finalX, initialY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeOval(finalX - pStrokeSize / 2, initialY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            } else {
                drawTool.fillOval(finalX, finalY, width, height);
                if (pStrokeSize != 0) {
                    drawTool.strokeOval(finalX - pStrokeSize / 2, finalY - pStrokeSize / 2, width + pStrokeSize, height + pStrokeSize);
                }
            }
        }
    }

    public void drawLine() {

    }

    public void drawErase(double x, double y) {
        double size = Double.parseDouble(bsize.getText());
        eraserTool = canvas.getGraphicsContext2D();
        eraserTool.clearRect(x - 2, y - 2, size, size);
    }

    public void fillCanvas(Canvas pCanvas, Color pColor) {
        GraphicsContext fillTool;
        fillTool = pCanvas.getGraphicsContext2D();
        fillTool.setFill(pColor);
        fillTool.fillRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight());
    }

    public void clearCanvas(Canvas p) {
        GraphicsContext clearTool;
        clearTool = p.getGraphicsContext2D();
        clearTool.clearRect(0, 0, p.getWidth(), p.getHeight());
    }

    public double calcDistance(double x1, double y1, double x2, double y2) {
        double distance = Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2);
        return distance;
    }

    public void setupForm() {
        //Initializes the canvas and sets event listeners.
        master.setPrefHeight(window.getHeight());
        master.setPrefWidth(window.getWidth());
        toolBar.setPrefWidth(window.getWidth());
        artboard.setPrefHeight(window.getHeight() - 165);
        artboard.setPrefWidth(window.getWidth());

        //Set Default Canvas Size to 300 px square
        //need to set position to top left
        canvas.setHeight(300);
        canvasPrev.setHeight(300);
        background.setHeight(300);
        canvas.setWidth(300);
        canvasPrev.setWidth(300);
        background.setWidth(300);

        fillCanvas(background, Color.WHITE);
        defaultBlur(canvas, hardness);

        window.heightProperty().addListener((observable, oldValue, newValue) -> {
            artboard.setPrefHeight(window.getHeight() - 165);
        });

        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            artboard.setPrefWidth(window.getWidth());
            menu.setPrefWidth(window.getWidth());
            toolBar.setPrefWidth(window.getWidth());
            transformToolBar.setPrefWidth(window.getWidth());
            botBar1.setPrefWidth(window.getWidth());
            botBar2.setPrefWidth(window.getWidth());
        });

        brushHardness.valueProperty().addListener((observable, oldValue, newValue) -> {
            hardness = newValue.intValue();
            defaultBlur(canvas, hardness);
        });
        backgroundPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            fillCanvas(background, backgroundPicker.getValue());
        });
    }

    public void defaultBlur(Canvas p, int pSize) {
        //Adds a slight blur tool the brush tool.
        p.setEffect(null);
        BoxBlur blur = new BoxBlur();
        blur.setWidth(pSize);
        blur.setHeight(pSize);
        blur.setIterations(pSize);
        p.setEffect(blur);
    }
}
