package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.AdvertisementEntity;
import bt.ricb.ricb_api.models.BusinessLinesDto;
import bt.ricb.ricb_api.models.CountryEntity;
import bt.ricb.ricb_api.models.LatestTransactionEntity;
import bt.ricb.ricb_api.models.LifePaymentDto;
import bt.ricb.ricb_api.models.OTPEntity;
import bt.ricb.ricb_api.models.PaymentTransactionDto;
import bt.ricb.ricb_api.models.ResponseDto;
import bt.ricb.ricb_api.models.UserEntity;
import bt.ricb.ricb_api.repository.AdvertisementRepository;
import bt.ricb.ricb_api.repository.BusinessLineRepository;
import bt.ricb.ricb_api.repository.CountryRepository;
import bt.ricb.ricb_api.repository.LatestTransactionRepository;
import bt.ricb.ricb_api.repository.OTPRepository;
import bt.ricb.ricb_api.repository.UserRepository;
import bt.ricb.ricb_api.services.ApiService;
import bt.ricb.ricb_api.services.EmailService;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class InsuranceService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private OTPRepository otpRepo;
	@Autowired
	private OTPRepository otpCodeRepo;
	@Autowired
	private LatestTransactionRepository latestTransRepo;
	@Autowired
	private BusinessLineRepository businessLineRepo;
	@Autowired
	private AdvertisementRepository adRepo;
	@Autowired
	private CountryRepository countryRepo;
	@Autowired
	private ApiService apiService;
	@Autowired
	private EmailService mailService;

	public void updateOrSaveUser(UserEntity user) {
		this.userRepo.save(user);
	}

	public UserEntity getUserByCid(String cid) {
		return this.userRepo.getByCid(cid);
	}

	public void updateOrSaveOTP(OTPEntity data) {
		this.otpRepo.save(data);
	}

	public ResponseDto insertLifePaymentDetails(LatestTransactionEntity trans) {
		ResponseDto response = new ResponseDto();

		try {
			Integer id = ((LatestTransactionEntity) this.latestTransRepo.save(trans)).getID();
			if (id != null) {
				response.setStatus(Integer.valueOf(1));
			} else {

				response.setStatus(Integer.valueOf(0));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	public UserEntity getUserDetails(String cidNo) throws Exception {
		UserEntity userDtls = new UserEntity();
		try {
			userDtls = this.userRepo.getByCid(cidNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userDtls;
	}

	public ResponseDto validatePassword(String cidNo, String password) throws Exception {
	    ResponseDto response = new ResponseDto();
	    try {
	        // Input validation
	        if (cidNo == null || cidNo.trim().isEmpty()) {
	            response.setExistStatus(0);
	            response.setMessage("CID number is required");
	            return response;
	        }
	        
	        if (password == null || password.trim().isEmpty()) {
	            response.setExistStatus(0);
	            response.setMessage("Password is required");
	            return response;
	        }
	        
	        // Use BCrypt or similar for password hashing comparison
	        // For now, using existing method but with improved error handling
	        Integer passwordExists = this.userRepo.checkPassword(password, cidNo.trim());
	        
	        if (passwordExists != null && passwordExists == 1) {
	            response.setExistStatus(1);
	            response.setMessage("Password validated successfully");
	        } else {
	            response.setExistStatus(0);
	            response.setMessage("Invalid credentials");
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setExistStatus(0);
	        response.setMessage("Validation failed");
	    }
	    return response;
	}

	public ResponseDto checkUserDetails(String cidNo, String mobileNo) {
		ResponseDto userResponse = new ResponseDto();
		try {
			List<Tuple> resultSet = this.userRepo.checkUserDetails(cidNo, mobileNo);
			Object existStatus = ((Tuple) resultSet.get(0)).get(0);
			Object status = ((Tuple) resultSet.get(0)).get(1);
			try {
				if (existStatus != null) {
					userResponse.setExistStatus(Integer.valueOf(Integer.parseInt(existStatus.toString())));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (status != null) {
					userResponse.setExistStatus(Integer.valueOf(Integer.parseInt(status.toString())));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return userResponse;
	}

	public ResponseDto validateOTP(String otp, String cidNo) {
		ResponseDto response = new ResponseDto();
		try {
			Boolean otpExist = this.otpCodeRepo.existsByCode(otp);
			if (otpExist.equals(Boolean.valueOf(false))) {
				response.setStatus(Integer.valueOf(0));
			} else {

				response.setStatus(Integer.valueOf(1));
				this.userRepo.updateUserStatus(cidNo);
				if (this.userRepo.getByCid(cidNo).getStatus().intValue() == 1) {
					response.setStatus(Integer.valueOf(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public List<BusinessLinesDto> getBusinessLines() {
		List<Tuple> resultSet = this.businessLineRepo.getAllBusinessLines();
		List<BusinessLinesDto> businessLines = new ArrayList<>();
		BusinessLinesDto singlebusinessLineDto = new BusinessLinesDto();
		try {
			if (resultSet != null) {
				for (int i = 0; i < resultSet.size(); i++) {
					singlebusinessLineDto.setBusinessId(
							Integer.valueOf(Integer.parseInt(((Tuple) resultSet.get(i)).get(0).toString())));
					singlebusinessLineDto.setBusinessName(((Tuple) resultSet.get(i)).get(1).toString());
					businessLines.add(singlebusinessLineDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return businessLines;
	}

	public String existInCommonDB(String cidNo) {
		String url = "http://ccdb.ricb.bt/api/getcustomerdetails/" + cidNo;
		String token = "ZJXS2TnhIosLeUeZI7rN2vcu3j3Tz2MMqlytKIwnxSaPkOnMf7PB0FwPhMG8ATgv";
		String data = null;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer ZJXS2TnhIosLeUeZI7rN2vcu3j3Tz2MMqlytKIwnxSaPkOnMf7PB0FwPhMG8ATgv");
		HttpEntity<String> entity = new HttpEntity("body", (MultiValueMap) headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class,
					new Object[0]);
			data = (String) response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public ResponseDto getOtp(UserEntity user, String methodType) {
	    ResponseDto response = new ResponseDto();
	    try {
	        boolean userExists = this.userRepo.existsByCidAndMobile(user.getCid(), user.getMobile()).booleanValue();
	        
	        if (!userExists) {
	            int otp = generateRandomOTP();
	            String messageContent = "Dear Customer, your OTP number to register is " + otp;
	            
	            if ("email".equals(methodType)) {
	                this.mailService.sendEmail(user.getEmail(), "Registration OTP", messageContent, null);
	            } else {
	                // Check mobile number prefix for SMS routing
	                String mobile = user.getMobile();
	                if (mobile.startsWith("17")) {
	                    this.apiService.sendSms(messageContent, mobile);
	                } else if (mobile.startsWith("77")) {
	                    this.apiService.sendSmsTcell(messageContent, mobile);
	                } else {
	                    // Default SMS service for other prefixes
	                    this.apiService.sendSms(messageContent, mobile);
	                }
	            }
	            response.setResponseData(String.valueOf(otp));
	        } else {
	            response.setResponseData("User already exists");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return response;
	}

	public void addUser(UserEntity user) {
		try {
			this.userRepo.save(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int generateRandomOTP() {
		SecureRandom secureRandom = new SecureRandom();
		return secureRandom.nextInt(10000);
	}

	public List<AdvertisementEntity> getAdvertisements() {
		return this.adRepo.findAll();
	}

	public List<CountryEntity> getCountries() {
		System.out.println("hellosdf");
		return this.countryRepo.findAll();
	}

	public LatestTransactionEntity getByOrderRefNo(String orderRefNo) {
		LatestTransactionEntity transaction = new LatestTransactionEntity();
		try {
			transaction = this.latestTransRepo.getByOrderReferenceNo(orderRefNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transaction;
	}

	public Integer insertLifePaymentDetails(LifePaymentDto lifePaymentDetails) {
		Integer response = Integer.valueOf(0);
		try {
			this.latestTransRepo.insertLifePaymentDetails(lifePaymentDetails.getCidNo(),
					lifePaymentDetails.getCustName(), lifePaymentDetails.getDeptCode(),
					lifePaymentDetails.getPolicyNo(), lifePaymentDetails.getAmount(), lifePaymentDetails.getOrderNo(),
					lifePaymentDetails.getRemitterCid(), lifePaymentDetails.getAuthCode(),
					lifePaymentDetails.getRemitterBank(), lifePaymentDetails.getRemitterName(),
					lifePaymentDetails.getRemitterAccountNo(), lifePaymentDetails.getResponseCode(),
					lifePaymentDetails.getTransactionId(), lifePaymentDetails.getTransactionStatus(), lifePaymentDetails.getRemitterMobileNo());
			response = Integer.valueOf(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("response " + response);
		return response;
	}

	public ResponseDto updateLifePaymentDetails(String txnId, String remitterBank, String accNo, String authCode, String orderNo) throws Exception {
	    System.out.print("111111/////////////////////");
	    
	    ResponseDto response = new ResponseDto();
	    String txnStatus = "FAILED";
	    String authId = "0";
	    String remarks = "Updated";
	    
	    if ("00".equals(authCode)) {
	        txnStatus = "SUCCESS";
	    }
	    
	    try {
	        System.out.print("222222/////////////////////");
	        
	        // Call repository method - now returns int (number of affected rows)
	        int updatedRows = this.latestTransRepo.updateLifePaymentDetails(
	            txnId, remitterBank, accNo, txnStatus, authCode, authId, remarks, orderNo
	        );
	        
	        System.out.print("....." + txnId + remitterBank + accNo + authCode + orderNo);
	        System.out.print("Updated rows: " + updatedRows);
            insertIntoOracle(txnId, remitterBank, accNo, authCode, orderNo);
            response.setResponseData("1");

	        // Check if any rows were actually updated
	        if (updatedRows > 0) {
	            // Insert into Oracle
	            insertIntoOracle(txnId, remitterBank, accNo, authCode, orderNo);
	            response.setResponseData("1");
	            response.setMessage("Payment details updated successfully. Rows affected: " + updatedRows);
	        } else {
	            response.setResponseData("0");
	            response.setMessage("No records found with ORDER_REFEREENCE_NO: " + orderNo);
	        }
	        
	    } catch (Exception e) {
	        // Proper exception handling with logging
	        System.err.println("Error updating payment details: " + e.getMessage());
	        e.printStackTrace();
	        response.setResponseData("0");
	        response.setMessage("Failed to update payment details: " + e.getMessage());
	    }
	    
	    return response;
	}

	public void insertIntoOracle(String txnId, String remitterBank, String accNo, String authCode, String orderNo) throws Exception {
	    System.out.print("33333/////////////////////");

	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    String GET_SEQ_ID = "SELECT RICB_UWR.APP_TRANSACTION_SEQ.NEXTVAL code FROM dual";
	    System.out.print("444/////////////////////");

	    String cidNo;
	    String policyNo;
	    String name;
	    String deptCode;
	    String amount;
	    String txnDate;
	    String remitterName;
	    String mobileNo;
	    String txnStatus;
	    System.out.print(orderNo + "order ref no.");
	    

	    System.out.print("55555/////////////////////");
	    // Fetch latest transaction details
	    LatestTransactionEntity transaction = this.latestTransRepo.getByOrderReferenceNo(orderNo);
	    cidNo = transaction.getCUSTOMER_CID();
	    policyNo = transaction.getPOLICY_ACCOUNT_NO();
	    name = transaction.getCUSTOMER_NAME();
	    deptCode = transaction.getDEPARTMENT_CODE();
	    amount = transaction.getAMOUNT_PAID();
	    txnDate = transaction.getTRANSACTION_DATE();
	    remitterName = transaction.getREMITTER_NAME();
	    mobileNo = transaction.getREMITTER_MOBILE_NO();
	    System.out.print("6666/////////////////////");

	    txnStatus = "00".equals(authCode) ? "SUCCESS" : "FAILED";

	    // Format dates
	    SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
	    String actualTxDate = myFormat.format(fromUser.parse(txnDate));

	    Date curDate = new Date();
	    System.out.println("Current date and time is " + fromUser.format(curDate));
	    System.out.println("Transaction Date is " + actualTxDate);
	    System.out.print("77777/////////////////////");

	    int seqId = 0;

	    try {
	        conn = ConnectionManager.getOracleConnection();

	        // Get sequence ID
	        pst = conn.prepareStatement(GET_SEQ_ID);
	        rs = pst.executeQuery();
	        if (rs.next()) {
	            seqId = rs.getInt("code");
	        }
	        rs.close();
	        pst.close();

	        // Insert into Oracle
	        String INSERT_SQL = "INSERT INTO ricb_mapp_transaction_details (" +
	                "id, request_id, customer_cid, policy_account_no, customer_name, department_code, " +
	                "amount_paid, order_reference_no, transaction_id, transaction_date, remitter_name, remitter_bank, " +
	                "remmiter_account_no, remitter_mobile_no, transaction_status, data_source, response_code, " +
	                "auth_code, auth_id, remarks, updated_date, remmiter_cid" +
	                ") VALUES (?, '0', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '0', ?, ?, ?)";

	        pst = conn.prepareStatement(INSERT_SQL);
	        pst.setInt(1, seqId);
	        pst.setString(2, cidNo);
	        pst.setString(3, policyNo);
	        pst.setString(4, name);
	        pst.setString(5, deptCode);
	        pst.setString(6, amount);
	        pst.setString(7, orderNo);
	        pst.setString(8, txnId);
//	        pst.setString(9, actualTxDate);
	        pst.setString(9, fromUser.format(curDate));
	        pst.setString(10, remitterName);
	        pst.setString(11, remitterBank);
	        pst.setString(12, accNo);
	        pst.setString(13, mobileNo);
	        pst.setString(14, txnStatus);
	        pst.setString(15, "MyRICB");
	        pst.setString(16, "0");
	        pst.setString(17, authCode);
	        pst.setString(18, "MyRICB");
	        pst.setString(19, fromUser.format(curDate));
	        pst.setString(20, cidNo);


	        pst.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }
	}


	public boolean isOracleConnectionAvailable() {
		Connection conn = null;
		try {
			conn = ConnectionManager.getOracleConnection();
			return (conn != null && !conn.isClosed());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectionManager.close(conn, null, null);
		}
	}

	public Map<String, Object> insertPaymentTransaction(PaymentTransactionDto paymentDetails) {
		String actualTxDate,
				INSERT_TO_ORACLE = "INSERT INTO ricb_mapp_transaction_details ( id, request_id,customer_cid, policy_account_no, customer_name, department_code, amount_paid, order_reference_no, transaction_id, transaction_date, remitter_name, remitter_bank, remmiter_account_no,remitter_mobile_no, transaction_status, data_source, response_code,  auth_code, auth_id, remarks, updated_date, remmiter_cid) VALUES (?, '0', ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, '0', ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String GET_SEQ_ID = "select RICB_UWR.APP_TRANSACTION_SEQ.nextval code from dual";
		String cidNo = paymentDetails.getCustCid();
		String policyNo = paymentDetails.getPolicyNo();
		String name = paymentDetails.getCustName();
		String deptCode = paymentDetails.getDeptCode();
		String amount = paymentDetails.getAmount();
		String orderNo = paymentDetails.getOrderNo();
		String txnId = paymentDetails.getTxnId();
		String remitterName = paymentDetails.getRemitterName();
		String remitterBank = paymentDetails.getRemitterBank();
		String accNo = paymentDetails.getAccNo();
		String mobileNo = paymentDetails.getMobileNo();
		String dataSource = paymentDetails.getDataSource();
		String authCode = paymentDetails.getAuthCode();

		int seqId = 0;

		SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date curdate = new Date();

		try {
			actualTxDate = myFormat.format(fromUser.parse(paymentDetails.getActualTxnDate()));
		} catch (ParseException e) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", Boolean.valueOf(false));
			response.put("message", "Failed to parse date: " + e.getMessage());
			return response;
		}

		System.out.println("current date and time is " + fromUser.format(curdate));
		System.out.println("Transaction Date is " + actualTxDate);
		try {
			String txnStatus;
			conn = ConnectionManager.getOracleConnection();
			pst = conn.prepareStatement(GET_SEQ_ID);
			rs = pst.executeQuery();
			if (paymentDetails.getAuthCode().equals("00")) {
				txnStatus = "SUCCESS";
			} else {
				txnStatus = "FAILED";
			}

			if (rs.next()) {
				seqId = rs.getInt("code");
			}
			rs.close();
			pst = conn.prepareStatement(
					"INSERT INTO ricb_mapp_transaction_details ( id, request_id,customer_cid, policy_account_no, customer_name, department_code, amount_paid, order_reference_no, transaction_id, transaction_date, remitter_name, remitter_bank, remmiter_account_no,remitter_mobile_no, transaction_status, data_source, response_code,  auth_code, auth_id, remarks, updated_date, remmiter_cid) VALUES (?, '0', ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, '0', ?, ?, ?)");
			pst.setInt(1, seqId);
			pst.setString(2, cidNo);
			pst.setString(3, policyNo);
			pst.setString(4, name);
			pst.setString(5, deptCode);
			pst.setString(6, amount);
			pst.setString(7, orderNo);
			pst.setString(8, txnId);
			pst.setString(9, actualTxDate);
			pst.setString(10, remitterName);
			pst.setString(11, remitterBank);
			pst.setString(12, accNo);
			pst.setString(13, mobileNo);
			pst.setString(14, txnStatus);
			pst.setString(15, dataSource);
			pst.setString(16, "0");
			pst.setString(17, authCode);
			pst.setString(18, dataSource);
			pst.setString(19, fromUser.format(curdate));
			pst.setString(20, cidNo);
			pst.executeUpdate();
			Map<String, Object> response = new HashMap<>();
			response.put("success", Boolean.valueOf(true));
			response.put("message", "Payment transaction inserted successfully.");
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> response = new HashMap<>();
			response.put("success", Boolean.valueOf(false));
			response.put("message", "Failed to insert payment transaction: " + e.getMessage());
			return response;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
	}
}