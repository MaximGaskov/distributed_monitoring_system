package edu.max.monsys.monitoring;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import edu.max.monsys.repository.PortRepository;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

@Component
public class MonitoringHandler {

    private final int CONNECTION_TIMEOUT = 1000; //ms

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private PortRepository portRepository;

    @Autowired
    private MonitoringHostRepository monitoringHostRepository;

    @Value("${server.address}")
    private String myIP;


    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void check() {
//        ftpPortCheck("185.54.136.70", 21); //ftp.mccme.ru
//        System.out.println(myIP);

        for (Host host : hostRepository.findAll()) {
            for (Port port : host.getPorts()) {

                if (ftpPortCheck(host.getIpAddress(), port.getNumber())) {
                    port.setUp(true);
                    port.setService("FTP");
                } else if (httpPortCheck(host.getIpAddress(), port.getNumber())) {
                    port.setUp(true);
                    port.setService("HTTP");

                } else if (smtpPortCheck(host.getIpAddress(), port.getNumber())) {
                    port.setUp(true);
                    port.setService("SMTP");

                } else if (pop3PortCheck(host.getIpAddress(), port.getNumber())) {
                    port.setUp(true);
                    port.setService("POP3");

                } else {
                    port.setUp(false);
                }
            }

        }

        this.hostRepository.flush();
    }


    public boolean ftpPortCheck(String hostname, int port) {


        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host : " + hostname);
            return false;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(CONNECTION_TIMEOUT);
            ftp.connect(host, port);
            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                System.out.println("FTP bad response (" + ftp.getReplyCode() + ") on " + hostname + ":" + port);
                return false;
            } else {
                ftp.sendCommand(FTPCmd.USER, "user");
                if (ftp.getReplyCode() == 331 || ftp.getReplyCode() == 332) {
                    System.out.println("FTP is working on " + hostname + ":" + port);
                    return true;
                } else {
                    System.out.println("FTP connection to" + hostname + ":" + port + " failed");
                    return false;
                }
            }
        } catch (IOException e) {
//            System.out.println("FTP connection to" + hostname + ":" + port + " failed");
        } finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
//                System.out.println("FTP disconnection problems on " + hostname + ":" + port + " failed");
            }
        }

        return false;
    }

    public boolean httpPortCheck(String hostname, int port) {

        Socket s = new Socket();

        try {

            s.connect(new InetSocketAddress(hostname, port),CONNECTION_TIMEOUT);

            PrintWriter pw = new PrintWriter(s.getOutputStream());
            pw.println("HEAD / HTTP/1.0");
            pw.println("Host: " + hostname);
            pw.println("");
            pw.flush();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

                String response = br.readLine();

                if (!response.matches("HTTP/.*")){
                    System.out.println("ERROR: not HTTP protocol on " + hostname + ":" +port);
                    return false;
                } else {
                    Scanner sc = new Scanner(response);
                    sc.next();
                    int status = sc.nextInt();
                    System.out.println("HTTP: IS WORKING (STATUS : " + status + ") on " + hostname + ":" +port);
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("HTTP: " + hostname + " port " + port + " is closed or connection problem");
        } finally {

            try {
                s.close();
            } catch (IOException e) {
                System.out.println("ERROR while closing socket (HTTP " + hostname + ":" + port + ")");
                e.printStackTrace();
            }

        }

        return false;

    }

    public boolean smtpPortCheck(String hostname, int port) {

        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host : " + hostname);
            return false;
        }

        SMTPClient smtp = new SMTPClient();
        try {
            smtp.setConnectTimeout(CONNECTION_TIMEOUT);
            smtp.connect(host, port);
            int reply = smtp.getReplyCode();

            if (!SMTPReply.isPositiveCompletion(reply)) {
                System.out.println("SMTP bad response (" + smtp.getReplyCode() + ") on " + hostname + ":" + port);
                return false;
            } else {
                smtp.sendCommand("HELO ME");
                if (smtp.getReplyCode() == 250) {
                    System.out.println("SMTP is working on " + hostname + ":" + port);
                    return true;
                } else {
                    System.out.println("SMTP connection to" + hostname + ":" + port + " failed");
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("SMTP connection to" + hostname + ":" + port + " failed");
        } finally {
            try {
                smtp.disconnect();
            } catch (IOException e) {
                System.out.println("SMTP disconnection problems on " + hostname + ":" + port + " failed");
            }
        }

        return false;
    }

    public boolean pop3PortCheck(String hostname, int port) {

        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host : " + hostname);
            return false;
        }

        POP3Client pop3 = new POP3Client();
        try {
            pop3.setConnectTimeout(CONNECTION_TIMEOUT);
            pop3.connect(host, port);

            System.out.println("POP3 is working on " + hostname + ":" + port);
            return true;

        } catch (IOException e) {
            System.out.println("POP3 connection to" + hostname + ":" + port + " failed");
        } finally {
            try {
                pop3.disconnect();
            } catch (IOException e) {
                System.out.println("POP3 disconnection problems on " + hostname + ":" + port + " failed");
            }
        }

        return false;
    }
}
