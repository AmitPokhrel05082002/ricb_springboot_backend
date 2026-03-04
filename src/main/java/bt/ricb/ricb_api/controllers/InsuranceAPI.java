package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.AdvertisementEntity;
import bt.ricb.ricb_api.models.BusinessLinesDto;
import bt.ricb.ricb_api.models.CountryEntity;
import bt.ricb.ricb_api.models.LatestTransactionEntity;
import bt.ricb.ricb_api.models.LifePaymentDto;
import bt.ricb.ricb_api.models.ResponseDto;
import bt.ricb.ricb_api.models.UserEntity;
import bt.ricb.ricb_api.services.InsuranceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RequestMapping({ "insurance" })
@RestController
public class InsuranceAPI {
	@Autowired
	private InsuranceService insuranceService;

	@PostMapping({ "getOtp/{methodType}" })
	public ResponseEntity<ResponseDto> addNewUser(@RequestBody UserEntity user, @PathVariable String methodType) {
		ResponseDto response = new ResponseDto();
		try {
			response = this.insuranceService.getOtp(user, methodType);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping({ "addNewUser" })
	public ResponseEntity<HttpStatus> addUser(@RequestBody UserEntity user) {
		try {
			this.insuranceService.addUser(user);
			return ResponseEntity.ok(HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<UserEntity> userprofile(@PathVariable("cidNo") String cidNo) throws Exception {
		try {
			UserEntity userDetails = this.insuranceService.getUserDetails(cidNo);
			return ResponseEntity.ok(userDetails);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

//	@GetMapping({"validatePassword"})
//	public ResponseEntity<ResponseDto> validatePassword(@RequestParam("cidNo") String cidNo,
//	        @RequestParam("password") String password) throws Exception {
//	    try {
//	        return ResponseEntity.ok(this.insuranceService.validatePassword(cidNo, password));
//	    } catch (Exception e) {
//	        
//	        ResponseDto errorResponse = new ResponseDto();
//	        errorResponse.setExistStatus(0); // Invalid credentials
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//	    }
//	}

	@GetMapping({ "verifyotp" })
	public ResponseEntity<ResponseDto> verifyotp(@RequestParam("otp") String otp, @RequestParam("cidNo") String cidNo)
			throws Exception {
		ResponseDto response = new ResponseDto();
		try {
			response = this.insuranceService.validateOTP(otp, cidNo);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "getByorderRefNo/{orderRefNo}" })
	public ResponseEntity<LatestTransactionEntity> getByorderRefNo(@PathVariable String orderRefNo) {
		LatestTransactionEntity transaction = new LatestTransactionEntity();
		try {
			transaction = this.insuranceService.getByOrderRefNo(orderRefNo);
			return ResponseEntity.ok(transaction);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "lineofbusiness" })
	public ResponseEntity<List<BusinessLinesDto>> getByorderRefNo() {
		List<BusinessLinesDto> businessLines = new ArrayList<>();
		businessLines = this.insuranceService.getBusinessLines();
		try {
			businessLines = this.insuranceService.getBusinessLines();
			return ResponseEntity.ok(businessLines);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "getAdvertisements" })
	public ResponseEntity<List<AdvertisementEntity>> getAdvertisements() {
		List<AdvertisementEntity> advertisements = new ArrayList<>();
		try {
			advertisements = this.insuranceService.getAdvertisements();
			return ResponseEntity.ok(advertisements);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "getCountries" })
	public ResponseEntity<List<CountryEntity>> getCounties() {
		List<CountryEntity> countries = new ArrayList<>();
		try {
			countries = this.insuranceService.getCountries();
			return ResponseEntity.ok(countries);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@PostMapping("insertLifePayment")
	public ResponseEntity<ResponseDto> insertLifePaymentDetails(@RequestBody LifePaymentDto lifePaymentReq) {
	    System.out.println("Received Life Payment Request: " + lifePaymentReq);
	    ResponseDto response = new ResponseDto();
	    try {
	        response.setStatus(this.insuranceService.insertLifePaymentDetails(lifePaymentReq));
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        System.err.println("Error processing life payment: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@CrossOrigin(origins = "*")
	@GetMapping("updatePaymentFinal")
	public ResponseEntity<String> updatePaymentFinal(
	        @RequestParam("txnId") String txnId,
	        @RequestParam(value = "remitterBank", required = false) String remitterBank,
	        @RequestParam(value = "accNo", required = false) String accNo,
	        @RequestParam(value = "authCode", required = false) String authCode,
	        @RequestParam(value = "orderNo", required = false) String orderNo) {

	    try {
	        if (txnId == null) {
	            return ResponseEntity.badRequest()
	                    .body("Error: please enter policy number of a policy holder.");
	        }
	        ResponseDto response = this.insuranceService
	                .updateLifePaymentDetails(txnId, remitterBank, accNo, authCode, orderNo);

	        return ResponseEntity.ok(response.getResponseData());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500)
	                .body("Server was not able to process your request");
	    }
	}


	@GetMapping({ "checkConnection" })
	public ResponseEntity<Boolean> checkOracleConnection() {
		try {
			boolean isAvailable = this.insuranceService.isOracleConnectionAvailable();
			return ResponseEntity.ok(Boolean.valueOf(isAvailable));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Boolean.valueOf(false));
		}
	}
}
