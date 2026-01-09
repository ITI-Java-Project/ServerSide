package services;

import network.ConnectionManager;
import network.MessageListener;

public class ServerService extends Thread {

    private ConnectionManager manager;

    public ServerService(int port, MessageListener listener) throws Exception {
        manager = new ConnectionManager(port, listener);
    }

    @Override
    public void run() {
        manager.start();
    }

    public void shutdown() throws Exception {
        manager.stopServer();

        // stop this thread (ServerService) Completely
        this.interrupt();
    }
}
