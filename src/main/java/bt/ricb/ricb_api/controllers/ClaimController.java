package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.ClaimAuditEntity;
import bt.ricb.ricb_api.models.ClaimDocumentsEntity;
import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.models.DTOs.ClaimActionDTO;
import bt.ricb.ricb_api.models.DTOs.ClaimDocumentsDTO;
import bt.ricb.ricb_api.models.DTOs.ClaimSummaryDTO;
import bt.ricb.ricb_api.models.DTOs.FullClaimDTO;
import bt.ricb.ricb_api.services.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    // ================= Submit a new claim =================
    @PostMapping()
    public String submitClaim(@RequestBody FullClaimDTO dto) {
        claimService.submitClaim(dto);
        return "Claim submitted successfully!";
    }

    @GetMapping("/{cin}/track")
    public Map<String, Object> getClaimDetails(@PathVariable String cin) {
        return claimService.getClaimDetails(cin);
    }

    // ===== 1. Dashboard status count =====
    @GetMapping("/status-counts")
    public ResponseEntity<Map<String, Long>> getClaimStatusCounts() {
        return ResponseEntity.ok(claimService.getClaimStatusCounts());
    }

    // ===== 2. Claim summaries for dashboard ===========
    @GetMapping("summaries")
    public ResponseEntity<List<ClaimSummaryDTO>> getAllClaimSummaries() {
        try {
            List<ClaimSummaryDTO> summaries = claimService.getAllClaimSummaries();
            return ResponseEntity.ok(summaries);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== 3. Full Claim Details =====
    @GetMapping("/{cin}")
    public ResponseEntity<FullClaimDTO> getFullClaim(@PathVariable String cin) {
        try {
            FullClaimDTO fullClaim = claimService.getFullClaimByCin(cin);
            return ResponseEntity.ok(fullClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/resubmit")
    public ResponseEntity<ClaimEntity> resubmitClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.resubmitClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/reject")
    public ResponseEntity<ClaimEntity> rejectClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.rejectClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/verify")
    public ResponseEntity<ClaimEntity> verifyClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.verifyClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/approve")
    public ResponseEntity<ClaimEntity> approveClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.approveClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PutMapping("/{cin}/documents")
    public ClaimDocumentsEntity updateClaimDocumentByCin(@PathVariable String cin,
                                                         @RequestBody ClaimDocumentsDTO dto) {
        return claimService.updateClaimDocumentByCin(cin, dto);
    }

    @GetMapping("/{cin}/audit")
    public ResponseEntity<List<ClaimAuditEntity>> getClaimAudit(@PathVariable String cin) {
        return ResponseEntity.ok(claimService.getClaimAuditTrail(cin));
    }
}
