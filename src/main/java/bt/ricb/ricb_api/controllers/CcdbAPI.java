package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.CcdbCustomerDto;
import bt.ricb.ricb_api.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RestController
@RequestMapping({ "ccdb" })
public class CcdbAPI {
	@Autowired
	private ApiService apiService;

	@GetMapping({ "/getcustomerdetails/{cid}" })
	public String consumeApi(@PathVariable String cid) throws Exception {
		String response = this.apiService.getcustomerdetails(cid);

		return response;
	}

	@PostMapping({ "createCCDBCustomer" })
	public ResponseEntity<String> createCustomer(@RequestBody CcdbCustomerDto data) throws Exception {
		String response = this.apiService.createCustomer(data);
		return ResponseEntity.ok(response);
	}
}
