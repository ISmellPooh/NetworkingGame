package org.sla;

public class AnimatorThread implements Runnable {
    private Controller myController;

    AnimatorThread(Controller controller) {
        myController = controller;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            myController.draw();
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
