
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread; //Поток слушающий входящие соединения
    private final BufferedReader in;
    private final TCPListener eventListener;//Слушатель событий
    private final BufferedWriter out;

    public TCPConnection(TCPListener eventListener, String ipAddress, int port) throws IOException{
        this(eventListener,new Socket(ipAddress,port));
    }

    public TCPConnection(TCPListener eventListener, Socket socket) throws IOException {
        this.eventListener=eventListener;
        this.socket=socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); // Экземпляр потока ввода
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)); //Экземпляр потока вывода
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendMessage(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
        }
    }

    @Override
    public String toString() {

        return "TCPConnection: "+ socket.getInetAddress() + ": "+ socket.getPort();

    }
}
