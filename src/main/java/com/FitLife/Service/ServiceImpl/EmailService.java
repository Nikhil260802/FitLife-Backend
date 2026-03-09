package com.FitLife.Service.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private static final String BREVO_URL =
            "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String to, String subject, String htmlContent) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();

            body.put("sender", Map.of(
                    "email", senderEmail,
                    "name", senderName
            ));

            body.put("to", List.of(
                    Map.of("email", to)
            ));

            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            restTemplate.postForEntity(
                    BREVO_URL,
                    request,
                    String.class
            );
        }catch (Exception e){
            e.printStackTrace();
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
            sendEmail(to, subject, emailBody);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
