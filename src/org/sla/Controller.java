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
    int px1;
    int py1;
    int px2;
    int py2;
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

    private boolean drawProjectile1;
    private boolean drawProjectile2;

    private int px1Delta;
    private int py1Delta;
    private int px2Delta;
    private int py2Delta;

    public void initialize() {
        inQueue = new Queue();
        outQueue = new Queue();
        connected = false;
        GUIUpdater updater = new GUIUpdater(inQueue, this);
        Thread updaterThread = new Thread(updater);
        updaterThread.start();

        arrowKeyAlreadySent = false;
        mouseAlreadySent = false;

        drawProjectile1 = false;
        drawProjectile2 = false;

        xbi = 0;
        ybi = 0;
        xr1 = 10;
        yr1 = 10;
        xr2 = 500;
        yr2 = 500;
        wr1 = 50;
        hr1 = 50;
        wr2 = 50;
        hr2 = 50;
        pw = 100;
        ph = 100;
        px1 = 0;
        py1 = 0;
        px2 = 490;
        py2 = 490;
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
                    yr1 = yr1 - 5;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr1 = yr1 + 5;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr1 = xr1 - 5;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr1 = xr1 + 5;
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

                if (event.getX() <= xr2 + 50 && event.getX() >= xr2) {
                    if (event.getY() <= yr2 + 50 && event.getY() >= yr2) {
                        clickCount = clickCount + 1;
                    }
                }

                mouseAlreadySent = true;
                String toSendVert = "what";
                String toSendHoriz = "what";
                boolean actuallySend = false;

                if (clickCount >= 1) {
                    if (!drawProjectile1) {
                        drawProjectile1 = true;

                        if (yr2 < yr1) {
                            py1 = yr1;
                            px1 = xr1;
                            py1Delta = -5;
                            toSendVert = "ShootUp";
                            actuallySend = true;
                        }
                        if (yr2 > yr1) {
                            py1 = yr1;
                            px1 = xr1;
                            py1Delta = 5;
                            toSendVert = "ShootDown";
                            actuallySend = true;
                        }
                        if (xr2 < xr1) {
                            py1 = yr1;
                            px1 = xr1;
                            px1Delta = -5;
                            toSendHoriz = "ShootLeft";
                            actuallySend = true;
                        }
                        if (xr2 > xr1) {
                            py1 = yr1;
                            px1 = xr1;
                            px1Delta = 5;
                            toSendHoriz = "ShootRight";
                            actuallySend = true;
                        }
                        if (px1 > 600) {
                            px1 = 0;
                        }
                        if (px1 < 0) {
                            px1 = 0;
                        }
                        if (py1 > 690) {
                            py1 = 0;
                        }
                        if (py1 < 0) {
                            py1 = 0;
                        }
                        if (px2 > 600) {
                            px2 = 490;
                        }
                        if (px2 < 0) {
                            px2 = 490;
                        }
                        if (py2 > 690) {
                            py2 = 490;
                        }
                        if (py2 < 0) {
                            py2 = 490;
                        }
                    }

                    if (actuallySend) {
                        Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendVert);
                        while (!outQueue.put(msgToSend)) {
                            Thread.currentThread().yield();
                        }
                        msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendHoriz);
                        while (!outQueue.put(msgToSend)) {
                            Thread.currentThread().yield();
                        }
                    }
                    actuallySend = false;
                    String toSend = "what";
                    if (clickCount == 5) {
                        wr2 = 0;
                        hr2 = 0;
                        graphicsContext.drawImage(rover2, xr2, yr2, wr2, hr2);
                        // change size to 0
                        System.out.println("Destruction Active");
                        toSend = "p1Click";
                        actuallySend = true;
                        clickCount = 0;
                    }

                    if (actuallySend) {
                        if (wr2 == 0 && hr2 == 0) {
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
                    yr2 = yr2 - 5;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr2 = yr2 + 5;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr2 = xr2 - 5;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr2 = xr2 + 5;
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

                if (event.getX() <= xr1 + 50 && event.getX() >= xr1) {
                    if (event.getY() <= yr1 + 50 && event.getY() >= yr1) {
                        clickCount = clickCount + 1;
                    }
                }

                mouseAlreadySent = true;
                String toSendVert = "what";
                String toSendHoriz = "what";
                boolean actuallySend = false;

                if (clickCount >= 1) {
                    if (!drawProjectile2) {
                        drawProjectile2 = true;

                        if (yr1 < yr2) {
                            py2 = yr2;
                            px2 = xr2;
                            py2Delta = -5;
                            toSendVert = "ShootUp";
                            actuallySend = true;
                        }
                        if (yr1 > yr2) {
                            py2 = yr2;
                            px2 = xr2;
                            py2Delta = 5;
                            toSendVert = "ShootDown";
                            actuallySend = true;
                        }
                        if (xr1 < xr2) {
                            py2 = yr2;
                            px2 = xr2;
                            px2Delta = -5;
                            toSendHoriz = "ShootLeft";
                            actuallySend = true;
                        }
                        if (xr1 > xr2) {
                            py2 = yr2;
                            px2 = xr2;
                            px2Delta = 5;
                            toSendHoriz = "ShootRight";
                            actuallySend = true;
                        }
                        if (px1 > 600) {
                            px1 = 0;
                        }
                        if (px1 < 0) {
                            px1 = 0;
                        }
                        if (py1 > 690) {
                            py1 = 0;
                        }
                        if (py1 < 0) {
                            py1 = 0;
                        }
                        if (px2 > 600) {
                            px2 = 490;
                        }
                        if (px2 < 0) {
                            px2 = 490;
                        }
                        if (py2 > 690) {
                            py2 = 490;
                        }
                        if (py2 < 0) {
                            py2 = 490;
                        }
                    }

                    if (actuallySend) {
                            Message msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendVert);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }
                            msgToSend = new Message(serverMode ? "Player 1" : "Player 2", toSendHoriz);
                            if (!outQueue.put(msgToSend)) {
                                Thread.currentThread().yield();
                            }

                    }
                    actuallySend = false;
                    String toSend = "what";
                    if (clickCount == 5) {
                        wr1 = 0;
                        hr1 = 0;
                        graphicsContext.drawImage(rover1, xr1, yr1, wr1, hr1);
                        // change size to 0
                        System.out.println("Destruction Active");
                        toSend = "p2Click";
                        actuallySend = true;
                        clickCount = 0;
                    }

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

    public void draw() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(backgroundImage, xbi, ybi, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(rover1, xr1, yr1, wr1, hr1);
        graphicsContext.drawImage(rover2, xr2, yr2, wr2, hr2);
        if (drawProjectile1) {
            graphicsContext.drawImage(projectile1, px1, py1, pw, ph);
            px1 = px1 + px1Delta;
            py1 = py1 + py1Delta;
        }
        if (drawProjectile2) {
            graphicsContext.drawImage(projectile2, px2, py2, pw, ph);
            px2 = px2 + px2Delta;
            py2 = py2 + py2Delta;
        }
    }

    void moveUPAndDraw() {
        if (serverMode) {
            yr2 = yr2 - 5;
        }
        if (!serverMode) {
            yr1 = yr1 - 5;
        }
    }

    void moveDOWNAndDraw() {
        if (serverMode) {
            yr2 = yr2 + 5;
        }
        if (!serverMode) {
            yr1 = yr1 + 5;
        }
    }

    void moveLEFTAndDraw() {
        if (serverMode) {
            xr2 = xr2 - 5;
        }
        if (!serverMode) {
            xr1 = xr1 - 5;
        }
    }

    void moveRIGHTAndDraw() {
        if (serverMode) {
            xr2 = xr2 + 5;
        }
        if (!serverMode){
            xr1 = xr1 + 5;
        }
    }

    void shootUP() {
        if (serverMode && py2 > 0) {
            py2 = py2 - 100;
        }
        if (!serverMode && py1 > 0) {
            py1 = py1 - 100;
        }
    }

    void shootDOWN() {
        if (serverMode && py2 < 690) {
            py2 = py2 + 100;
        }
        if (!serverMode && py1 < 690) {
            py1 = py1 + 100;
        }
    }

    void shootLEFT() {
        if (serverMode && px2 > 0) {
            px2 = px2 - 100;
        }
        if (!serverMode && px1 > 0) {
            px1 = px1 - 100;
        }
    }

    void shootRIGHT() {
        if (serverMode && px2 < 600) {
            px2 = px2 + 100;
        }
        if (!serverMode && px1 < 600) {
            px1 = px1 + 100;
        }
    }

    void playerClicked(int whichPlayer) {
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
    }

    public void setStage(Stage theStage) {
        stage = theStage;
    }
}