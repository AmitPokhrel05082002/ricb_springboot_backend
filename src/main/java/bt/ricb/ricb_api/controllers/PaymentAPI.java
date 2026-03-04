package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.PaymentTransactionDto;
import bt.ricb.ricb_api.services.InsuranceService;
import bt.ricb.ricb_api.services.PaymentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin({ "*" })
public class PaymentAPI {
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private InsuranceService insuranceService;

	@GetMapping({ "/paymentrequest" })
	public ResponseEntity<String> paymentrequest(@RequestParam("messagetype") String messagetype,
			@RequestParam("amount") String amount, @RequestParam("email") String email) throws Exception {
		try {
			System.out.println(messagetype);
			String result = this.paymentService.paymentrequest(messagetype, amount, email);
			return new ResponseEntity(result, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("server error");
		}
	}

	@GetMapping({ "/accountenq" })
	public ResponseEntity<String> returnVerificationResponse(@RequestParam("messagetype") String messagetype,
			@RequestParam("benf_TxnId") String benf_TxnId,
			@RequestParam("bfs_remitterBankId") String bfs_remitterBankId,
			@RequestParam("bfs_remitterAccNo") String bfs_remitterAccNo) throws Exception {
		try {
			return ResponseEntity.ok(this.paymentService.returnVerificationResponse(messagetype, benf_TxnId,
					bfs_remitterBankId, bfs_remitterAccNo));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "/finalPaymentRequest" })
	public ResponseEntity<String> returnVerificationResponse(@RequestParam("messagetype") String messagetype,
			@RequestParam("benf_TxnId") String benf_TxnId, @RequestParam("bfs_remitterOtp") String bfs_remitterOtp)
			throws Exception {
		try {
			return ResponseEntity
					.ok(this.paymentService.returnPaymentResponse(messagetype, benf_TxnId, bfs_remitterOtp));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping({ "/insertPaymentTrans" })
	public ResponseEntity<Map<String, Object>> insertPaymentTransaction(
			@RequestBody PaymentTransactionDto paymentDetails) {
		Map<String, Object> response = this.insuranceService.insertPaymentTransaction(paymentDetails);
		HttpStatus status = ((Boolean) response.get("success")).booleanValue() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity(response, status);
	}
}
