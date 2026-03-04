package bt.ricb.ricb_api.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.RliNewPolicy;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RliNewPolicyController {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/rli-policies")
    @Transactional
    public ResponseEntity<?> createPolicy(@Valid @RequestBody RliNewPolicy policy) {
        try {
            // Set audit fields
            policy.setUpdatedDate(LocalDateTime.now());
            System.out.println("Received Policy: " + policy);

            // Save in Postgres
            entityManager.persist(policy);
            entityManager.flush();

            // Replicate in Oracle
            replicatePolicyToOracleDatabase(policy);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Policy created successfully");
            response.put("serialNo", policy.getSerialNo());
            response.put("data", policy);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating policy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{serialNo}")
    public ResponseEntity<?> getPolicyBySerialNo(@PathVariable Integer serialNo) {
        try {
            RliNewPolicy policy = entityManager.find(RliNewPolicy.class, serialNo);

            if (policy == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Policy not found with serial number: " + serialNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", policy);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving policy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<?> getPolicyByTransactionId(@PathVariable String transactionId) {
        try {
            RliNewPolicy policy = entityManager
                    .createQuery("SELECT p FROM RliNewPolicy p WHERE p.transactionId = :transactionId", RliNewPolicy.class)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", policy);

            return ResponseEntity.ok(response);

        } catch (jakarta.persistence.NoResultException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Policy not found with transaction ID: " + transactionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving policy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{serialNo}")
    @Transactional
    public ResponseEntity<?> updatePolicy(@PathVariable Integer serialNo, @Valid @RequestBody RliNewPolicy updatedPolicy) {
        try {
            RliNewPolicy existingPolicy = entityManager.find(RliNewPolicy.class, serialNo);

            if (existingPolicy == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Policy not found with serial number: " + serialNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update fields
            existingPolicy.setId(updatedPolicy.getId());
            existingPolicy.setRequestId(updatedPolicy.getRequestId());
            existingPolicy.setTransactionId(updatedPolicy.getTransactionId());
            existingPolicy.setTransactionDate(updatedPolicy.getTransactionDate());
            existingPolicy.setRemitterName(updatedPolicy.getRemitterName());
            existingPolicy.setRemitterBank(updatedPolicy.getRemitterBank());
            existingPolicy.setRemitterMobileNo(updatedPolicy.getRemitterMobileNo());
            existingPolicy.setTransactionStatus(updatedPolicy.getTransactionStatus());
            existingPolicy.setDataSource(updatedPolicy.getDataSource());
            existingPolicy.setAuthCode(updatedPolicy.getAuthCode());
            existingPolicy.setAuthId(updatedPolicy.getAuthId());
            existingPolicy.setResponseCode(updatedPolicy.getResponseCode());
            existingPolicy.setUpdatedDate(LocalDateTime.now());
            existingPolicy.setRemarks(updatedPolicy.getRemarks());
            existingPolicy.setRemmiterAccountNo(updatedPolicy.getRemmiterAccountNo());
            existingPolicy.setProcessCore(updatedPolicy.getProcessCore());
            existingPolicy.setErrLog(updatedPolicy.getErrLog());
            existingPolicy.setUnderwritingYear(updatedPolicy.getUnderwritingYear());
            existingPolicy.setHouseholdNo(updatedPolicy.getHouseholdNo());
            existingPolicy.setCustomerCid(updatedPolicy.getCustomerCid());
            existingPolicy.setNoOfEligibleMembers(updatedPolicy.getnoOfEligibleMembers());
            existingPolicy.setSubsidyInd(updatedPolicy.getSubsidyInd());
            existingPolicy.setContactPersonName(updatedPolicy.getContactPersonName());
            existingPolicy.setContactPersonNo(updatedPolicy.getContactPersonNo());

            entityManager.flush();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Policy updated successfully");
            response.put("data", existingPolicy);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating policy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{serialNo}")
    @Transactional
    public ResponseEntity<?> deletePolicy(@PathVariable Integer serialNo) {
        try {
            RliNewPolicy policy = entityManager.find(RliNewPolicy.class, serialNo);

            if (policy == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Policy not found with serial number: " + serialNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            entityManager.remove(policy);
            entityManager.flush();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Policy deleted successfully");
            response.put("serialNo", serialNo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting policy: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private void replicatePolicyToOracleDatabase(RliNewPolicy policy) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = ConnectionManager.getOracleConnection();

            String sql = "INSERT INTO RICB_LI.TL_LI_TR_RURAL_2026_TRN@RICB_COM "
                    + "(SERIAL_NO, ID, REQUEST_ID, CUSTOMER_CID, BRANCH_CODE, PRESENT_HOUSEHOLD_NO, "
                    + "NO_OF_MEMBERS, AMOUNT_PAID, UNDERWRITING_YEAR, SUBSIDY_TAKEN, TRANSACTION_ID, "
                    + "TRANSACTION_DATE, REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, "
                    + "DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, REMARKS, REMMITER_ACCOUNT_NO, "
                    + "PROCESS_CORE, ERR_LOG, CONTACT_PERSON_NAME, CONTACT_PERSON_NO) "
                    + "VALUES (?, ?, ?, ?,'B001', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pst = conn.prepareStatement(sql);

            DateTimeFormatter transactionDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter updatedDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            pst.setObject(1, policy.getSerialNo());
            pst.setObject(2, parseNumeric(policy.getId()));
            pst.setObject(3, parseNumeric(policy.getRequestId()));
            pst.setString(4, policy.getCustomerCid());
            pst.setString(5, policy.getHouseholdNo());
            pst.setString(6, policy.getnoOfEligibleMembers());
            pst.setBigDecimal(7, policy.getTotalPremiumAmount());
            pst.setString(8, policy.getUnderwritingYear());
            pst.setString(9, policy.getSubsidyInd());
            pst.setString(10, policy.getTransactionId());
            pst.setString(11, policy.getTransactionDate() != null ? policy.getTransactionDate().format(transactionDateFormatter) : null);
            pst.setString(12, policy.getRemitterName());
            pst.setString(13, policy.getRemitterBank());
            pst.setString(14, policy.getRemitterMobileNo());
            pst.setString(15, policy.getTransactionStatus());
            pst.setString(16, policy.getDataSource());
            pst.setString(17, policy.getAuthCode());
            pst.setString(18, policy.getAuthId());
            pst.setString(19, policy.getResponseCode());
            pst.setString(20, policy.getUpdatedDate() != null ? policy.getUpdatedDate().format(updatedDateFormatter) : null);
            pst.setString(21, policy.getRemarks());
            pst.setString(22, policy.getRemmiterAccountNo());
            pst.setString(23, policy.getProcessCore());
            pst.setString(24, policy.getErrLog());
            pst.setString(25, policy.getContactPersonName());
            pst.setString(26, policy.getContactPersonNo());

            int rowsAffected = pst.executeUpdate();
            System.out.println("Successfully inserted " + rowsAffected + " row(s) into Oracle database");

        } catch (Exception e) {
            System.err.println("Failed to replicate RLI policy to Oracle database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, null, pst);
        }
    }

    private Long parseNumeric(String value) {
        if (value == null) return null;
        String numeric = value.replaceAll("[^0-9]", "");
        return numeric.isEmpty() ? Math.abs(value.hashCode()) : Long.parseLong(numeric);
    }
}
