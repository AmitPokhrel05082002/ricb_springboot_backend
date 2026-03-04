package bt.ricb.ricb_api.dao.implementations;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.LifeInsuranceDao;
import bt.ricb.ricb_api.models.FamilyDetailsDto;
import bt.ricb.ricb_api.models.LifeInsuranceMainDto;
import bt.ricb.ricb_api.models.NomineeDto;
import bt.ricb.ricb_api.models.PolicyCoverDto;
import bt.ricb.ricb_api.models.PolicyDiscountLoadDTO;
import bt.ricb.ricb_api.models.PolicyDto;
import bt.ricb.ricb_api.models.PolicyPremiumDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.springframework.stereotype.Repository;

@Repository
public class LifeInsuranceImpl implements LifeInsuranceDao {
	public void lifeInsuranceMain(LifeInsuranceMainDto lifeInsuranceMainDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_MAIN@ricb_com (POLICY_SERIAL, ID, REQUEST_ID, CUSTOMER_CID, CUSTOMER_NAME, DEPARTMENT_CODE, AMOUNT_PAID, TRANSACTION_ID, TRANSACTION_DATE, REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, REMARKS, REMMITER_ACCOUNT_NO, PROCESS_CORE, ERR_LOG) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, lifeInsuranceMainDetails.getPolicySerial().longValue());
			pst.setString(2, lifeInsuranceMainDetails.getId());
			pst.setString(3, lifeInsuranceMainDetails.getRequestId());
			pst.setString(4, lifeInsuranceMainDetails.getCustomerCid());
			pst.setString(5, lifeInsuranceMainDetails.getCustomerName());
			pst.setString(6, lifeInsuranceMainDetails.getDepartmentCode());
			pst.setBigDecimal(7, lifeInsuranceMainDetails.getAmountPaid());
			pst.setString(8, lifeInsuranceMainDetails.getTransactionId());
			pst.setDate(9, lifeInsuranceMainDetails.getTransactionDate());
			pst.setString(10, lifeInsuranceMainDetails.getRemitterName());
			pst.setString(11, lifeInsuranceMainDetails.getRemitterBank());
			pst.setString(12, lifeInsuranceMainDetails.getRemitterMobileNo());
			pst.setString(13, lifeInsuranceMainDetails.getTransactionStatus());
			pst.setString(14, lifeInsuranceMainDetails.getDataSource());
			pst.setString(15, lifeInsuranceMainDetails.getAuthCode());
			pst.setString(16, lifeInsuranceMainDetails.getAuthId());
			pst.setString(17, lifeInsuranceMainDetails.getResponseCode());
			pst.setDate(18, lifeInsuranceMainDetails.getUpdatedDate());
			pst.setString(19, lifeInsuranceMainDetails.getRemarks());
			pst.setString(20, lifeInsuranceMainDetails.getRemitterAccountNo());
			pst.setString(21, lifeInsuranceMainDetails.getProcessCore());
			pst.setString(22, lifeInsuranceMainDetails.getErrLog());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save premium details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertDiscLoadDetails(PolicyDiscountLoadDTO discLoadDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_DISC_LOAD@ricb_com (POLICY_SERIAL, DISC_LOAD_CODE, DISC_LOAD_TYPE, DISC_LOAD_DESC, RATE_PER_1000_SA, RATE_PCNT_ON_PREM, DOCTOR_BMHC_NO, MEDICAL_FEES_AMT, REIMB_PROPOSER_AMT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, discLoadDetails.getPolicySerial().longValue());
			pst.setString(2, discLoadDetails.getDiscLoadCode());
			pst.setString(3, discLoadDetails.getDiscLoadType());
			pst.setString(4, discLoadDetails.getDiscLoadDesc());
			pst.setBigDecimal(5, discLoadDetails.getRatePer1000SA());
			pst.setBigDecimal(6, discLoadDetails.getRatePcntOnPrem());
			pst.setString(7, discLoadDetails.getDoctorBMHCNo());
			pst.setBigDecimal(8, discLoadDetails.getMedicalFeesAmt());
			pst.setBigDecimal(9, discLoadDetails.getReimbProposerAmt());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save discount/loading details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertCoverDetails(PolicyCoverDto coverDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_COVER@ricb_com (POLICY_SERIAL, COVER_CODE, COVER_DESC, COVER_AMOUNT, PREMIUM_RATE, PREMIUM_AMOUNT) VALUES (?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, coverDetails.getPolicySerial().longValue());
			pst.setString(2, coverDetails.getCoverCode());
			pst.setString(3, coverDetails.getCoverDesc());
			pst.setBigDecimal(4, coverDetails.getCoverAmount());
			pst.setBigDecimal(5, coverDetails.getPremiumRate());
			pst.setBigDecimal(6, coverDetails.getPremiumAmount());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save cover details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertNomineeDetails(NomineeDto nomineeDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_NOMINEE@ricb_com (POLICY_SERIAL, BENEFIT_SHARE_PERCENT, FIRST_NAME, MIDDLE_NAME, LAST_NAME, CUSTOMER_NAME, GENDER, DATE_OF_BIRTH, CITIZEN_ID, PROPOSER_RELATION, ADDRESS_1, ADDRESS_2, ADDRESS_3, GEWOG, DZONGKHAG, MOBILE_NO, APPOINTEE_NAME, APPOINTEE_ADDRESS_1, APPOINTEE_ADDRESS_2, APPOINTEE_ADDRESS_3, APPOINTEE_GEWOG, APPOINTEE_DZONGKHAG, APPOINTEE_MOBILE, APPOINTEE_EMAIL_ID, APPOINTEE_CID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, nomineeDetails.getPolicySerial().longValue());
			pst.setDouble(2, nomineeDetails.getBenefitSharePercent().doubleValue());
			pst.setString(3, nomineeDetails.getFirstName());
			pst.setString(4, nomineeDetails.getMiddleName());
			pst.setString(5, nomineeDetails.getLastName());
			pst.setString(6, nomineeDetails.getCustomerName());
			pst.setString(7, nomineeDetails.getGender());
			pst.setDate(8, nomineeDetails.getDateOfBirth());
			pst.setString(9, nomineeDetails.getCitizenId());
			pst.setString(10, nomineeDetails.getProposerRelation());
			pst.setString(11, nomineeDetails.getAddress1());
			pst.setString(12, nomineeDetails.getAddress2());
			pst.setString(13, nomineeDetails.getAddress3());
			pst.setString(14, nomineeDetails.getGewog());
			pst.setString(15, nomineeDetails.getDzongkhag());
			pst.setString(16, nomineeDetails.getMobileNo());
			pst.setString(17, nomineeDetails.getAppointeeName());
			pst.setString(18, nomineeDetails.getAppointeeAddress1());
			pst.setString(19, nomineeDetails.getAppointeeAddress2());
			pst.setString(20, nomineeDetails.getAppointeeAddress3());
			pst.setString(21, nomineeDetails.getAppointeeGewog());
			pst.setString(22, nomineeDetails.getAppointeeDzongkhag());
			pst.setString(23, nomineeDetails.getAppointeeMobile());
			pst.setString(24, nomineeDetails.getAppointeeEmailId());
			pst.setString(25, nomineeDetails.getAppointeeCid());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save nominee details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertFamilyDetails(FamilyDetailsDto familyDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_FAMILY@ricb_com (POLICY_SERIAL, FIRST_NAME, MIDDLE_NAME, LAST_NAME, CUSTOMER_NAME, GENDER, DATE_OF_BIRTH, CITIZEN_ID, PROPOSER_RELATION, ADDRESS_1, ADDRESS_2, ADDRESS_3, GEWOG, DZONGKHAG, MOBILE_NO, RELATIONSHIP, STATUS, AGE, YEAR_OF_DEATH,CAUSE_OF_DEATH) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, familyDetails.getPolicySerial().longValue());
			pst.setString(2, familyDetails.getFirstName());
			pst.setString(3, familyDetails.getMiddleName());
			pst.setString(4, familyDetails.getLastName());
			pst.setString(5, familyDetails.getCustomerName());
			pst.setString(6, familyDetails.getGender());
			pst.setDate(7, familyDetails.getDateOfBirth());
			pst.setString(8, familyDetails.getCitizenId());
			pst.setString(9, familyDetails.getProposerRelation());
			pst.setString(10, familyDetails.getAddress1());
			pst.setString(11, familyDetails.getAddress2());
			pst.setString(12, familyDetails.getAddress3());
			pst.setString(13, familyDetails.getGewog());
			pst.setString(14, familyDetails.getDzongkhag());
			pst.setString(15, familyDetails.getMobileNo());
			pst.setString(16, familyDetails.getRelationship());
			pst.setString(17, familyDetails.getStatus());
			pst.setInt(18, familyDetails.getAge().intValue());
			pst.setString(19, familyDetails.getYearOfDeath());
			pst.setString(20, familyDetails.getCauseOfDeath());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save cover details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertPremiumDetails(PolicyPremiumDto premiumDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_PREMIUM@ricb_com (POLICY_SERIAL, TABULAR_PREMIUM, STAFF_REBATE, AGENT_REBATE, PAYMENT_MODE_ADJ, SA_REBATE, ACCIDENT_EXTRA, OCCUP_HEALTH_EXTRA, PREMIUM_WAIVER, TERM_RIDER, EDUCATION_ALLOWANCE, ANNUAL_PREMIUM, PREMIUM_INSTALMENT, DISCOUNT_INSTALMENT, LOADING_INSTALMENT, ANNUAL_PREMIUM_PAYABLE, PAYABLE_INSTALMENT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, premiumDetails.getPolicySerial().longValue());
			pst.setBigDecimal(2, premiumDetails.getTabularPremium());
			pst.setBigDecimal(3, premiumDetails.getStaffRebate());
			pst.setBigDecimal(4, premiumDetails.getAgentRebate());
			pst.setBigDecimal(5, premiumDetails.getPaymentModeAdj());
			pst.setBigDecimal(6, premiumDetails.getSaRebate());
			pst.setBigDecimal(7, premiumDetails.getAccidentExtra());
			pst.setBigDecimal(8, premiumDetails.getOccupHealthExtra());
			pst.setBigDecimal(9, premiumDetails.getPremiumWaiver());
			pst.setBigDecimal(10, premiumDetails.getTermRider());
			pst.setBigDecimal(11, premiumDetails.getEducationAllowance());
			pst.setBigDecimal(12, premiumDetails.getAnnualPremium());
			pst.setBigDecimal(13, premiumDetails.getPremiumInstalment());
			pst.setBigDecimal(14, premiumDetails.getDiscountInstalment());
			pst.setBigDecimal(15, premiumDetails.getLoadingInstalment());
			pst.setBigDecimal(16, premiumDetails.getAnnualPremiumPayable());
			pst.setBigDecimal(17, premiumDetails.getPayableInstalment());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save premium details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}

	public void insertPolicyDetails(PolicyDto policyDetails) {
		String sql = "INSERT INTO ricb_li.MOBAPP_UWR_POLICY@ricb_com (    POLICY_SERIAL, BRANCH_CODE, PROPOSAL_DATE, PRODUCT_CODE, CUSTOMER_CODE, JOINT_HOLDER_CODE, POLICY_TERM,    PREMIUM_PAYING_TERM, POLICY_START_DATE, POLICY_END_DATE, SUM_ASSURED, MODE_OF_PAYMENT, ANNUAL_PREMIUM_AMOUNT,    NO_OF_INSTALMENTS, PREMIUM_PER_INSTALMENT, RICB_EMPLOYEE, RICB_AGENT, ENTRY_AGE_PROPOSER, ENTRY_AGE_JOINT_HOLDER) VALUES (    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;

		try {
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(sql);
			pst.setLong(1, policyDetails.getPolicySerial().longValue());
			pst.setString(2, policyDetails.getBranchCode());
			pst.setDate(3, policyDetails.getProposalDate());
			pst.setString(4, policyDetails.getProductCode());
			pst.setString(5, policyDetails.getCustomerCode());
			pst.setString(6, policyDetails.getJointHolderCode());
			pst.setInt(7, policyDetails.getPolicyTerm().intValue());
			pst.setInt(8, policyDetails.getPremiumPayingTerm().intValue());
			pst.setDate(9, policyDetails.getPolicyStartDate());
			pst.setDate(10, policyDetails.getPolicyEndDate());
			pst.setBigDecimal(11, policyDetails.getSumAssured());
			pst.setString(12, policyDetails.getModeOfPayment());
			pst.setBigDecimal(13, policyDetails.getAnnualPremiumAmount());
			pst.setInt(14, policyDetails.getNoOfInstalments().intValue());
			pst.setBigDecimal(15, policyDetails.getPremiumPerInstalment());
			pst.setString(16, policyDetails.getRicbEmployee());
			pst.setString(17, policyDetails.getRicbAgent());
			pst.setInt(18, policyDetails.getEntryAgeProposer().intValue());
			pst.setInt(19, policyDetails.getEntryAgeJointHolder().intValue());
			pst.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to save Policy details: " + e.getMessage(), e);
		} finally {
			ConnectionManager.close(conn, null, pst);
		}
	}
}
