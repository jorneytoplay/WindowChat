
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPListener {
    public static void main(String[] args) {

        new ChatServer();
    }

    private final ArrayList<TCPConnection> connectionsList = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(9935)) {
            while(true){
                try {
                    new TCPConnection(this,serverSocket.accept());

                }
                catch (IOException e){
                    System.out.println("TCPConnection exception: "+ e );
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connectionsList.add(tcpConnection);
        sendToAllConnections("Client connected: "+ tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connectionsList.remove(tcpConnection);
        sendToAllConnections("Client disconnected: "+ tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: "+ e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connectionsList.size();
        for (TCPConnection tcpConnection : connectionsList) {
            tcpConnection.sendMessage(value);
        }
    }
}
