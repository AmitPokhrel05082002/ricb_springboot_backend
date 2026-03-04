package bt.ricb.ricb_api.services;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.PolicyRHIEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Service
public class RHIService {
	
	@Autowired
    private EntityManager entityManager;
	
	public static void insertRHIDetails(PolicyRHIEntity policy) {
	    Connection conn = null;
	    PreparedStatement pst = null;

	    try {
	        conn = ConnectionManager.getOracleConnection(); // Oracle DB connection

	        String sql = "INSERT INTO RICB_GI.MOBAPP_RHI_NEW_POLICY@RICB_COM (" +
	            "POL_SERIAL_NO, ID, REQUEST_ID, TRANSACTION_ID, TRANSACTION_DATE, " +
	            "REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, " +
	            "DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, " +
	            "REMARKS, REMMITER_ACCOUNT_NO, PROCESS_CORE, ERR_LOG, " +
	            "UNDERWRITING_YEAR, POLICY_START_DATE, POLICY_END_DATE, DZONG_CODE, " +
	            "GEWOG_CODE, VILLAGE, CUSTOMER_CID, CUSTOMER_NAME, PREVIOUS_POLICY_NO, " +
	            "HOUSEHOLD_NO, HOUSE_CATEGORY, SUM_INSURED, FAMILY_PREM, " +
	            "SUBSIDY_PREM, TOTAL_PREM, COLLECTED_PREMIUM, COLLECTION_DATE, STATUS_CODE) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	        pst = conn.prepareStatement(sql);
	        System.out.println("Expected parameters: " + pst.getParameterMetaData().getParameterCount());

	        pst.setLong(1, policy.getPolSerialNo());
	        pst.setLong(2, policy.getId());
	        pst.setLong(3, policy.getRequestId());
	        pst.setString(4, policy.getTransactionId());
	        pst.setDate(5, new java.sql.Date(policy.getTransactionDate().getTime()));
	        pst.setString(6, policy.getRemitterName());
	        pst.setString(7, policy.getRemitterBank());
	        pst.setString(8, policy.getRemitterMobileNo());
	        pst.setString(9, policy.getTransactionStatus());
	        pst.setString(10, policy.getDataSource());
	        pst.setString(11, policy.getAuthCode());
	        pst.setString(12, policy.getAuthId());
	        pst.setString(13, policy.getResponseCode());
	        pst.setDate(14, new java.sql.Date(policy.getUpdatedDate().getTime()));
	        pst.setString(15, policy.getRemarks());
	        pst.setString(16, policy.getRemitterAccountNo());
	        pst.setString(17, policy.getProcessCore());
	        pst.setString(18, policy.getErrLog());
	        pst.setString(19, policy.getUnderwritingYear());
	        pst.setDate(20, new java.sql.Date(policy.getPolicyStartDate().getTime()));
	        pst.setDate(21, new java.sql.Date(policy.getPolicyEndDate().getTime()));
	        pst.setString(22, policy.getDzongCode());
	        pst.setString(23, policy.getGewogCode());
	        pst.setString(24, policy.getVillage());
	        pst.setString(25, policy.getCustomerCid());
	        pst.setString(26, policy.getCustomerName());
	        pst.setString(27, policy.getPreviousPolicyNo());
	        pst.setString(28, policy.getHouseholdNo());
	        pst.setString(29, policy.getHouseCategory());
	        pst.setBigDecimal(30, policy.getSumInsured());
	        pst.setBigDecimal(31, policy.getFamilyPrem());
	        pst.setBigDecimal(32, policy.getSubsidyPrem());
	        pst.setBigDecimal(33, policy.getTotalPrem());
	        pst.setBigDecimal(34, policy.getCollectedPremium());
	        pst.setDate(35, new java.sql.Date(policy.getCollectionDate().getTime()));
	        pst.setString(36, policy.getStatusCode());

	        pst.executeUpdate();

	    } catch (Exception e) {
	        System.err.println("Failed to replicate policy to external database: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        ConnectionManager.close(conn, null, pst);
	    }
	}

}
