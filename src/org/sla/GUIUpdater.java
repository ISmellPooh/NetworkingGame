package org.sla;

public class GUIUpdater implements Runnable {
    private Queue originalQueue;

    GUIUpdater(Queue queue) {
        originalQueue = queue;
    }

    public void run() {
        Thread.currentThread().setName("GUIUpdater Thread");

        while (!Thread.interrupted()) {

        }
    }
}
//Intheeventpushesfa