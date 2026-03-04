package bt.ricb.ricb_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.models.Nyekor;
import bt.ricb.ricb_api.services.NyekorService;

@CrossOrigin({ "*" })
@RestController
@RequestMapping("/api/nyekor")
public class NyekorController {
	
    @Autowired
	private NyekorService policyService;

    @PostMapping
    public ResponseEntity<Long> createPolicy(@RequestBody Nyekor policyRequest) throws Exception {
        Long polSerialNo = policyService.createPolicy(policyRequest);
        return ResponseEntity.ok(polSerialNo);
    }
}
