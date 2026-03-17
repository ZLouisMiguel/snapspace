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

        String body = """
                <div style="background-color:#f2f5ff; padding:40px 20px; font-family: Arial, sans-serif;">
                
                    <div style="max-width:480px; margin:auto; background:#ffffff; border-radius:8px; padding:32px; box-shadow:0 6px 20px rgba(43,50,63,0.12);">
                
                        <!-- Logo / Title -->
                        <h1 style="margin:0; font-size:24px; letter-spacing:2px; color:#2b323f; text-align:center;">
                            SNAP<span style="color:#ec5e27;">SPACE</span>
                        </h1>
                
                        <!-- Message -->
                        <p style="margin-top:24px; font-size:15px; color:#8a919e; text-align:center;">
                            Use the code below to finish signing in
                        </p>
                
                        <!-- OTP Box -->
                        <div style="text-align:center; margin:28px 0;">
                            <span style="
                                display:inline-block;
                                font-size:30px;
                                font-weight:bold;
                                letter-spacing:6px;
                                color:#2b323f;
                                background:#f2f5ff;
                                padding:14px 26px;
                                border-radius:6px;
                                border:1px solid #e8eaf0;
                            ">
                """ + otpCode + """
                            </span>
                        </div>
                
                        <!-- Expiry -->
                        <p style="font-size:13px; color:#8a919e; text-align:center;">
                            This code expires in <strong>5 minutes</strong>
                        </p>
                
                        <!-- Divider -->
                        <div style="height:1px; background:#e8eaf0; margin:24px 0;"></div>
                
                        <!-- Footer -->
                        <p style="font-size:12px; color:#8a919e; text-align:center;">
                            If you didn’t request this, you can safely ignore this email.
                        </p>
                
                    </div>
                </div>
                """;

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");

        // IMPORTANT: send as HTML
        message.setContent(body, "text/html; charset=UTF-8");

        Transport.send(message);
    }
}

