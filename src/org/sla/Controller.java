package org.sla;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;

public class Controller {
    private Image backgroundImage;
    private Image rover1;
    private Image rover2;
    int xbi;
    int ybi;
    int xr1;
    int yr1;
    int xr2;
    int yr2;

    private Queue myQueue;
    private Stage stage;

    public Canvas canvas;
    public GraphicsContext graphicsContext;

    public void initialize() {
        xbi = 0;
        ybi = 0;
        xr1 = 10;
        yr1 = 10;
        xr2 = 20;
        yr2 = 20;
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(backgroundImage, xbi, ybi);
        graphicsContext.drawImage(rover1, xr1, yr1);
        graphicsContext.drawImage(rover2, xr2, yr2);

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode()) {

                }
            }
        }

    }

    public void setStage(Stage theStage) {
        stage = theStage;
    }

    public void moveRover1() {
    }
}
