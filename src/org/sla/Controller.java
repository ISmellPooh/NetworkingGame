package org.sla;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class Controller {
    public TextField IPAddressText;
    public TextField portText;
    public Button startButton;
    public TextField statusText;
    private Image backgroundImage;
    private Image rover1;
    private Image rover2;
    private Image projectile1;
    private Image projectile2;
    private Image explosion;
    int xbi;
    int ybi;
    int xr1;
    int yr1;
    int xr2;
    int yr2;
    int wr1;
    int hr1;
    int wr2;
    int hr2;
    int pw;
    int ph;
    ArrayList<Projectile> p1;
    ArrayList<Projectile> p2;
    int r1Health;
    int r2Health;

    boolean arrowKeyAlreadySent;
    boolean mouseAlreadySent;
    int clickCount;

    private GraphicsContext graphicsContext;
    //private GraphicsContext graphicsContext2;

    private Queue outQueue;
    private Queue inQueue;
    private Stage stage;

    public Canvas canvas;
    //public Canvas canvas2;

    private boolean serverMode;
    static boolean connected;

    private boolean drawCollision1;
    private int drawCollision1Count;
    private boolean drawCollision2;
    private int drawCollision2Count;

    public void initialize() {
        inQueue = new Queue();
        outQueue = new Queue();
        connected = false;
        GUIUpdater updater = new GUIUpdater(inQueue, this);
        Thread updaterThread = new Thread(updater);
        updaterThread.start();

        arrowKeyAlreadySent = false;
        mouseAlreadySent = false;

        xbi = 0;
        ybi = 0;
        xr1 = 10;
        yr1 = 10;
        xr2 = 500;
        yr2 = 500;
        wr1 = 40;
        hr1 = 40;
        wr2 = 40;
        hr2 = 40;
        pw = 10;
        ph = 10;
        p1 = new ArrayList();
        p2 = new ArrayList();
        r1Health = 10;
        r2Health = 10;
        graphicsContext = canvas.getGraphicsContext2D();
        String imagePath1 = "org/sla/backgroundImage.png";
        backgroundImage = new Image(imagePath1);
        String imagePath2 = "org/sla/rover1.png";
        rover1 = new Image(imagePath2);
        String imagePath3 = "org/sla/rover2.png";
        rover2 = new Image(imagePath3);
        String imagePath4 = "org/sla/projectile1.png";
        projectile1 = new Image(imagePath4);
        String imagePath5 = "org/sla/projectile2.png";
        projectile2 = new Image(imagePath5);
        String imagePath6 = "org/sla/explosion.png";
        explosion = new Image(imagePath6);
        //graphicsContext2 = canvas2.getGraphicsContext2D();

        canvas.setFocusTraversable(true);
        //canvas2.setFocusTraversable(false);
        clickCount = 0;

        AnimatorThread animator = new AnimatorThread(this);
        Thread animatorThread = new Thread(animator);
        animatorThread.start();
    }

    void setServerMode() {
        serverMode = true;
        startButton.setText("Start");
        try {
            // display the computer's IP address
            IPAddressText.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception ex) {
            ex.printStackTrace();
            statusText.setText("Server start: getLocalHost failed. Exiting....");
        }

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (arrowKeyAlreadySent) {
                    event.consume();
                    return;
                }

                arrowKeyAlreadySent = true;
                String toSend = "what";
                boolean actuallySend = false;

                if (event.getCode() == KeyCode.UP) {
                    yr1 = yr1 - 20;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr1 = yr1 + 20;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr1 = xr1 - 20;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr1 = xr1 + 20;
                    toSend = "right";
                    actuallySend = true;
                }
                if (actuallySend) {
                    Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSend);
                    if (!outQueue.put(msgToSend)) {
                        Thread.currentThread().yield();
                    }
                }
            }
        });
        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                arrowKeyAlreadySent = false;
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mouseAlreadySent) {
                    event.consume();
                    return;
                }

                int clickX = (int)event.getX();
                int clickY = (int)event.getY();

                if (event.getX() <= xr2 + 50 && event.getX() >= xr2) {
                    if (event.getY() <= yr2 + 50 && event.getY() >= yr2) {
                        clickCount = clickCount + 1;
                    }
                }

                mouseAlreadySent = true;
                String toSendVert = "what";
                String toSendHoriz = "what";
                boolean actuallySend = false;

                int px1 = xr1+(wr1/2);
                int py1 = yr1+(hr1/2);;
                int deltaX1 = (clickX-xr1)/10;
                int deltaY1 = (clickY-yr1)/10;
                if (clickCount >= 1) {
                    if (clickY < yr1) {
                        toSendVert = "ShootUp";
                        actuallySend = true;
                    }
                    if (clickY > yr1) {
                        toSendVert = "ShootDown";
                        actuallySend = true;
                    }
                    if (clickX < xr1) {
                        toSendHoriz = "ShootLeft";
                        actuallySend = true;
                    }
                    if (clickX > xr1) {
                        toSendHoriz = "ShootRight";
                        actuallySend = true;
                    }
                }
                    Projectile newP = new Projectile(1, px1,py1,deltaX1,deltaY1);
                    p1.add(newP);

                    if (actuallySend) {
                        Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendVert, clickX, clickY);
                        while (!outQueue.put(msgToSend)) {
                            Thread.currentThread().yield();
                        }
                        msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendHoriz, clickX, clickY);
                        while (!outQueue.put(msgToSend)) {
                            Thread.currentThread().yield();
                        }
                    }
                    actuallySend = false;
                    String toSend = "what";

                    /*if (px2 < xr1 + wr1 && xr1 + wr1 < px2 + px2Delta) {
                        wr2 = 0;
                        hr2 = 0;
                        graphicsContext.drawImage(rover2, xr2, yr2, wr2, hr2);
                        System.out.println("Destruction Active");
                        toSend = "p1Click";
                        actuallySend = true;
                    }*/

                    if (actuallySend) {
                        if (wr2 == 0 && hr2 == 0) {
                            Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSend);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }
                        }
                    }
                }


        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAlreadySent = false;
            }
        });
    }

    void setClientMode() {
        serverMode = false;
        startButton.setText("Connect");
        // display the IP address for the local computer
        IPAddressText.setText("");

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (arrowKeyAlreadySent) {
                    event.consume();
                    return;
                }

                arrowKeyAlreadySent = true;
                boolean actuallySend = false;
                String toSend = "what";

                if (event.getCode() == KeyCode.UP) {
                    yr2 = yr2 - 20;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr2 = yr2 + 20;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr2 = xr2 - 20;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr2 = xr2 + 20;
                    toSend = "right";
                    actuallySend = true;
                }
                if (actuallySend) {
                    Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSend);
                    if (!outQueue.put(msgToSend)) {
                        Thread.currentThread().yield();
                    }
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                arrowKeyAlreadySent = false;
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mouseAlreadySent) {
                    event.consume();
                    return;
                }

                int clickX = (int)event.getX();
                int clickY = (int)event.getY();

                if (event.getX() <= xr1 + 50 && event.getX() >= xr1) {
                    if (event.getY() <= yr1 + 50 && event.getY() >= yr1) {
                        clickCount = clickCount + 1;
                    }
                }

                mouseAlreadySent = true;
                String toSendVert = "what";
                String toSendHoriz = "what";
                boolean actuallySend = false;

                int px2 = xr2+(wr2/2);
                int py2 = yr2+(hr2/2);;
                int deltaX2 = (clickX-xr2)/10;
                int deltaY2 = (clickY-yr2)/10;
                if (clickCount >= 1) {
                    if (clickY < yr2) {
                        toSendVert = "ShootUp";
                        actuallySend = true;
                    }
                    if (clickY > yr2) {
                        toSendVert = "ShootDown";
                        actuallySend = true;
                    }
                    if (clickX < xr2) {
                        toSendHoriz = "ShootLeft";
                        actuallySend = true;
                    }
                    if (clickX > xr2) {
                        toSendHoriz = "ShootRight";
                        actuallySend = true;
                    }

                    Projectile newP = new Projectile(1, px2,py2,deltaX2,deltaY2);
                    p1.add(newP);

                    if (actuallySend) {
                            Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendVert, clickX, clickY);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }
                            msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendHoriz, clickX, clickY);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }

                    }
                    actuallySend = false;
                    String toSend = "what";
                    /*if (clickCount == 5) {
                        wr1 = 0;
                        hr1 = 0;
                        graphicsContext.drawImage(rover1, xr1, yr1, wr1, hr1);
                        // change size to 0
                        System.out.println("Destruction Active");
                        toSend = "p2Click";
                        actuallySend = true;
                        clickCount = 0;
                    }*/

                    if (actuallySend) {
                        if (wr1 == 0 && hr1 == 0) {
                            Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSend);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }
                        }
                    }
                }
            }

        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAlreadySent = false;
            }
        });
    }

    public void startButtonPressed() {
        // If we're already connected, start button should be disabled
        if (connected) {
            // don't do anything else; the threads will stop and everything will be cleaned up by them.
            return;
        }

        // We can't start network connection if Port number is unknown
        if (portText.getText().isEmpty()) {
            // user did not enter a Port number, so we can't connect.
            statusText.setText("Type a port number BEFORE connecting.");
            return;
        }

        // We're gonna start network connection!
        connected = true;
        startButton.setDisable(true);

        if (serverMode) {

            // We're a server: create a thread for listening for connecting clients
            ConnectToNewClients connectToNewClients = new ConnectToNewClients(Integer.parseInt(portText.getText()), inQueue, outQueue, statusText);
            Thread connectThread = new Thread(connectToNewClients);
            connectThread.start();

        } else {

            // We're a client: connect to a server
            try {
                Socket socketClientSide = new Socket(IPAddressText.getText(), Integer.parseInt(portText.getText()));
                statusText.setText("Connected to server at IP address " + IPAddressText.getText() + " on port " + portText.getText());

                // The socketClientSide provides 2 separate streams for 2-way communication
                //   the InputStream is for communication FROM server TO client
                //   the OutputStream is for communication TO server FROM client
                // Create data reader and writer from those stream (NOTE: ObjectOutputStream MUST be created FIRST)

                // Every client prepares for communication with its server by creating 2 new threads:
                //   Thread 1: handles communication TO server FROM client
                CommunicationOut communicationOut = new CommunicationOut(socketClientSide, new ObjectOutputStream(socketClientSide.getOutputStream()), outQueue, statusText);
                Thread communicationOutThread = new Thread(communicationOut);
                communicationOutThread.start();

                //   Thread 2: handles communication FROM server TO client
                CommunicationIn communicationIn = new CommunicationIn(socketClientSide, new ObjectInputStream(socketClientSide.getInputStream()), inQueue, null, statusText);
                Thread communicationInThread = new Thread(communicationIn);
                communicationInThread.start();

            } catch (Exception ex) {
                ex.printStackTrace();
                statusText.setText("Client start: networking failed. Exiting....");
            }

            // We connected!
        }
    }

    void draw() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(backgroundImage, xbi, ybi, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(rover1, xr1, yr1, wr1, hr1);
        graphicsContext.drawImage(rover2, xr2, yr2, wr2, hr2);
        if (drawCollision1) {
            if (p1.size() > 0) {
                p1.clear();
            }
            graphicsContext.drawImage(explosion, xr2, yr2, wr2, hr2);
            drawCollision1Count = drawCollision1Count - 1;
            if (drawCollision1Count == 0) {
                drawCollision1 = false;
                // move rover2 to new location
                if (!serverMode) {
                    xr2 = (int) (Math.random() * (canvas.getWidth()));
                    yr2 = (int) (Math.random() * (canvas.getHeight()));
                    Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", "NewLocation", xr2, yr2);
                    if (!outQueue.put(msgToSend)) {
                        Thread.currentThread().yield();
                    }
                }
            }
        }
        if (drawCollision2) {
            graphicsContext.drawImage(explosion, xr1, yr1, wr1, hr1);
            drawCollision2Count = drawCollision2Count - 1;
            if (drawCollision2Count == 0) {
                drawCollision2 = false;
                // move rover1 to new location
                if (serverMode) {
                    xr1 = (int) (Math.random() * (canvas.getWidth()));
                    yr1 = (int) (Math.random() * (canvas.getHeight()));
                    Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", "NewLocation", xr1, yr1);
                    if (!outQueue.put(msgToSend)) {
                        Thread.currentThread().yield();
                    }

                }
            }
        }


        for (int i = 0; i < p1.size(); i = i + 1) {
            Projectile p = p1.get(i);
            graphicsContext.drawImage(projectile1, p.getX(), p.getY(), pw, ph);
            // check if projectile1 HIT
            if (projectileCollision(p.getX()-p.getDeltaX(),p.getY()-p.getDeltaY(),p.getX(),p.getY(),xr2,yr2)) {
                drawCollision1 = true;
                drawCollision1Count = 10;
                // Increment player1's score
            }
            p.setX(p.getX() + p.getDeltaX());
            p.setY(p.getY() + p.getDeltaY());
        }

        /*if (drawProjectile1) {
            graphicsContext.drawImage(projectile1, px1, py1, pw, ph);
            // check if projectile1 HIT
            if (projectileCollision(px1-px1Delta,py1-py1Delta,px1,py1,xr2,yr2)) {
                drawProjectile1 = false;
                drawCollision1 = true;
                drawCollision1Count = 10;
                // Increment player1's score
            }
            px1 = px1 + px1Delta;
            py1 = py1 + py1Delta;

            if (px1 > 600) {
                px1 = 0;
                drawProjectile1 = false;
            }
            if (px1 < 0) {
                px1 = 0;
                drawProjectile1 = false;
            }
            if (py1 > 690) {
                py1 = 0;
                drawProjectile1 = false;
            }
            if (py1 < 0) {
                py1 = 0;
                drawProjectile1 = false;
            }
        }*/
        for (int i = 0; i < p2.size(); i = i + 1) {
            Projectile p = p2.get(i);
            graphicsContext.drawImage(projectile2, p.getX(), p.getY(), pw, ph);
            // check if projectile1 HIT
            if (projectileCollision(p.getX()-p.getDeltaX(),p.getY()-p.getDeltaY(),p.getX(),p.getY(),xr1,yr1)) {
                drawCollision2 = true;
                drawCollision2Count = 10;
                // Increment player1's score
            }
            p.setX(p.getX() + p.getDeltaX());
            p.setY(p.getY() + p.getDeltaY());
        }

        /*if (drawProjectile2) {
            graphicsContext.drawImage(projectile2, px2, py2, pw, ph);
            if (projectileCollision(px2-px2Delta,py2-py2Delta,px2,py2,xr1,yr1)) {
                drawProjectile2 = false;
                drawCollision2 = true;
                drawCollision2Count = 10;
            }
            px2 = px2 + px2Delta;
            py2 = py2 + py2Delta;

            if (px2 > 600) {
                px2 = 490;
                drawProjectile2 = false;
            }
            if (px2 < 0) {
                px2 = 490;
                drawProjectile2 = false;
            }
            if (py2 > 690) {
                py2 = 490;
                drawProjectile2 = false;
            }
            if (py2 < 0) {
                py2 = 490;
                drawProjectile2 = false;
            }
        }*/
    }

    void moveUPAndDraw() {
        if (serverMode) {
            yr2 = yr2 - 20;
        }
        if (!serverMode) {
            yr1 = yr1 - 20;
        }
    }

    void moveDOWNAndDraw() {
        if (serverMode) {
            yr2 = yr2 + 20;
        }
        if (!serverMode) {
            yr1 = yr1 + 20;
        }
    }

    void moveLEFTAndDraw() {
        if (serverMode) {
            xr2 = xr2 - 20;
        }
        if (!serverMode) {
            xr1 = xr1 - 20;
        }
    }

    void moveRIGHTAndDraw() {
        if (serverMode) {
            xr2 = xr2 + 20;
        }
        if (!serverMode){
            xr1 = xr1 + 20;
        }
    }

    void newLocation(int whichPlayer, int x, int y) {
        if (whichPlayer == 1) {
            xr1 = x;
            yr1 = y;
        } else {
            xr2 = x;
            yr2 = y;
        }
    }

    void shootUP(int player, int clickX, int clickY) {
        if (player == 2) {
            drawProjectile2 = true;
            px2 = xr2+(wr2/2);
            py2 = yr2+(hr2/2);
            py2Delta = (clickY-yr2)/10;
        }
        if (player == 1) {
            drawProjectile1 = true;
            px1 = xr1+(wr1/2);
            py1 = yr1+(hr1/2);
            py1Delta = (clickY-yr1)/10;
        }
    }

    void shootDOWN(int player, int clickX, int clickY) {
        if (player == 2) {
            drawProjectile2 = true;
            px2 = xr2+(wr2/2);
            py2 = yr2+(hr2/2);
            py2Delta = (clickY-yr2)/10;
        }
        if (player == 1) {
            drawProjectile1 = true;
            px1 = xr1+(wr1/2);
            py1 = yr1+(hr1/2);
            py1Delta = (clickY-yr1)/10;
        }
    }

    void shootLEFT(int player, int clickX, int clickY) {
        if (player == 2) {
            drawProjectile2 = true;
            px2 = xr2+(wr2/2);
            py2 = yr2+(hr2/2);
            px2Delta = (clickX-xr2)/10;
        }
        if (player == 1) {
            drawProjectile1 = true;
            px1 = xr1+(wr1/2);
            py1 = yr1+(hr1/2);
            px1Delta = (clickX-xr1)/10;
        }
    }

    void shootRIGHT(int player, int clickX, int clickY) {
        if (player == 2) {
            drawProjectile2 = true;
            px2 = xr2+(wr2/2);
            py2 = yr2+(hr2/2);
            px2Delta = (clickX-xr2)/10;
        }
        if (player == 1) {
            drawProjectile1 = true;
            px1 = xr1+(wr1/2);
            py1 = yr1+(hr1/2);
            px1Delta = (clickX-xr1)/10;
        }
    }

    private boolean projectileCollision(int oldpx, int oldpy, int newpx, int newpy, int xr, int yr) {
        double smallDeltaX = Math.abs(newpx - oldpx);
        double smallDeltaY = Math.abs(newpy - oldpy);
        if (smallDeltaX > smallDeltaY) {
            smallDeltaY = smallDeltaY / smallDeltaX;
            smallDeltaX = 1.0;
        } else {
            smallDeltaX = smallDeltaX / smallDeltaY;
            smallDeltaY = 1.0;
        }

        double leftX, rightX, topY, bottomY;
        if (oldpx < newpx) {
            leftX = oldpx;
            rightX = newpx;
        } else {
            leftX = newpx;
            rightX = oldpx;
        }
        if (oldpy < newpy) {
            topY = oldpy;
            bottomY = newpy;
        } else {
            topY = newpy;
            bottomY = oldpy;
        }
        for (double x = leftX; x < rightX; x = x + smallDeltaX) {
            for (double y = topY; y < bottomY; y = y + smallDeltaY) {

                boolean betweenXs = ((xr <= x) && (x <= xr + wr1));
                boolean betweenYs = ((yr <= y) && (y <= yr + hr1));
                if (betweenXs && betweenYs) {
                    // got collision
                    return true;
                }


            }
        }
        return false;
    }

    /*void playerClicked(int whichPlayer) {
        if (whichPlayer == 1) {
            wr2 = 0;
            hr2 = 0;
            graphicsContext.drawImage(rover2, xr2, yr2, wr2, hr2);
        }
        if (whichPlayer == 2) {
            wr1 = 0;
            hr1 = 0;
            graphicsContext.drawImage(rover1, xr1, yr1, wr1, hr1);
        }
    }*/

    public void setStage(Stage theStage) {
        stage = theStage;
    }
}