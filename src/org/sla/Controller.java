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
    int r1Health;
    int r2Health;

    private GraphicsContext graphicsContext;
    private GraphicsContext graphicsContext2;

    private Queue myQueue;
    private Stage stage;

    public Canvas canvas;
    public Canvas canvas2;

    String toSend;

    public void initialize() {
        myQueue = new Queue();
        GUIUpdater updater = new GUIUpdater(myQueue);
        Thread updaterThread = new Thread(updater);
        updaterThread.start();

        xbi = 0;
        ybi = 0;
        xr1 = 10;
        yr1 = 10;
        xr2 = 100;
        yr2 = 100;
        r1Health = 10;
        r2Health = 10;
        graphicsContext = canvas.getGraphicsContext2D();
        String imagePath1 = "org/sla/backgroundImage.png";
        backgroundImage = new Image(imagePath1);
        String imagePath2 = "org/sla/rover1.png";
        rover1 = new Image(imagePath2);
        String imagePath3 = "org/sla/rover2.png";
        rover2 = new Image(imagePath3);

        graphicsContext2 = canvas2.getGraphicsContext2D();

        draw();
        canvas.setFocusTraversable(true);
        canvas2.setFocusTraversable(false);

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP) {
                    yr1 = yr1 - 1;
                    toSend = "up";
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr1 = yr1 + 1;
                    toSend = "down";
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr1 = xr1 - 1;
                    toSend = "left";
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr1 = xr1 + 1;
                    toSend = "right";
                }
                draw();
                myQueue.put(toSend);
            }
        });
    }

    void draw() {
        graphicsContext.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(backgroundImage, xbi, ybi, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(rover1, xr1, yr1, 50, 50);
        graphicsContext.drawImage(rover2, xr2, yr2, 50, 50);

        graphicsContext2.clearRect(0,0,canvas2.getWidth(), canvas2.getHeight());
        graphicsContext2.drawImage(backgroundImage, xbi, ybi, canvas2.getWidth(), canvas2.getHeight());
        graphicsContext2.drawImage(rover1, xr1, yr1, 50, 50);
        graphicsContext2.drawImage(rover2, xr2, yr2, 50, 50);
    }

    public void setStage(Stage theStage) {
        stage = theStage;
    }
}
//