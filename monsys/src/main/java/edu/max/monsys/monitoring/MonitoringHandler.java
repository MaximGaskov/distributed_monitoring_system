package edu.max.monsys.monitoring;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.*;
import java.util.Date;


public class MonitoringHandler {

    private final int CONNECTION_TIMEOUT = 1000; //ms

    @Autowired
    private HostRepository hostRepository;


    @Autowired
    private MonitoringHostRepository monitoringHostRepository;

//    @Value("${server.address}")
//    private String myIP;

//    @Value("${fixedDelay.in.milliseconds}")
//    private int delay;

    @Transactional
    public void check() {
//        ftpPortCheck("185.54.136.70", 21); //ftp.mccme.ru
//        //System.out.println(myIP);
        System.out.println(new Date().toString());

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


    private boolean ftpPortCheck(String hostname, int port) {


        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(CONNECTION_TIMEOUT);
            ftp.connect(host, port);
            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                //System.out.println("FTP bad response (" + ftp.getReplyCode() + ") on " + hostname + ":" + port);
                return false;
            } else {
                ftp.sendCommand(FTPCmd.USER, "user");
                return ftp.getReplyCode() == 331 || ftp.getReplyCode() == 332;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ftp.disconnect();
            } catch (Exception e) {
                return false;
            }
        }
    }

    private boolean httpPortCheck(String hostname, int port) {

        HttpURLConnection con = null;
        try {
            URL url = new URL("http://" + hostname + ":" + port);
            //System.out.println(hostname);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                    + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
            int status = con.getResponseCode();
            return status != -1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    private boolean smtpPortCheck(String hostname, int port) {

        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return false;
        }

        SMTPClient smtp = new SMTPClient();
        try {
            smtp.setConnectTimeout(CONNECTION_TIMEOUT);
            smtp.connect(host, port);
            int reply = smtp.getReplyCode();

            if (!SMTPReply.isPositiveCompletion(reply)) {
                return false;
            } else {
                smtp.sendCommand("HELO ME");
                return smtp.getReplyCode() == 250;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                smtp.disconnect();
            } catch (Exception e) {
                return false;
            }
        }

    }

    private boolean pop3PortCheck(String hostname, int port) {

        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return false;
        }

        POP3Client pop3 = new POP3Client();
        try {
            pop3.setConnectTimeout(CONNECTION_TIMEOUT);
            pop3.connect(host, port);

            return true;

        } catch (Exception e) {
            return false;
        } finally {
            try {
                pop3.disconnect();
            } catch (IOException e) {
                return false;
            }
        }

    }
}
