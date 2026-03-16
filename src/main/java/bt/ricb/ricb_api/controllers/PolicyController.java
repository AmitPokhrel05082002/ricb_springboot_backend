//package bt.ricb.ricb_api.controllers;
//
//import bt.ricb.ricb_api.services.PolicyService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/policies")
//public class PolicyController {
//
//    @Autowired
//    private PolicyService policyService;
//
//    @GetMapping("/{cid}")
//    public Map<String,Object> getPolicies(
//            @PathVariable String cid,
//            @RequestParam(required = false) String orgCode){
//
//        return policyService.getPolicies(cid, orgCode);
//    }
//
//    @GetMapping("/all")
//    public Map<String, Object> getAllPolicies() {
//        return policyService.getAllPolicies();
//    }
//
//}