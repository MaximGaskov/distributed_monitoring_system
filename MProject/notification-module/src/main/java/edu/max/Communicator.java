package edu.max;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator {

    private ServerSocket serverSocket;

    private void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Notifyer notifyer = new Notifyer();
        while (true)
            new CommunicatorClientHandler(serverSocket.accept(), notifyer).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class CommunicatorClientHandler extends Thread {

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private Notifyer notifyer;

        public CommunicatorClientHandler(Socket socket, Notifyer notifyer) {
            this.clientSocket = socket;
            this.notifyer = notifyer;
        }

        public void run()  {

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String greeting = in.readLine();

                if ("monsys n-module hello".equals(greeting))
                    out.println("hello monsys m-module");
                else {
                    out.println("unrecognised greeting");
                    in.close();
                    out.close();
                    clientSocket.close();
                }

                String event = in.readLine();
                notifyer.notifyAdministrator(event);
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) throws IOException {


        Communicator server = new Communicator();
        server.start(6666);
        server.stop();

    }
}
