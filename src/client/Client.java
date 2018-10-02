import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
public class Client {
    private Socket socket;
    private Scanner scanner;
    private Client(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }
    private void start() throws IOException {
        String data, input, request;
        int bytesRead, current;
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Please enter the name of the file: ");
        input = scanner.nextLine();
        request = "SET /"+input+" HTTP/1.0";
        System.out.println("Sending "+request);
        out.println(request);
        out.flush();
        System.out.println(br.readLine());

        socket.close();
        this.socket = new Socket(InetAddress.getLocalHost(), 6000);
        out = new PrintWriter(this.socket.getOutputStream(), true);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        request = "GET /"+input+" HTTP/1.0";
        System.out.println("Sending "+request);
        out.println(request);
        out.flush();


        data = br.readLine();
        System.out.println(data);

        if (!("HTTP/1.0 404 Not Found".equals(data))) {

            data = br.readLine();
            System.out.println("Content-Length: "+data);
            data = br.readLine();


            byte [] myByteArray  = new byte [6022386]; //can change file size if needed
            InputStream is = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(input);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = is.read(myByteArray,0,myByteArray.length);
            current = bytesRead;
            do {
                bytesRead = is.read(myByteArray, current, (myByteArray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);
            bos.write(myByteArray, 0 , current);
            bos.flush();
            System.out.println("File "+input+" downloaded ("+current+" bytes read)");
            fos.close();
            bos.close();
            if (socket != null) socket.close();
        }
    }
    public static void main(String[] args) throws Exception {
        Client client;
        if (args[0].equals("localhost")){
            client = new Client(InetAddress.getLocalHost(),Integer.parseInt(args[1]));
        }
        else {
            client = new Client(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
        }
        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        client.start();
    }
}