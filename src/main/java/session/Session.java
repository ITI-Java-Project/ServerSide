package session;


import network.ClientHandler;

public class Session {

    private ClientHandler x, o;
    private char turn = 'X';

    public Session(ClientHandler x, ClientHandler o) {
        this.x = x;
        this.o = o;
    }

    // Note : Here i assume that index is 1 to 9 may be changed based on idx logic
    public synchronized void playMove(ClientHandler sender, int idx) {
        if (!isTurn(sender)) {
            sender.send("NOT_YOUR_TURN");
            return;
        }

        broadcast("MOVE " + idx + " " + turn);
        turn = (turn == 'X') ? 'O' : 'X';
    }

    private boolean isTurn(ClientHandler c) {
        return (turn == 'X' && c == x) || (turn == 'O' && c == o);
    }

    private void broadcast(String msg) {
        x.send(msg);
        o.send(msg);
    }
}
