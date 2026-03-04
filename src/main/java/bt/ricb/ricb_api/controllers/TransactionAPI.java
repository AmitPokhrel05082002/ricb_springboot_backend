package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.dao.TransactionDao;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RestController
@RequestMapping({ "ricb-transaction" })
public class TransactionAPI {
	@Autowired
	private TransactionDao transDao;

	@GetMapping({ "getLifeInsuraceDetails/{cidNo}" })
	public ResponseEntity<String> policyNumber(@PathVariable String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "lifeinsurance");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

//	@GetMapping({ "generalinsurancedetails" })
//	public ResponseEntity<String> policyDetails(@RequestParam("policyNo") String policyNo) throws Exception {
//		JSONArray json = new JSONArray();
//
//		try {
//			if (policyNo == null) {
//				return ResponseEntity.status(400).body("Error:Please enter policy number");
//			}
//			json = this.transDao.getGeneralInsuranceDtls(policyNo);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(500).body("Server was not able to process your request");
//		}
//		return ResponseEntity.ok().body(json.toString());
//	}

	@GetMapping({ "creditInvestmentDetails1" })
	public ResponseEntity<String> creditInvestmentDetails1(@RequestParam("accountNo") String accountNo)
			throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getCreditDetails(accountNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getotipplan" })
	public ResponseEntity<String> getotipplan(@RequestParam("days") String days, @RequestParam("age") String age)
			throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getotipplan(days, age);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getotipplanjp" })
	public ResponseEntity<String> getotipplanjp(@RequestParam("days") String days, @RequestParam("age") String age)
			throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getotipplanjp(days, age);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getotipplanasian" })
	public ResponseEntity<String> getotipplanasian(@RequestParam("days") String days, @RequestParam("age") String age)
			throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getotipplanasian(days, age);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getotipplanall" })
	public ResponseEntity<String> getotipplanall(@RequestParam("days") String days, @RequestParam("age") String age)
			throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getotipplanall(days, age);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getotipplanfinal" })
	public ResponseEntity<String> getotipplanfinal(@RequestParam("days") String days, @RequestParam("age") String age,
			@RequestParam("age") String plan) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getotipplanfinal(days, age, plan);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/genclaimStatus" })
	public ResponseEntity<String> genclaimStatus(@RequestParam("accountNo") String accountNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.genclaimStatus(accountNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/geninsurance" })
	public ResponseEntity<String> geninsurance(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "generalinsurance");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/motortppolicy" })
	public ResponseEntity<String> motortppolicy(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "motortppolicy");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/motortpactivepolicy" })
	public ResponseEntity<String> motortpactivepolicy(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "motortpactivepolicy");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/genclaimothers" })
	public ResponseEntity<String> genclaimothers(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "genclaimothers");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/getruralmember" })
	public ResponseEntity<String> getruralmember(@RequestParam("cidNo") String cidNo,
			@RequestParam("uwYear") String uwYear) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getRLImember(cidNo, uwYear);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/getOtipCustomer" })
	public ResponseEntity<String> getruralmember(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "getOtipCustomer");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "/checkrlpayment" })
	public ResponseEntity<String> checkrlpayment(@RequestParam("serialNo") String serialNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(serialNo, "getOtipCustomer");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "geninsurancedetails" })
	public ResponseEntity<String> geninsurancedetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getGeneralPolicyDetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "motorinsurancedetails" })
	public ResponseEntity<String> motorinsurancedetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.motorinsurancedetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "motordetails" })
	public ResponseEntity<String> motordetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.motordetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "mtpdetails" })
	public ResponseEntity<String> mtpdetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.mtpdetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "deferredannuity" })
	public ResponseEntity<String> deferredannuity(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "deferredannuity");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "deferredannuitydetails" })
	public ResponseEntity<String> deferredannuitydetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getDeferredDetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "immediateannuity" })
	public ResponseEntity<String> immediateannuity(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "immediateannuity");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "immediateannuitydetails" })
	public ResponseEntity<String> immediateannuitydetails(@RequestParam("policyNo") String policyNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getImmediateDetails(policyNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "ppfmemo" })
	public ResponseEntity<String> updatePassword(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "ppfmemo");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "gis" })
	public ResponseEntity<String> gis(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getPolicyDetails(cidNo, "gis");
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "ppfmemostatement" })
	public ResponseEntity<String> ppfmemostatement(@RequestParam("cidNumber") String cidNumber) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getppfmemo(cidNumber);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "gettitle" })
	public ResponseEntity<String> gettitle() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.gettitle();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getMstatus" })
	public ResponseEntity<String> getMstatus() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getMstatus();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getContactType" })
	public ResponseEntity<String> getContactType() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getContactType();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getSeqId" })
	public ResponseEntity<String> getSeqId() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getSeqId();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

//	@GetMapping({ "getHousingDetailsAgainstCID" })
//	public ResponseEntity<String> getHousingDetailsAgainstCID(@RequestParam String cidNo) throws Exception {
//		JSONArray jsonData = new JSONArray();
//		try {
//			jsonData = this.transDao.getHousingDetailsAgainstCID(cidNo);
//			return ResponseEntity.ok().body(jsonData.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(500).body("Server was not able to process your request");
//		}
//	}

	@GetMapping({ "getAllDzongkhags" })
	public ResponseEntity<String> getAllDzongkhags() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getAllDzongkhangs();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getAllGewogs" })
	public ResponseEntity<String> getAllGewogs() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getAllGewogs();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getDzongkhagByDzoCode" })
	public ResponseEntity<String> getDzongkhagById(@RequestParam("dzoCode") String dzoCode) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getDzongkhagByDzoCode(dzoCode);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getGewogByGewogCode" })
	public ResponseEntity<String> getGewogByGewogCode(@RequestParam("gewogCode") String gewogCode) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getGewogByGewogCode(gewogCode);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getGewogsUnderDzongkhags" })
	public ResponseEntity<String> getGewogsUnderDzongkhags(@RequestParam("dzoCode") String dzoCode) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getGewogsUnderDzongkhags(dzoCode);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getPPFMemo" })
	public ResponseEntity<String> getPPFMemo(@RequestParam("cidNo") String cidNo) {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.transDao.getppfmemo(cidNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "trstisAgent/{cidNo}" })
	public ResponseEntity<String> isAgent(@PathVariable String cidNo) {
		JSONArray jsonData = new JSONArray();

		try {
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "isAgent/{cidNo}" })
	public Map<String, Object> isTestAgent(@PathVariable String cidNo) {
		Map<String, Object> map = new HashMap<>();
		map = this.transDao.isAgent(cidNo);
		return map;
	}

	@GetMapping({ "getCustomerCode/{cidNo}" })
	public Map<String, Object> getCustomerCode(@PathVariable String cidNo) {
		Map<String, Object> map = new HashMap<>();
		map = this.transDao.getCustomerCode(cidNo);
		return map;
	}
	
	@GetMapping({ "getCustomerCodeForGeneralInsurance/{cidNo}" })
	public Map<String, Object> getCustomerCodeForGeneralInsurance(@PathVariable String cidNo) {
		Map<String, Object> map = new HashMap<>();
		map = this.transDao.getCustomerCodeForGeneralInsurance(cidNo);
		return map;
	}

	@GetMapping({ "getSumAssuredThreshold" })
	public Map<String, Object> getSumAssuredThreshold(@RequestParam("cidNo") String cidNo,
			@RequestParam("prodCode") String prodCode) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map = this.transDao.getSumAssuredThreshold(cidNo, prodCode);
		return map;
	}
}