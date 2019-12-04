package chat.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {
    ServerSocket serviceSocket;
    private static Map<String ,Socket> socketMap;
    public Server() throws IOException {
        serviceSocket = new ServerSocket(8081);
        socketMap=new HashMap<>();
    }

    public static Map<String, Socket> getSocketMap() {
        return socketMap;
    }
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while (true) {
            System.out.println("等待连接。。。。。");
            Socket socket = server.serviceSocket.accept();
            Send send = new Send(socket);
            new Thread(send).start();
        }
    }
}
