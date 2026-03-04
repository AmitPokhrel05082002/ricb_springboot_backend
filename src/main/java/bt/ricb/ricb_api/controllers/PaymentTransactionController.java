package bt.ricb.ricb_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.models.FullPaymentTransactionDto;
import bt.ricb.ricb_api.services.PaymentTransactionService;

@RestController
@CrossOrigin({ "*" })
@RequestMapping("/api/payments")
public class PaymentTransactionController {
	@Autowired
	private PaymentTransactionService paymentTransactionService;


	@PostMapping("/transactions")
	public ResponseEntity<String> createPaymentTransaction(@RequestBody FullPaymentTransactionDto paymentTransactionDto) {
	    try {
	        paymentTransactionService.createPaymentTransaction(paymentTransactionDto);
	        return ResponseEntity.ok("Payment transaction recorded successfully");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	               .body("Failed to record payment transaction: " + e.getMessage());
	    }
	}
}
