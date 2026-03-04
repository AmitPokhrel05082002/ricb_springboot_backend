package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.dao.LifeInsuranceDao;
import bt.ricb.ricb_api.models.FamilyDetailsDto;
import bt.ricb.ricb_api.models.LifeInsuranceMainDto;
import bt.ricb.ricb_api.models.NomineeDto;
import bt.ricb.ricb_api.models.PolicyCoverDto;
import bt.ricb.ricb_api.models.PolicyDiscountLoadDTO;
import bt.ricb.ricb_api.models.PolicyDto;
import bt.ricb.ricb_api.models.PolicyPremiumDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RequestMapping({ "life" })
@RestController
public class LifeInsuranceApi {
	@Autowired
	private LifeInsuranceDao lifeInsuarance;

	@PostMapping({ "/insuranceMainDetails" })
	public ResponseEntity<String> lifeInsuranceMain(@RequestBody LifeInsuranceMainDto insuranceDetails) {
		try {
			this.lifeInsuarance.lifeInsuranceMain(insuranceDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertDiscLoadDetails" })
	public ResponseEntity<String> insertDiscLoadDetails(@RequestBody PolicyDiscountLoadDTO discLoadDetails) {
		try {
			this.lifeInsuarance.insertDiscLoadDetails(discLoadDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertCoverDetails" })
	public ResponseEntity<String> insertCoverDetails(@RequestBody PolicyCoverDto coverDetails) {
		try {
			this.lifeInsuarance.insertCoverDetails(coverDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertNomineeDetails" })
	public ResponseEntity<String> insertNomineeDetails(@RequestBody NomineeDto nomineeDetails) {
		try {
			this.lifeInsuarance.insertNomineeDetails(nomineeDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertFamilyDetails" })
	public ResponseEntity<String> insertFamilyDetails(@RequestBody FamilyDetailsDto familyDetails) {
		try {
			this.lifeInsuarance.insertFamilyDetails(familyDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertPremiumDetails" })
	public ResponseEntity<String> insertPremiumDetails(@RequestBody PolicyPremiumDto premiumDetails) {
		try {
			this.lifeInsuarance.insertPremiumDetails(premiumDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping({ "/insertPolicyDetails" })
	public ResponseEntity<String> insertPolicyDetails(@RequestBody PolicyDto policyDetails) {
		try {
			this.lifeInsuarance.insertPolicyDetails(policyDetails);
			return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}