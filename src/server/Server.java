import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Server {
    private ServerSocket myServer;
    private String http200 = "HTTP/1.0 200 OK";
    private String http400 = "HTTP/1.0 400 Bad Request";
    private String http404 = "HTTP/1.0 404 Not Found";

    public Server(int port) throws Exception {
        if (port > 0) {
            myServer = new ServerSocket(port, 1, InetAddress.getLocalHost());
        }
        else {
            myServer = new ServerSocket(0, 1, InetAddress.getLocalHost());
        }
    }

    private void listen() throws Exception {
        String data;
        Socket client;
        String clientAddress;
        BufferedReader br;
        PrintWriter out;

        while (true) {
            System.out.println("Waiting for connection");
            client = this.myServer.accept();
            clientAddress = client.getInetAddress().getHostAddress();
            System.out.println("Connection from: " + clientAddress);

            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            try {
                while ((data = br.readLine()) != null) {
                    System.out.println("Message from " + clientAddress + ": " + data);
                    System.out.println(parse(data));

                    out.println("Result: "+parse(data));
                    out.flush();

                }
            } catch (SocketException e) {
                System.out.println("Connection Terminated");
                client.close();
                br.close();
                out.close();
            }
        }
    }

    public String parse(String request) {
        String fileName = "";
        if (request.matches("^(GET).+HTTP/1.0")) {
            String regexStr = " .+H";
            Pattern p = Pattern.compile(regexStr);
            Matcher m = p.matcher(request);
            if (m.find()) {
                fileName = m.group();
                return fileName.substring(0,fileName.length() - 1).trim();
            }
        }
        else {
            return http400;
        }
        return fileName;
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