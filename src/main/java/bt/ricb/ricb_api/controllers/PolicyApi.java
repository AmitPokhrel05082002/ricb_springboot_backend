package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.dao.PolicyDao;
import bt.ricb.ricb_api.models.PolicyReqDto;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RestController
public class PolicyApi {
	@Autowired
	private PolicyDao policydao;

	@PostMapping({ "insertNyekorDtiPolicy" })
	public ResponseEntity<Integer> insertNyekorDtiPolicy(@RequestBody PolicyReqDto policyDetails) {
		Integer id = null;
		try {
			id = this.policydao.insertNyekorDtiPolicy(policyDetails, policyDetails.getCustomers());
			return ResponseEntity.ok().body(id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping({ "getDtiNyekkorDetailsAgainstCidNo" })
	public ResponseEntity<String> immediateannuity(@RequestParam("cidNo") String cidNo,
			@RequestParam("productCode") String productCode) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.policydao.getdtiNyekkorDetailsAgainstCid(cidNo, productCode);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getPolciciesAgainstCid" })
	public ResponseEntity<String> getPolciciesAgainstCid(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.policydao.getPolciciesAgainstCid(cidNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getUserDetailsAgainstTpn" })
	public ResponseEntity<String> getDetailsAgainstTpn(@RequestParam("cidNo") String cidNo) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.policydao.getUserDetailsAgainstTpn(cidNo);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}
}
