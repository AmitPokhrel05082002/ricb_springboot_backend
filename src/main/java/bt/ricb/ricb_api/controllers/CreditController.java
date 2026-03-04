package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RequestMapping({ "credit" })
@RestController
public class CreditController {
	@Autowired
	private ApiService apiService;

	@GetMapping({ "/creditInvestment/{cid}" })
	public String consumeApi(@PathVariable String cid) throws Exception {
		return this.apiService.getCreditAccount(cid);
	}
}
