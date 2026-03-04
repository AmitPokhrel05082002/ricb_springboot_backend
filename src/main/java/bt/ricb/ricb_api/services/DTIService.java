package bt.ricb.ricb_api.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import bt.ricb.ricb_api.config.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bt.ricb.ricb_api.dao.PolicyDTIDetailDto;
import bt.ricb.ricb_api.dao.PolicyDTIDto;
import bt.ricb.ricb_api.models.PolicyDTIDetailsEntity;
import bt.ricb.ricb_api.models.PolicyDTIEntity;
import bt.ricb.ricb_api.repository.PolicyDTIDetailsRepository;
import bt.ricb.ricb_api.repository.PolicyDTIRepository;

@Service
public class DTIService {
    
    @Autowired
    private PolicyDTIDetailsRepository dtiDetailsRepository;
    
    @Autowired
    private PolicyDTIRepository dtiRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Transactional
    public PolicyDTIDetailsEntity createCompletePolicy(List<PolicyDTIDetailDto> detailDtos, PolicyDTIDto policyDto) {
        // Convert and save all policy details
        PolicyDTIDetailsEntity firstSavedDetail = null;
        List<PolicyDTIDetailsEntity> savedDetails = new ArrayList<>();

        for (PolicyDTIDetailDto detailDto : detailDtos) {
            PolicyDTIDetailsEntity detail = convertToPolicyDetail(detailDto);
            PolicyDTIDetailsEntity savedDetail = dtiDetailsRepository.save(detail);
            
            savedDetails.add(savedDetail);

            // Keep reference to the first saved detail to return
            if (firstSavedDetail == null) {
                firstSavedDetail = savedDetail;
            }
        }
        
        // Convert and save the main policy
        PolicyDTIEntity policy = convertToPolicy(policyDto);
        
        // Set serial number from first detail (if needed)
        if (firstSavedDetail != null) {
            policy.setPolSerialNo(firstSavedDetail.getPolSerialNo());
        }
        
        // Calculate derived fields
        calculateDerivedFields(policy);
        
        // Save to policy table
//        dtiRepository.save(policy);
        PolicyDTIEntity savedPolicy = dtiRepository.save(policy);

     // Replicate data to external database
        for (PolicyDTIDetailsEntity detail : savedDetails) {
            replicatePolicyDetailsToExternalDatabase(detail);
        }
        
        replicatePolicyToExternalDatabase(savedPolicy);

        
        return firstSavedDetail;
    }
    
    private PolicyDTIDetailsEntity convertToPolicyDetail(PolicyDTIDetailDto dto) {
        PolicyDTIDetailsEntity detail = new PolicyDTIDetailsEntity();
        detail.setPolSerialNo(dto.getPolSerialNo());
        detail.setCouponNo(dto.getCouponNo());
        detail.setAssuredName(dto.getAssuredName());
        detail.setCitizenId(dto.getCitizenId());
        detail.setDateOfBirth(dto.getDateOfBirth());
        detail.setGender(dto.getGender());
        detail.setPhoneNo(dto.getPhoneNo());
        detail.setNomineeName(dto.getNomineeName());
        detail.setRelation(dto.getRelation());
        detail.setNomineeAddress(dto.getNomineeAddress());
        detail.setNomineeCid(dto.getNomineeCid());
        detail.setNoOfPassenger(1);
        detail.setIndividualSumInsured(dto.getIndividualSumInsured());
        detail.setPremiumAmount(dto.getPremiumAmount());
        detail.setTotalPremiumAmount(dto.getTotalPremiumAmount());
        return detail;
    }
    
    private PolicyDTIEntity convertToPolicy(PolicyDTIDto dto) {
        PolicyDTIEntity policy = new PolicyDTIEntity();
        policy.setPolSerialNo(dto.getPolSerialNo());
        policy.setId(dto.getId());
        policy.setRequestId(dto.getRequestId());
        policy.setTransactionId(dto.getTransactionId());
        policy.setTransactionDate(dto.getTransactionDate());
        policy.setRemitterName(dto.getRemitterName());
        policy.setRemitterBank(dto.getRemitterBank());
        policy.setRemitterMobileNo(dto.getRemitterMobileNo());
        policy.setTransactionStatus(dto.getTransactionStatus());
        policy.setDataSource(dto.getDataSource());
        policy.setAuthCode(dto.getAuthCode());
        policy.setAuthId(dto.getAuthId());
        policy.setResponseCode(dto.getResponseCode());
        policy.setUpdatedDate(dto.getUpdatedDate());
        policy.setRemarks(dto.getRemarks());
        policy.setRemitterAccountNo(dto.getRemitterAccountNo());
        policy.setProcessCore(dto.getProcessCore());
        policy.setErrLog(dto.getErrLog());
        policy.setUnderwritingYear(dto.getUnderwritingYear());
        policy.setCustomerCode(dto.getCustomerCode());
        policy.setCustomerName(dto.getCustomerName());
        policy.setCustomerType(dto.getCustomerType());
        policy.setAgentCode(dto.getAgentCode());
        policy.setJourneyStartDate(dto.getJourneyStartDate());
        policy.setJourneyEndDate(dto.getJourneyEndDate());
        policy.setNumberOfDays(dto.getNumberOfDays());
        policy.setPlaceFrom(dto.getPlaceFrom());
        policy.setPlaceTo(dto.getPlaceTo());
        policy.setCarrierType(dto.getCarrierType());
        policy.setCarrierRegnNo(dto.getCarrierRegnNo());
        policy.setNumberOfPassenger(dto.getNumberOfPassenger());
        policy.setSumInsuredPerPerson(dto.getSumInsuredPerPerson());
        policy.setTotalSumInsured(dto.getTotalSumInsured());
        policy.setTotalPremium(dto.getTotalPremium());
        return policy;
    }
    
