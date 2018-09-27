import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private ServerSocket myServer;

    public Server(int port) throws Exception {
        if (port > 0) {
            myServer = new ServerSocket(port, 1, InetAddress.getLocalHost());
        }
        else {
            myServer = new ServerSocket(0, 1, InetAddress.getLocalHost());
        }
    }

    private void listen() throws Exception {
        String data = null;
        Socket client;
        String clientAddress;
        BufferedReader br;

        while (true) {
            System.out.println("Waiting for connection");
            client = this.myServer.accept();
            clientAddress = client.getInetAddress().getHostAddress();
            System.out.println("Connection from: " + clientAddress);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));

            try {
                while ((data = br.readLine()) != null) {
                    System.out.println("Message from " + clientAddress + ": " + data);
                }
            } catch (SocketException e) {
                System.out.println("Connection Terminated");
                client.close();
                br.close();
            }
        }
    }

    public String getSocketAddress() {
        return this.myServer.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return this.myServer.getLocalPort();
    }

    public static void main(String args[]) throws Exception {
        Server s1 = new Server(Integer.valueOf(args[0]));
        System.out.println("Server is running at: "+s1.getSocketAddress()+" at port: "+s1.getPort());
        s1.listen();
    }
}