package edu.cit.tapales.saritrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    message, true, "UTF-8");

            helper.setFrom("SariTrack <saritrack.official@gmail.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = isHtml

            mailSender.send(message);
            System.out.println("--- HTML EMAIL SENT TO " + to + " ---");
        } catch (Exception e) {
            System.err.println("--- FAILED TO SEND HTML EMAIL: " + e.getMessage() + " ---");
        }
    }

    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to SariTrack, " + name + "! 🚀";
        
        StringBuilder html = new StringBuilder();
        html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 20px; overflow: hidden;'>");
        
        // Header
        html.append("<div style='background-color: #16A394; color: white; padding: 40px; text-align: center;'>");
        html.append("<h1 style='margin: 0;'>SariTrack</h1>");
        html.append("<p style='opacity: 0.8;'>Your Store, Digitalized.</p>");
        html.append("</div>");
        
        // Body
        html.append("<div style='padding: 40px; line-height: 1.6; color: #333;'>");
        html.append("<h2>Welcome to the family, ").append(name).append("!</h2>");
        html.append("<p>We are thrilled to help you modernize your sari-sari store. With SariTrack, you can now track your inventory, manage 'listahan' debts, and accept digital payments with ease.</p>");
        
        html.append("<div style='background-color: #f9f9f9; padding: 20px; border-radius: 15px; margin: 25px 0;'>");
        html.append("<h4 style='margin-top: 0; color: #16A394;'>Quick Start Guide:</h4>");
        html.append("<ul style='padding-left: 20px;'>");
        html.append("<li>Add your first products in the <b>Inventory</b> tab.</li>");
        html.append("<li>Register your regular customers in the <b>Listahan</b> tab.</li>");
        html.append("<li>Start selling using the <b>Sales (POS)</b> dashboard!</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<p>If you have any questions, simply reply to this email. We're here to help!</p>");
        html.append("<p>Best regards,<br><b>The SariTrack Team</b></p>");
        html.append("</div>");
        
        html.append("<div style='background-color: #f1f1f1; padding: 20px; text-align: center; font-size: 12px; color: #999;'>");
        html.append("© 2026 SariTrack Platform. All rights reserved.");
        html.append("</div>");
        html.append("</div>");

        sendEmail(to, subject, html.toString());
    }
}