    private void calculateDerivedFields(PolicyDTIEntity policy) {
        // Calculate number of days if journey dates are present
        if (policy.getJourneyStartDate() != null && policy.getJourneyEndDate() != null) {
            long diffInMillis = policy.getJourneyEndDate().getTime() - policy.getJourneyStartDate().getTime();
            long days = diffInMillis / (1000 * 60 * 60 * 24);
            policy.setNumberOfDays((int) days + 1); // Inclusive count
        }
        
//        // Calculate total sum insured if not provided
//        if (policy.getSumInsuredPerPerson() != null && policy.getNumberOfPassenger() != null) {
//        	int total = policy.getSumInsuredPerPerson()
//                .multiply(BigDecimal.valueOf(policy.getNumberOfPassenger()));
//            policy.setTotalSumInsured(total);
//        }
    }
    
    private void replicatePolicyToExternalDatabase(PolicyDTIEntity policy) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = ConnectionManager.getOracleConnection(); // Ensure this is Oracle connection

            String sql = "INSERT INTO RICB_GI.MOBAPP_DTI_NEW_POLICY@RICB_COM (" +
                "POL_SERIAL_NO, ID, REQUEST_ID, TRANSACTION_ID, TRANSACTION_DATE, " +
                "REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, " +
                "DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, UPDATED_DATE, " +
                "REMARKS, REMMITER_ACCOUNT_NO, PROCESS_CORE, ERR_LOG, " +
                "UNDERWRITING_YEAR, CUSTOMER_CODE, CUSTOMER_NAME, CUSTOMER_TYPE, " +
                "AGENT_CODE, JOURNEY_START_DATE, JOURNEY_END_DATE, NUMBER_OF_DAYS, " +
                "PLACE_FROM, PLACE_TO, CARRIER_TYPE, CARRIER_REGN_NO, " +
                "NUMBER_OF_PASSENGER, SUM_INSURED_PER_PERSON, TOTAL_SUM_INSURED, TOTAL_PREMIUM) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pst = conn.prepareStatement(sql);

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
            pst.setLong(19, policy.getUnderwritingYear());
            pst.setString(20, policy.getCustomerCode());
            pst.setString(21, policy.getCustomerName());
            pst.setString(22, policy.getCustomerType());
            pst.setString(23, policy.getAgentCode());
            pst.setDate(24, new java.sql.Date(policy.getJourneyStartDate().getTime()));
            pst.setDate(25, new java.sql.Date(policy.getJourneyEndDate().getTime()));
            pst.setInt(26, policy.getNumberOfDays());
            pst.setString(27, policy.getPlaceFrom());
            pst.setString(28, policy.getPlaceTo());
            pst.setString(29, policy.getCarrierType());
            pst.setString(30, policy.getCarrierRegnNo());
            pst.setInt(31, policy.getNumberOfPassenger());
            pst.setBigDecimal(32, policy.getSumInsuredPerPerson());
            pst.setBigDecimal(33, policy.getTotalSumInsured());
            pst.setBigDecimal(34, policy.getTotalPremium());

            pst.executeUpdate();

        } catch (Exception e) {
            System.err.println("Failed to replicate policy to external database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, null, pst);
        }
    }



    private void replicatePolicyDetailsToExternalDatabase(PolicyDTIDetailsEntity detail) {
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = ConnectionManager.getOracleConnection();

            if (conn != null) {
            	String sql = "INSERT INTO RICB_GI.MOBAPP_DTI_NEW_POLICY_DTL@ricb_com (" +
            		    "POL_SERIAL_NO, COUPON_NO, ASSURED_NAME, CITIZEN_ID, " +
            		    "DATE_OF_BIRTH, GENDER, PHONE_NO, NOMINEE_NAME, RELATION, " +
            		    "NOMINEE_ADDRESS, NOMINEE_CID, NO_OF_PASSENGER, INDIVIDUAL_SUM_INSURED, " +
            		    "PREMIUM_AMOUNT, TOTAL_PREMIUM_AMOUNT) " +
            		    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


                pst = conn.prepareStatement(sql);
                pst.setLong(1, detail.getPolSerialNo());
                pst.setString(2, detail.getCouponNo());
                pst.setString(3, detail.getAssuredName());
                pst.setString(4, detail.getCitizenId());
                pst.setDate(5, new java.sql.Date(detail.getDateOfBirth().getTime()));
                pst.setString(6, detail.getGender());
                pst.setString(7, detail.getPhoneNo());
                pst.setString(8, detail.getNomineeName());
                pst.setString(9, detail.getRelation());
                pst.setString(10, detail.getNomineeAddress());
                pst.setString(11, detail.getNomineeCid());
                pst.setInt(12, detail.getNoOfPassenger());
                pst.setBigDecimal(13, detail.getIndividualSumInsured());
                pst.setBigDecimal(14, detail.getPremiumAmount());
                pst.setBigDecimal(15, detail.getTotalPremiumAmount());

                pst.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Failed to replicate policy details to external database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, null, pst);
        }
    }


}

