package org.sla;

public class GUIUpdater implements Runnable {
    private Queue originalQueue;

    GUIUpdater(Queue queue) {
        originalQueue = queue;
    }

    public void run() {
        Thread.currentThread().setName("GUIUpdater Thread");

        while (!Thread.interrupted()) {
            String originalString = (String)originalQueue.get();
            while (originalString == null) {
                Thread.currentThread().yield();
                originalString = (String)originalQueue.get();
            }

            if (originalString.equals("up")) {
                // move rover1 up
                System.out.println("up");
            }
            if (originalString.equals("down")) {
                // move rover1 down
                System.out.println("down");
            }
            if (originalString.equals("left")) {
                // move rover1 left
                System.out.println("left");
            }
            if (originalString.equals("right")) {
                // move rover1 right
                System.out.println("right");
            }
        }
    }
}
