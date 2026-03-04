package bt.ricb.ricb_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.controllers.DTIController.CompletePolicyRequest;
import bt.ricb.ricb_api.dao.PolicyDTIDetailDto;
import bt.ricb.ricb_api.dao.PolicyDTIDto;
import bt.ricb.ricb_api.models.PolicyDTIDetailsEntity;
import bt.ricb.ricb_api.models.PolicyRHIEntity;
import bt.ricb.ricb_api.services.DTIService;
import bt.ricb.ricb_api.services.RHIService;

@RestController
@CrossOrigin({ "*" })
@RequestMapping("/api/RHI")
public class RHIController {
	@Autowired
    private RHIService rhiService;
    
	 @PostMapping("/insert")
	    public ResponseEntity<String> createCompletePolicy(
	            @RequestBody List<PolicyRHIEntity> request) {
		 
		 for (PolicyRHIEntity policy : request) {
	            try {
				    RHIService.insertRHIDetails(policy);
	            } catch (Exception e) {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("Failed to replicate some policies: " + e.getMessage());
	            }
	        }
	        return ResponseEntity.ok("All policies replicated successfully.");
	        
//		  try {
//	            return ResponseEntity.ok("Policy successfully inserted");
//	        } catch (Exception e) {
//	            return ResponseEntity.status(500).body("Error replicating policy: " + e.getMessage());
//	        }
	    }
}
