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
            if (message.data().equals("ShootUp")) {
                if (message.sender().equals("Player 1")) {
                    myController.shootUP(1, message.x(), message.y());
                } else {
                    myController.shootUP(2, message.x(), message.y());
                }
                System.out.println("ShootUp");
            }
            if (message.data().equals("ShootDown")) {
                if (message.sender().equals("Player 1")) {
                    myController.shootDOWN(1, message.x(), message.y());
                } else {
                    myController.shootDOWN(2, message.x(), message.y());
                }
                System.out.println("ShootDown");
            }
            if (message.data().equals("ShootLeft")) {
                if (message.sender().equals("Player 1")) {
                    myController.shootLEFT(1, message.x(), message.y());
                } else {
                    myController.shootLEFT(2, message.x(), message.y());
                }
                System.out.println("ShootLeft");
            }
            if (message.data().equals("ShootRight")) {
                if (message.sender().equals("Player 1")) {
                    myController.shootRIGHT(1, message.x(), message.y());
                } else {
                    myController.shootRIGHT(2, message.x(), message.y());
                }
                System.out.println("ShootRight");
            }
            /*if (message.data().equals("player1Click")) {
                myController.playerClicked(1);
                System.out.println("player1Click");
            }
            if (message.data().equals("player2Click")) {
                myController.playerClicked(2);
                System.out.println("player2Click");
            }*/
            if (message.data().equals("NewLocation")) {
                if (message.sender().equals("Player 1")) {
                    myController.newLocation(1, message.x(), message.y());
                } else {
                    myController.newLocation(2, message.x(), message.y());
                }
                System.out.println("player2Click");
            }
        }
    }
}
