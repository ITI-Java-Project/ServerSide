package session;

import com.mycompany.serverside.dao.PlayerDao;
import network.ClientHandler;

public class Session {

    private ClientHandler x, o;
    private char turn = 'X';
    private SessionManager sessionManager;

    private char[][] board = new char[3][3];
    private int movesCount = 0;

    public Session(ClientHandler x, ClientHandler o, SessionManager manager) {
        this.x = x;
        this.o = o;
        this.sessionManager = manager;

        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    public synchronized void playMove(ClientHandler sender, int row, int col) {

        // check turn
        if (!isTurn(sender)) {
            sender.send("NOT_YOUR_TURN");
            return;
        }

        // validate position
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            sender.send("INVALID_POSITION");
            return;
        }

        // check if cell is empty
        if (board[row][col] != '-') {
            sender.send("CELL_OCCUPIED");
            return;
        }

        // apply move
        board[row][col] = turn;
        movesCount++;

        broadcast("MOVE " + row + " " + col + " " + turn);

        // check win
        if (checkWin(turn)) {
            if (sender == x) {
                sender.send("WIN " + turn);
                o.send("Lose " + 'O');
                PlayerDao.increaseWinnerScore(x.getPlayer().getId());
            } else if (sender == o) {
                sender.send("WIN " + turn);
                x.send("Lose " + 'X');
                PlayerDao.increaseWinnerScore(o.getPlayer().getId());
            }
            // HERE : WE CAN CHANGE BASED ON LOGIC OF GAME SESSION
            endSession();
            return;
        }

        // check draw
        if (movesCount == 9) {
            broadcast("DRAW");
            return;
        }

        // switch turn
        turn = (turn == 'X') ? 'O' : 'X';
    }

    private boolean isTurn(ClientHandler c) {
        return (turn == 'X' && c == x) || (turn == 'O' && c == o);
    }

    private boolean checkWin(char p) {

        // rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == p && board[i][1] == p && board[i][2] == p) {
                return true;
            }
        }

        // columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == p && board[1][i] == p && board[2][i] == p) {
                return true;
            }
        }

        // diagonals
        if (board[0][0] == p && board[1][1] == p && board[2][2] == p) {
            return true;
        }

        if (board[0][2] == p && board[1][1] == p && board[2][0] == p) {
            return true;
        }

        return false;
    }

    private void broadcast(String msg) {
        System.out.println("BroadCast Session Message : " + msg);
        x.send(msg);
        o.send(msg);
    }

    public boolean hasPlayer(ClientHandler client) {
        return client == x || client == o;
    }

    private void endSession() {
        broadcast("END");
        sessionManager.finishSession(this);
    }
}
