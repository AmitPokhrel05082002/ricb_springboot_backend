package bt.ricb.ricb_api.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.math.BigDecimal;

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
import bt.ricb.ricb_api.models.MtpNewPolicy;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/mtp-policies")
@CrossOrigin(origins = "*")
public class MtpNewPolicyController {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @PostMapping
    @Transactional
    public ResponseEntity<?> createPolicy(@Valid @RequestBody MtpNewPolicy policy) {
        try {
            // Set auto-populated fields
            policy.setUpdatedDate(LocalDateTime.now());
            
            // Persist the entity
            entityManager.persist(policy);
            entityManager.flush();
            
            // Replicate to Oracle database
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
    
    @PostMapping("/bulk")
    @Transactional
    public ResponseEntity<?> createPoliciesBulk(@Valid @RequestBody java.util.List<MtpNewPolicy> policies) {
        try {
            int successCount = 0;
            java.util.List<Integer> serialNumbers = new java.util.ArrayList<>();
            
            for (MtpNewPolicy policy : policies) {
                // Set auto-populated fields
                policy.setUpdatedDate(LocalDateTime.now());
                
                entityManager.persist(policy);
                successCount++;
                serialNumbers.add(policy.getSerialNo());
                
                // Flush periodically for large batches
                if (successCount % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            
            entityManager.flush();
            
            // Replicate all policies to Oracle database
            for (MtpNewPolicy policy : policies) {
                replicatePolicyToOracleDatabase(policy);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk policies created successfully");
            response.put("count", successCount);
            response.put("serialNumbers", serialNumbers);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating bulk policies: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/{serialNo}")
    public ResponseEntity<?> getPolicyBySerialNo(@PathVariable Integer serialNo) {
        try {
            MtpNewPolicy policy = entityManager.find(MtpNewPolicy.class, serialNo);
            
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
            MtpNewPolicy policy = entityManager
                .createQuery("SELECT p FROM MtpNewPolicy p WHERE p.transactionId = :transactionId", MtpNewPolicy.class)
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
    public ResponseEntity<?> updatePolicy(@PathVariable Integer serialNo, @Valid @RequestBody MtpNewPolicy updatedPolicy) {
        try {
            MtpNewPolicy existingPolicy = entityManager.find(MtpNewPolicy.class, serialNo);
            
            if (existingPolicy == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Policy not found with serial number: " + serialNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Update fields (excluding serialNo)
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
            existingPolicy.setProductCode(updatedPolicy.getProductCode());
            existingPolicy.setCustomerCode(updatedPolicy.getCustomerCode());
            existingPolicy.setCustomerName(updatedPolicy.getCustomerName());
            existingPolicy.setAgentCode(updatedPolicy.getAgentCode());
            existingPolicy.setPolicyStartDate(updatedPolicy.getPolicyStartDate());
            existingPolicy.setPolicyEndDate(updatedPolicy.getPolicyEndDate());
            existingPolicy.setBasicTpCoverPremium(updatedPolicy.getBasicTpCoverPremium());
            existingPolicy.setPremiumOwnerDriver(updatedPolicy.getPremiumOwnerDriver());
            existingPolicy.setPremiumHelper(updatedPolicy.getPremiumHelper());
            existingPolicy.setPremiumPassenger(updatedPolicy.getPremiumPassenger());
            existingPolicy.setTotalPremiumAmount(updatedPolicy.getTotalPremiumAmount());
            existingPolicy.setInsuredType(updatedPolicy.getInsuredType());
            existingPolicy.setRicbEmployee(updatedPolicy.getRicbEmployee());
            existingPolicy.setQuotaHolderName(updatedPolicy.getQuotaHolderName());
            existingPolicy.setQuotaNumber(updatedPolicy.getQuotaNumber());
            existingPolicy.setQuotaDate(updatedPolicy.getQuotaDate());
            existingPolicy.setTpLiability(updatedPolicy.getTpLiability());
            existingPolicy.setImported(updatedPolicy.getImported());
            existingPolicy.setModel(updatedPolicy.getModel());
            existingPolicy.setMonthYear(updatedPolicy.getMonthYear());
            existingPolicy.setCubicCapacity(updatedPolicy.getCubicCapacity());
            existingPolicy.setEngineNo(updatedPolicy.getEngineNo());
            existingPolicy.setChassisNo(updatedPolicy.getChassisNo());
            existingPolicy.setRegistrationNo(updatedPolicy.getRegistrationNo());
            existingPolicy.setVehicleType(updatedPolicy.getVehicleType());
            existingPolicy.setFuelType(updatedPolicy.getFuelType());
            existingPolicy.setPassengerCapacity(updatedPolicy.getPassengerCapacity());
            existingPolicy.setWeight(updatedPolicy.getWeight());
            existingPolicy.setPurchaseDate(updatedPolicy.getPurchaseDate());
            existingPolicy.setRegistrationDate(updatedPolicy.getRegistrationDate());
            existingPolicy.setDriverName(updatedPolicy.getDriverName());
            existingPolicy.setDriverCid(updatedPolicy.getDriverCid());
            existingPolicy.setDriverDob(updatedPolicy.getDriverDob());
            existingPolicy.setVehicleCategory(updatedPolicy.getVehicleCategory());
            existingPolicy.setCustomerCid(updatedPolicy.getCustomerCid());
            existingPolicy.setGstPremium(updatedPolicy.getGstPremium());
            
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
            MtpNewPolicy policy = entityManager.find(MtpNewPolicy.class, serialNo);
            
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
    
    @GetMapping("/customer/{customerCode}")
    public ResponseEntity<?> getPoliciesByCustomer(@PathVariable String customerCode) {
        try {
            java.util.List<MtpNewPolicy> policies = entityManager
                .createQuery("SELECT p FROM MtpNewPolicy p WHERE p.customerCode = :customerCode ORDER BY p.transactionDate DESC", MtpNewPolicy.class)
                .setParameter("customerCode", customerCode)
                .getResultList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", policies.size());
            response.put("data", policies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving policies: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/agent/{agentCode}")
    public ResponseEntity<?> getPoliciesByAgent(@PathVariable String agentCode) {
        try {
            java.util.List<MtpNewPolicy> policies = entityManager
                .createQuery("SELECT p FROM MtpNewPolicy p WHERE p.agentCode = :agentCode ORDER BY p.transactionDate DESC", MtpNewPolicy.class)
                .setParameter("agentCode", agentCode)
                .getResultList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", policies.size());
            response.put("data", policies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving policies: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private void replicatePolicyToOracleDatabase(MtpNewPolicy policy) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = ConnectionManager.getGeneralInsuranceConnection(); // Ensure this is Oracle connection

            String sql = "INSERT INTO RICB_GI.MOBAPP_MTP_NEW_POLICY (" +
                    "SERIAL_NO, ID, REQUEST_ID, TRANSACTION_ID, TRANSACTION_DATE, " +
                    "REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, " +
                    "DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, " +
                    "REMARKS, REMMITER_ACCOUNT_NO, PROCESS_CORE, ERR_LOG, " +
                    "UNDERWRITING_YEAR, PRODUCT_CODE, CUSTOMER_CODE, CUSTOMER_NAME, " +
                    "AGENT_CODE, POLICY_START_DATE, POLICY_END_DATE, BASIC_TP_COVER_PREMIUM, " +
                    "PREMIUM_OWNER_DRIVER, PREMIUM_HELPER, PREMIUM_PASSENGER, TOTAL_PREMIUM_AMOUNT, " +
                    "INSURED_TYPE, RICB_EMPLOYEE, QUOTA_HOLDER_NAME, QUOTA_NUMBER, " +
                    "QUOTA_DATE, TP_LIABILITY, IMPORTED, MODEL, MONTH_YEAR, " +
                    "CUBIC_CAPACITY, ENGINE_NO, CHASIS_NO, REGISTRATION_NO, VEHICLE_TYPE, " +
                    "FUEL_TYPE, PASSENGER_CAPACITY, WEIGHT, PURCHASE_DATE, REGISTRATION_DATE, " +
                    "DRIVER_NAME, DRIVER_CID, DRIVER_DOB, VEHICLE_CATEGORY, CUSTOMER_CID,GST_PREMIUM) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pst = conn.prepareStatement(sql);
            System.out.println("Expected parameters: " + pst.getParameterMetaData().getParameterCount());

            // Date formatters to match Excel format
            DateTimeFormatter transactionDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter updatedDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DateTimeFormatter policyDateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy ");

            // 1. SERIAL_NO - NUMBER(10) - PK
            if (policy.getSerialNo() != null) {
                pst.setLong(1, policy.getSerialNo().longValue());
            } else {
                pst.setNull(1, java.sql.Types.NUMERIC);
            }
            
            // 2. ID - NUMBER(38) - CRITICAL: Convert String to Number
            try {
                // Try to extract numeric part from ID string (e.g., "POL1755062885186" -> 1755062885186)
                String idStr = policy.getId();
                if (idStr != null) {
                    // Remove non-numeric characters and parse
                    String numericId = idStr.replaceAll("[^0-9]", "");
                    if (!numericId.isEmpty()) {
                        pst.setLong(2, Long.parseLong(numericId));
                    } else {
                        // If no numbers found, use hash code as fallback
                        pst.setLong(2, Math.abs(idStr.hashCode()));
                    }
                } else {
                    pst.setNull(2, java.sql.Types.NUMERIC);
                }
            } catch (Exception e) {
                // Fallback: use hash code of the ID string
                pst.setLong(2, Math.abs(policy.getId().hashCode()));
            }
            
            // 3. REQUEST_ID - NUMBER(38) - CRITICAL: Convert String to Number
            try {
                String reqIdStr = policy.getRequestId();
                if (reqIdStr != null) {
                    // Remove non-numeric characters and parse
                    String numericReqId = reqIdStr.replaceAll("[^0-9]", "");
                    if (!numericReqId.isEmpty()) {
                        pst.setLong(3, Long.parseLong(numericReqId));
                    } else {
                        // If no numbers found, use hash code as fallback
                        pst.setLong(3, Math.abs(reqIdStr.hashCode()));
                    }
                } else {
                    pst.setNull(3, java.sql.Types.NUMERIC);
                }
            } catch (Exception e) {
                // Fallback: use hash code of the REQUEST_ID string
                pst.setLong(3, Math.abs(policy.getRequestId().hashCode()));
            }
            
            // 4. TRANSACTION_ID - VARCHAR2(100)
            pst.setString(4, policy.getTransactionId());
            
            // 5. TRANSACTION_DATE - VARCHAR2(50) - Format: "25/10/2024"
            if (policy.getTransactionDate() != null) {
                String formattedTransactionDate = policy.getTransactionDate().format(transactionDateFormatter);
                pst.setString(5, formattedTransactionDate);
            } else {
                pst.setString(5, null);
            }
            
            // 6-13. String fields
            pst.setString(6, policy.getRemitterName());
            pst.setString(7, policy.getRemitterBank());
            pst.setString(8, policy.getRemitterMobileNo());
            pst.setString(9, policy.getTransactionStatus());
            pst.setString(10, policy.getDataSource());
            pst.setString(11, policy.getAuthCode());
            pst.setString(12, policy.getAuthId());
            pst.setString(13, policy.getResponseCode());
            
            // 14. UPDATED_DATE - VARCHAR2(51) - Format: "25/10/2024 14:19"
            if (policy.getUpdatedDate() != null) {
                String formattedUpdatedDate = policy.getUpdatedDate().format(updatedDateFormatter);
                pst.setString(14, formattedUpdatedDate);
            } else {
                pst.setString(14, null);
            }
            
            // 15-18. String fields
            pst.setString(15, policy.getRemarks());
            pst.setString(16, policy.getRemmiterAccountNo());
            pst.setString(17, policy.getProcessCore());
            pst.setString(18, policy.getErrLog());
            
            // 19. UNDERWRITING_YEAR - NUMBER(4)
            if (policy.getUnderwritingYear() != null) {
                pst.setInt(19, policy.getUnderwritingYear());
            } else {
                pst.setNull(19, java.sql.Types.NUMERIC);
            }
            
            // 20-23. String fields
            pst.setString(20, policy.getProductCode());
            pst.setString(21, policy.getCustomerCode());
            pst.setString(22, policy.getCustomerName());
            pst.setString(23, policy.getAgentCode());
            
            // 24-25. Date fields - Format as Strings: "25/Oct/2024 "
            if (policy.getPolicyStartDate() != null) {
                String formattedStartDate = policy.getPolicyStartDate().format(policyDateFormatter);
                pst.setString(24, formattedStartDate);  // Store as VARCHAR2, not DATE
            } else {
                pst.setString(24, null);
            }
            
            if (policy.getPolicyEndDate() != null) {
                String formattedEndDate = policy.getPolicyEndDate().format(policyDateFormatter);
                pst.setString(25, formattedEndDate);  // Store as VARCHAR2, not DATE
            } else {
                pst.setString(25, null);
            }
            
            // 26-30. Premium amounts - NUMBER(20,2)
            pst.setBigDecimal(26, policy.getBasicTpCoverPremium());
            pst.setBigDecimal(27, policy.getPremiumOwnerDriver());
            pst.setBigDecimal(28, policy.getPremiumHelper());
            pst.setBigDecimal(29, policy.getPremiumPassenger());
            pst.setBigDecimal(30, policy.getTotalPremiumAmount());
            
            // 31-34. String fields
            pst.setString(31, policy.getInsuredType());
            pst.setString(32, policy.getRicbEmployee());
            pst.setString(33, policy.getQuotaHolderName());
            pst.setString(34, policy.getQuotaNumber());
            
            // 35. QUOTA_DATE - Store as String with format: "25/Oct/2024 "
            if (policy.getQuotaDate() != null) {
                String formattedQuotaDate = policy.getQuotaDate().format(policyDateFormatter);
                pst.setString(35, formattedQuotaDate);
            } else {
                pst.setString(35, null);
            }
            
            // 36. TP_LIABILITY - NUMBER(15,2)
            pst.setBigDecimal(36, policy.getTpLiability());
            
            // 37-39. String fields
            pst.setString(37, policy.getImported());
            pst.setString(38, policy.getModel());
            pst.setString(39, policy.getMonthYear());
            
            // 40. CUBIC_CAPACITY - NUMBER(10,2) - Convert Integer to BigDecimal
            if (policy.getCubicCapacity() != null) {
                pst.setBigDecimal(40, new BigDecimal(policy.getCubicCapacity()));
            } else {
                pst.setNull(40, java.sql.Types.NUMERIC);
            }
            
            // 41-43. String fields - NOTE: Oracle uses CHASIS_NO (not CHASSIS_NO)
            pst.setString(41, policy.getEngineNo());
            pst.setString(42, policy.getChassisNo());  // Entity has chassisNo, Oracle has CHASIS_NO
            pst.setString(43, policy.getRegistrationNo());
            
            // 44. VEHICLE_TYPE - VARCHAR2(10) - Convert Integer to String
            pst.setString(44, policy.getVehicleType() != null ? String.valueOf(policy.getVehicleType()) : null);
            
            // 45. FUEL_TYPE - VARCHAR2(10)
            pst.setString(45, policy.getFuelType());
            
            // 46. PASSENGER_CAPACITY - NUMBER(5,2) - Convert Integer to BigDecimal
            if (policy.getPassengerCapacity() != null) {
                pst.setBigDecimal(46, new BigDecimal(policy.getPassengerCapacity()));
            } else {
                pst.setNull(46, java.sql.Types.NUMERIC);
            }
            
            // 47. WEIGHT - NUMBER(15,2)
            pst.setBigDecimal(47, policy.getWeight());
            
            // 48-49. Date fields - Store as Strings: "15/Oct/2024 "
            if (policy.getPurchaseDate() != null) {
                String formattedPurchaseDate = policy.getPurchaseDate().format(policyDateFormatter);
                pst.setString(48, formattedPurchaseDate);
            } else {
                pst.setString(48, null);
            }
            
            if (policy.getRegistrationDate() != null) {
                String formattedRegistrationDate = policy.getRegistrationDate().format(policyDateFormatter);
                pst.setString(49, formattedRegistrationDate);
            } else {
                pst.setString(49, null);
            }
            
            // 50-52. String and Date fields
            pst.setString(50, policy.getDriverName());
            pst.setString(51, policy.getDriverCid());
            
            // 52. DRIVER_DOB - Store as String: "01/Jan/2000 "
            if (policy.getDriverDob() != null) {
                String formattedDriverDob = policy.getDriverDob().format(policyDateFormatter);
                pst.setString(52, formattedDriverDob);
            } else {
                pst.setString(52, null);
            }
            
            // 53. VEHICLE_CATEGORY - VARCHAR2(10) - Convert Integer to String
            pst.setString(53, policy.getVehicleCategory() != null ? String.valueOf(policy.getVehicleCategory()) : null);
            
            // 54. CUSTOMER_CID - VARCHAR2(50)
            pst.setString(54, policy.getCustomerCid());
            
         // 55. GST PREMIUM
            pst.setBigDecimal(55, policy.getGstPremium());

            int rowsAffected = pst.executeUpdate();
            System.out.println("Successfully inserted " + rowsAffected + " row(s) into Oracle database");
            
            // Debug output to verify formatting
            System.out.println("Date formatting applied:");
            System.out.println("Transaction Date: " + (policy.getTransactionDate() != null ? policy.getTransactionDate().format(transactionDateFormatter) : "null"));
            System.out.println("Updated Date: " + (policy.getUpdatedDate() != null ? policy.getUpdatedDate().format(updatedDateFormatter) : "null"));
            System.out.println("Policy Start Date: " + (policy.getPolicyStartDate() != null ? policy.getPolicyStartDate().format(policyDateFormatter) : "null"));
            System.out.println("Policy End Date: " + (policy.getPolicyEndDate() != null ? policy.getPolicyEndDate().format(policyDateFormatter) : "null"));

        } catch (Exception e) {
            System.err.println("Failed to replicate MTP policy to Oracle database: " + e.getMessage());
            e.printStackTrace();
            
            // Additional debugging information
            System.err.println("Policy details for debugging:");
            System.err.println("Serial No: " + policy.getSerialNo());
            System.err.println("ID: " + policy.getId());
            System.err.println("Request ID: " + policy.getRequestId());
            System.err.println("Transaction Date: " + policy.getTransactionDate());
            System.err.println("Policy Start Date: " + policy.getPolicyStartDate());
            System.err.println("Policy End Date: " + policy.getPolicyEndDate());
            
        } finally {
            ConnectionManager.close(conn, null, pst);
        }
    }
}