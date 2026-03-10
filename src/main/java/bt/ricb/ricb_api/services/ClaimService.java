package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.models.*;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    public Map<String, Long> getClaimStatusCounts() {
        Map<String, Long> counts = new HashMap<>();

        counts.put("totalClaims", claimRepo.count());
        counts.put("pending", claimRepo.countByStatus("Pending"));
        counts.put("approved", claimRepo.countByStatus("Approved"));
        counts.put("rejected", claimRepo.countByStatus("Rejected"));
        counts.put("resubmission", claimRepo.countByStatus("Resubmission Required"));

        return counts;
    }



    // ================= CIN Generator =================
    private String generateCin() {

        // Get the current year
        int currentYear = java.time.LocalDate.now().getYear();

        // Get the last CIN from DB that starts with the current year
        String lastCin = claimRepo.getLastCinByYear(currentYear);

        int serialNumber = 1; // default if no CIN exists for this year

        if (lastCin != null) {
            // lastCin is like "CIN-20260028"
            String lastNumberStr = lastCin.substring(8); // get "0028"
            serialNumber = Integer.parseInt(lastNumberStr) + 1;
        }

        return String.format("CIN-%d%04d", currentYear, serialNumber);
    }

//    private String generateCin() {
//
//        String lastCin = claimRepo.getLastCin();
//
//        if (lastCin == null) {
//            return "CIN-00000001";
//        }
//
//        int number = Integer.parseInt(lastCin.split("-")[1]);
//        number++;
//
//        return String.format("CIN-%08d", number);
//    }

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
        PolicyDTO policyDTO = dto.getPolicy();

        if (policyDTO != null) {
            PolicyEntity policy = new PolicyEntity();
            policy.setPolicyHolderId(policyHolder.getId());
            policy.setPolicyName(policyDTO.getPolicyName());
            policy.setPolicyNumber(policyDTO.getPolicyNumber());
            policy.setIntimationDate(policyDTO.getIntimationDate());
            policy.setNomineeName(policyDTO.getNomineeName());
            policy.setRelation(policyDTO.getRelation());
            policy.setSumAssured(policyDTO.getSumAssured());
            policy.setStatus(policyDTO.getStatus());
            policy.setCreatedAt(java.time.LocalDateTime.now());

            policyRepo.save(policy);
        }


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

        claim.setStatus("Draft");

        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepo.save(claim);


        // ================= Documents =================
        ClaimDocumentsDTO docDTO = dto.getDocuments();

        if (docDTO != null) {

            ClaimDocumentsEntity doc = new ClaimDocumentsEntity();

            doc.setClaimId(claim.getId());
            doc.setZipFilePath(docDTO.getZipFilePath());

            // FIX Long → Integer
            if (docDTO.getFileSizeKb() != null) {
                doc.setFileSizeKb(docDTO.getFileSizeKb().intValue());
            }

            doc.setUploadedAt(LocalDateTime.now());

            claimDocumentsRepo.save(doc);
        }
    }


    // ================= Get All Claims =================
    public List<ClaimEntity> getAllClaims() {
        return claimRepo.findAll();
    }


    // ================= Get Claims by CID =================
    public List<ClaimEntity> getClaimsByCid(String cid) {

        Optional<ClaimantEntity> claimant = claimantRepo.findByCid(cid);

        if (claimant.isPresent()) {
            return claimRepo.findByClaimantId(claimant.get().getId());
        }

        return List.of();
    }


    // ================= Get Claim by CIN =================
    public Optional<ClaimEntity> getClaimByCin(String cin) {
        return claimRepo.findByCin(cin);
    }


    // ========== 1. Claim Summary ==========
    public ClaimSummaryDTO getClaimSummaryByCin(String cin) {
        ClaimEntity claim = claimRepo.findByCin(cin)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        ClaimantEntity claimant = claimantRepo.findById(claim.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        PolicyHolderEntity policyHolder = policyHolderRepo.findById(claim.getPolicyHolderId())
                .orElseThrow(() -> new RuntimeException("Policy holder not found"));

        ClaimSummaryDTO summary = new ClaimSummaryDTO();
        summary.setCin(claim.getCin());
        summary.setClaimantName(claimant.getFullName());
        summary.setPolicyHolderName(policyHolder.getCid()); // can use full name if available
        summary.setSubmittedDate(claim.getUpdatedAt());
        summary.setStatus(claim.getStatus());
        summary.setCreatedAt(claim.getCreatedAt());

        return summary;
    }

    // ========== 2. Full Claim Details ==========
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

        // ===== Claimant DTO =====
        ClaimantDTO claimantDTO = new ClaimantDTO();
        claimantDTO.setCid(claimant.getCid());
        claimantDTO.setFullName(claimant.getFullName());
        claimantDTO.setMobileNumber(claimant.getMobileNumber());
        claimantDTO.setEmailAddress(claimant.getEmailAddress());

        // ===== Payee DTO =====
        PayeeDTO payeeDTO = new PayeeDTO();
        payeeDTO.setCid(payee.getCid());
        payeeDTO.setAccountHolderName(payee.getAccountHolderName());
        payeeDTO.setAccountNumber(payee.getAccountNumber());
        payeeDTO.setMobileNumber(payee.getMobileNumber());
        payeeDTO.setSameAsClaimant(payee.getSameAsClaimant());

        // ===== Policy Holder DTO =====
        PolicyHolderDTO phDTO = new PolicyHolderDTO();
        phDTO.setCid(policyHolder.getCid());
        phDTO.setDateOfBirth(policyHolder.getDateOfBirth());

        // ===== Claim DTO =====
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setNearestBranchName(claimRepo.getBranchNameById(claim.getNearestBranchId()));
        claimDTO.setClaimType(claim.getClaimType());
        claimDTO.setG2cApplicationNumber(claim.getG2cApplicationNumber());
        claimDTO.setDateOfDeath(claim.getDateOfDeath());
        claimDTO.setPlaceOfDeath(claim.getPlaceOfDeath());
        claimDTO.setDeathType(claim.getDeathType());
        claimDTO.setCauseOfDeath(claim.getCauseOfDeath());

        // ===== Documents DTO =====
        ClaimDocumentsDTO docDTO = null;
        if (documents != null) {
            docDTO = new ClaimDocumentsDTO();
            docDTO.setZipFilePath(documents.getZipFilePath());
            docDTO.setFileSizeKb(documents.getFileSizeKb());
        }

        // ===== Full Response =====
        FullClaimDTO response = new FullClaimDTO();
        response.setClaimant(claimantDTO);
        response.setPayee(payeeDTO);
        response.setPolicyHolder(phDTO);
        response.setClaim(claimDTO);
        response.setDocuments(docDTO);

        return response;
    }

}