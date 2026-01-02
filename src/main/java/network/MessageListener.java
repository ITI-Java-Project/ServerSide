package network;

public interface MessageListener {
    void onMessage(String msg, ClientHandler client);
    void addClient(ClientHandler client);
}