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
        String data, clientAddress;
        Socket client;
        BufferedReader br;
        PrintWriter out;
        FileInputStream fis;
        BufferedInputStream bis;
        OutputStream os;
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
                    System.out.println("Response: " +parse(data));
                    if (parse(data).equals(http400)) {
                        out.println(parse(data));
                        out.flush();
                    }
                    else {
                        File myFile = new File(parse(data));
                        if (!myFile.exists()) {
                            System.out.println("Response: " +http404);
                            out.println(http404);
                            out.flush();
                        }
                        else {
                            System.out.println("Response: " +http200);
                            out.println(http200);
                            out.flush();

                            System.out.println("Content-Length: "+(int)myFile.length());
                            out.println("Content-Length: "+(int)myFile.length());
                            out.println("");
                            out.flush();

                            byte [] myByteArray  = new byte [(int)myFile.length()];
                            fis = new FileInputStream(myFile);
                            bis = new BufferedInputStream(fis);
                            bis.read(myByteArray,0,myByteArray.length);
                            os = client.getOutputStream();
                            System.out.println("Sending " + parse(data) + "(" + myByteArray.length + " bytes)");
                            os.write(myByteArray,0,myByteArray.length);
                            os.flush();
                            System.out.println("Finished transfer!");
                            bis.close();
                            os.close();
                            client.close();
                        }
                    }
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
                return fileName.substring(2,fileName.length() - 1).trim();
            }
        }
        else {
            return http400;
        }
        return fileName;
    }
    public static void main(String args[]) throws Exception {
        Server s1 = new Server(Integer.valueOf(args[0]));
        System.out.println("Server is running at: "+s1.myServer.getInetAddress().getHostAddress()+" at port: "+s1.myServer.getLocalPort());
        s1.listen();
    }
}