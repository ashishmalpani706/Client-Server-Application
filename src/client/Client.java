import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
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
        String data;
        String input, request;
        int bytesRead;
        int current = 0;
        FileOutputStream fos;
        BufferedOutputStream bos;
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Please enter the name of the file: ");
        input = scanner.nextLine();

        request = "SET "+input+" HTTP/1.0";
        System.out.println("Sending "+request);
        out.println(request);
        out.flush();
        System.out.println(br.readLine());

        request = "GET "+input+" HTTP/1.0";
        System.out.println("Sending "+request);
        out.println(request);
        out.flush();
        data = br.readLine();
        System.out.println(data);
        if (!("Result: HTTP/1.0 404 Not Found".equals(data))) {
            byte [] mybytearray  = new byte [100000];
            InputStream is = socket.getInputStream();
            fos = new FileOutputStream(input);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + input
                    + " downloaded (" + current + " bytes read)");
        }

//        while ((data = br.readLine()) != null) {
//            System.out.println(data);
//        }

//        while (true) {
//            input = scanner.nextLine();
//            out.println(input);
//            out.flush();
//        }
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