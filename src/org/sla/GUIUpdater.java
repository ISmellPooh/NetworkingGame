package org.sla;

public class GUIUpdater implements Runnable {
    private Queue originalQueue;
    private String origanalString;

    GUIUpdater(Queue queue) {
        originalQueue = queue;
    }

    public void run() {
        Thread.currentThread().setName("GUIUpdater Thread");

        while (!Thread.interrupted()) {
            String left = (String)originalQueue.get();
            while (left == null) {
                Thread.currentThread().yield();
                left = (String)originalQueue.get();
            }
            origanalString = "left";
        }

        while (!Thread.interrupted()) {
            String right = (String)originalQueue.get();
            while (right == null) {
                Thread.currentThread().yield();
                right = (String)originalQueue.get();
            }
            origanalString = "right";
        }

        while (!Thread.interrupted()) {
            String up = (String)originalQueue.get();
            while (up == null) {
                Thread.currentThread().yield();
                up = (String)originalQueue.get();
            }
            origanalString = "up";
        }

        while (!Thread.interrupted()) {
            String down = (String)originalQueue.get();
            while (down == null) {
                Thread.currentThread().yield();
                down = (String)originalQueue.get();
            }
            origanalString = "down";
        }
    }
}
