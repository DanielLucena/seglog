package br.dev.danielrl.server.heartbeat;

public class HeartBeatTask extends Thread {

    private Runnable action;

    public HeartBeatTask(Runnable action) {
        this.action = action;
    }

    @Override
    public void run() {
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing heartbeat task: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
