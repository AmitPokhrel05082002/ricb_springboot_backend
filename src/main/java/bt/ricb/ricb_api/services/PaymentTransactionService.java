package bt.ricb.ricb_api.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bt.ricb.ricb_api.models.FullPaymentTransactionDto;
import bt.ricb.ricb_api.repository.PaymentTransactionRepository;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Service
public class PaymentTransactionService {

	@Autowired
	private PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public void createPaymentTransaction(FullPaymentTransactionDto paymentTransactionDto) {
        // You can add business logic/validation here before saving
        
        // If you want to generate current timestamp in the service layer
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        
        paymentTransactionRepository.insertPaymentTransaction(
                paymentTransactionDto.getCustomerCid(),
                paymentTransactionDto.getCustomerName(),
                paymentTransactionDto.getDepartmentCode(),
                paymentTransactionDto.getPolicyAccountNo(),
                paymentTransactionDto.getAmountPaid(),
                paymentTransactionDto.getOrderReferenceNo(),
                paymentTransactionDto.getTransactionId(),
                paymentTransactionDto.getRemitterName(),
                paymentTransactionDto.getRemitterBank(),
                paymentTransactionDto.getRemitterMobileNo(),
                paymentTransactionDto.getTransactionStatus(),
                paymentTransactionDto.getDataSource(),
                paymentTransactionDto.getAuthCode(),
                paymentTransactionDto.getAuthId(),
                paymentTransactionDto.getRemitterCid(),
                paymentTransactionDto.getRequestId()
        );
    }
    
}
