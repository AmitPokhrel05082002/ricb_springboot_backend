package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.config.ConnectionManager;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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



    // ================= Claim Status Counts =================
    public Map<String, Long> getClaimStatusCounts() {

        Map<String, Long> counts = new HashMap<>();

        counts.put("totalClaims", claimRepo.count());
        counts.put("pending", claimRepo.countByStatus("Pending"));
        counts.put("approved", claimRepo.countByStatus("Approved"));
        counts.put("rejected", claimRepo.countByStatus("Rejected"));
        counts.put("resubmission", claimRepo.countByStatus("Resubmission Required"));
        counts.put("Completed", claimRepo.countByStatus("Completed"));

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
        claimant.setRelation(claimantDTO.getRelation());
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
                policy.setPolicyHolderName(policyDTO.getPolicyHolderName());
                policy.setPolicyNumber(policyDTO.getPolicyNumber());
                policy.setPolicySerialNumber(policyDTO.getPolicySerialNumber());
                policy.setIntimationDate(policyDTO.getIntimationDate());
                policy.setNomineeName(policyDTO.getNomineeName());
                policy.setSumAssured(policyDTO.getSumAssured());
                policy.setBranchCode(policyDTO.getBranchCode());
                policy.setStatus(policyDTO.getStatus() != null ? policyDTO.getStatus() : "Active");
                policy.setClaimStatus("Pending");
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

        } else {
            // For all other claim types
            claim.setG2cApplicationNumber(claimDTO.getG2cApplicationNumber());
            claim.setDateOfDeath(claimDTO.getDateOfDeath());
            claim.setPlaceOfDeath(claimDTO.getPlaceOfDeath());
            claim.setDeathType(claimDTO.getDeathType());
            claim.setCauseOfDeath(claimDTO.getCauseOfDeath());

        }
        claim.setStatus("Pending");
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepo.saveAndFlush(claim);
        // ================= Documents =================
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file uploaded. A ZIP file is required.");
        }

        // ✅ Single validation method (handles everything)
        validateZipFile(file);

        try {
            String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "_");

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
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
        // ================= Notification =================
        String cin = claim.getCin();
        String claimantFullName = claimant.getFullName();

        try {
            String mobile = claimant.getMobileNumber();

            if (mobile != null && !mobile.isBlank()) {

                if (mobile.startsWith("17")) {
                    apiService.sendSms("Your life insurance claim is successfully registered. You can track the progress of your claim with [" + cin + "]", mobile);

                } else if (mobile.startsWith("77")) {
                    apiService.sendSmsTcell("Your life insurance claim is successfully registered. You can track the progress of your claim with [" + cin + "]", mobile);
                }
            }

            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {

                String subject = "Claim Registration Successful";

                String body = "Dear " + claimantFullName + ",\n\n"
                        + "This is to acknowledge on having successfully registered your life insurance claim online. "
                        + "Our team is currently reviewing the claims and will keep you informed of any updates or if further information is required.\n\n"
                        + "Use the [" + cin + "] to track its progress online via your “My Business Profile” on the RICB website.\n\n"
                        + "Should you require any further clarifications, please feel free to contact us at our toll-free number 1818 during office hours or drop a mail to contactus@ricb.bt.\n\n"
                        + "Best regards,\n"
                        + "RICB";

                emailService.sendEmail(
                        claimant.getEmailAddress(),
                        subject,
                        body,
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

        List<ClaimEntity> claims = claimRepo.findAll();

        return claims.stream().map(claim -> {

            // ================= Claimant =================
            ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                    .orElseThrow(() ->
                            new RuntimeException("Claimant not found for ID: " + claim.getClaimantId())
                    );

            // ================= Policy =================
            List<PolicyEntity> policies =
                    Optional.ofNullable(policyRepo.findByPolicyHolderId(claim.getPolicyHolderId()))
                            .orElse(Collections.emptyList());

            String policyHolderName;

            if (!policies.isEmpty()) {
                // ✅ Take first policy
                policyHolderName = policies.get(0).getPolicyHolderName();
            } else {
                // ✅ FIX: Instead of throwing error → fallback
                policyHolderName = "N/A";
            }

            // ================= DTO Mapping =================
            ClaimSummaryDTO summary = new ClaimSummaryDTO();
            summary.setCin(claim.getCin());
            summary.setClaimantName(claimant.getFullName());
            summary.setPolicyHolderName(policyHolderName);
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
        claimantDTO.setRelation(claimant.getRelation());
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

        // Build PolicyHolderDTO
        PolicyHolderDTO phDTO = new PolicyHolderDTO();
        phDTO.setCid(policyHolder.getCid());
        phDTO.setDateOfBirth(policyHolder.getDateOfBirth());

        // ================= PolicyDTO List =================
        List<PolicyDTO> policyDTOList = new ArrayList<>();
        List<PolicyEntity> policyEntities = policyRepo.findByPolicyHolderId(policyHolder.getId());
        for (PolicyEntity policy : policyEntities) {
            PolicyDTO policyDTO = new PolicyDTO();
            policyDTO.setPolicyNumber(policy.getPolicyNumber());
            policyDTO.setPolicyHolderName(policy.getPolicyHolderName());
            policyDTO.setIntimationDate(policy.getIntimationDate());
            policyDTO.setPolicySerialNumber(policy.getPolicySerialNumber());
            policyDTO.setNomineeName(policy.getNomineeName());
            policyDTO.setSumAssured(policy.getSumAssured());
            policyDTO.setBranchCode(policy.getBranchCode());
            policyDTO.setStatus(policy.getStatus());
            policyDTO.setClaimStatus(policy.getClaimStatus());
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

    // ================= Complete Claim =================
    @Transactional
    public ClaimEntity completeClaim(ClaimCompleteDTO dto) {

        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));

        List<PolicyEntity> policies = policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());

        boolean hasPending = policies.stream()
                .anyMatch(p -> "Pending".equalsIgnoreCase(p.getClaimStatus()));

        if (hasPending) {
            throw new RuntimeException("Cannot complete claim. Some policies are still pending.");
        }

        boolean allApproved = policies.stream()
                .allMatch(p -> "Approved".equalsIgnoreCase(p.getClaimStatus()));

        boolean allRejected = policies.stream()
                .allMatch(p -> "Rejected".equalsIgnoreCase(p.getClaimStatus()));

        String oldStatus = claim.getStatus();
        String newStatus;

        if (allApproved) {
            newStatus = "Approved";
        } else if (allRejected) {
            newStatus = "Rejected";
        } else {
            newStatus = "Completed";
        }

        claim.setStatus(newStatus);
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Completed);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        logAudit(claim, oldStatus, newStatus, dto.getRemarks(), dto.getActionedBy());

        return claim;
    }

    // ================= Resubmit Claim =================
    @Transactional
    public ClaimEntity resubmitClaim(ClaimCompleteDTO dto) {

        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));

        String oldStatus = claim.getStatus();

        // ================= CLAIM UPDATE ONLY =================
        claim.setStatus("Resubmission Required");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        // ================= CLAIM ACTION (CLAIM LEVEL ONLY) =================
        ClaimActionEntity action = new ClaimActionEntity();
        action.setClaimId(claim.getId());
        action.setActionType(ClaimActionEntity.ActionType.Resubmitted);
        action.setRemarks(dto.getRemarks());
        action.setActionedBy(dto.getActionedBy());
        action.setActionedAt(LocalDateTime.now());
        claimActionsRepo.save(action);

        // ================= AUDIT (CLAIM LEVEL ONLY) =================
        logAudit(claim, oldStatus, "Resubmission Required", dto.getRemarks(), dto.getActionedBy());

        // ================= NOTIFICATION =================
        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        List<PolicyEntity> policies = policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());

        // ONLY for message display (NOT action-based)
        String policyNumbers = policies.stream()
                .map(PolicyEntity::getPolicyNumber)
                .collect(Collectors.joining(", "));

        try {
            String mobile = claimant.getMobileNumber();

            if (mobile != null && !mobile.isBlank()) {

                String smsMessage =
                        "Your life insurance claim with CIN:" + claim.getCin() +
                                " for the policy no. " + policyNumbers +
                                " requires additional documents. Please visit My Business Profile for details.";

                if (mobile.startsWith("17")) {
                    apiService.sendSms(smsMessage, mobile);
                } else if (mobile.startsWith("77")) {
                    apiService.sendSmsTcell(smsMessage, mobile);
                }
            }

            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {

                String subject = "Life Insurance Claim - Resubmission Required";

                String body =
                        "Dear " + claimant.getFullName() + ",\n\n" +
                                "Thank you for submitting your claim.\n\n" +
                                "To proceed with the assessment, we kindly request you to provide additional documentation.\n\n" +
                                "Please upload required documents in My Business Profile on the RICB website.\n\n" +
                                "Should you require any further clarifications, please contact 1818 or email contactus@ricb.bt.\n\n" +
                                "Best regards,\nRICB";

                emailService.sendEmail(claimant.getEmailAddress(), subject, body, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return claim;
    }

    // ================= Reject Claim =================
    @Transactional
    public void rejectPolicies(ClaimActionDTO dto, MultipartFile file) {

        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found for CIN: " + dto.getCin()));

        List<PolicyEntity> policies = policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());

        List<PolicyEntity> selectedPolicies = policies.stream()
                .filter(p -> dto.getPolicyNumbers().contains(p.getPolicyNumber()))
                .toList();

        if (selectedPolicies.isEmpty()) {
            throw new RuntimeException("No matching policies found for rejection");
        }

        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        List<String> rejectedPolicyNumbers = new ArrayList<>();

        for (PolicyEntity policy : selectedPolicies) {

            policy.setClaimStatus("Rejected");
            policy.setRemarks(dto.getRemarks());
            policy.setUpdatedAt(LocalDateTime.now());

            rejectedPolicyNumbers.add(policy.getPolicyNumber());

            // ACTION
            ClaimActionEntity action = new ClaimActionEntity();
            action.setClaimId(claim.getId());
            action.setPolicyNumber(policy.getPolicyNumber());
            action.setActionType(ClaimActionEntity.ActionType.Rejected);
            action.setRemarks(dto.getRemarks());
            action.setActionedBy(dto.getActionedBy());
            action.setActionedAt(LocalDateTime.now());
            claimActionsRepo.save(action);

            // AUDIT
            ClaimAuditEntity audit = new ClaimAuditEntity();
            audit.setClaimId(claim.getId());
            audit.setCin(claim.getCin());
            audit.setPolicyNumber(policy.getPolicyNumber());
            audit.setPreviousStatus("Pending");
            audit.setNewStatus("Rejected");
            audit.setRemarks(dto.getRemarks());
            audit.setActionedBy(dto.getActionedBy());
            audit.setActionedAt(LocalDateTime.now());
            claimAuditRepo.save(audit);
        }

        policyRepo.saveAll(selectedPolicies);

        claim.setStatus("Pending");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        // ================= SMS + EMAIL =================
        try {

            String policyList = String.join(", ", rejectedPolicyNumbers);

            // SMS
            if (claimant.getMobileNumber() != null && !claimant.getMobileNumber().isBlank()) {

                String smsMessage =
                        "Your life insurance claim with CIN: " + claim.getCin() +
                                " for the policy no. " + policyList +
                                " has been found ineligible. Please visit My Business Profile for further details.";

                if (claimant.getMobileNumber().startsWith("17")) {
                    apiService.sendSms(smsMessage, claimant.getMobileNumber());
                } else if (claimant.getMobileNumber().startsWith("77")) {
                    apiService.sendSmsTcell(smsMessage, claimant.getMobileNumber());
                }
            }

            // EMAIL WITH ATTACHMENT
            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {

                String subject = "Life Insurance Claim - Rejected";

                String body =
                        "Dear " + claimant.getFullName() + ",\n\n"
                                + "Thank you for submitting your claim.\n\n"
                                + "This is to inform you that after careful review and examination of the claim against the policy number " +
                                policyList +
                                ", the claim has been found ineligible as per the policy terms and conditions. Therefore, we regret to inform you that the claim has been declined.\n\n"
                                + "Kindly access your “My Business Profile” on the RICB website for detailed report.\n\n"
                                + "Should you require any further clarifications, please feel free to contact us at our toll-free number 1818 during office hours or drop a mail to contactus@ricb.bt.\n\n"
                                + "Thank you for your understanding.";

                emailService.sendEmail(
                        claimant.getEmailAddress(),
                        subject,
                        body,
                        file // 👈 attachment
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= Approve Claim =================
//    @Transactional
//    public ClaimEntity approveClaim(ClaimActionDTO dto) {
//        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
//                .orElseThrow(() -> new RuntimeException("Claim not found with CIN: " + dto.getCin()));
//        String oldStatus = claim.getStatus();
//        claim.setStatus("Approved");
//        claim.setRemarks(dto.getRemarks());
//        claim.setUpdatedAt(LocalDateTime.now());
//        claimRepo.save(claim);
//
//        ClaimActionEntity action = new ClaimActionEntity();
//        action.setClaimId(claim.getId());
//        action.setActionType(ClaimActionEntity.ActionType.Approved);
//        action.setRemarks(dto.getRemarks());
//        action.setActionedBy(dto.getActionedBy());
//        action.setActionedAt(LocalDateTime.now());
//        claimActionsRepo.save(action);
//
//        logAudit(claim, oldStatus, "Approved", dto.getRemarks(), dto.getActionedBy());
//
//        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
//                .orElseThrow(() -> new RuntimeException("Claimant not found for CIN: " + dto.getCin()));
//
//        List<PolicyEntity> policies = policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());
//        String policyNumbers = policies.stream()
//                .map(PolicyEntity::getPolicyNumber)
//                .collect(Collectors.joining(", "));
//
//        String cin = claim.getCin();
//        String claimantFullName = claimant.getFullName();
//
//        try {
//            String mobile = claimant.getMobileNumber();
//            if (mobile != null && !mobile.isBlank()) {
//                String smsMessage = "Your life insurance claim with CIN: " + cin + " for the policy no. " + policyNumbers + " is approved and the benefit amount will be deposited into the account of nominee(s).";
//                if (mobile.startsWith("17")) {
//                    apiService.sendSms(smsMessage, mobile);
//                } else if (mobile.startsWith("77")) {
//                    apiService.sendSmsTcell(smsMessage, mobile);
//                }
//            }
//
//            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {
//                String subject = "Claim Approval Notification";        //"Life Insurance Claim - Approved";
//                String body = "Dear " + claimantFullName + ",\n\n"
//                        + "We are pleased to inform you that your claim [" + cin + "] has been reviewed and approved for the policy " + policyNumbers + ". The payment will be processed shortly and credited to the bank account number of nominee/s as per our internal procedures.\n\n"
//                        + "Should you require any further clarifications, please feel free to contact us at out toll-free number 1818 during office hours or drop a mail to contactus@ricb.bt.\n\n"
//                        + "Best regards,\n"
//                        + "RICB";
//
//                emailService.sendEmail(claimant.getEmailAddress(), subject, body, null);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return claim;
//    }

    // ================= Approve Claim =================
    @Transactional
    public void approvePolicies(ClaimActionDTO dto, MultipartFile file) {

        ClaimEntity claim = claimRepo.findByCin(dto.getCin())
                .orElseThrow(() -> new RuntimeException("Claim not found for CIN: " + dto.getCin()));

        List<PolicyEntity> policies = policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());

        List<PolicyEntity> selectedPolicies = policies.stream()
                .filter(p -> dto.getPolicyNumbers().contains(p.getPolicyNumber()))
                .toList();

        if (selectedPolicies.isEmpty()) {
            throw new RuntimeException("No matching policies found for approval");
        }

        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        List<String> approvedPolicyNumbers = new ArrayList<>();

        for (PolicyEntity policy : selectedPolicies) {

            policy.setClaimStatus("Approved");
            policy.setRemarks(dto.getRemarks());
            policy.setUpdatedAt(LocalDateTime.now());

            approvedPolicyNumbers.add(policy.getPolicyNumber());

            // ACTION
            ClaimActionEntity action = new ClaimActionEntity();
            action.setClaimId(claim.getId());
            action.setPolicyNumber(policy.getPolicyNumber());
            action.setActionType(ClaimActionEntity.ActionType.Approved);
            action.setRemarks(dto.getRemarks());
            action.setActionedBy(dto.getActionedBy());
            action.setActionedAt(LocalDateTime.now());
            claimActionsRepo.save(action);

            // AUDIT
            ClaimAuditEntity audit = new ClaimAuditEntity();
            audit.setClaimId(claim.getId());
            audit.setCin(claim.getCin());
            audit.setPolicyNumber(policy.getPolicyNumber());
            audit.setPreviousStatus("Pending");
            audit.setNewStatus("Approved");
            audit.setRemarks(dto.getRemarks());
            audit.setActionedBy(dto.getActionedBy());
            audit.setActionedAt(LocalDateTime.now());
            claimAuditRepo.save(audit);
        }

        policyRepo.saveAll(selectedPolicies);

        claim.setStatus("Pending");
        claim.setRemarks(dto.getRemarks());
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        // ================= SMS + EMAIL =================
        try {

            String policyList = String.join(", ", approvedPolicyNumbers);

            // SMS
            if (claimant.getMobileNumber() != null && !claimant.getMobileNumber().isBlank()) {

                String smsMessage =
                        "Your life insurance claim with CIN: " + claim.getCin() +
                                " for the policy no. " + policyList +
                                " has been approved and the benefit amount will be deposited into the account of nominee(s).";

                if (claimant.getMobileNumber().startsWith("17")) {
                    apiService.sendSms(smsMessage, claimant.getMobileNumber());
                } else if (claimant.getMobileNumber().startsWith("77")) {
                    apiService.sendSmsTcell(smsMessage, claimant.getMobileNumber());
                }
            }

            // EMAIL WITH ATTACHMENT
            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {

                String subject = "Claim Approval Notification";

                String body =
                        "Dear " + claimant.getFullName() + ",\n\n"
                                + "We are pleased to inform you that your claim [" + claim.getCin() + "] has been reviewed and approved for the policy " + policyList + ". The payment will be processed shortly and credited to the bank account of nominee(s) as per our internal procedures.\n\n"
                                + "Should you require any further clarifications, please feel free to contact us at our toll-free number 1818 during office hours or drop a mail to contactus@ricb.bt.\n\n"
                                + "Best regards,\n"
                                + "RICB";

                emailService.sendEmail(
                        claimant.getEmailAddress(),
                        subject,
                        body,
                        file // 👈 attachment
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // ================= Policy Details =================
        List<PolicyEntity> policies =
                policyRepo.findByPolicyHolderId(claim.getPolicyHolderId());

        List<Map<String, Object>> policyList = new ArrayList<>();

        for (PolicyEntity policy : policies) {
            Map<String, Object> p = new HashMap<>();
            p.put("policyNumber", policy.getPolicyNumber());
            p.put("claimStatus", policy.getClaimStatus());
            p.put("remarks", policy.getRemarks());

            policyList.add(p);
        }

        response.put("policies", policyList);

        // ================= Audit History =================
        List<Map<String, Object>> auditHistory = new ArrayList<>();
        List<ClaimAuditEntity> audits =
                claimAuditRepository.findByCinOrderByActionedAtDesc(cin);

        for (ClaimAuditEntity audit : audits) {
            Map<String, Object> auditMap = new HashMap<>();
            auditMap.put("actionedAt", audit.getActionedAt());
            auditMap.put("policyNumber", audit.getPolicyNumber());
            auditMap.put("newStatus", audit.getNewStatus());
            auditMap.put("remarks", audit.getRemarks()); // added here
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

        List<BranchDTO> list = new ArrayList<>();

        String sql = "SELECT branch_code, Branch_name " +
                "FROM RICB_COM.TL_IN_MAS_BRANCH " +
                "WHERE status_code = 'A' " +
                "ORDER BY branch_code";

        try (Connection conn = ConnectionManager.getLifeConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                BranchDTO dto = new BranchDTO();
                dto.setBranchCode(rs.getString("branch_code"));
                dto.setBranchName(rs.getString("Branch_name"));

                list.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching branches: " + e.getMessage(), e);
        }

        return list;
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

    // validateZipFile
    private void validateZipFile(MultipartFile file) {

        // ================= BASIC FILE VALIDATION =================
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        // ✅ File size check (20MB)
        long maxSizeBytes = 20L * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new RuntimeException("File size exceeds 20 MB");
        }

        // ================= FILE NAME VALIDATION =================
        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
            throw new RuntimeException("Invalid file extension. Only ZIP files are allowed.");
        }

        // ================= MIME TYPE VALIDATION =================
        String contentType = file.getContentType();

        if (contentType == null ||
                (!contentType.equalsIgnoreCase("application/zip") &&
                        !contentType.equalsIgnoreCase("application/x-zip-compressed"))) {

            throw new RuntimeException("Invalid file type. Only ZIP files are allowed.");
        }

        // ================= ZIP CONTENT VALIDATION =================
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {

            ZipEntry entry;
            int fileCount = 0;
            long totalSize = 0;

            List<String> allowedExtensions = Arrays.asList(
                    ".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx"
            );

            while ((entry = zis.getNextEntry()) != null) {

                String entryName = entry.getName().toLowerCase();

                // ❌ Prevent directory traversal attack
                if (entryName.contains("..") || entryName.startsWith("/")) {
                    throw new RuntimeException("Invalid file path inside ZIP");
                }

                // Skip directories
                if (entry.isDirectory()) {
                    continue;
                }

                fileCount++;

                // ❌ Limit number of files
                if (fileCount > 20) {
                    throw new RuntimeException("Too many files inside ZIP (max 20 allowed)");
                }

                // ❌ Validate file extension inside ZIP
                boolean valid = allowedExtensions.stream()
                        .anyMatch(entryName::endsWith);

                if (!valid) {
                    throw new RuntimeException("Invalid file type inside ZIP: " + entryName);
                }

                // ❌ Block nested ZIP files
                if (entryName.endsWith(".zip")) {
                    throw new RuntimeException("Nested ZIP files are not allowed");
                }

                // ❌ Prevent ZIP bomb attack
                byte[] buffer = new byte[4096];
                int bytesRead;
                long entrySize = 0;

                while ((bytesRead = zis.read(buffer)) != -1) {
                    entrySize += bytesRead;
                    totalSize += bytesRead;

                    // Max 10MB per file
                    if (entrySize > 10 * 1024 * 1024) {
                        throw new RuntimeException("File inside ZIP too large");
                    }

                    // Max 100MB total extracted content
                    if (totalSize > 100 * 1024 * 1024) {
                        throw new RuntimeException("ZIP content too large");
                    }
                }
            }

            if (fileCount == 0) {
                throw new RuntimeException("ZIP file is empty");
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid ZIP file: " + e.getMessage(), e);
        }
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

        // ✅ ADD THIS LINE ONLY
        validateZipFile(file);

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