package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.Nyekor;
import bt.ricb.ricb_api.models.NyekorDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import bt.ricb.ricb_api.config.ConnectionManager;
import java.sql.Connection;

import java.sql.*;
import java.util.List;

@Repository
public class NyekorRepository {

	@Transactional
    public Long createPolicy(Nyekor policyRequest) throws Exception {
        Connection conn = null;
        PreparedStatement pst = null;
        
        try {
            // Get Oracle connection
            conn = ConnectionManager.getOracleConnection();
            
            // First get the next sequence value if needed
            Long polSerialNo = policyRequest.getPolSerialNo();
            if (polSerialNo == null) {
                polSerialNo = getNextSequenceValue(conn);
            } else {
            }
            
            // Insert policy details if they exist
            if (policyRequest.getPolicyDetails() != null && !policyRequest.getPolicyDetails().isEmpty()) {
                createPolicyDetails(conn, polSerialNo, policyRequest.getPolicyDetails());
            }
            
            // Main policy insert SQL
            String policySql = "INSERT INTO RICB_GI.MOBAPP_NEYKOR_NEW_POLICY@RICB_COM ("
                + "POL_SERIAL_NO, ID, REQUEST_ID, TRANSACTION_ID, TRANSACTION_DATE, "
                + "REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, "
                + "DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, "
                + "REMARKS, REMMITER_ACCOUNT_NO, PROCESS_CORE, ERR_LOG, "
                + "UNDERWRITING_YEAR, CUSTOMER_CODE, CUSTOMER_NAME, CUSTOMER_TYPE, "
                + "AGENT_CODE, JOURNEY_START_DATE, JOURNEY_END_DATE, NUMBER_OF_DAYS, "
                + "PLACE_FROM, PLACE_TO, CARRIER_TYPE, CARRIER_REGN_NO, "
                + "NUMBER_OF_PASSENGER, SUM_INSURED_PER_PERSON, PREMIUM_PER_PERSON, TOTAL_SUM_INSURED, TOTAL_PREMIUM) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            // Prepare statement without expecting generated keys
            pst = conn.prepareStatement(policySql);
            
            // Set parameters
            pst.setLong(1, polSerialNo);
            pst.setLong(2, policyRequest.getId());
            pst.setLong(3, policyRequest.getRequestId());
            pst.setString(4, policyRequest.getTransactionId());
            pst.setTimestamp(5, new Timestamp(policyRequest.getTransactionDate().getTime()));
            pst.setString(6, policyRequest.getRemitterName());
            pst.setString(7, policyRequest.getRemitterBank());
            pst.setString(8, policyRequest.getRemitterMobileNo());
            pst.setString(9, policyRequest.getTransactionStatus());
            pst.setString(10, policyRequest.getDataSource());
            pst.setString(11, policyRequest.getAuthCode());
            pst.setString(12, policyRequest.getAuthId());
            pst.setString(13, policyRequest.getResponseCode());
            pst.setTimestamp(14, new Timestamp(policyRequest.getUpdatedDate().getTime()));
            pst.setString(15, policyRequest.getRemarks());
            pst.setString(16, policyRequest.getRemitterAccountNo());
            pst.setString(17, policyRequest.getProcessCore());
            pst.setString(18, policyRequest.getErrLog());
            pst.setInt(19, policyRequest.getUnderwritingYear());
            pst.setString(20, policyRequest.getCustomerCode());
            pst.setString(21, policyRequest.getCustomerName());
            pst.setString(22, policyRequest.getCustomerType());
            pst.setString(23, policyRequest.getAgentCode());
            pst.setTimestamp(24, new Timestamp(policyRequest.getJourneyStartDate().getTime()));
            pst.setTimestamp(25, new Timestamp(policyRequest.getJourneyEndDate().getTime()));
            pst.setInt(26, policyRequest.getNumberOfDays());
            pst.setString(27, policyRequest.getPlaceFrom());
            pst.setString(28, policyRequest.getPlaceTo());
            pst.setString(29, policyRequest.getCarrierType());
            pst.setString(30, policyRequest.getCarrierRegnNo());
            pst.setInt(31, policyRequest.getNumberOfPassenger());
            pst.setDouble(32, policyRequest.getSumInsuredPerPerson());
            pst.setDouble(33, policyRequest.getPremiumPerPerson());
            pst.setDouble(34, policyRequest.getTotalSumInsured());
            pst.setDouble(35, policyRequest.getTotalPremium());
            
            // Execute update
            int affectedRows = pst.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating policy failed, no rows affected.");
            }
            
            
           
            
            return polSerialNo;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating policy", e);
        } finally {
            // Close resources in finally block
            closeResources(pst, conn);
        }
    }

    private Long getNextSequenceValue(Connection conn) throws SQLException {
        String sequenceQuery = "SELECT RICB_GI.POLICY_SEQ.NEXTVAL FROM DUAL";
        try (PreparedStatement seqStmt = conn.prepareStatement(sequenceQuery);
             ResultSet rs = seqStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Unable to get sequence value");
        }
    }

    private void createPolicyDetails(Connection conn, Long polSerialNo, List<NyekorDetails> details) throws SQLException {
        if (details == null || details.isEmpty()) {
            return;
        }

        String detailSql = "INSERT INTO RICB_GI.MOBAPP_NEYKOR_NEW_POLICY_DTL@RICB_COM ("
            + "POL_SERIAL_NO, ASSURED_NAME, CITIZEN_ID, DATE_OF_BIRTH, GENDER, "
            + "PHONE_NO, EMERGENCY_PHONE, ADDRESS, PASSPORT_NO, NOMINEE_NAME, "
            + "RELATION, NOMINEE_CID, SUM_INSURED, PREMIUM_AMOUNT) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = conn.prepareStatement(detailSql)) {
            for (NyekorDetails detail : details) {
                pst.setLong(1, polSerialNo);
                pst.setString(2, detail.getAssuredName());
                pst.setString(3, detail.getCitizenId());
                pst.setTimestamp(4, detail.getDateOfBirth() != null ? 
                    new Timestamp(detail.getDateOfBirth().getTime()) : null);
                pst.setString(5, detail.getGender());
                pst.setString(6, detail.getPhoneNo());
                pst.setString(7, detail.getEmergencyPhone());
                pst.setString(8, detail.getAddress());
                pst.setString(9, detail.getPassportNo());
                pst.setString(10, detail.getNomineeName());
                pst.setString(11, detail.getRelation());
                pst.setString(12, detail.getNomineeCid());
                pst.setDouble(13, detail.getSumInsured());
                pst.setDouble(14, detail.getPremiumAmount());
                
                pst.addBatch();
            }
            
            int[] results = pst.executeBatch();
         
        } catch (SQLException e) {
            
            throw e;
        }
    }

    private void closeResources(Statement stmt, Connection conn) {
        try { 
            if (stmt != null) stmt.close(); 
        } catch (SQLException e) { 
        }
        try { 
            if (conn != null) conn.close(); 
        } catch (SQLException e) { 
        }
    }
}