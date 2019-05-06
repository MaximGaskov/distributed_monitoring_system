package edu.max.monsys.monitoring;

import edu.max.monsys.entity.Host;
import edu.max.monsys.entity.Log;
import edu.max.monsys.entity.MonitoringHost;
import edu.max.monsys.entity.Port;
import edu.max.monsys.repository.HostRepository;
import edu.max.monsys.repository.LogRepository;
import edu.max.monsys.repository.MonitoringHostRepository;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;


public class MonitoringHandler {

    private final int CONNECTION_TIMEOUT = 1000; //ms

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private MonitoringHostRepository monitoringHostRepository;

    @Autowired
    private LogRepository logRepository;


    @Value("${server.address}")
    private String myIP;


    @Transactional
    public void check() {

        if (monitoringHostRepository.findMonitoringHostByIpAddress(myIP).isPresent()) {
            monitoringHostRepository.findMonitoringHostByIpAddress(myIP).get().setUp(true);
            String targetIP = monitoringHostRepository.findMonitoringHostByIpAddress(myIP).get().getAnotherMHIpAdress();
            Optional<MonitoringHost> targetMH = monitoringHostRepository.findMonitoringHostByIpAddress(targetIP);

            if (targetIP != null && sshCheck(targetIP, 22) && targetMH.isPresent()) {

                targetMH.get().setUp(true);
            }
            else if (targetIP != null && targetMH.isPresent()) {
                targetMH.get().setUp(false);
            }

        } else {
            System.out.println("add host to cluster");
            return;
        }

        for (Host host : hostRepository.findAll()) {
            for (Port port : host.getPorts()) {

                if (ftpPortCheck(host.getIpAddress(), port.getNumber())) {
                    logPortIsUp(host, port);
                    port.setUp(true);
                    port.setService("FTP");
                } else if (httpPortCheck(host.getIpAddress(), port.getNumber())) {
                    logPortIsUp(host, port);
                    port.setUp(true);
                    port.setService("HTTP");
                } else if (smtpPortCheck(host.getIpAddress(), port.getNumber())) {
                    logPortIsUp(host, port);
                    port.setUp(true);
                    port.setService("SMTP");
                } else if (pop3PortCheck(host.getIpAddress(), port.getNumber())) {
                    logPortIsUp(host, port);
                    port.setUp(true);
                    port.setService("POP3");
                } else if (sshCheck(host.getIpAddress(), port.getNumber())) {
                    logPortIsUp(host, port);
                    port.setUp(true);
                    port.setService("SSH");
                } else {
                    if (port.isUp()) {
                        Date date = new Date();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

                        logRepository.save(
                                new Log(df.format(date), host.getIpAddress(), port.getNumber(), "недоступен"));
                        logRepository.flush();
                    }
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
            return false;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(CONNECTION_TIMEOUT);
            ftp.connect(host, port);
            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
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
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                    + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
            int status = con.getResponseCode();
            return status != -1;

        } catch (Exception e) {
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

    private boolean sshCheck(String hostname, int port) {

        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostname,port), CONNECTION_TIMEOUT);
            InputStreamReader streamReader= new InputStreamReader(socket.getInputStream());
            BufferedReader reader= new BufferedReader(streamReader);

            String greeting= reader.readLine();

            boolean isSSH;
            isSSH = greeting.contains("SSH");

            reader.close();

            return isSSH;

        } catch(Exception e) {
            return false;
        }finally{
            if(socket != null){
                try {
                    socket.close();
                }catch(IOException e){
                    return false;
                }
            }
        }

    }

    private void logPortIsUp (Host host, Port port) {
        if (!port.isUp()) {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            logRepository.save(
                    new Log(df.format(date), host.getIpAddress(), port.getNumber(), "доступен"));
            logRepository.flush();
        }
    }
}
