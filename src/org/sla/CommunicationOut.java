package org.sla;

import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class CommunicationOut implements Runnable {
    private Socket socket;
    private ObjectOutputStream writer;
    private ArrayList<ObjectOutputStream> outStreams;
    private Queue outQueue;
    private TextField statusText;
    private boolean serverMode;

    CommunicationOut(Socket s, ObjectOutputStream out, Queue outQ, TextField status) {
        socket = s;
        writer = out;
        outQueue = outQ;
        statusText = status;
        serverMode = false;
    }

    CommunicationOut(Socket s, ArrayList<ObjectOutputStream> outs, Queue outQ, TextField status) {
        socket = s;
        outStreams = outs;
        outQueue = outQ;
        statusText = status;
        serverMode = true;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CommunicationOut Thread");
        System.out.println("CommunicationOut thread running");

        try {
            while (Controller.connected && !Thread.interrupted()) {
                // keep getting from output Queue until it has a message
                Message message = (Message) outQueue.get();
                while (message == null) {
                    Thread.currentThread().yield();
                    message = (Message) outQueue.get();
                }
                Message finalMessage = message;
                System.out.println("CommunicationOut GOT: " + message);

                //writer.writeObject(message);
                writer.flush();

                Platform.runLater(() -> statusText.setText("SENT: " + finalMessage));
                System.out.println("CommunicationOut SENT: " + message);
            }

            // while loop ended!
            socket.close();
            System.out.println("CommunicationOut thread DONE; reader and socket closed.");

        } catch (Exception ex) {
            if (Controller.connected) {
                ex.printStackTrace();
                Platform.runLater(() -> statusText.setText("CommunicationOut: networking failed. Exiting...."));
            }
        }

        try {
            // CommunicationOut ending!
            socket.close();
            System.out.println("CommunicationOut thread DONE; reader and socket closed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> statusText.setText("CommunicationOut: reader and socket closing failed...."));
        }
    }
}
