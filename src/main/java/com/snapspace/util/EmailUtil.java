package com.snapspace.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Utility class for sending emails using Jakarta Mail.
 *
 * <p>
 * SMTP configuration is loaded dynamically from {@code config.properties}
 * via {@link PropertiesUtil}. All keys must be prefixed with
 * {@code mail.smtp.}, e.g. {@code mail.smtp.host}, {@code mail.smtp.user},
 * {@code mail.smtp.password}, etc.
 * </p>
 */
public class EmailUtil {

    private static final Session session;
    private static final String fromAddress;

    static {
        Properties props = new Properties();
        props.put("mail.smtp.host", PropertiesUtil.get("mail.smtp.host"));
        props.put("mail.smtp.port", PropertiesUtil.get("mail.smtp.port", "587"));
        props.put("mail.smtp.auth", PropertiesUtil.get("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", PropertiesUtil.get("mail.smtp.starttls.enable", "true"));

        final String username = PropertiesUtil.get("mail.smtp.user");
        final String password = PropertiesUtil.get("mail.smtp.password");
        fromAddress = username;

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static void send(String to, String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8");

        Transport.send(message);
    }

    /**
     * Convenience helper for login one-time passwords.
     */
    public static void sendOtp(String to, String otpCode) throws MessagingException {
        String subject = "Your SnapSpace verification code";
        String body = "Use the following code to finish signing in to SnapSpace:\n\n"
                + otpCode + "\n\n"
                + "For your security, this code expires in 5 minutes. "
                + "If you didn't try to sign in, you can ignore this email.";
        send(to, subject, body);
    }
}

