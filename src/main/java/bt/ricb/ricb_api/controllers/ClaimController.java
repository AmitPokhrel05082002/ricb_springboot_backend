package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.DTOs.ClaimSummaryDTO;
import bt.ricb.ricb_api.models.DTOs.FullClaimDTO;
import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.services.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    // ================= Submit full claim =================
    @PostMapping("/submit")
    public String submitClaim(@RequestBody FullClaimDTO dto) {
        claimService.submitClaim(dto);
        return "Claim submitted successfully!";
    }


    // ===== 1. Claim Summary =====
    @GetMapping("dashboard/claims")
    public ResponseEntity<List<ClaimSummaryDTO>> getAllClaimSummaries() {
        try {
            List<ClaimSummaryDTO> summaries = claimService.getAllClaimSummaries();
            return ResponseEntity.ok(summaries);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== 2. Full Claim Details =====
    @GetMapping("/cin/{cin}/full")
    public ResponseEntity<FullClaimDTO> getFullClaim(@PathVariable String cin) {
        try {
            FullClaimDTO fullClaim = claimService.getFullClaimByCin(cin);
            return ResponseEntity.ok(fullClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dashboard/counts")
    public ResponseEntity<Map<String, Long>> getClaimStatusCounts() {
        return ResponseEntity.ok(claimService.getClaimStatusCounts());
    }
}