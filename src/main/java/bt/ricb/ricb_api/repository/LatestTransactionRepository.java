package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.LatestTransactionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LatestTransactionRepository extends JpaRepository<LatestTransactionEntity, Integer> {
	@Modifying
	@Transactional
	@Query(value = "UPDATE apps_users SET status=1 WHERE cid=:cid", nativeQuery = true)
	void updateUserStatus(@Param("cid") String paramString);

	@Modifying
	@Transactional
	@Query(value = "UPDATE\r\n\ttransactions_latest\r\nSET\r\n\tTRANSACTION_ID = :tranId,\r\n\tREMITTER_BANK = :remBank,\r\n\tREMMITER_ACCOUNT_NO = :accNo,\r\n\tTRANSACTION_STATUS = :txnStatus,\r\n\tDATA_SOURCE = 'MyRICB',\r\n\tAUTH_CODE = :authCode,\r\n\tAUTH_ID = :authId,\r\n\tREMARKS = :remarks\r\nWHERE\r\n\tORDER_REFEREENCE_NO = :refNo", nativeQuery = true)
	int updateLifePaymentDetails(@Param("tranId") String paramString1, 
	                            @Param("remBank") String paramString2,
	                            @Param("accNo") String paramString3, 
	                            @Param("txnStatus") String paramString4,
	                            @Param("authCode") String paramString5, 
	                            @Param("authId") String paramString6,
	                            @Param("remarks") String paramString7, 
	                            @Param("refNo") String paramString8);

	@Query(value = "SELECT * FROM transactions_latest WHERE ORDER_REFEREENCE_NO = :orderReferenceNo", nativeQuery = true)
	LatestTransactionEntity getByOrderReferenceNo(@Param("orderReferenceNo") String paramString);

	@Query(value = "select RICB_UWR.APP_TRANSACTION_SEQ.nextval code from dual", nativeQuery = true)
	String getSeqId();

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO transactions_latest (CUSTOMER_CID, CUSTOMER_NAME, DEPARTMENT_CODE, POLICY_ACCOUNT_NO, AMOUNT_PAID, ORDER_REFEREENCE_NO, TRANSACTION_ID, TRANSACTION_DATE, REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, DATA_SOURCE, AUTH_CODE, AUTH_ID, RESPONSE_CODE, REMMITER_ACCOUNT_NO, CREATED_DATE, REMITTER_CID, REQUEST_ID) " +
	        "VALUES (:cidNo, :custName, :deptCode, :policyNo, :amount, :orderNo, :transactionId, DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'), " +
	        ":remitterName, :remitterBank, :remitterMobileNo, :transactionStatus, 'MyRICB', :authCode, '00', :responseCode, :remitterAccountNo, " +
	        "DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'), :remitterCid, 0)", 
	        nativeQuery = true)
	void insertLifePaymentDetails(@Param("cidNo") String cidNo,
	                              @Param("custName") String custName,
	                              @Param("deptCode") String deptCode,
	                              @Param("policyNo") String policyNo,
	                              @Param("amount") String amount,
	                              @Param("orderNo") String orderNo,
	                              @Param("remitterCid") String remitterCid,
	                              @Param("authCode") String authCode,
	                              @Param("remitterBank") String remitterBank,
	                              @Param("remitterName") String remitterName,
	                              @Param("remitterAccountNo") String remitterAccountNo,
	                              @Param("responseCode") String responseCode,
	                              @Param("transactionId") String transactionId,
	                              @Param("transactionStatus") String transactionStatus,
	                              @Param("remitterMobileNo") String remitterMobileNo);

}