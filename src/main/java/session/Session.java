package session;

import com.mycompany.serverside.dao.PlayerDao;
import com.mycompany.serverside.dao.SessionDao;
import com.mycompany.serverside.dto.SessionDto;
import network.ClientHandler;
import com.google.gson.Gson;

public class Session {

    private ClientHandler x, o;
    private char turn = 'X';
    private SessionManager sessionManager;
    private int sessionId;
    private int player1Score = 0;  // Player 1's score in this session (X player)
    private int player2Score = 0;  // Player 2's score in this session (O player)
    private int player1Id;
    private int player2Id;

    private char[][] board = new char[3][3];
    private int movesCount = 0;

    public Session(ClientHandler x, ClientHandler o, SessionManager manager, SessionDto sessionData) {
        this.x = x;
        this.o = o;
        this.sessionManager = manager;
        this.sessionId = sessionData.getId();
        this.player1Id = sessionData.getPlayer1Id();
        this.player2Id = sessionData.getPlayer2Id();
        
        // Load existing scores from the session
        this.player1Score = sessionData.getPlayer1Score();
        this.player2Score = sessionData.getPlayer2Score();
        
        System.out.println("Session " + sessionId + " initialized with scores - Player1: " + player1Score + ", Player2: " + player2Score);

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
                // Player 1 (X) wins
                player1Score++;
                sender.send("WIN " + turn);
                o.send("Lose " + 'O');
                
                // Update PLAYER table - increase total score
                PlayerDao.increaseWinnerScore(x.getPlayer().getId());
                System.out.println("Player " + x.getPlayer().getName() + " total score increased");
                
            } else if (sender == o) {
                // Player 2 (O) wins
                player2Score++;
                sender.send("WIN " + turn);
                x.send("Lose " + 'X');
                
                // Update PLAYER table - increase total score
                PlayerDao.increaseWinnerScore(o.getPlayer().getId());
                System.out.println("Player " + o.getPlayer().getName() + " total score increased");
            }
            
            // Update SESSION table - update head-to-head scores
            boolean updated = SessionDao.updateSessionScores(sessionId, player1Score, player2Score);
            
            if (updated) {
                System.out.println("Session scores updated - P1: " + player1Score + ", P2: " + player2Score);
                // Send updated session data to both players
                sendUpdatedSessionData();
            }
            
            endSession();
            return;
        }

        // check draw
        if (movesCount == 9) {
            broadcast("DRAW");
            // Send session data even on draw (scores don't change but client might want to see)
            sendUpdatedSessionData();
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

    private void sendUpdatedSessionData() {
        // Get the updated session data from database
        SessionDto updatedSession = SessionDao.getSessionById(sessionId);
        
        if (updatedSession != null) {
            Gson gson = new Gson();
            String sessionJson = gson.toJson(updatedSession);
            
            System.out.println("Sending updated session data: " + sessionJson);
            x.send("SESSION_DATA:" + sessionJson);
            o.send("SESSION_DATA:" + sessionJson);
        }
    }

    public boolean hasPlayer(ClientHandler client) {
        return client == x || client == o;
    }

    private void endSession() {
        broadcast("END");
        sessionManager.finishSession(this);
    }
}