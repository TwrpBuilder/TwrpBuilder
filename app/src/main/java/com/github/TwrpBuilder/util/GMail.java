package com.github.TwrpBuilder.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by androidlover5842 on 20.3.2018.
 */

public class GMail extends javax.mail.Authenticator {
    static {
        Security.addProvider(new JSSEProvider());
    }

    private String user;
    private String password;
    private Session session;

    public GMail(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        String mailhost = "smtp.gmail.com";
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    @NonNull
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) {
        new mainThread(body, sender, subject, recipients).execute();
    }

    class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private final String type;

        ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
            this.type = "text/plain";
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        @NonNull
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        @NonNull
        public String getName() {
            return "ByteArrayDataSource";
        }

        @NonNull
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

    class mainThread extends AsyncTask<String, String, String> {

        final String body;
        final String sender;
        final String subject;
        final String recipients;

        mainThread(String body, String sender, String subject, String recipients) {
            this.body = body;
            this.sender = sender;
            this.subject = subject;
            this.recipients = recipients;
        }

        @Nullable
        @Override
        protected String doInBackground(String... strings) {
            try {
                MimeMessage message = new MimeMessage(session);
                DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes()));
                message.setSender(new InternetAddress(sender));
                message.setSubject(subject);
                message.setDataHandler(handler);
                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                else
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
