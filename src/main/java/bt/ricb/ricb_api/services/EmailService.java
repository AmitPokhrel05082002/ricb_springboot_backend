package bt.ricb.ricb_api.services;

import java.io.IOException;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String body, MultipartFile attachment)
	        throws MessagingException, IOException {

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true);

	    helper.setFrom("donotreply@ricb.bt");
	    helper.setTo(to);
	    helper.setSubject(subject);
	    helper.setText(body, false);

	    if (attachment != null && !attachment.isEmpty()) {
			helper.addAttachment(attachment.getOriginalFilename(), (InputStreamSource) attachment);
		}

	    mailSender.send(message);

	    System.out.println("EMAIL SENT SUCCESSFULLY TO: " + to);
	}


	public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentContent)
			throws MessagingException {
		MimeMessage message = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);

		helper.addAttachment("generated.pdf",
				(DataSource) new ByteArrayDataSource(attachmentContent, "application/pdf"));

		this.mailSender.send(message);
	}
}