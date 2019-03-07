package org.sla;

public class AnimatorThread implements Runnable {
    private Controller myController;

    AnimatorThread(Controller controller) {
        myController = controller;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("I DREW");
            myController.draw();
            try {
                Thread.currentThread().wait(1000000000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
