package org.sla;

public class GUIUpdater implements Runnable {
    private Queue originalQueue;
    private Controller myController;

    GUIUpdater(Queue queue, Controller controller) {
        originalQueue = queue;
        myController = controller;
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
                myController.moveUPAndDraw();
                System.out.println("up");
            }
            if (message.data().equals("down")) {
                // move rover1 down
                myController.moveDOWNAndDraw();
                System.out.println("down");
            }
            if (message.data().equals("left")) {
                // move rover1 left
                myController.moveLEFTAndDraw();
                System.out.println("left");
            }
            if (message.data().equals("right")) {
                // move rover1 right
                myController.moveRIGHTAndDraw();
                System.out.println("right");
            }
            if (message.data().equals("p1Click")) {
                myController.playerClicked(1);
                System.out.println("p1Click");
            }
            if (message.data().equals("p2Click")) {
                myController.playerClicked(2);
                System.out.println("p2Click");
            }
        }
    }
}//
