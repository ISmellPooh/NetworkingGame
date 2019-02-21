package org.sla;

import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class CommunicationIn implements Runnable{
    private Socket socket;
    private ObjectInputStream messageReader;
    private Queue inQueue;
    private Queue outQueue;
    private TextField statusText;
    private boolean serverMode;

    CommunicationIn(Socket s, ObjectInputStream in, Queue inQ, Queue outQ, TextField status) {
        socket = s;
        messageReader = in;
        inQueue = inQ;
        outQueue = outQ;
        statusText = status;
        serverMode = (outQ != null);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CommunicationIn Thread");
        System.out.println("CommunicationIn thread running");

        try {
            while (Controller.connected && !Thread.interrupted()) {
                Message message = null;
                while (message == null) {
                    try {
                        message = (Message) messageReader.readObject();
                    } catch (EOFException ex) {
                        // EOFException means data has NOT been written yet; so yield and try reading again
                        Thread.currentThread().yield();
                    }
                }
                Message finalMessage = message;
                System.out.println("CommunicationIn: RECEIVING " + message);
                // Receiving incoming message!!!

                Platform.runLater(() -> statusText.setText("RECEIVED: " + finalMessage));

                // ignore any messages sent by yourself: only put messages from others into your inQueue
                    // Now put message on the InputQueue so that the GUI will see it
                boolean putSucceeded = inQueue.put(message);
                while (!putSucceeded) {
                    Thread.currentThread().yield();
                    putSucceeded = inQueue.put(message);
                }
                    System.out.println("CommunicationIn PUT into InputQueue: " + message);
                    Platform.runLater(() -> statusText.setText("PUT into InputQueue: " + finalMessage));

                if (serverMode) {
                    putSucceeded = outQueue.put(message);
                    while (!putSucceeded) {
                        Thread.currentThread().yield();
                        putSucceeded = outQueue.put(message);
                    }
                    System.out.println("CommunicationIn MULTICAST into OutputQueue: " + message);
                    Platform.runLater(() -> statusText.setText("MULTICAST into OutputQueue: " + finalMessage));

                }
            }

            // while loop ended!  close reader and socket
            socket.close();
            System.out.println("CommunicationIn thread DONE; reader and socket closed.");

        } catch (SocketException se) {
            // nothing to do
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> statusText.setText("CommunicationIn: networking failed. Exiting...."));
        }

        try {
            // CommunicationIn ending!
            socket.close();
            System.out.println("CommunicationIn thread DONE; reader and socket closed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> statusText.setText("CommunicationIn: reader and socket closing failed...."));
        }
    }
}
