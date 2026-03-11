package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.models.*;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private EmailService emailService;

    @Autowired
    private ApiService apiService;

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
    public void submitClaim(FullClaimDTO dto) {

        // ================= Claimant =================
        ClaimantDTO claimantDTO = dto.getClaimant();

        ClaimantEntity claimant = claimantRepo
                .findByCid(claimantDTO.getCid())
                .orElse(new ClaimantEntity());

        claimant.setCid(claimantDTO.getCid());
        claimant.setFullName(claimantDTO.getFullName());
        claimant.setMobileNumber(claimantDTO.getMobileNumber());
        claimant.setEmailAddress(claimantDTO.getEmailAddress());

        claimant.setDzongkhagId(
                claimantRepo.getDzongkhagIdByName(claimantDTO.getDzongkhagName())
        );

        claimant.setGewogId(
                claimantRepo.getGewogIdByName(claimantDTO.getGewogName())
        );

        claimant.setVillageId(
                claimantRepo.getVillageIdByName(claimantDTO.getVillageName())
        );

        claimant.setUpdatedAt(LocalDateTime.now());

        if (claimant.getId() == null) {
            claimant.setCreatedAt(LocalDateTime.now());
        }

        claimantRepo.save(claimant);


        // ================= Policy Holder =================
        PolicyHolderDTO phDTO = dto.getPolicyHolder();

        PolicyHolderEntity policyHolder = policyHolderRepo
                .findByCid(phDTO.getCid())
                .orElse(new PolicyHolderEntity());

        policyHolder.setCid(phDTO.getCid());
        policyHolder.setDateOfBirth(phDTO.getDateOfBirth());
        policyHolder.setIsValidated(1);
        policyHolder.setUpdatedAt(LocalDateTime.now());

        if (policyHolder.getId() == null) {
            policyHolder.setCreatedAt(LocalDateTime.now());
        }

        policyHolderRepo.save(policyHolder);


        // ================= Policy =================
//        PolicyDTO policyDTO = dto.getPolicy();
//
//        if (policyDTO != null) {
//
//            PolicyEntity policy = new PolicyEntity();
//
//            policy.setPolicyHolderId(policyHolder.getId());
//            policy.setPolicyName(policyDTO.getPolicyName());
//            policy.setPolicyNumber(policyDTO.getPolicyNumber());
//            policy.setIntimationDate(policyDTO.getIntimationDate());
//            policy.setNomineeName(policyDTO.getNomineeName());
//            policy.setRelation(policyDTO.getRelation());
//            policy.setSumAssured(policyDTO.getSumAssured());
//            policy.setStatus(policyDTO.getStatus());
//            policy.setCreatedAt(LocalDateTime.now());
//
//            policyRepo.save(policy);
//        }


        // ================= Payee =================
        PayeeDTO payeeDTO = dto.getPayee();

        PayeeEntity payee = payeeRepo
                .findByCid(payeeDTO.getCid())
                .orElse(new PayeeEntity());

        payee.setClaimantId(claimant.getId());
        payee.setSameAsClaimant(payeeDTO.getSameAsClaimant());
        payee.setCid(payeeDTO.getCid());
        payee.setAccountHolderName(payeeDTO.getAccountHolderName());
        payee.setAccountNumber(payeeDTO.getAccountNumber());
        payee.setMobileNumber(payeeDTO.getMobileNumber());

        payee.setBankId(
                payeeRepo.getBankIdByName(payeeDTO.getBankName())
        );

        payee.setUpdatedAt(LocalDateTime.now());

        if (payee.getId() == null) {
            payee.setCreatedAt(LocalDateTime.now());
        }

        payeeRepo.save(payee);


        // ================= Claim =================
        ClaimDTO claimDTO = dto.getClaim();

        ClaimEntity claim = new ClaimEntity();

        claim.setCin(generateCin());
        claim.setClaimantId(claimant.getId());
        claim.setPayeeId(payee.getId());
        claim.setPolicyHolderId(policyHolder.getId());

        claim.setNearestBranchId(
                claimRepo.getBranchIdByName(claimDTO.getNearestBranchName())
        );

        claim.setClaimType(claimDTO.getClaimType());
        claim.setG2cApplicationNumber(claimDTO.getG2cApplicationNumber());

        claim.setDateOfDeath(claimDTO.getDateOfDeath());
        claim.setPlaceOfDeath(claimDTO.getPlaceOfDeath());
        claim.setDeathType(claimDTO.getDeathType());
        claim.setCauseOfDeath(claimDTO.getCauseOfDeath());

        claim.setStatus("Pending");

        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepo.save(claim);


        // ================= Documents =================
        ClaimDocumentsDTO docDTO = dto.getDocuments();

        if (docDTO != null) {

            ClaimDocumentsEntity doc = new ClaimDocumentsEntity();

            doc.setClaimId(claim.getId());
            doc.setZipFilePath(docDTO.getZipFilePath());

            if (docDTO.getFileSizeKb() != null) {
                doc.setFileSizeKb(docDTO.getFileSizeKb().intValue());
            }

            doc.setUploadedAt(LocalDateTime.now());

            claimDocumentsRepo.save(doc);
        }


        // ================= Notification =================

        String cin = claim.getCin();

        String smsMessage = "Your claim has been submitted successfully. CIN: " + cin;

        String emailSubject = "Insurance Claim Submitted";

        String emailBody =
                "Dear " + claimant.getFullName() + ",\n\n" +
                        "Your insurance claim has been submitted successfully.\n\n" +
                        "CIN Number: " + cin + "\n\n" +
                        "Please keep this CIN number for claim tracking.\n\n" +
                        "Thank you.";

        try {

            if (claimant.getMobileNumber() != null && !claimant.getMobileNumber().isBlank()) {
                apiService.sendSms(smsMessage, claimant.getMobileNumber());
            }

            if (claimant.getEmailAddress() != null && !claimant.getEmailAddress().isBlank()) {
                emailService.sendEmail(
                        claimant.getEmailAddress(),
                        emailSubject,
                        emailBody,
                        null
                );
            }

        } catch (Exception e) {

            System.out.println("Notification failed: " + e.getMessage());
        }

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
    public FullClaimDTO getFullClaimByCin(String cin) {

        ClaimEntity claim = claimRepo.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        PayeeEntity payee = payeeRepo.findById(claim.getPayeeId())
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        PolicyHolderEntity policyHolder = policyHolderRepo.findById(claim.getPolicyHolderId())
                .orElseThrow(() -> new RuntimeException("Policy holder not found"));

        ClaimDocumentsEntity documents = claimDocumentsRepo
                .findByClaimId(claim.getId())
                .orElse(null);

        ClaimantDTO claimantDTO = new ClaimantDTO();
        claimantDTO.setCid(claimant.getCid());
        claimantDTO.setFullName(claimant.getFullName());
        claimantDTO.setMobileNumber(claimant.getMobileNumber());
        claimantDTO.setEmailAddress(claimant.getEmailAddress());

        PayeeDTO payeeDTO = new PayeeDTO();
        payeeDTO.setCid(payee.getCid());
        payeeDTO.setAccountHolderName(payee.getAccountHolderName());
        payeeDTO.setAccountNumber(payee.getAccountNumber());
        payeeDTO.setMobileNumber(payee.getMobileNumber());
        payeeDTO.setSameAsClaimant(payee.getSameAsClaimant());

        PolicyHolderDTO phDTO = new PolicyHolderDTO();
        phDTO.setCid(policyHolder.getCid());
        phDTO.setDateOfBirth(policyHolder.getDateOfBirth());

        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setNearestBranchName(claimRepo.getBranchNameById(claim.getNearestBranchId()));
        claimDTO.setClaimType(claim.getClaimType());
        claimDTO.setG2cApplicationNumber(claim.getG2cApplicationNumber());
        claimDTO.setDateOfDeath(claim.getDateOfDeath());
        claimDTO.setPlaceOfDeath(claim.getPlaceOfDeath());
        claimDTO.setDeathType(claim.getDeathType());
        claimDTO.setCauseOfDeath(claim.getCauseOfDeath());

        ClaimDocumentsDTO docDTO = null;

        if (documents != null) {
            docDTO = new ClaimDocumentsDTO();
            docDTO.setZipFilePath(documents.getZipFilePath());
            docDTO.setFileSizeKb(documents.getFileSizeKb());
        }

        FullClaimDTO response = new FullClaimDTO();
        response.setClaimant(claimantDTO);
        response.setPayee(payeeDTO);
        response.setPolicyHolder(phDTO);
        response.setClaim(claimDTO);
        response.setDocuments(docDTO);

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
        action.setActionType(ClaimActionEntity.ActionType.Approved); // or add Verified to enum
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


}