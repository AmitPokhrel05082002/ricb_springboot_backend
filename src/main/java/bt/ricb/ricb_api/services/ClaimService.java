package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.models.*;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    @Autowired
    private ClaimantRepository claimantRepo;

    @Autowired
    private PayeeRepository payeeRepo;

    @Autowired
    private PolicyHolderRepository policyHolderRepo;

    @Autowired
    private PolicyRepository policyRepo;

    @Autowired
    private ClaimRepository claimRepo;

    @Autowired
    private ClaimDocumentsRepository claimDocumentsRepo;

    @Autowired
    private ClaimActionsRepository claimActionsRepo;

    @Autowired
    private ClaimAuditRepository claimAuditRepo;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApiService apiService;
    @Autowired
    private DzongkhagRepository dzongkhagRepo;
    @Autowired
    private GewogRepository gewogRepo;
    @Autowired
    private VillageRepository villageRepo;
    @Autowired
    private BankRepository bankRepo;
    @Autowired
    private BranchRepository branchRepo;


    // ================= Claim Status Counts =================
    public Map<String, Long> getClaimStatusCounts() {

        Map<String, Long> counts = new HashMap<>();

        counts.put("totalClaims", claimRepo.count());
        counts.put("pending", claimRepo.countByStatus("Pending"));
        counts.put("approved", claimRepo.countByStatus("Approved"));
        counts.put("rejected", claimRepo.countByStatus("Rejected"));
        counts.put("resubmission", claimRepo.countByStatus("Resubmission Required"));
        counts.put("verified", claimRepo.countByStatus("Verified"));

        return counts;
    }


    // ================= CIN Generator =================
    private String generateCin() {

        int currentYear = java.time.LocalDate.now().getYear();

        String lastCin = claimRepo.getLastCinByYear(currentYear);

        int serialNumber = 1;

        if (lastCin != null) {
            String lastNumberStr = lastCin.substring(8);
            serialNumber = Integer.parseInt(lastNumberStr) + 1;
        }

        return String.format("CIN-%d%04d", currentYear, serialNumber);
    }


    // ================= Submit Full Claim =================
    @Transactional
    public Map<String, Object> submitClaim(FullClaimDTO dto, MultipartFile file) {

        // ================= Claimant =================
        ClaimantDTO claimantDTO = dto.getClaimant();

        ClaimantEntity claimant = new ClaimantEntity();
        claimant.setCid(claimantDTO.getCid());  // duplicate allowed
        claimant.setFullName(claimantDTO.getFullName());
        claimant.setMobileNumber(claimantDTO.getMobileNumber());
        claimant.setEmailAddress(claimantDTO.getEmailAddress());
        claimant.setDzongkhagId(claimantDTO.getDzongkhagId());
        claimant.setGewogId(claimantDTO.getGewogId());
        claimant.setVillageId(claimantDTO.getVillageId());
        claimant.setCreatedAt(LocalDateTime.now());
        claimant.setUpdatedAt(LocalDateTime.now());

        claimantRepo.save(claimant);

        // ================= Policy Holder =================
        PolicyHolderDTO phDTO = dto.getPolicyHolder();

        PolicyHolderEntity policyHolder = new PolicyHolderEntity();
        policyHolder.setCid(phDTO.getCid()); // duplicate allowed
        policyHolder.setDateOfBirth(phDTO.getDateOfBirth());
        policyHolder.setIsValidated(1);
        policyHolder.setCreatedAt(LocalDateTime.now());
        policyHolder.setUpdatedAt(LocalDateTime.now());

        policyHolderRepo.save(policyHolder);

        // ================= Policies =================
        List<PolicyDTO> policyDTOList = dto.getPolicies();
        List<String> duplicatePolicies = new ArrayList<>();

        if (policyDTOList != null && !policyDTOList.isEmpty()) {
            List<PolicyEntity> policiesToInsert = new ArrayList<>();

            for (PolicyDTO policyDTO : policyDTOList) {

                // Check if policy number already exists
                boolean exists = policyRepo.existsByPolicyNumber(policyDTO.getPolicyNumber());
                if (exists) {
                    duplicatePolicies.add(policyDTO.getPolicyNumber());
                    continue; // skip this policy
                }

                PolicyEntity policy = new PolicyEntity();
                policy.setPolicyHolderId(policyHolder.getId());
                policy.setPolicyName(policyDTO.getPolicyName());
                policy.setPolicyNumber(policyDTO.getPolicyNumber());
                policy.setIntimationDate(policyDTO.getIntimationDate());
                policy.setNomineeName(policyDTO.getNomineeName());
                policy.setRelation(policyDTO.getRelation());
                policy.setSumAssured(policyDTO.getSumAssured());
                policy.setStatus(policyDTO.getStatus() != null ? policyDTO.getStatus() : "Active");
                policy.setCreatedAt(LocalDateTime.now());

                policiesToInsert.add(policy);
            }

            if (!policiesToInsert.isEmpty()) {
                policyRepo.saveAll(policiesToInsert);
            }
        }

// If any duplicates found, throw an exception or return in response
        if (!duplicatePolicies.isEmpty()) {
            throw new RuntimeException("Policy already exists: " + String.join(", ", duplicatePolicies));
        }


        // ================= Payee =================
        PayeeDTO payeeDTO = dto.getPayee();
        PayeeEntity payee = new PayeeEntity();
        payee.setClaimantId(claimant.getId());
        payee.setSameAsClaimant("Yes".equalsIgnoreCase(payeeDTO.getSameAsClaimant()) ? 1 : 0);
        payee.setCid(payeeDTO.getCid()); // duplicate allowed
        payee.setAccountHolderName(payeeDTO.getAccountHolderName());
        payee.setAccountNumber(payeeDTO.getAccountNumber());
        payee.setMobileNumber(payeeDTO.getMobileNumber());
        payee.setBankId(payeeDTO.getBankId());
        payee.setCreatedAt(LocalDateTime.now());
        payee.setUpdatedAt(LocalDateTime.now());

        payeeRepo.save(payee);

        // ================= Claim =================
        ClaimDTO claimDTO = dto.getClaim();
        ClaimEntity claim = new ClaimEntity();
        claim.setCin(generateCin()); // unique CIN
        claim.setClaimantId(claimant.getId());
        claim.setPayeeId(payee.getId());
        claim.setPolicyHolderId(policyHolder.getId());
        claim.setNearestBranchId(claimDTO.getNearestBranchId());
        claim.setClaimType(claimDTO.getClaimType());

        // Conditional nulling based on claim type
        if ("Death".equalsIgnoreCase(claimDTO.getClaimType())) {
            claim.setG2cApplicationNumber(null);
            claim.setDateOfDeath(claimDTO.getDateOfDeath());
            claim.setPlaceOfDeath(claimDTO.getPlaceOfDeath());
            claim.setDeathType(claimDTO.getDeathType());
            claim.setCauseOfDeath(claimDTO.getCauseOfDeath());

            // PTD fields null
            claim.setDateOfLoss(null);
            claim.setPlaceOfLoss(null);
            claim.setCauseOfLoss(null);

        } else if ("Permanent Total Disability".equalsIgnoreCase(claimDTO.getClaimType())) {
            claim.setG2cApplicationNumber(claimDTO.getG2cApplicationNumber());
            claim.setDateOfDeath(null);
            claim.setPlaceOfDeath(null);
            claim.setDeathType(null);
            claim.setCauseOfDeath(null);

            claim.setDateOfLoss(claimDTO.getDateOfLoss());
            claim.setPlaceOfLoss(claimDTO.getPlaceOfLoss());
            claim.setCauseOfLoss(claimDTO.getCauseOfLoss());
        } else {
            claim.setG2cApplicationNumber(claimDTO.getG2cApplicationNumber());
            claim.setDateOfDeath(claimDTO.getDateOfDeath());
            claim.setPlaceOfDeath(claimDTO.getPlaceOfDeath());
            claim.setDeathType(claimDTO.getDeathType());
            claim.setCauseOfDeath(claimDTO.getCauseOfDeath());

            claim.setDateOfLoss(claimDTO.getDateOfLoss());
            claim.setPlaceOfLoss(claimDTO.getPlaceOfLoss());
            claim.setCauseOfLoss(claimDTO.getCauseOfLoss());
        }

        claim.setStatus("Pending");
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepo.saveAndFlush(claim);

        // ================= Documents =================
        if (file != null && !file.isEmpty()) {

            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".zip")) {
                throw new RuntimeException("Invalid file type. Only ZIP files are allowed.");
            }

            long maxSizeBytes = 20L * 1024 * 1024;
            if (file.getSize() > maxSizeBytes) {
                throw new RuntimeException("File size exceeds 20 MB.");
            }

            try {
                String fileName = "claims/" + claim.getCin() + "_" + originalFileName;

                String fileUrl = s3Service.uploadFile(
                        fileName,
                        file.getInputStream(),
                        file.getSize()
                );

                ClaimDocumentsEntity doc = new ClaimDocumentsEntity();
                doc.setClaimId(claim.getId());
                doc.setZipFilePath(fileUrl);
                doc.setFileSizeKb((int) (file.getSize() / 1024));
                doc.setUploadedAt(LocalDateTime.now());

                claimDocumentsRepo.save(doc);

            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }

        } else {
            throw new RuntimeException("No file uploaded. A ZIP file is required.");
        }

        // ================= Notification =================
        String cin = claim.getCin();

        try {
            String mobile = claimant.getMobileNumber();

            if (mobile != null && !mobile.isBlank()) {

                if (mobile.startsWith("17")) {
                    apiService.sendSms("Your claim submitted. CIN: " + cin, mobile);

                } else if (mobile.startsWith("77")) {
                    apiService.sendSmsTcell("Your claim submitted. CIN: " + cin, mobile);
                }
            }

            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {
                emailService.sendEmail(
                        claimant.getEmailAddress(),
                        "Insurance Claim Submitted",
                        "Your claim has been submitted successfully. CIN: " + cin,
                        null
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Map.of(
                "cin", claim.getCin(),
                "mobileNumber", claimant.getMobileNumber(),
                "email", claimant.getEmailAddress()
        );
    }

    // ================= Claim Summary =================
    public List<ClaimSummaryDTO> getAllClaimSummaries() {

        List<ClaimEntity> claims = claimRepo.findAll(); // fetch all claims

        return claims.stream().map(claim -> {
            ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                    .orElseThrow(() -> new RuntimeException("Claimant not found"));

            ClaimSummaryDTO summary = new ClaimSummaryDTO();
            summary.setCin(claim.getCin());
            summary.setClaimantName(claimant.getFullName());
            summary.setSubmittedDate(claim.getUpdatedAt());
            summary.setStatus(claim.getStatus());
            summary.setRemarks(claim.getRemarks());
            summary.setCreatedAt(claim.getCreatedAt());

            return summary;
        }).collect(Collectors.toList());
    }


    // ================= Full Claim Details =================
    public ClaimResponseDRO getFullClaimByCin(String cin) {

        // Fetch claim
        ClaimEntity claim = claimRepo.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim not found for CIN: " + cin));

        // Fetch related entities
        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        PayeeEntity payee = payeeRepo.findById(claim.getPayeeId())
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        PolicyHolderEntity policyHolder = policyHolderRepo.findById(claim.getPolicyHolderId())
                .orElseThrow(() -> new RuntimeException("Policy holder not found"));

        // Build ClaimantDTO
        ClaimantDTO claimantDTO = new ClaimantDTO();
        claimantDTO.setCid(claimant.getCid());
        claimantDTO.setFullName(claimant.getFullName());
        claimantDTO.setMobileNumber(claimant.getMobileNumber());
        claimantDTO.setEmailAddress(claimant.getEmailAddress());
        claimantDTO.setDzongkhagId(claimant.getDzongkhagId());
        claimantDTO.setGewogId(claimant.getGewogId());
        claimantDTO.setVillageId(claimant.getVillageId());

        // Build PayeeDTO
        PayeeDTO payeeDTO = new PayeeDTO();
        payeeDTO.setCid(payee.getCid());
        payeeDTO.setAccountHolderName(payee.getAccountHolderName());
        payeeDTO.setBankId(payee.getBankId());
        payeeDTO.setAccountNumber(payee.getAccountNumber());
        payeeDTO.setMobileNumber(payee.getMobileNumber());
        payeeDTO.setSameAsClaimant(payee.getSameAsClaimant() == 1 ? "Yes" : "No");

        // Build PolicyHolderDTO
        PolicyHolderDTO phDTO = new PolicyHolderDTO();
        phDTO.setCid(policyHolder.getCid());
        phDTO.setDateOfBirth(policyHolder.getDateOfBirth());

        // ================= PolicyDTO List =================
        List<PolicyDTO> policyDTOList = new ArrayList<>();
        List<PolicyEntity> policyEntities = policyRepo.findByPolicyHolderId(policyHolder.getId());
        for (PolicyEntity policy : policyEntities) {
            PolicyDTO policyDTO = new PolicyDTO();
            policyDTO.setPolicyName(policy.getPolicyName());
            policyDTO.setPolicyNumber(policy.getPolicyNumber());
            policyDTO.setIntimationDate(policy.getIntimationDate());
            policyDTO.setNomineeName(policy.getNomineeName());
            policyDTO.setRelation(policy.getRelation());
            policyDTO.setSumAssured(policy.getSumAssured());
            policyDTO.setStatus(policy.getStatus());

            policyDTOList.add(policyDTO);
        }

        // Build ClaimDTO
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setNearestBranchId(claim.getNearestBranchId());
        claimDTO.setClaimType(claim.getClaimType());
        claimDTO.setG2cApplicationNumber(claim.getG2cApplicationNumber());
        claimDTO.setDateOfDeath(claim.getDateOfDeath());
        claimDTO.setPlaceOfDeath(claim.getPlaceOfDeath());
        claimDTO.setDeathType(claim.getDeathType());
        claimDTO.setCauseOfDeath(claim.getCauseOfDeath());
        claimDTO.setDateOfLoss(claim.getDateOfLoss());
        claimDTO.setPlaceOfLoss(claim.getPlaceOfLoss());
        claimDTO.setCauseOfLoss(claim.getCauseOfLoss());

        // Build Response
        ClaimResponseDRO response = new ClaimResponseDRO();
        response.setClaimant(claimantDTO);
        response.setPayee(payeeDTO);
        response.setPolicyHolder(phDTO);
        response.setPolicies(policyDTOList);
        response.setClaim(claimDTO);
        response.setCin(claim.getCin());
        response.setCreatedAt(claim.getCreatedAt());
        response.setStatus(claim.getStatus());
        return response;
    }


    private void logAudit(ClaimEntity claim, String previousStatus, String newStatus, String remarks, Integer actionedBy) {
        ClaimAuditEntity audit = new ClaimAuditEntity();
        audit.setClaimId(claim.getId());
        audit.setCin(claim.getCin());
        audit.setPreviousStatus(previousStatus);
        audit.setNewStatus(newStatus);
        audit.setRemarks(remarks);
        audit.setActionedBy(actionedBy);
        audit.setActionedAt(LocalDateTime.now());
        claimAuditRepo.save(audit);
    }


    // ================= Resubmit Claim =================
    @Transactional
    public ClaimEntity resubmitClaim(ClaimActionDTO dto) {
        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));
        String oldStatus = claim.getStatus();
        claim.setStatus("Resubmission Required");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Resubmitted);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        logAudit(claim, oldStatus, "Resubmission Required", dto.getRemarks(), dto.getActionedBy());
        return claim;
    }

    // ================= Reject Claim =================
    @Transactional
    public ClaimEntity rejectClaim(ClaimActionDTO dto) {
        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));
        String oldStatus = claim.getStatus();
        claim.setStatus("Rejected");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Rejected);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        logAudit(claim, oldStatus, "Rejected", dto.getRemarks(), dto.getActionedBy());
        return claim;
    }

    // ================= Verify Claim =================
    @Transactional
    public ClaimEntity verifyClaim(ClaimActionDTO dto) {
        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));
        String oldStatus = claim.getStatus();
        claim.setStatus("Verified");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Verified);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        logAudit(claim, oldStatus, "Verified", dto.getRemarks(), dto.getActionedBy());
        return claim;
    }

    // ================= Approve Claim =================
    @Transactional
    public ClaimEntity approveClaim(ClaimActionDTO dto) {
        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));
        String oldStatus = claim.getStatus();
        claim.setStatus("Approved");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Approved);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        logAudit(claim, oldStatus, "Approved", dto.getRemarks(), dto.getActionedBy());
        return claim;
    }

    public List<ClaimAuditEntity> getClaimAuditTrail(String cin) {
        return claimAuditRepo.findByCinOrderByActionedAtDesc(cin);
    }

    // ================= Track Records =================
    private final ClaimRepository claimRepository;
    private final ClaimAuditRepository claimAuditRepository;

    public ClaimService(ClaimRepository claimRepository, ClaimAuditRepository claimAuditRepository) {
        this.claimRepository = claimRepository;
        this.claimAuditRepository = claimAuditRepository;
    }

    public Map<String, Object> getClaimDetails(String cin) {
        ClaimEntity claim = claimRepository.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + cin));

        Map<String, Object> response = new HashMap<>();
        response.put("cin", claim.getCin());
        response.put("createdAt", claim.getCreatedAt());
        response.put("updatedAt", claim.getUpdatedAt());
        response.put("status", claim.getStatus());
        response.put("remarks", claim.getRemarks());

        List<Map<String, Object>> auditHistory = new ArrayList<>();
        List<ClaimAuditEntity> audits = claimAuditRepository.findByCinOrderByActionedAtDesc(cin);

        for (ClaimAuditEntity audit : audits) {
            Map<String, Object> auditMap = new HashMap<>();
            auditMap.put("actionedAt", audit.getActionedAt());
            auditMap.put("newStatus", audit.getNewStatus());
            auditHistory.add(auditMap);
        }

        response.put("auditHistory", auditHistory);

        return response;
    }

    // Dzongkhags
    public List<DzongkhagDTO> getDzongkhagNames() {
        return dzongkhagRepo.findAll().stream()
                .map(d -> {
                    DzongkhagDTO dto = new DzongkhagDTO();
                    dto.setDzongkhagId(d.getDzongkhagId());
                    dto.setDzongkhagName(d.getDzongkhagName());
                    dto.setIsActive(d.getIsActive());
                    dto.setCreatedAt(d.getCreatedAt());
                    dto.setUpdatedAt(d.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Gewogs filtered by Dzongkhag
    public List<GewogDTO> getGewogsByDzongkhag(Integer dzongkhagId) {
        return gewogRepo.findByDzongkhagId(dzongkhagId).stream()
                .map(g -> {
                    GewogDTO dto = new GewogDTO();
                    dto.setGewogId(g.getGewogId());
                    dto.setDzongkhagId(g.getDzongkhagId());
                    dto.setGewogName(g.getGewogName());
                    dto.setIsActive(g.getIsActive());
                    dto.setCreatedAt(g.getCreatedAt());
                    dto.setUpdatedAt(g.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Villages filtered by Gewog
    public List<VillageDTO> getVillagesByGewog(Integer gewogId) {
        return villageRepo.findByGewogId(gewogId).stream()
                .map(v -> {
                    VillageDTO dto = new VillageDTO();
                    dto.setVillageId(v.getVillageId());
                    dto.setGewogId(v.getGewogId());
                    dto.setVillageName(v.getVillageName());
                    dto.setIsActive(v.getIsActive());
                    dto.setCreatedAt(v.getCreatedAt());
                    dto.setUpdatedAt(v.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Banks
    public List<BankDTO> getBanks() {
        return bankRepo.findAll().stream()
                .map(b -> {
                    BankDTO dto = new BankDTO();
                    dto.setId(b.getId());
                    dto.setName(b.getName());
                    dto.setCreatedAt(b.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Branches
    public List<BranchDTO> getBranches() {
        return branchRepo.findAll().stream()
                .map(b -> {
                    BranchDTO dto = new BranchDTO();
                    dto.setId(b.getId());
                    dto.setName(b.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Download claim ZIP file by CIN
     */
    public ResponseEntity<ByteArrayResource> downloadClaimFileByCin(String cin) {

        // Find claim by CIN
        ClaimEntity claim = claimRepo.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim with CIN " + cin + " not found"));

        // Find document by claim ID
        ClaimDocumentsEntity doc = claimDocumentsRepo.findByClaimId(claim.getId())
                .orElseThrow(() -> new RuntimeException("No document found for CIN " + cin));

        // Extract S3 key
        String key = doc.getZipFilePath();
        if (key.startsWith("https://")) {
            key = key.substring(key.indexOf("claims/"));
        }

        // Get file from S3
        byte[] fileBytes = s3Service.downloadFile(key);

        if (fileBytes == null || fileBytes.length == 0) {
            throw new RuntimeException("File is empty or not found in S3 for CIN " + cin);
        }

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + extractFileName(doc.getZipFilePath()) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    private String extractFileName(String path) {
        if (path == null) return "file.zip";
        return path.substring(path.lastIndexOf("/") + 1);
    }

    // ================= Update Claim Document =================
    public void updateClaimDocumentByCin(String cin, MultipartFile file) throws Exception {

        // ================= 1️⃣ Validate File =================
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".zip")) {
            throw new RuntimeException("Only ZIP files are allowed");
        }

        long maxSize = 20L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size must be less than 20MB");
        }

        // ================= 2️⃣ Get Claim =================
        ClaimEntity claim = claimRepo.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim with CIN " + cin + " not found"));

        String oldStatus = claim.getStatus();

        // ================= 3️⃣ Get Existing Document =================
        ClaimDocumentsEntity doc = claimDocumentsRepo.findByClaimId(claim.getId())
                .orElse(null);

        // ================= 4️⃣ Prepare File Name (SAME AS INSERT) =================
        String cleanFileName = originalFileName.replaceAll("\\s+", "_");
        String fileName = "claims/" + cin + "_" + cleanFileName;

        // ================= 5️⃣ Delete Old File (if exists) =================
        if (doc != null && doc.getZipFilePath() != null) {

            String oldFilePath = doc.getZipFilePath();

            // Convert FULL URL → S3 KEY
            if (oldFilePath.startsWith("http")) {
                oldFilePath = oldFilePath.substring(oldFilePath.indexOf(".com/") + 5);
            }

            try {
                s3Service.deleteFile(oldFilePath);
            } catch (Exception e) {
                System.err.println("Failed to delete old file from S3: " + e.getMessage());
            }

        } else {
            doc = new ClaimDocumentsEntity();
            doc.setClaimId(claim.getId());
        }

        // ================= 6️⃣ Upload New File (SAME AS INSERT) =================
        String fileUrl = s3Service.uploadFile(
                fileName,
                file.getInputStream(),
                file.getSize()
        );

        // ================= 7️⃣ Save Document =================
        doc.setZipFilePath(fileUrl); // ✅ FULL URL
        doc.setFileSizeKb((int) (file.getSize() / 1024));
        doc.setUploadedAt(LocalDateTime.now());

        claimDocumentsRepo.save(doc);

        // ================= 8️⃣ Update Claim =================
        claim.setStatus("Pending");
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepo.save(claim);

        // ================= 9️⃣ Audit Log =================
        logAudit(
                claim,
                oldStatus,
                "Pending",
                "Document updated for CIN: " + cin,
                0 // Replace with logged-in user ID
        );
    }
}