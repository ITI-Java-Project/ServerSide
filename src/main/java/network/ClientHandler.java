    package network;

    import java.io.DataInputStream;
    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.net.Socket;
    import network.MessageListener;

    public class ClientHandler implements Runnable {

        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private MessageListener listener; 

        public ClientHandler(Socket socket, MessageListener listener) throws IOException {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = in.readUTF(); 
                    try {
                        listener.onMessage(msg, this); 
                    } catch (Exception e) {
                        System.out.println("Error handling message: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                } catch (IOException ex) {
                    System.out.println("Error closing resources: " + ex.getMessage());
                }
            }
        }

        public void send(String msg) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        }

    }
