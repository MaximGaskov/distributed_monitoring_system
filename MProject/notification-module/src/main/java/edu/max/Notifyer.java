package edu.max;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Notifyer {


    public void notifyAdministrator(String eventMessage) {


        try(InputStream input = new FileInputStream(this.getFileFromResources("mail.properties"))) {

            Properties props = new Properties();

            props.load(input);

            String reciever = props.getProperty("reciever");
            final String password = props.getProperty("sender_password");
            final String user = props.getProperty("sender");


            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
                message.setSubject("Произошло событие в системе мониторинга!");
                message.setText(eventMessage);

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }


        } catch(
        IOException ex)

        {
            ex.printStackTrace();
        }

    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
