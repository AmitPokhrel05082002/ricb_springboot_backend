package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.services.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    // ================= Submit a new claim =================

//    @PostMapping("/submitdep")
//    public String submitClaim(@RequestBody FullClaimDTO dto) {
//        claimService.submitClaimDep(dto);
//        return "Claim submitted successfully!";
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitClaim(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // ================= Validate File =================
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "No file uploaded. A ZIP file is required.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Invalid file type. Only ZIP files are allowed.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            long maxSizeBytes = 50L * 1024 * 1024;
            if (file.getSize() > maxSizeBytes) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "File size exceeds 50 MB.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // ================= Parse JSON =================
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            FullClaimDTO dto = mapper.readValue(data, FullClaimDTO.class);

            // ================= Call Service =================
            Map<String, Object> result = claimService.submitClaim(dto, file);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Claim submitted successfully",
                    "data", result,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", "Claim submission failed",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }


    // Get all Dzongkhags
    @GetMapping("/dzongkhags")
    public List<DzongkhagDTO> getDzongkhagNames() {
        return claimService.getDzongkhagNames();
    }

    // Get Gewogs by Dzongkhag ID
    @GetMapping("/gewogs/{dzongkhagId}")
    public List<GewogDTO> getGewogs(@PathVariable Integer dzongkhagId) {
        return claimService.getGewogsByDzongkhag(dzongkhagId);
    }

    // Get Villages by Gewog ID
    @GetMapping("/villages/{gewogId}")
    public List<VillageDTO> getVillages(@PathVariable Integer gewogId) {
        return claimService.getVillagesByGewog(gewogId);
    }

    // Get all Banks
    @GetMapping("/banks")
    public List<BankDTO> getBanks() {
        return claimService.getBanks();
    }

    // Get all Branches
    @GetMapping("/branches")
    public List<BranchDTO> getBranches() {
        return claimService.getBranches();
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
    public ResponseEntity<ClaimResponseDRO> getFullClaim(@PathVariable String cin) {
        try {
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(cin);
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

    @GetMapping("/download/{cin}")
    public ResponseEntity<ByteArrayResource> downloadClaim(@PathVariable String cin) {
        return claimService.downloadClaimFileByCin(cin);
    }

    @PostMapping(value = "/update-document/{cin}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateClaimDocument(
            @PathVariable String cin,
            @RequestPart("file") MultipartFile file) throws Exception {

        // Only accept ZIP files
        if (!file.getOriginalFilename().endsWith(".zip")) {
            return ResponseEntity.badRequest().body("Only ZIP files are allowed");
        }

        // Max 50MB
        if (file.getSize() > 50 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size must be less than 50MB");
        }

        claimService.updateClaimDocumentByCin(cin, file);

        return ResponseEntity.ok("Document updated successfully!");
    }
}
