package org.sla;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimatorThread implements Runnable {
    private Image backgroundImage;
    private Image rover1;
    private Image rover2;
    private Image projectile1;
    private Image projectile2;
    private int xBackgroundImage;
    private int yBackgroundImage;
    private int xRover1;
    private int yRover1;
    private int xRover2;
    private int yRover2;
    private int wRover1;
    private int hRover1;
    private int wRover2;
    private int hRover2;
    private int projectileWidth;
    private int projectileHeight;
    private int projectileX1;
    private int projectileY1;
    private int projectileX2;
    private int projectileY2;
    private GraphicsContext graphicsContext;
    private Canvas canvas;

    AnimatorThread(Image bi, Image r1, Image r2, Image p1, Image p2, int xbi, int ybi, int xr1, int yr1, int xr2, int yr2,
                   int wr1, int hr1, int wr2, int hr2, int pw, int ph, int px1, int py1, int px2, int py2) {
        backgroundImage = bi;
        rover1 = r1;
        rover2 = r2;
        projectile1 = p1;
        projectile2 = p2;
        xBackgroundImage = xbi;
        yBackgroundImage = ybi;
        xRover1 = xr1;
        yRover1 = yr1;
        xRover2 = xr2;
        yRover2 = yr2;
        wRover1 = wr1;
        hRover1 = hr1;
        wRover2 = wr2;
        hRover2 = hr2;
        projectileWidth = pw;
        projectileHeight = ph;
        projectileX1 = px1;
        projectileY1 = py1;
        projectileX2 = px2;
        projectileY2 = py2;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("I DREW");
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphicsContext.drawImage(backgroundImage, xBackgroundImage, yBackgroundImage, canvas.getWidth(), canvas.getHeight());
            graphicsContext.drawImage(rover1, xRover1, yRover1, wRover1, hRover1);
            graphicsContext.drawImage(rover2, xRover2, yRover2, wRover2, hRover2);
            graphicsContext.drawImage(projectile1, projectileX1, projectileY1, projectileWidth, projectileHeight);
            graphicsContext.drawImage(projectile2, projectileX2, projectileY2, projectileWidth, projectileHeight);
            try {
                Thread.currentThread().wait(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
