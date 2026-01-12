package com.FitLife.Service.ServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSessionLink(String to, String userName, String meetingDate, String meetingTime, String meetingLink) {
        String subject = "You're Invited: Scheduled Session Details";

        String emailBody = "<html><body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>"
                + "<div style='max-width: 600px; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='color: #007bff; text-align: center;'>Session Invitation</h2>"
                + "<p style='font-size: 16px;'>Dear <strong>" + userName + "</strong>,</p>"
                + "<p style='font-size: 14px;'>You are invited to a scheduled session organized by <strong>" + "FitLife" + "</strong>. Below are the details:</p>"

                + "<h3 style='color: #333; text-align: center;'>Session Details</h3>"
                + "<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>"
                + "<tr><td style='padding: 8px;'><strong>Date:</strong></td><td style='padding: 8px;'>" + meetingDate + "</td></tr>"
                + "<tr style='background-color: #f1f1f1;'><td style='padding: 8px;'><strong>Time:</strong></td><td style='padding: 8px;'>" + meetingTime + " (IST)</td></tr>"
                + "<tr><td style='padding: 8px;'><strong>Platform:</strong></td><td style='padding: 8px;'>Google Meet</td></tr>"
                + "<tr style='background-color: #f1f1f1;'><td style='padding: 8px;'><strong>Session Link:</strong></td>"
                + "<td style='padding: 8px;'><a href='" + meetingLink + "' style='display: inline-block; padding: 10px 15px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px;'>Join Session</a></td></tr>"
                + "</table>"

                + "<p style='font-size: 14px;'>Please make sure to join on time. If you have any questions or need to reschedule, feel free to contact  <strong>" + "FitLife Team" + "</strong>.</p>"
                + "<p style='font-size: 14px;'>Looking forward to your participation.</p>"

                + "<p style='margin-top: 20px; font-size: 14px;'><strong>Best Regards,</strong></p>"
                + "<p style='font-size: 14px;'><strong style='color: #007bff;'>FitLife</strong></p>"
                + "</div></body></html>";


        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
