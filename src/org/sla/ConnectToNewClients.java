package org.sla;

import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectToNewClients implements Runnable {
    private int connectionPort;
    private ServerSocket connectionSocket;
    private ArrayList<ObjectOutputStream> clientOutputStreams;
    private TextField statusText;
    private Queue inQueue;
    private Queue outQueue;

    ConnectToNewClients (int port, Queue inQ, Queue outQ, TextField status) {
        connectionPort = port;
        inQueue = inQ;
        outQueue = outQ;
        statusText = status;
    }

    public void run() {
        Thread.currentThread().setName("ConnectToNewClients Thread");
        System.out.println("ConnectToNewClients thread running");

        try {
            // ONLY server connects to new clients on this thread
            // Every time a new client connects, the server creates 2 extra threads:
            //   1 thread for communication FROM that new client TO server
            //   1 thread for communication TO that client FROM server

            // Start listening for client connections
            Platform.runLater(() -> statusText.setText("Listening on port " + connectionPort));
            connectionSocket = new ServerSocket(connectionPort);

            while (Controller.connected && !Thread.interrupted()) {
                // Wait until a client tries to connect
                Socket socketServerSide = connectionSocket.accept();
                Platform.runLater(() -> statusText.setText("Client has connected!"));

                // EACH SEPARATE client that is accepted results in 1 extra Socket named socketServerSide
                // socketServerSide provides 2 separate streams for 2-way communication
                //   the OutputStream is for communication TO client FROM server
                //   the InputStream is for communication FROM client TO server
                // Create data reader and writer from those stream (NOTE: ObjectOutputStream MUST be created FIRST)
                ObjectOutputStream dataWriter = new ObjectOutputStream(socketServerSide.getOutputStream());
                ObjectInputStream dataReader = new ObjectInputStream(socketServerSide.getInputStream());

                // The server prepares for communication with EACH client by creating 2 new threads:
                //   Thread 1: handles communication TO that client FROM server
                //   if multi-cast is enabled, communicationOut sends data TO ALL clients FROM server
                CommunicationOut communicationOut;

                communicationOut = new CommunicationOut(socketServerSide, dataWriter, outQueue, statusText);

                Thread communicationOutThread = new Thread(communicationOut);
                communicationOutThread.start();

                //   Thread 2: handles communication FROM that client TO server
                CommunicationIn communicationIn = new CommunicationIn(socketServerSide, dataReader, inQueue, outQueue, statusText);
                Thread communicationInThread = new Thread(communicationIn);
                communicationInThread.start();
            }

            // Server has been stopped (TwoWayCommunicationController.connected == false)
            // So close its connection socket
            connectionSocket.close();
            System.out.println("ConnectToNewClients thread ended; connectionSocket closed.");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Server ConnectToNewClients: networking failed.  Exiting...");
        }
    }
}
