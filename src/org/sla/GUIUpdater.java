package org.sla;

public class GUIUpdater implements Runnable {
    private Queue originalQueue;
    private Controller myController;

    GUIUpdater(Queue queue, Controller controller) {
        originalQueue = queue;
    }

    public void run() {
        Thread.currentThread().setName("GUIUpdater Thread");

        while (!Thread.interrupted()) {
            Message message = (Message)originalQueue.get();
            while (message == null) {
                Thread.currentThread().yield();
                message = (Message)originalQueue.get();
            }

            if (message.data().equals("up")) {
                // move rover1 up
                System.out.println("up");
                //myController.moveUPandDraw();
            }
            if (message.data().equals("down")) {
                // move rover1 down
                System.out.println("down");
            }
            if (message.data().equals("left")) {
                // move rover1 left
                System.out.println("left");
            }
            if (message.data().equals("right")) {
                // move rover1 right
                System.out.println("right");
            }
        }
    }
}
