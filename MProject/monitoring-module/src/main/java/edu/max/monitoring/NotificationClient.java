package edu.max.monitoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


@Component
public class NotificationClient {


    @Value("${notification.module.domain}")
    private String notificationModuleDomain;

    @Value("${notification.module.port}")
    private int notificationModulePort;

    public boolean sendMessage(String msg) {

        try {

            Socket clientSocket = new Socket(notificationModuleDomain, notificationModulePort);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println(msg);
            String resp = in.readLine();

            in.close();
            out.close();
            clientSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
