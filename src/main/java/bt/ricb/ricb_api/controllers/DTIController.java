package bt.ricb.ricb_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.dao.PolicyDTIDetailDto;
import bt.ricb.ricb_api.dao.PolicyDTIDto;
import bt.ricb.ricb_api.models.PolicyDTIDetailsEntity;
import bt.ricb.ricb_api.services.DTIService;

@RestController
@CrossOrigin({ "*" })
@RequestMapping("/api/dti")
public class DTIController {
	
	@Autowired
    private DTIService dtiService;
    
	 @PostMapping("/complete")
	    public ResponseEntity<PolicyDTIDetailsEntity> createCompletePolicy(
	            @RequestBody CompletePolicyRequest request) {
	        
		 PolicyDTIDetailsEntity savedPolicy = dtiService.createCompletePolicy(
	            request.getPolicyDetail(), 
	            request.getPolicy()
	        );
	        
	        return ResponseEntity.ok(savedPolicy);
	    }
	    
	    // Inner class for request wrapper
		 public static class CompletePolicyRequest {
			    private List<PolicyDTIDetailDto> policyDetail;
			    private PolicyDTIDto policy;
			    
			    // Getters and setters
			    public List<PolicyDTIDetailDto> getPolicyDetail() { return policyDetail; }
			    public void setPolicyDetail(List<PolicyDTIDetailDto> policyDetail) { this.policyDetail = policyDetail; }
			    public PolicyDTIDto getPolicy() { return policy; }
			    public void setPolicy(PolicyDTIDto policy) { this.policy = policy; }
			}
}
