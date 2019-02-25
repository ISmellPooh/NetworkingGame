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
    int xbi;
    int ybi;
    int xr1;
    int yr1;
    int xr2;
    int yr2;
    int r1Health;
    int r2Health;

    boolean arrowKeyAlreadySent;
    boolean mouseAlreadySent;

    private GraphicsContext graphicsContext;
    //private GraphicsContext graphicsContext2;

    private Queue outQueue;
    private Queue inQueue;
    private Stage stage;

    public Canvas canvas;
    //public Canvas canvas2;

    private boolean serverMode;
    static boolean connected;

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
        r1Health = 10;
        r2Health = 10;
        graphicsContext = canvas.getGraphicsContext2D();
        String imagePath1 = "org/sla/backgroundImage.png";
        backgroundImage = new Image(imagePath1);
        String imagePath2 = "org/sla/rover1.png";
        rover1 = new Image(imagePath2);
        String imagePath3 = "org/sla/rover2.png";
        rover2 = new Image(imagePath3);

        //graphicsContext2 = canvas2.getGraphicsContext2D();

        draw();
        canvas.setFocusTraversable(true);
        //canvas2.setFocusTraversable(false);
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
                String toSend = "why?";
                boolean actuallySend = false;

                if (event.getCode() == KeyCode.UP) {
                    yr1 = yr1 - 1;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr1 = yr1 + 1;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr1 = xr1 - 1;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr1 = xr1 + 1;
                    toSend = "right";
                    actuallySend = true;
                }
                if (actuallySend) {
                    draw();
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

                mouseAlreadySent = true;
                String toSend = "why?";
                boolean actuallySend = false;
                if (event.getClickCount() == 5) {
                    if (event.getX() <= xr2 + 50 && event.getX() >= xr2) {
                        if (event.getY() <= yr2 + 50 && event.getY() >= yr2) {
                            graphicsContext.drawImage(rover2, xr2, yr2, 0, 0);
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
                String toSend = "huh?";
                if (event.getCode() == KeyCode.UP) {
                    yr2 = yr2 - 1;
                    toSend = "up";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yr2 = yr2 + 1;
                    toSend = "down";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xr2 = xr2 - 1;
                    toSend = "left";
                    actuallySend = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xr2 = xr2 + 1;
                    toSend = "right";
                    actuallySend = true;
                }
                if (actuallySend) {
                    draw();
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

                mouseAlreadySent = true;
                String toSend = "why?";
                boolean actuallySend = false;
                if (event.getClickCount() == 5) {
                    if (event.getX() <= xr1 + 50 && event.getX() >= xr1) {
                        if (event.getY() <= yr1 + 50 && event.getY() >= yr1) {
                            graphicsContext.drawImage(rover1, xr1, yr1, 0, 0);
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
        System.out.println("I DREW");
        graphicsContext.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(backgroundImage, xbi, ybi, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(rover1, xr1, yr1, 50, 50);
        graphicsContext.drawImage(rover2, xr2, yr2, 50, 50);
        graphicsContext.rect(xr1, yr1, 50, 50);
        graphicsContext.rect(xr2, yr2, 50, 50);

        /*graphicsContext2.clearRect(0,0,canvas2.getWidth(), canvas2.getHeight());
        graphicsContext2.drawImage(backgroundImage, xbi, ybi, canvas2.getWidth(), canvas2.getHeight());
        graphicsContext2.drawImage(rover1, xr1, yr1, 50, 50);
        graphicsContext2.drawImage(rover2, xr2, yr2, 50, 50);*/
    }

    void moveUPAndDraw() {
        if (serverMode) {
            yr2 = yr2 - 1;
        }
        if (!serverMode) {
            yr1 = yr1 - 1;
        }
        draw();
    }

    void moveDOWNAndDraw() {
        if (serverMode) {
            yr2 = yr2 + 1;
        }
        if (!serverMode) {
            yr1 = yr1 + 1;
        }
        draw();
    }

    void moveLEFTAndDraw() {
        if (serverMode) {
            xr2 = xr2 - 1;
        }
        if (!serverMode) {
            xr1 = xr1 - 1;
        }
        draw();
    }

    void moveRIGHTAndDraw() {
        if (serverMode) {
            xr2 = xr2 + 1;
        }
        if (!serverMode){
            xr1 = xr1 + 1;
        }
        draw();
    }

    public void setStage(Stage theStage) {
        stage = theStage;
    }

}