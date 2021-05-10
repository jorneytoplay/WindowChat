
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPListener {

    private static final String IP_ADDRESS ="localhost";
    private static final int PORT =9935;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("NICKNAME");
    private final JTextField fieldMessage = new JTextField();

    private TCPConnection connection;

    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);//Всегда было по середине
        setAlwaysOnTop(true);//Окна не насидали друг на друга

        log.setEditable(false);
        log.setLineWrap(true);
        add(log,BorderLayout.CENTER);

        fieldMessage.addActionListener(this);
        add(fieldMessage,BorderLayout.SOUTH);
        add(fieldNickname,BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this,IP_ADDRESS,PORT);
        } catch (IOException e) {
            printMessage("Connection exception: "+ e);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldMessage.getText();
        if(message.equals("")) return;
        fieldMessage.setText(null);
        connection.sendMessage(fieldNickname.getText() + ": "+ message);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection is ready....");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection is closed.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

        printMessage("Connection exception: "+ e);
    }

    private synchronized void printMessage(String message){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
