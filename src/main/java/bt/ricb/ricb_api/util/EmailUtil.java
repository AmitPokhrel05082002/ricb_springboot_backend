package bt.ricb.ricb_api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;

@Component
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Basic email sending method
     * @param to Recipient email address
     * @param from Sender email address
     * @param subject Email subject
     * @param message Email body content
     */
    public void generateAndSendEmail(String to, String from, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

    /**
     * Extended version with CC recipients
     * @param to Recipient email address(es)
     * @param cc CC recipient email address(es)
     * @param from Sender email address
     * @param subject Email subject
     * @param message Email body content
     */
    public void generateAndSendEmail(String[] to, String[] cc, String from, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setCc(cc);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

    /**
     * HTML email version
     * @param to Recipient email address
     * @param from Sender email address
     * @param subject Email subject
     * @param htmlMessage Email body content in HTML format
     * @throws MessagingException if there's an error creating the message
     */
    public void generateAndSendHtmlEmail(String to, String from, String subject, String htmlMessage) 
            throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlMessage, true);
        
        javaMailSender.send(mimeMessage);
    }

    /**
     * Email with attachments
     * @param to Recipient email address
     * @param from Sender email address
     * @param subject Email subject
     * @param message Email body content
     * @param attachments Files to attach
     * @throws MessagingException if there's an error creating the message
     */
    public void generateAndSendEmailWithAttachments(String to, String from, String subject, 
            String message, MultipartFile[] attachments) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message);
        
        for (MultipartFile file : attachments) {
            if (file != null && !file.isEmpty()) {
                helper.addAttachment(
                    Objects.requireNonNull(file.getOriginalFilename()), 
                    file
                );
            }
        }
        
        javaMailSender.send(mimeMessage);
    }
}