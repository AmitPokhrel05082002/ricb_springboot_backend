package bt.ricb.ricb_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import bt.ricb.ricb_api.models.FullPaymentTransactionDto;
import bt.ricb.ricb_api.models.LatestTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<LatestTransactionEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO transactions_latest (CUSTOMER_CID, CUSTOMER_NAME, DEPARTMENT_CODE, POLICY_ACCOUNT_NO, AMOUNT_PAID, ORDER_REFEREENCE_NO, TRANSACTION_ID, TRANSACTION_DATE, REMITTER_NAME, REMITTER_BANK, REMITTER_MOBILE_NO, TRANSACTION_STATUS, DATA_SOURCE, AUTH_CODE, AUTH_ID, CREATED_DATE, REMITTER_CID, REQUEST_ID) " +
            "VALUES (:customerCid, :customerName, :departmentCode, :policyAccountNo, :amountPaid, :orderReferenceNo, :transactionId, DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'), :remitterName, :remitterBank, :remitterMobileNo, :transactionStatus, :dataSource, :authCode, :authId, DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'), :remitterCid, :requestId)", 
            nativeQuery = true)
    void insertPaymentTransaction(
            @Param("customerCid") String customerCid,
            @Param("customerName") String customerName,
            @Param("departmentCode") String departmentCode,
            @Param("policyAccountNo") String policyAccountNo,
            @Param("amountPaid") String amountPaid,
            @Param("orderReferenceNo") String orderReferenceNo,
            @Param("transactionId") String transactionId,
            @Param("remitterName") String remitterName,
            @Param("remitterBank") String remitterBank,
            @Param("remitterMobileNo") String remitterMobileNo,
            @Param("transactionStatus") String transactionStatus,
            @Param("dataSource") String dataSource,
            @Param("authCode") String authCode,
            @Param("authId") String authId,
            @Param("remitterCid") String remitterCid,
            @Param("requestId") Long requestId);
}
