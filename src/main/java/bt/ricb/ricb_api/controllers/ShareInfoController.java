package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.services.ApiService;
 import bt.ricb.ricb_api.services.EmailService;
 import jakarta.mail.MessagingException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.CrossOrigin;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 import org.springframework.web.multipart.MultipartFile;
 
 @CrossOrigin({"*"})
 @RestController
 public class ShareInfoController
 {
   @Autowired
   private EmailService emailService;
   @Autowired
   private ApiService apiService;
   
   @PostMapping({"/sendEmail"})
   public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body, @RequestParam(required = false) MultipartFile attachment) {
     try {
       this.emailService.sendEmail(to, subject, body, attachment);
       return ResponseEntity.ok("Email sent successfully.");
     } catch (MessagingException|java.io.IOException e) {
       return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
     } 
   }
 
   
   @PostMapping({"/sendSms"})
   public ResponseEntity<String> sendSms(@RequestParam String messageContent, @RequestParam String mobileNo) {
     return this.apiService.sendSms(messageContent, mobileNo);
   }
 }
