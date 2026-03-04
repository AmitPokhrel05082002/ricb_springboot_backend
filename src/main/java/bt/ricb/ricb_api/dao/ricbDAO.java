package bt.ricb.ricb_api.dao;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.util.EmailUtil;
import bt.ricb.ricb_api.util.ToJSON;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

@Repository
public class ricbDAO {

    private DataSource dataSource = null;
    private DataSource mySQLDataSource = null;
    private EmailUtil emailUtil = null;

    @Autowired
    public void RicbDAO(DataSource dataSource, DataSource mySQLDataSource, EmailUtil emailUtil) {
        this.dataSource = dataSource;
        this.mySQLDataSource = mySQLDataSource;
        this.emailUtil = emailUtil;
    }
    
    private static final String GET_ALL_FIRE_SF_DETAILS = ""
			 + "select * from V_FIRE_SF_CURR_POLICY where PREVIOUS_POLICY_NO LIKE ? ";

  //RLI 2026
    private static final String GET_RLI_PREMIUM = "select "
    	    + "'' as premium "
    	    + "from dual";
    
    private static final String GET_RLI_STATUS = "select * from V_RLI_POLICY where PRESENT_HOUSEHOLD_NO =? ";
    
    private static final String GET_MOTOR_CP_POLICY_NO = ""
         + "select * from V_MCP_POLICY where POL_CIVIL_ID in  (?) order by EXPIRY_DATE,POL_NO ";
    
    private static final String GET_ALL_MOTOR_CP_DETAILS = ""
        + "select * from V_MCP_CURR_POLICY where PREVIOUS_POLICY_NO LIKE ? ";
    
    private static final String GET_ALL_MCP_PREV_DETAILS = ""
        + "select * from V_MCP_POLICY where POL_NO LIKE ? order by EXPIRY_DATE,POL_NO ";
    
    
    private static final String GET_FIRE_SF_POLICY_NO = ""
         + "select * from V_FIRE_SF_POLICY where POL_CIVIL_ID in  (?) order by EXPIRY_DATE,POL_NO ";
    
    private static final String GET_ALL_FIRE_SF_PREV_DETAILS = ""
        + "select * from V_FIRE_SF_POLICY where POL_NO LIKE ? order by EXPIRY_DATE,POL_NO ";
    
    // All your SQL constants remain exactly the same
    private static final String GET_POLICY_DETAILS = "SELECT "
			+ "  a.product_code product_code, "
			+ "  a.policy_no policy_no, "
			+ "  TO_CHAR( "
			+ "    a.policy_start_date, "
			+ "    'DD/MM/RRRR' "
			+ "  ) incept_date, "
			+ "  TO_CHAR(a.policy_end_date, 'DD/MM/RRRR') maturity_date, "
			+ "  TO_CHAR( "
			+ "    a.sum_assured, "
			+ "    '9,999,999,999,999.99' "
			+ "  ) sum_assured, "
			+ "  c.premium_per_instalment premium, "
			+ "  DECODE( "
			+ "    a.mode_of_payment, "
			+ "    'M', "
			+ "    'MONTHLY', "
			+ "    'S', "
			+ "    'SSS', "
			+ "    'Q', "
			+ "    'QUARTERLY', "
			+ "    'H', "
			+ "    'HALF YEARLY', "
			+ "    'Y', "
			+ "    'YEARLY', "
			+ "    'O', "
			+ "    'SINGLE' "
			+ "  ) AS p_mode, "
			+ "  DECODE( "
			+ "    a.status_code, "
			+ "    'G', "
			+ "    'Active Policy', "
			+ "    'H', "
			+ "    'LAPSED', "
			+ "    'I', "
			+ "    'Lapsed Paid "
			+ "Up', "
			+ "    'J', "
			+ "    'Forfieted', "
			+ "    'K', "
			+ "    'Matured', "
			+ "    'L', "
			+ "    'Closed- Maturity', "
			+ "    'M', "
			+ "    'Death "
			+ "Claim Entered/Registered', "
			+ "    'MM', "
			+ "    'Surrender Claim Entered/', "
			+ "    'N', "
			+ "    'Closed- "
			+ "Death', "
			+ "    'P', "
			+ "    'Closed- Surrender', "
			+ "    'Q', "
			+ "    'Closed- Forfeiture', "
			+ "    'S', "
			+ "    'Closed- "
			+ "Claim Declined', "
			+ "    'U', "
			+ "    'Closed- PTD', "
			+ "    'V', "
			+ "    'Cancelled', "
			+ "    'X', "
			+ "    'Rejected' "
			+ "  ) POLSTATUS, "
			+ "  TO_CHAR (MIN (aa.due_date), 'DD/MM/YYYY') next_repay, "
			+ "  b.customer_name POLICY_HOLDER, "
			+ "  b.citizen_id CUST_CID, "
			+ "  a.policy_term term, "
			+ "  TO_CHAR (MAX (bb.due_date), 'DD/MM/YYYY') LAST_PAID_DATE "
			+ "FROM "
			+ "  tl_li_tr_policy_header a, "
			+ "  tl_in_mas_customer b, "
			+ "  tl_li_tr_premium_list c, "
			+ "  (SELECT "
			+ "    * "
			+ "  FROM "
			+ "    tl_li_tr_premium_list "
			+ "  WHERE status_code = 'PENDING') aa, "
			+ "  (SELECT "
			+ "    * "
			+ "  FROM "
			+ "    tl_li_tr_premium_list "
			+ "  WHERE status_code = 'PAID') bb "
			+ "WHERE a.customer_code = b.customer_code "
			+ "  AND aa.policy_serial_no = a.serial_no "
			+ "  AND bb.policy_serial_no = a.serial_no "
			+ "  AND a.serial_no = c.policy_serial_no "
			+ "  AND c.status_code = 'PENDING' "
			+ "  AND b.citizen_id = ? "
			+ "GROUP BY a.product_code, "
			+ "  a.policy_no, "
			+ "  a.sum_assured, "
			+ "  a.policy_start_date, "
			+ "  a.policy_end_date, "
			+ "  a.mode_of_payment, "
			+ "  a.status_code, "
			+ "  b.customer_name, "
			+ "  c.premium_per_instalment, "
			+ "  a.policy_term, "
			+ "  b.citizen_id";
    private static final String GET_ALL_POLICY_DETAILS = "SELECT "
			+ "  a.product_code product_code, "
			+ "  a.policy_no policy_no, "
			+ "  a.serial_no pol_prop_sys_id, "
			+ "  TO_CHAR( "
			+ "    a.policy_start_date, "
			+ "    'DD/MM/RRRR' "
			+ "  ) inception_date, "
			+ "  TO_CHAR(a.policy_end_date, 'DD/MM/RRRR') maturity_date, "
			+ "  TO_CHAR( "
			+ "    a.sum_assured, "
			+ "    '9,999,999,999,999.99' "
			+ "  ) sum_assured, "
			+ "  TO_CHAR(c.premium_per_instalment, '9,999,999,999,999.99') premium, "
			+ "  DECODE( "
			+ "    a.status_code, "
			+ "    'G', "
			+ "    'ACTIVE', "
			+ "    'T', "
			+ "    'ACTIVE', "
			+ "    'TT', "
			+ "    'ACTIVE', "
			+ "    'H', "
			+ "    'LAPSED', "
			+ "    'I', "
			+ "    'Lapsed Paid Up', "
			+ "    'J', "
			+ "    'Forfieted', "
			+ "    'K', "
			+ "    'Matured', "
			+ "    'L', "
			+ "    'Closed- Maturity', "
			+ "    'M', "
			+ "    'Death Claim Entered/Registered', "
			+ "    'MM', "
			+ "    'Surrender Claim Entered/', "
			+ "    'N', "
			+ "    'Closed-Death', "
			+ "    'P', "
			+ "    'Closed- Surrender', "
			+ "    'Q', "
			+ "    'Closed- Forfeiture', "
			+ "    'S', "
			+ "    'Closed-Claim Declined', "
			+ "    'U', "
			+ "    'Closed- PTD', "
			+ "    'V', "
			+ "    'Cancelled', "
			+ "    'X', "
			+ "    'Rejected' "
			+ "  ) polstatus, "
			+ "  DECODE ( "
			+ "    a.mode_of_payment, "
			+ "    'M', "
			+ "    'MONTHLY', "
			+ "    'S', "
			+ "    'MONTHLY', "
			+ "    'Q', "
			+ "    'QUARTERLY', "
			+ "    'H', "
			+ "    'HALF YEARLY', "
			+ "    'Y', "
			+ "    'YEARLY', "
			+ "    'O', "
			+ "    'SINGLE PREMIUM' "
			+ "  ) AS p_mode, "
			+ "  TO_CHAR (MIN(aa.due_date)) next_repay, "
			+ "  b.customer_name POLICY_HOLDER, "
			+ "  'a' ADDRESS1, "
			+ "  'b' ADDRESS2, "
			+ "  'c' ADDRESS3, "
			+ "  b.citizen_id CUST_CID, "
			+ "  a.policy_term TERM, "
			+ "  TO_CHAR (MAX(bb.due_date)) LAST_PAID_DATE, "
			+ "  '1' TOTAL_PAID, "
			+ "  'd' BROKER_CODE "
			+ "FROM "
			+ "  tl_li_tr_policy_header a, "
			+ "  tl_in_mas_customer b, "
			+ "  tl_li_tr_premium_list c, "
			+ "  (SELECT "
			+ "    * "
			+ "  FROM "
			+ "    tl_li_tr_premium_list "
			+ "  WHERE status_code = 'PENDING') aa, "
			+ "  (SELECT "
			+ "    * "
			+ "  FROM "
			+ "    tl_li_tr_premium_list "
			+ "  WHERE status_code = 'PAID') bb "
			+ "WHERE a.customer_code = b.customer_code "
			+ "  AND aa.policy_serial_no = a.serial_no "
			+ "  AND bb.policy_serial_no = a.serial_no "
			+ "  AND a.serial_no = c.policy_serial_no "
			+ "  AND c.status_code = 'PENDING' "
			+ "  AND a.policy_no = ? "
			+ "GROUP BY a.product_code, "
			+ "  a.policy_no, "
			+ "  a.serial_no, "
			+ "  a.sum_assured, "
			+ "  a.policy_start_date, "
			+ "  a.policy_end_date, "
			+ "  a.mode_of_payment, "
			+ "  a.status_code, "
			+ "  b.customer_name, "
			+ "  c.premium_per_instalment, "
			+ "  a.policy_term, "
			+ "  b.citizen_id";

	private static final String INSERT_USER_DETAILS = "INSERT INTO apps_users(NAME, cid, email, mobile, PASSWORD, status) VALUES (?,?,?,?,?,?)";


	private static final String CHECK_USER_DETAILS = "SELECT count(*) userExist, status FROM apps_users WHERE cid=? and mobile=?";


	private static final String CHECK_PASSWORD = "SELECT count(*) savedPassword FROM apps_users WHERE password=? and cid=? and status=1";

	private static final String GET_CREDIT_ACCOUNT = "http://183.82.97.1:91/ricb_dev_orl/ServiceIntegration/eFIMOIntegration.asmx/GetCollectionData_JSON?loanaccountnumber=?";

	private static final String GET_ALL_CREDIT_DETAILS = ""
			+ "SELECT A.ACCOUNT_holder, "
			+ " "
			+ "NVL(A.PVR_Citizenship_number, A.PVR_Registration_number) cid_no, "
			+ " "
			+ "A.credit_id "
			+ " "
			+ ",A.credit_account_number account_no "
			+ " "
			+ ",to_char(A.sanction_amount, '999,999,999,999.99') sanc_amt "
			+ " "
			+ ",to_char(A.sanction_date,'dd/mm/rrrr') sanc_date "
			+ " "
			+ ",to_char(A.expiry_date,'dd/mm/rrrr') exp_dt "
			+ " "
			+ ",to_char(A.repayment_effective_date,'dd/mm/rrrr') repay_date "
			+ " "
			+ ",A.rate_of_interest int_rt "
			+ " "
			+ ",to_char(A.installment_amount, '999,999,999,999.99') emi "
			+ " "
			+ ",A.status STATUS "
			+ " "
			+ ",A.term "
			+ " "
			+ ",A.mode_of_repayment repay_mode "
			+ " "
			+ ",A.repayment_effective_date "
			+ " "
			+ ",get_nextrepaymentdt(to_char(A.repayment_effective_date,'dd/mm/rrrr'),A.mode_of_repayment) next_repaymt "
			+ " "
			+ ",to_char(B.INTEREST_OD_AMOUNT, '999,999,999,999.99') INTODAMT "
			+ " "
			+ ",B.LATE_FEE LATEFEE "
			+ " "
			+ ",B.OD_INDICATOR OD_DAYS "
			+ " "
			+ "FROM T_CID_LOAN_LISTS A "
			+ " "
			+ ",RICB_UWR.V_CREDIT_PAYMENT_CYCLE B "
			+ " "
			+ "WHERE credit_account_number IS NOT NULL AND A.credit_account_number = ? "
			+ " "
			+ "AND UPPER(A.status) NOT LIKE "
			+ " "
			+ "'CLOSED%' AND UPPER(A.status) NOT LIKE 'DECLINED%' "
			+ " "
			+ "AND A.CREDIT_ID = B.CRDT_CREDIT_ID "
			+ " "
			+ "AND TO_CHAR(B.CREATED_DATE, 'DD/MM/RRRR HH24:MI:SS') "
			+ " "
			+ "= (SELECT "
			+ " "
			+ "TO_CHAR(MAX(X.CREATED_DATE),'DD/MM/RRRR HH24:MI:SS') "
			+ " "
			+ "FROM RICB_UWR.V_CREDIT_PAYMENT_CYCLE X "
			+ " "
			+ "WHERE X.CRDT_CREDIT_ID = A.CREDIT_ID "
			+ " "
			+ "AND x.transaction_type = x.transaction_type "
			+ " "
			+ "AND X.CRDT_CREDIT_PAYMENT_CYCLE_ID IS NULL)";
	
	private static final String GET_OTIP_PLAN = " "
			+ "select a.col_1 code,  a.col_6 premium,  b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53 "
			+ "and a.col_1='T2'  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1 " ;
	
	private static final String GET_OTIP_PLAN_JP = " "
			+ "select a.col_1 code, a.col_6 premium,  b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53 "
			+ " and a.col_1 in ('T2', 'T1')  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1 ";
	
	private static final String GET_OTIP_PLAN_ASIAN = " "
			+ "select a.col_1 code, a.col_6 premium,  b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53 "
			+ "and a.col_1 in ('T2', 'T1','T3')  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1 ";
	
	private static final String GET_OTIP_PLAN_ALL = " "
			+ "select a.col_1 code, a.col_6 premium,  b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53 "
			+ "and a.col_1 in ('T2', 'T1')  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1 ";
	
	private static final String GET_OTIP_PLAN_FINAL = " "
			+ "select to_char(a.col_6, '9,999,999,999,999.99') premium,  b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured  from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53 "
			+ "and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1 and b.code_desc=? ";

	private static final String GET_GENERAL_POLICY_NO = ""
			+ "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, c.product_group_ri policycode, a.policy_no pol_no,to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, to_char(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date, "
			+ "to_char(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date, to_char(a.premium_amount, '9,999,999,999,999.99') premium, "
			+ "Decode(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status from RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b, RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c "
			+ " where  b.citizen_id in  (?) and a.customer_code=b.CUSTOMER_CODE and a.product_code=c.product_code and a.status_code in ('G','A','H') ";
	
	private static final String GET_MOTOR_TP_POLICY_NO = ""
			 + "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, 'MTP' policycode, a.policy_no pol_no, to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, "
			 + "a.policy_start_date inception_date, a.policy_end_date expiry_date, to_char(e.code_value, '9,999,999,999,999.99')premium, decode(a.status_code,'G','Active','I','Expired',a.status_code) status, "
			 + "c.registration_no reg_no "
			 + "from ricb_gi.tl_gi_tr_policy_header@RICB_COM a, ricb_gi.tl_gi_mas_customer@RICB_COM b, ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e "
			 + "where a.product_code = 'PR00000043' "
			 + "and a.customer_code = b.customer_code "
			 + "and a.serial_no = c.policy_hdr_srl_no "
			 + "and a.SERIAL_NO = e.POLICY_SRL_NO AND e.CODE_CODE = 8 "
			 + "and a.policy_end_date between add_months(sysdate,-12) and add_months(sysdate,3) "
			 + "and a.renewed_policy_no is null "
			 + "and a.status_code in ('G','I') "
			 + "AND b.citizen_id in  (?) "
			 + "order by a.policy_end_date,a.policy_no ";
	
	private static final String GET_MOTOR_TP_ACTIVE_POLICY_NO = ""
			 + "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, 'MTP' policycode, a.policy_no pol_no, to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, "
			 + "a.policy_start_date inception_date, a.policy_end_date expiry_date, to_char(e.code_value, '9,999,999,999,999.99')premium, decode(a.status_code,'G','Active','I','Expired',a.status_code) status, "
			 + "c.registration_no reg_no "
			 + "from ricb_gi.tl_gi_tr_policy_header@RICB_COM a, ricb_gi.tl_gi_mas_customer@RICB_COM b, ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e "
			 + "where a.product_code = 'PR00000043' "
			 + "and a.customer_code = b.customer_code "
			 + "and a.serial_no = c.policy_hdr_srl_no "
			 + "and a.SERIAL_NO = e.POLICY_SRL_NO AND e.CODE_CODE = 8 "
			 + "and a.renewed_policy_no is null "
			 + "and a.status_code in ('G','F') "
			 + "AND b.citizen_id in  (?) "
			 + "order by a.policy_end_date,a.policy_no ";
	
	private static final String GET_OTIP_ACTIVE_POLICY_NO = ""
			 + "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, a.policy_no pol_no, to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, "
			 + "a.policy_start_date inception_date, a.policy_end_date expiry_date, to_char(a.premium_amount, '9,999,999,999,999.99')premium, decode(a.status_code,'G','Active','I','Expired',a.status_code) status "
			 + "from ricb_gi.tl_gi_tr_policy_header@RICB_COM a, ricb_gi.tl_gi_mas_customer@RICB_COM b "
			 + "where a.product_code = 'PR00000032' "
			 + "and a.customer_code = b.customer_code "
			 + "and a.renewed_policy_no is null "
			 + "and a.status_code in ('G','F') "
			 + "AND b.citizen_id in  (?) "
			 + "order by a.policy_end_date,a.policy_no ";
	
			 
	private static final String GET_GENERAL_CLAIM_OTHERS = ""
			+ "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, c.product_group_ri policycode, a.policy_no pol_no,to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, to_char(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date, "
			+ "to_char(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date, to_char(a.premium_amount, '9,999,999,999,999.99') premium, "
			+ "Decode(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status, d.registration_no reg_no from RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b, RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c , RICB_GI.TL_GI_TR_MOTOR_DTL@RICB_COM d"
			+ " where  b.citizen_id in  (?) and a.customer_code=b.CUSTOMER_CODE and a.product_code=c.product_code and a.status_code in ('G','A','H') "
			+ "and c.product_group_ri='MOTR' and a.serial_no=d.policy_hdr_srl_no ";
	
	
	private static final String GET_GENERAL_CLAIM_TRACK = ""
			 + "SELECT a.policy_no POLICY_NO, a.CLAIM_INTM_NO CLAIM_INTM_NO, to_char(a.CLAIM_INTM_DATE, 'dd/mm/rrrr') CLAIM_INTM_DATE, to_char(a.DATE_OF_LOSS, 'dd/mm/rrrr') DATE_OF_LOSS, "
            + "c.STATUS_DESC, a.status_code, b.INTIMATOR_CID " 
            + "from RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,RICB_GI.CLAIMS_INTIMATION@RICB_COM b, ricb_com.tl_in_mas_status@RICB_COM c "
             + "where a.policy_no=b.policy_no and b.INTIMATOR_CID= ? "
             + "AND a.status_code= c.status_code AND c.table_name= 'tl_gi_tr_claims_header' "                
            + "group by  a.policy_no, a.CLAIM_INTM_NO, a.CLAIM_INTM_DATE, a.DATE_OF_LOSS, c.STATUS_DESC, b.INTIMATOR_CID, a.status_code " ;
	private static final String GET_GEN_CLAIM_STATUS = ""
			 + "SELECT a.policy_no POLICY_NO,a.status_code, b.INTIMATOR_CID "
	            + "from RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,RICB_GI.CLAIMS_INTIMATION@RICB_COM b, ricb_com.tl_in_mas_status@RICB_COM c "
	              + "where a.policy_no=b.policy_no and A.POLICY_NO=? "
	             + "AND a.status_code= c.status_code AND c.table_name= 'tl_gi_tr_claims_header' "
	             + "and a.status_code not in ('G','X', 'F') "             
	            + "group by  a.policy_no, b.INTIMATOR_CID, a.status_code "  ;
	
	
	/* New SQL FOR RLI */
	private static final String GET_RURAL_LIFE = ""		
	+ "SELECT b.SERIAL_NO,b.NO_OF_MEMBERS totalmembers, "
       + "b.HEAD_FAMILY_NAME HOH, "
        + "b.CITIZEN_ID HOH_CID, "
        + "b.TOTAL_FAMILY_PREM totalpremium, " 
        + "b.PRESENT_HOUSEHOLD_NO, " 
        + "a.MEMBER_NAME FULL_NAME, "
       + "b.PERMANENT_HOUSE_NO, "
        + "b.VILLAGE, "
        + "b.GEWOG_NAME, "
        + "b.DZONG_NAME "
    + "FROM RICB_LI.TL_LI_TR_RURAL_POL_DTL@RICB_COM  a, RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM b "
    + "WHERE a.HDR_SERIAL_NO = b.SERIAL_NO "
       + "AND a.CITIZEN_ID = ? and b.UNDERWRITING_YEAR = ?";
	
	private static final String GET_RURAL_LIFE_MEMBER = ""		
			+ "SELECT b.MEMBER_NAME, b.DATE_OF_BIRTH, b.CITIZEN_ID, DECODE(b.STATUS_CODE,'A','Active','X','Deceased','') status "
	        + "FROM RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM a, RICB_LI.TL_LI_TR_RURAL_POL_DTL@RICB_COM b "
	        + "WHERE a.SERIAL_NO = b.HDR_SERIAL_NO "
	           + "AND a.CITIZEN_ID = ? AND a.UNDERWRITING_YEAR = ? AND b.STATUS_CODE = 'A' ORDER BY b.DATE_OF_BIRTH ";
	

	private static final String GET_RL_PAYMENT = ""				
			+ "SELECT a.UNDERWRITING_YEAR, a.STATUS_CODE from RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM a "
			+ "WHERE a.status_code='D' and a.SERIAL_NO= ? ";
	
private static final String GET_OTIP_CUSTOMER = ""
			 + "select CUSTOMER_NAME, to_char(DATE_OF_BIRTH,'rrrr-mm-dd') DOB, CITIZEN_ID, PASSPORT_NO from RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM "
			 + "WHERE CITIZEN_ID LIKE ? ";
	private static final String GET_ALL_GENERAL_DETAILS = ""
			+ "select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, c.product_group_ri policycode, a.policy_no pol_no, to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, to_char(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,"
			+ " "
			+ "to_char(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date, to_char(a.premium_amount, '9,999,999,999,999.99') premium,b.customer_name,"
			+ " "
			+ " Decode(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') STATUS, d.REGISTRATION_NO from RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b, RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c"
			+ " "
			+ ",RICB_GI.TL_GI_TR_MOTOR_DTL@RICB_COM d where a.policy_no LIKE ? and a.customer_code=b.CUSTOMER_CODE and a.product_code=c.product_code and a.serial_no=d.policy_hdr_srl_no(+) and a.status_code in ('G','A','H')";
	private static final String GET_ALL_MOTOR_TP_DETAILS = ""
			+"select a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, 'MTP' policycode, a.policy_no pol_no, to_char(a.sum_assured, '9,999,999,999,999.99') sum_insured, "
			+"a.policy_start_date inception_date, a.policy_end_date expiry_date, e.code_value premium, decode(a.status_code,'G','Active','I','Expired',a.status_code) status, "
			+"c.registration_no reg_no "
			+"from ricb_gi.tl_gi_tr_policy_header@RICB_COM a, ricb_gi.tl_gi_mas_customer@RICB_COM b, ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e "
			+"where a.product_code = 'PR00000043' "
			+"and a.customer_code = b.customer_code "
			+"and a.serial_no = c.policy_hdr_srl_no "
			+"and a.SERIAL_NO = e.POLICY_SRL_NO AND e.CODE_CODE = 8 "
			+"and a.renewed_policy_no is null "
			+"and a.status_code in ('G','I') "
			+"and a.policy_no LIKE ? "
			+"order by a.policy_end_date,a.policy_no ";
				 

	private static final String GET_DEFERRED_POLICY_NO = "select pm.policyno from lifeannuity_planformember pm inner join lifeannuity_membermaster lmmst"
			+ " on pm.memberid=lmmst.id where pm.status='Active' and lmmst.cityzenshipid=?";

	private static final String GET_ALL_DEFERRED_DETAILS = ""
			+ "SELECT "
			+ " "
			+ "  B.Custname, "
			+ " "
			+ "  b.cityzenshipid, "
			+ " "
			+ "  to_char ( "
			+ " "
			+ "    b.SUMASSURED, "
			+ " "
			+ "    '999,999,999,999.99' "
			+ " "
			+ "  ) SUMASSURED, "
			+ " "
			+ "  b.policyno ploicy_no, "
			+ " "
			+ "   to_char ( "
			+ " "
			+ "    b.Effectivepremiumamount, "
			+ " "
			+ "    '999,999,999,999.99' "
			+ " "
			+ "  ) PREMIUMAMOUNT, "
			+ " "
			+ "  B.premiumtype, "
			+ " "
			+ "    to_char (b.POLICYDATE, 'dd/mm/rrrr') ploicy_date, "
			+ " "
			+ "    to_char (b.premiumclosingdate, 'dd/mm/rrrr') ploicy_vesting_date, "
			+ " "
			+ "  to_char (a.lastpremiumdate, 'dd/mm/rrrr') Paid_up_to_date, "
			+ " "
			+ "  CASE "
			+ " "
			+ "    WHEN to_char (A.lastpremiumdate, 'DD') = to_char (b.policydate, 'DD') "
			+ " "
			+ "    AND A.lastpremiumdate IS NOT NULL "
			+ " "
			+ "    THEN add_months (A.lastpremiumdate, 1) "
			+ " "
			+ "    WHEN to_char (A.lastpremiumdate, 'DD') <> to_char (b.policydate, 'DD') "
			+ " "
			+ "    AND ( "
			+ " "
			+ "      ( "
			+ " "
			+ "        to_char ( "
			+ " "
			+ "          add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "          'MON' "
			+ " "
			+ "        ) <> 'FEB' "
			+ " "
			+ "      ) "
			+ " "
			+ "      OR ( "
			+ " "
			+ "        to_char ( "
			+ " "
			+ "          add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "          'MON' "
			+ " "
			+ "        ) = 'FEB' "
			+ " "
			+ "        AND to_char (b.policydate, 'DD') <= '28' "
			+ " "
			+ "      ) "
			+ " "
			+ "    ) "
			+ " "
			+ "    AND A.lastpremiumdate IS NOT NULL "
			+ " "
			+ "    THEN to_date ( "
			+ " "
			+ "      to_char (b.policydate, 'DD') || '-' || to_char ( "
			+ " "
			+ "        add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "        'MON' "
			+ " "
			+ "      ) || '-' || to_char ( "
			+ " "
			+ "        add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "        'YYYY' "
			+ " "
			+ "      ), "
			+ " "
			+ "      'dd-mon-yyyy' "
			+ " "
			+ "    ) "
			+ " "
			+ "    WHEN to_char (A.lastpremiumdate, 'DD') <> to_char (b.policydate, 'DD') "
			+ " "
			+ "    AND to_char ( "
			+ " "
			+ "      add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "      'MON' "
			+ " "
			+ "    ) = 'FEB' "
			+ " "
			+ "    AND to_char (b.policydate, 'DD') > '28' "
			+ " "
			+ "    AND A.lastpremiumdate IS NOT NULL "
			+ " "
			+ "    THEN to_date ( "
			+ " "
			+ "      '28' || '-' || to_char ( "
			+ " "
			+ "        add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "        'MON' "
			+ " "
			+ "      ) || '-' || to_char ( "
			+ " "
			+ "        add_months (A.lastpremiumdate, 1), "
			+ " "
			+ "        'YYYY' "
			+ " "
			+ "      ), "
			+ " "
			+ "      'dd-mon-yyyy' "
			+ " "
			+ "    ) "
			+ " "
			+ "    WHEN A.lastpremiumdate IS NULL "
			+ " "
			+ "    THEN b.policydate "
			+ " "
			+ "  END next_due_months, "
			+ " "
			+ "  trunc ( "
			+ " "
			+ "    months_between ( "
			+ " "
			+ "      to_char (SYSDATE, 'DD-MON-YY'), "
			+ " "
			+ "      ( "
			+ " "
			+ "        add_months (A.lastpremiumdate, B.noofmonths) "
			+ " "
			+ "      ) "
			+ " "
			+ "    ), "
			+ " "
			+ "    0 "
			+ " "
			+ "  ) + 1 deu_months, "
			+ " "
			+ "  b.effectivepremiumamount, "
			+ " "
			+ "  ( "
			+ " "
			+ "    trunc ( "
			+ " "
			+ "      months_between ( "
			+ " "
			+ "        to_char (SYSDATE, 'DD-MON-YY'), "
			+ " "
			+ "        ( "
			+ " "
			+ "          add_months (A.lastpremiumdate, B.noofmonths) "
			+ " "
			+ "        ) "
			+ " "
			+ "      ), "
			+ " "
			+ "      0 "
			+ " "
			+ "    ) + 1 "
			+ " "
			+ "  ) * b.effectivepremiumamount AS total_payable "
			+ " "
			+ "FROM "
			+ " "
			+ "  (SELECT DISTINCT "
			+ " "
			+ "    pm.memberid, "
			+ " "
			+ "    (lmmst.Title || ' ' || lmmst.name) CUSTNAME, "
			+ " "
			+ "    lmmst.currentaddress CURRADDRESS, "
			+ " "
			+ "    lmmst.cityzenshipid, "
			+ " "
			+ "    lmmst.customerno, "
			+ " "
			+ "    pm.sumassured, "
			+ " "
			+ "    pm.policyno, "
			+ " "
			+ "    pm.policydate, "
			+ " "
			+ "    pm.premiumclosingdate, "
			+ " "
			+ "    plm.planname, "
			+ " "
			+ "    am.annuitytype, "
			+ " "
			+ "    nvl (prmst.noofmonths, 0) noofmonths, "
			+ " "
			+ "    prmst.premiumtype, "
			+ " "
			+ "    nvl (prmst.penaltydays, 0) penaltydays, "
			+ " "
			+ "    va.vestingage, "
			+ " "
			+ "    nvl (l_md.organizationcode, 'Direct') Agent, "
			+ " "
			+ "    ( "
			+ " "
			+ "      pm.premiumamount + pm.insuraranceamount "
			+ " "
			+ "    ) ANNUITYAMOUNT, "
			+ " "
			+ "    pm.effectivepremiumamount "
			+ " "
			+ "  FROM "
			+ " "
			+ "    lifeannuity_planformember pm "
			+ " "
			+ "    INNER JOIN lifeannuity_membermaster lmmst "
			+ " "
			+ "      ON pm.memberid = lmmst.id "
			+ " "
			+ "    INNER JOIN lifeannuity_planmaster plm "
			+ " "
			+ "      ON pm.memberplanid = plm.id "
			+ " "
			+ "    INNER JOIN lifeannuity_annuitytypemaster am "
			+ " "
			+ "      ON pm.memberannuitytypeid = am.id "
			+ " "
			+ "    LEFT OUTER JOIN lifeannuity_vestingage va "
			+ " "
			+ "      ON pm.membervestingageid = va.id "
			+ " "
			+ "    LEFT OUTER JOIN lifeannuity_premiumtypemaster prmst "
			+ " "
			+ "      ON pm.memberpremiumtypeid = prmst.id "
			+ " "
			+ "    LEFT OUTER JOIN lifeannuity_memberdetails l_md "
			+ " "
			+ "      ON pm.id = l_md.policyid "
			+ " "
			+ "  WHERE pm.isactive = 1 "
			+ " "
			+ "    AND pm.policydate <= to_char (SYSDATE, 'DD-MON-YY') "
			+ " "
			+ "    AND pm.status = 'Active' "
			+ " "
			+ "    AND pm.memberannuitytypeid = 2) B "
			+ " "
			+ "  LEFT OUTER JOIN "
			+ " "
			+ "    (SELECT "
			+ " "
			+ "      MAX(PremiumDate) lastpremiumdate, "
			+ " "
			+ "      policyno "
			+ " "
			+ "    FROM "
			+ " "
			+ "      la_annuitypremium "
			+ " "
			+ "    GROUP BY policyno) A "
			+ " "
			+ "    ON B.policyno = A.policyno "
			+ " "
			+ "WHERE B.penaltydays <> 0 "
			+ " "
			+ "  AND b.policyno = ? "
			+ " "
			+ "ORDER BY B.memberid";


	private static final String GET_IMMEDIATE_POLICY_NO = ""
			+ "SELECT distinct pm.memberid memberid, "
			+ "                   (lmmst.Title || ' ' || lmmst.name) CUSTNAME, "
			+ "                   lmmst.currentaddress CURRADDRESS, "
			+ "                   lmmst.cityzenshipid cid, "
			+ "                   lmmst.customerno custno, "
			+ "                   pm.sumassured sumassr, "
			+ "                   pm.policyno polno, "
			+ "                   pm.policydate poldate "
			+ "                   ,pm.premiumamount preamt "
			+ "                   from lifeannuity_planformember pm "
			+ "                   inner join lifeannuity_membermaster lmmst "
			+ "                   on pm.memberid=lmmst.id "
			+ "                   where "
			+ "               pm.memberannuitytypeid=1 "
			+ "               and pm.membercitizenshipid=?";

	private static final String GET_ALL_IMMEDIATE_DETAILS = ""
			+ "SELECT DISTINCT pm.memberid memberid, "
			+ " "
			+ "                   (lmmst.Title || ' ' || lmmst.name) CUSTNAME, "
			+ " "
			+ "                   lmmst.currentaddress CURRADDRESS, "
			+ " "
			+ "                   lmmst.cityzenshipid cid, "
			+ " "
			+ "                   lmmst.customerno custno, "
			+ " "
			+ "                   to_char(pm.sumassured, '999,999,999,999.99')  sumassr, "
			+ " "
			+ "                   pm.policyno polno, "
			+ " "
			+ "                   to_char(pm.policydate,'dd/mm/rrrr')  poldate "
			+ " "
			+ "                   ,to_char(pm.premiumamount, '999,999,999,999.99') preamt "
			+ " "
			+ "                   FROM lifeannuity_planformember pm "
			+ " "
			+ "                   INNER JOIN lifeannuity_membermaster lmmst "
			+ " "
			+ "                   ON pm.memberid=lmmst.id "
			+ " "
			+ "                   WHERE "
			+ " "
			+ "               pm.memberannuitytypeid=1 "
			+ " "
			+ "               AND pm.policyno=?";
	

	private static final String INSERT_USER_OTP = "INSERT INTO sms_codes(user_id, code, status) values(?, ?, 0)";


	private static final String SELECT_USER_ID = "select id from apps_users where cid =?";


	private static final String CHECK_OTP = "SELECT count(*) otp FROM sms_codes WHERE code=?";


	
	private static final String INSERT_LIFE_PAYMENT = "INSERT INTO `transactions_latest`(`CUSTOMER_CID`, `CUSTOMER_NAME`, `DEPARTMENT_CODE`, `POLICY_ACCOUNT_NO`, "
			+ "`AMOUNT_PAID`, `ORDER_REFEREENCE_NO`, `TRANSACTION_ID`, `TRANSACTION_DATE`, `REMITTER_NAME`, `REMITTER_BANK`, `REMITTER_MOBILE_NO`, `TRANSACTION_STATUS`, "
			+ "`DATA_SOURCE`, `AUTH_CODE`, `AUTH_ID`, `CREATED_DATE`, `REMITTER_CID`, REQUEST_ID) values (?,?,?,?,?,?,'',DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'),(SELECT name FROM `apps_users` WHERE cid=?),'',(SELECT mobile FROM `apps_users` WHERE cid=?),'','MyRICB','','',DATE_FORMAT(CURRENT_TIMESTAMP, '%d/%m/%Y %H:%i:%s'),?,0)";


	private static final String GET_LINE_OF_BUSINESS = "SELECT DISTINCT(business_name) AS business_name, business_id FROM line_of_business order by business_id";
	private static final String GET_LINE_OF_BUSINESS_DETAILS = "SELECT contents FROM line_of_business where business_id=?";


	private static final String GET_AD_IMAGE_URL = "SELECT image from advertisement";


	private static final String UPDATE_PASSWORD = "UPDATE apps_users SET PASSWORD = ? where cid=? AND mobile= ?";


	private static final String GET_PPF_NO = "SELECT * FROM V_PPF_MEMO_C_YEAR_BAL WHERE citizenid=?";


	private static final String GET_GIS_NO = "SELECT "
			+ "  pol.policyno polno, SC.CODE gisno, "
			+ "  dpt.name || ' - ' || pol.name || ' (' || pol.policyno || ' )' BRANCHDEPARTMENT, "
			+ "  sc.registeredname membername, "
			+ "  ieog.citizenid CITIZENID, "
			+ "  ieog.designation desig, "
			+ "  to_char (ieog.joiningdate, 'dd/mm/rrrr') joindate, "
			+ "  DECODE( "
			+ "    ieog.groupid, "
			+ "    '4', "
			+ "    '200,000', "
			+ "    '3', "
			+ "    '300,000', "
			+ "    '2', "
			+ "    '400,000', "
			+ "    '1', "
			+ "    '500,000' "
			+ "  ) sumassured, "
			+ "  DECODE ( "
			+ "    ieog.isactiveee, "
			+ "    '1', "
			+ "    'Active', "
			+ "    '0', "
			+ "    'Inactive' "
			+ "  ) STATUS "
			+ "FROM "
			+ "  gis_sale_customer SC "
			+ "  INNER JOIN gis_investoreeorganisatndata ieog "
			+ "    ON SC.id = ieog.customerid "
			+ "  INNER JOIN gis_investorerlocation dpt "
			+ "    ON ieog.departmentid = dpt.id "
			+ "  INNER JOIN gis_policymaster pol "
			+ "    ON pol.id = ieog.policyid "
			+ "WHERE ieog.citizenid = ? "
			+ "  AND ieog.isactiveee = '1'";


	private static final String GET_USER_DETAILS = "SELECT * from apps_users where cid=?";


	private static final String UPDATE_USER_STATUS = "UPDATE apps_users SET status=1 WHERE cid=?";


	private static final String UPDATE_USER_DETAILS = "UPDATE apps_users SET NAME=?, cid=?, email=?, mobile=?, PASSWORD=?, status=? WHERE cid=?";


	private static final String GET_PPF_MEMO = ""
+ "SELECT a.code PFNumber,b.designation desig,b.citizenid,e.journaldate,to_char(e.fromdate,'Month, YYYY') due_month, amountmask(d.ercontribution) er_contr,amountmask(d.eecontribution) ee_contr,e.rtype "
+ ",( SELECT amountmask(PPFINTEREST) FROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.ercontribution,e.rtype,TO_DATE(SYSDATE)) )) er_intr "
+ ",( SELECT amountmask(PPFINTEREST) FROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.eecontribution,e.rtype,TO_DATE(SYSDATE)) )) ee_intr "
+ "FROM sale_customer a "
+ "JOIN sale_investoreeorganisatndata b "
+ "ON a.id=b.customerid "
+ "JOIN ppf_contributionflow d "
+ "ON d.ppfeeuid=a.code "
+ "JOIN ppf_contributionheader e "
+ "ON e.id=d.contributionheaderid "
+ "WHERE b.citizenid =? AND b.isactiveee=1 AND to_char(e.journaldate, 'YYYY') =to_char(SYSDATE, 'YYYY') "
+ "ORDER BY a.code,b.citizenid,d.ercontribution,d.eecontribution";


	private static final String UPDATE_OTP = "UPDATE sms_codes SET code=? where user_id=(SELECT id FROM apps_users WHERE cid=?)";

	private static final String UPDATE_LIFE_PAYMENT = "UPDATE transactions_latest SET TRANSACTION_ID=?, REMITTER_BANK=?, REMMITER_ACCOUNT_NO=?, TRANSACTION_STATUS=?, DATA_SOURCE='MyRICB', AUTH_CODE=?, AUTH_ID=?, REMARKS=? WHERE ORDER_REFEREENCE_NO=?";

	private static final String GET_FINAL_POLICY_DTLS = "SELECT * FROM transactions_latest WHERE ORDER_REFEREENCE_NO=?";

	private static final String INSERT_TO_ORACLE = "INSERT INTO ricb_mapp_transaction_details ( id, request_id,customer_cid, policy_account_no, customer_name, department_code, amount_paid, order_reference_no, transaction_id, transaction_date, remitter_name, remitter_bank, remmiter_account_no,remitter_mobile_no, transaction_status, data_source, response_code,  auth_code, auth_id, remarks, updated_date) "
			+ "VALUES (?, '0', ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, '0', ?, ? )";

	private static final String GET_SEQ_ID = "select APP_TRANSACTION_SEQ.nextval@RICB_TEST code from dual ";

	private static final String GET_APP_UPDATE_STATUS = "SELECT updatestatus FROM app_update";
	private static final String GET_COUNTRY = "select * from countries";
	private static final String GET_TITLE = "select * from ricb_com.tl_in_mas_code@RICB_COM where type_code ='4' AND status_code = 'Active' order by seq_no";
	private static final String GET_MSTATUS = "select * from ricb_com.tl_in_mas_code@RICB_COM where type_code = '2' AND status_code = 'Active' order by seq_no";
	private static final String GET_CONTACT_TYPE = "select * from ricb_com.tl_in_mas_code@RICB_COM where type_code = '3' and code_code != '2' AND status_code = 'Active' order by seq_no";
	private static final String GET_SEQ_SRL_NO = "select RICB_UWR.SEQ_OTIP_ID.nextval CODE from dual";
	
	private static final String GET_ACTIVE_GI_POLICY= "select * from V_ACTIVE_GI_POLICIES where POL_CIVIL_ID =? ";
	
	private static final String GET_ACTIVE_LI_POLICY= "select * from V_ACTIVE_LIFE_POLICY where CITIZEN_ID =? ";
	
	
	private static ricbDAO ricbdao = null; 

	public ricbDAO getInstance() {
		if(ricbdao == null){
			ricbdao = new ricbDAO();
		}
		return ricbdao;
	}
	
    public JSONArray getPolicyDetails(String cidNo, String type) throws Exception {
    	System.out.print(cidNo);
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        ToJSON convertor = new ToJSON();
        JSONArray json = new JSONArray();
        
        try {
       	 conn = ConnectionManager.getOracleConnection();
            
            if(conn != null){
                if(type.equalsIgnoreCase("lifeinsurance")){
                	System.out.print(".................");
                    pst = conn.prepareStatement(GET_POLICY_DETAILS);
                    pst.setString(1, cidNo);
                }
                if(type.equalsIgnoreCase("creditinvestment")){
					pst = conn.prepareStatement(GET_CREDIT_ACCOUNT);
					pst.setString(1, cidNo);
					pst.setString(2, cidNo);
				}
				if(type.equalsIgnoreCase("generalinsurance")){
					
					pst = conn.prepareStatement(GET_GENERAL_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("motortppolicy")){
								
					pst = conn.prepareStatement(GET_MOTOR_TP_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("motortpactivepolicy")){
					
					pst = conn.prepareStatement(GET_MOTOR_TP_ACTIVE_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("otipactivepolicy")){
					
					pst = conn.prepareStatement(GET_OTIP_ACTIVE_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("genclaimothers")){
					
					pst = conn.prepareStatement(GET_GENERAL_CLAIM_OTHERS);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("genTrackclaim")){
					
					pst = conn.prepareStatement(GET_GENERAL_CLAIM_TRACK);
					pst.setString(1, cidNo);
					
				}

				if(type.equalsIgnoreCase("checkpayment")){
						
						pst = conn.prepareStatement(GET_RL_PAYMENT);
						pst.setString(1, cidNo);
					}
				if(type.equalsIgnoreCase("getOtipCustomer")){
					
					pst = conn.prepareStatement(GET_OTIP_CUSTOMER);
					pst.setString(1, cidNo);
				}
				
				if(type.equalsIgnoreCase("deferredannuity")){
					
					pst = conn.prepareStatement(GET_DEFERRED_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("immediateannuity")){
					pst = conn.prepareStatement(GET_IMMEDIATE_POLICY_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("ppfmemo")){
					pst = conn.prepareStatement(GET_PPF_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("gis")){
					pst = conn.prepareStatement(GET_GIS_NO);
					pst.setString(1, cidNo);
				}
				if(type.equalsIgnoreCase("motorcppolicy")){
			          
			          pst = conn.prepareStatement(GET_MOTOR_CP_POLICY_NO);
			          pst.setString(1, cidNo);
			        }
			        //fire-sf
			        if(type.equalsIgnoreCase("firesfpolicy")){
			          
			          pst = conn.prepareStatement(GET_FIRE_SF_POLICY_NO);
			          pst.setString(1, cidNo);
			        }
			        //GI active policies
			        if(type.equalsIgnoreCase("generalactivepolicy")){
			          
			          pst = conn.prepareStatement(GET_ACTIVE_GI_POLICY);
			          pst.setString(1, cidNo);
			          
			        }
			      //LI active policies
			        if(type.equalsIgnoreCase("lifeactivepolicy")){
			          
			          pst = conn.prepareStatement(GET_ACTIVE_LI_POLICY);
			          pst.setString(1, cidNo);
			          
			        }
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        } finally {
            ConnectionManager.close(conn, rs, pst);
        }
        
        return json;
    }

    
	public JSONArray getRLIdetails(String cidNo,String uwYear) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_RURAL_LIFE);
				pst.setString(1, cidNo);
				pst.setString(2, uwYear);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}  

	
	public JSONArray getRLImember(String cidNo,String uwYear) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_RURAL_LIFE_MEMBER);
				pst.setString(1, cidNo);
				pst.setString(2, uwYear);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}  
	
	public JSONArray getAllPolicyDetails(String policyNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
	       	 conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_POLICY_DETAILS);
				pst.setString(1, policyNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}  
	public JSONArray motorinsurancedetails(String policyNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_MOTOR_TP_DETAILS);
				pst.setString(1, policyNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray checkrlpayment(String serialNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_RL_PAYMENT);
				pst.setString(1, serialNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	/**
	 * user registration method
	 * @param userName
	 * @param password
	 * @param phoneNo
	 * @param email
	 * @param cidNumber
	 * @return registration status
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public JSONArray insertUserDetails(String userName, String password, String phoneNo, String email,
			String cidNumber, String otp) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		
		try {
			conn = mySQLDataSource.getConnection();
			boolean userExist = false;
			String userCheck;
			int userStatus = 0;
			if(conn != null){
				//check if user is already registered or not
				pst = conn.prepareStatement(CHECK_USER_DETAILS);
				pst.setString(1, cidNumber);
				pst.setString(2, phoneNo);
				rs = pst.executeQuery();
				
				while(rs.next()){
					userCheck = rs.getString("userExist");
					if(Integer.parseInt(userCheck) == 0){
						userExist = false;
					}
					else{
						userExist = true;
					}
					userStatus = rs.getInt("status");
				}
				String userId = "";
				if(!userExist){
					conn.setAutoCommit(false);
					
					pst = conn.prepareStatement(INSERT_USER_DETAILS);
					pst.setString(1, userName);
					pst.setString(2, cidNumber);
					pst.setString(3, email);
					pst.setString(4, phoneNo);
					pst.setString(5, password);
					pst.setString(6, "0");
					int rst = pst.executeUpdate();
					
					if(rst > 0){
						//get user id
						pst = conn.prepareStatement(SELECT_USER_ID);
						pst.setString(1, cidNumber);
						rs = pst.executeQuery();
						
						while(rs.next()){
							userId = rs.getString("id");
						}
						//after successful user insertion, insert otp for that user
						if(!userId.isEmpty()){
							pst = conn.prepareStatement(INSERT_USER_OTP);
							pst.setString(1, userId);
							pst.setString(2, otp);
							rst = pst.executeUpdate();
							
							if(rst > 0){
								conn.commit();
								jsonObject.put("status", "1");
								json.put(jsonObject);
							}
							else{
								jsonObject.put("status", "2");
								json.put(jsonObject);
							}
						}
					}
				}
				else if(userExist && userStatus == 0){
					//user is already registered but didn't verify otp
					pst = conn.prepareStatement(UPDATE_USER_DETAILS);
					pst.setString(1, userName);
					pst.setString(2, cidNumber);
					pst.setString(3, email);
					pst.setString(4, phoneNo);
					pst.setString(5, password);
					pst.setString(6, "0");
					pst.setString(7, cidNumber);
					int rst = pst.executeUpdate();
					
					//update otp
					if(rst > 0){
						pst = conn.prepareStatement(UPDATE_OTP);
						pst.setString(1, otp);
						pst.setString(2, cidNumber);
						rst = pst.executeUpdate();
					}
					
					if(rst > 0){
						jsonObject.put("status", "1");
						json.put(jsonObject);
					}else{
						jsonObject.put("status", "3");
						json.put(jsonObject);
					}
				}
				else{
					jsonObject.put("status", "2");
					json.put(jsonObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("status", "3");
			json.put(jsonObject);
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray validatePassword(String cid, String password) throws Exception {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    JSONArray json = new JSONArray();
	    JSONObject jsonObject = new JSONObject();
	    
	    try {
	        conn = mySQLDataSource.getConnection();
	        if (conn != null) {
	            pst = conn.prepareStatement(CHECK_PASSWORD);
	            pst.setString(1, password);
	            pst.setString(2, cid);
	            rs = pst.executeQuery();
	            
	            if (rs.next()) {
	                int passwordCount = rs.getInt("savedPassword");
	                if (passwordCount > 0) {
	                    // Password matched
	                    jsonObject.put("status", "1");
	                    jsonObject.put("message", "Password validated successfully");
	                } else {
	                    // Password unmatched
	                    jsonObject.put("status", "0");
	                    jsonObject.put("message", "Invalid credentials");
	                }
	            } else {
	                // No result found
	                jsonObject.put("status", "0");
	                jsonObject.put("message", "User not found");
	            }
	            json.put(jsonObject);
	        } else {
	            jsonObject.put("status", "0");
	            jsonObject.put("message", "Database connection failed");
	            json.put(jsonObject);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Return error status instead of empty array
	        jsonObject.put("status", "0");
	        jsonObject.put("message", "Validation failed");
	        json.put(jsonObject);
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	}

	public JSONArray getCreditDetails(String accountNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_CREDIT_DETAILS);
				pst.setString(1, accountNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getotipplan(String days, String age) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			int number=Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
			
				pst = conn.prepareStatement(GET_OTIP_PLAN);
				pst.setInt(1, number);
				pst.setString(2, age);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getotipplanjp(String days, String age) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			int number=Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
			
				pst = conn.prepareStatement(GET_OTIP_PLAN_JP);
				pst.setInt(1, number);
				pst.setString(2, age);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getotipplanasian(String days, String age) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			int number=Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
			
				pst = conn.prepareStatement(GET_OTIP_PLAN_ASIAN);
				pst.setInt(1, number);
				pst.setString(2, age);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getotipplanall(String days, String age) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			int number=Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
			
				pst = conn.prepareStatement(GET_OTIP_PLAN_ALL);
				pst.setInt(1, number);
				pst.setString(2, age);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getotipplanfinal(String days, String age, String plan) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			int number=Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
			
				pst = conn.prepareStatement(GET_OTIP_PLAN_FINAL);
				pst.setInt(1, number);
				pst.setString(2, age);
				pst.setString(3, plan);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray genclaimStatus(String accountNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_GEN_CLAIM_STATUS);
				pst.setString(1, accountNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	

	public JSONArray getGeneralPolicyDetails(String policyNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_GENERAL_DETAILS);
				pst.setString(1, policyNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray getDeferredDetails(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_DEFERRED_DETAILS);
				pst.setString(1, policyNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray getImmediateDetails(String policyNo)  throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_ALL_IMMEDIATE_DETAILS);
				pst.setString(1, policyNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray validateOTP(String otp, String cid) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			conn = mySQLDataSource.getConnection();
			String otpCheck;
			if(conn != null){
				pst = conn.prepareStatement(CHECK_OTP);
				pst.setString(1, otp);
				rs = pst.executeQuery();
				while(rs.next()){
					otpCheck = rs.getString("otp");
					if(otpCheck.equals("0")){
						//otp unmatched
						jsonObject.put("status", "0");
						json.put(jsonObject);
					}
					else{
						//update user status first
						pst = conn.prepareStatement(UPDATE_USER_STATUS);
						pst.setString(1, cid);
						int rst = pst.executeUpdate();
						if(rst > 0){
							//otp matched
							jsonObject.put("status", "1");
							json.put(jsonObject);
						}
						else{
							//otp unmatched
							jsonObject.put("status", "0");
							json.put(jsonObject);
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray insertLifePaymentDetails(String cidNo, String custName, String deptCode, String policyNo,
			String amount, String orderNo, String remitterCid) throws Exception {
		System.out.println("remitter cid "+remitterCid);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(INSERT_LIFE_PAYMENT);
				pst.setString(1, cidNo);
				pst.setString(2, custName);
				pst.setString(3, deptCode);
				pst.setString(4, policyNo);
				pst.setString(5, amount);
				pst.setString(6, orderNo);
				pst.setString(7, remitterCid);
				pst.setString(8, remitterCid);
				pst.setString(9, remitterCid);
				int rst = pst.executeUpdate();
				
				if(rst > 0){
					jsonObject.put("status", "1");
					json.put(jsonObject);
				}
				else{
					jsonObject.put("status", "0");
					json.put(jsonObject);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray updateLifePaymentDetails(String txnId, String remitterBank, String accNo, String authCode,
			String orderNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(UPDATE_LIFE_PAYMENT);
				pst.setString(1, txnId);
				pst.setString(2, remitterBank);
				pst.setString(3, accNo);
				System.out.println("auth code is "+authCode);
				if(authCode.equals("00"))
					pst.setString(4, "SUCCESS");
				else
					pst.setString(4, "FAILED");
				pst.setString(5, authCode);
				pst.setString(6, "0");
				pst.setString(7, "Updated");
				pst.setString(8, orderNo);
				int rst = pst.executeUpdate();
				//pst.close();
				
				if(rst > 0){
					insertToOracle(txnId, remitterBank, accNo,  authCode, orderNo);
				}
				else{
					jsonObject.put("status", "0");
					json.put(jsonObject);
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	private void insertToOracle(String txnId, String remitterBank, String accNo, String authCode, String orderNo) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pst = null;
		//PreparedStatement pst1 = null;
		ResultSet rs = null;
		try{
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				//get all details from transaction_latest to insert to oracle db
				String cidNo = null; String policyNo = null; String name = null; String deptCode = null; String amount = null;//double amount = 0; 
				String txnDate = null; String remitterName = null; String mobileNo = null; String txnStatus = null;
				pst = conn.prepareStatement(GET_FINAL_POLICY_DTLS);
				pst.setString(1, orderNo);
				rs = pst.executeQuery();
				while(rs.next()){
					cidNo = rs.getString("CUSTOMER_CID");
					policyNo = rs.getString("POLICY_ACCOUNT_NO");
					name = rs.getString("CUSTOMER_NAME");
					deptCode = rs.getString("DEPARTMENT_CODE");
//					amount = rs.getInt("AMOUNT_PAID");
					amount = rs.getString("AMOUNT_PAID");
					txnDate = rs.getString("TRANSACTION_DATE");
					remitterName = rs.getString("REMITTER_NAME");
					mobileNo = rs.getString("REMITTER_MOBILE_NO");
					if(authCode.equals("00"))
						txnStatus = "SUCCESS";
					else
						txnStatus = "FAILED";
				}
				rs.close();
				pst.close();
				//get seqid from oracle db
//				int convertedAmt = (int) amount;
				SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
				String actualTxDate = myFormat.format(fromUser.parse(txnDate));
				//DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date curdate = new Date();
				System.out.println("current date and time is "+fromUser.format(curdate));
//				System.out.println("amount is "+convertedAmt);
				System.out.println("Transaction Date is "+actualTxDate);
				int seqId = 0;
				Connection connOra = dataSource.getConnection();
				pst = connOra.prepareStatement(GET_SEQ_ID);
				rs = pst.executeQuery();
				while(rs.next()){
					seqId = rs.getInt("code");
					//seqId ++;
				}
				rs.close();
				
				//now insert in oracle db
				pst = connOra.prepareStatement(INSERT_TO_ORACLE);
				pst.setInt(1, seqId);
				pst.setString(2, cidNo);
				pst.setString(3, policyNo);
				pst.setString(4, name);
				pst.setString(5, deptCode);
//				pst.setLong(6, convertedAmt);
				pst.setString(6, amount);
				pst.setString(7, orderNo);
				pst.setString(8, txnId);
				pst.setString(9, actualTxDate);
				pst.setString(10, remitterName);
				pst.setString(11, remitterBank);
				pst.setString(12, accNo);
				pst.setString(13, mobileNo);
				pst.setString(14, txnStatus);
				pst.setString(15, "MyRICB");
				pst.setString(16, "0");
				pst.setString(17, authCode);
				pst.setString(18, "MyRICB");
				pst.setString(19, fromUser.format(curdate));
				rs = pst.executeQuery();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
	}

	public JSONArray lineofbusiness() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_LINE_OF_BUSINESS);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray lineofbusinessdetails(String id) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_LINE_OF_BUSINESS_DETAILS);
				pst.setString(1, id);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray advertisement() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_AD_IMAGE_URL);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray updatePassword(String cidNo, String mobileNo, String password) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(UPDATE_PASSWORD);
				pst.setString(1, password);
				pst.setString(2, cidNo);
				pst.setString(3, mobileNo);
				int rst = pst.executeUpdate();
				
				if(rst > 0){
					//Password Update
					jsonObject.put("status", "1");
					json.put(jsonObject);
				}
				else{
					//Update failed
					jsonObject.put("status", "0");
					json.put(jsonObject);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}

	public JSONArray getUserDetails(String cidNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		ToJSON convertor = new ToJSON();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_USER_DETAILS);
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray sendmail(String to, String from, String subject, String message) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		//JSONObject jsonObject = new JSONObject();
		try {
			
			emailUtil.generateAndSendEmail(to, from, subject, message);
			
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	
	}

	public JSONArray forgotpasswordotp(String cidNumber, String otp)  throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(INSERT_USER_OTP);
				pst.setString(1, cidNumber);
				pst.setString(2, otp);
				int rst = pst.executeUpdate();
				if(rst > 0)
				{
					jsonObject.put("status", "1");
					json.put(jsonObject);
				}
				else{
					jsonObject.put("status", "0");
					json.put(jsonObject);
				}
					
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray ppfmemo(String cidNo)  throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_PPF_MEMO);
				pst.setString(1, cidNo);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	//check for app update
	public JSONArray appversion()  throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		ToJSON convertor = new ToJSON();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_APP_UPDATE_STATUS);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
					
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	public JSONArray getcountry()  throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		ToJSON convertor = new ToJSON();
		try {
			conn = mySQLDataSource.getConnection();
			if(conn != null){
				pst = conn.prepareStatement(GET_COUNTRY);
				rs = pst.executeQuery();
				json = convertor.toJSONArray(rs);
					
			}
		}catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray gettitle() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_TITLE);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray getMstatus() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_MSTATUS);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray getContactType() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_CONTACT_TYPE);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	public JSONArray getSeqId() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		
		try {
			conn = ConnectionManager.getOracleConnection();
			
			if(conn != null){
				pst = conn.prepareStatement(GET_SEQ_SRL_NO);
				rs = pst.executeQuery();

				json = convertor.toJSONArray(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		
		return json;
	}
	
	//RLI 2026
	  public JSONArray getRLIprem()  throws Exception {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    JSONArray json = new JSONArray();
	    ToJSON convertor = new ToJSON();
	    try {
	      conn = ConnectionManager.getLifeConnection();
	      if(conn != null){
	        pst = conn.prepareStatement(GET_RLI_PREMIUM);
	        rs = pst.executeQuery();
	        json = convertor.toJSONArray(rs);
	          
	      }
	    }catch (Exception e) {
	      e.printStackTrace();
	      return json;
	    } finally {
	      ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	  }
	  
	  public JSONArray getPaidStatus(String householdNumber)  throws Exception {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    ToJSON convertor = new ToJSON();
	    JSONArray json = new JSONArray();
	    
	    try {
	      conn = ConnectionManager.getOracleConnection();
	      
	      if(conn != null){
	        pst = conn.prepareStatement(GET_RLI_STATUS);
	        pst.setString(1, householdNumber);
	        rs = pst.executeQuery();

	        json = convertor.toJSONArray(rs);
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      return json;
	    } finally {
	      ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	  }
	  
	  //MCP RENEWAL
	  public JSONArray motormcpinsurancedetails(String policyNo) throws Exception
	  {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    ToJSON convertor = new ToJSON();
	    JSONArray json = new JSONArray();
	    
	    try {
	      conn = ConnectionManager.getOracleConnection();
	      
	      if(conn != null){
	        pst = conn.prepareStatement(GET_ALL_MOTOR_CP_DETAILS);
	        pst.setString(1, policyNo);
	        rs = pst.executeQuery();

	        json = convertor.toJSONArray(rs);
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      return json;
	    } finally {
	      ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	  }
	    
	    public JSONArray mcpPrevdetails(String policyNo) throws Exception
	  {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    ToJSON convertor = new ToJSON();
	    JSONArray json = new JSONArray();
	    
	    try {
	      conn = ConnectionManager.getOracleConnection();
	      
	      if(conn != null){
	        pst = conn.prepareStatement(GET_ALL_MCP_PREV_DETAILS);
	        pst.setString(1, policyNo);
	        rs = pst.executeQuery();

	        json = convertor.toJSONArray(rs);
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      return json;
	    } finally {
	      ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	  }
	    
	    //FIRE_SF RENEWAL
	    public JSONArray fireSFPrevdetails(String policyNo) throws Exception
	  {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    ToJSON convertor = new ToJSON();
	    JSONArray json = new JSONArray();
	    
	    try {
	      conn = ConnectionManager.getOracleConnection();
	      
	      if(conn != null){
	        pst = conn.prepareStatement(GET_ALL_FIRE_SF_PREV_DETAILS);
	        pst.setString(1, policyNo);
	        rs = pst.executeQuery();

	        json = convertor.toJSONArray(rs);
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      return json;
	    } finally {
	      ConnectionManager.close(conn, rs, pst);
	    }
	    
	    return json;
	  }
	
	    public JSONArray fireSFdetails(String policyNo) throws Exception
		{
			Connection conn = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			
			ToJSON convertor = new ToJSON();
			JSONArray json = new JSONArray();
			
			try {
				conn = ConnectionManager.getOracleConnection();
				
				if(conn != null){
					pst = conn.prepareStatement(GET_ALL_FIRE_SF_DETAILS);
					pst.setString(1, policyNo);
					rs = pst.executeQuery();

					json = convertor.toJSONArray(rs);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return json;
			} finally {
				ConnectionManager.close(conn, rs, pst);
			}
			
			return json;
		} 
	    
	    public JSONArray resetPin(String cidNo, String newPIN) throws Exception {
	        Connection conn = null;
	        PreparedStatement pst = null;
	        ResultSet rs = null;
	        JSONArray json = new JSONArray();
	        ToJSON convertor = new ToJSON();

	        try {
	            conn = mySQLDataSource.getConnection();
	            if (conn != null) {
	                // Check if user exists
	                String checkSql = "SELECT * FROM apps_users WHERE cid = ?";
	                pst = conn.prepareStatement(checkSql);
	                pst.setString(1, cidNo);
	                rs = pst.executeQuery();

	                if (rs.next()) {
	                    // Update password (PIN)
	                    String updateSql = "UPDATE apps_users SET password = ? WHERE cid = ?";
	                    pst = conn.prepareStatement(updateSql);
	                    pst.setString(1, newPIN);
	                    pst.setString(2, cidNo);
	                    int updated = pst.executeUpdate();

	                    // Prepare a simple result dataset
	                    String statusSql = "SELECT " + (updated > 0 ? "'1'" : "'0'") + " AS status";
	                    pst = conn.prepareStatement(statusSql);
	                    rs = pst.executeQuery();

	                    json = convertor.toJSONArray(rs);
	                } else {
	                    // User not found
	                    String notFoundSql = "SELECT '0' AS status";
	                    pst = conn.prepareStatement(notFoundSql);
	                    rs = pst.executeQuery();
	                    json = convertor.toJSONArray(rs);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return json;
	        } finally {
	            ConnectionManager.close(conn, rs, pst);
	        }

	        return json;
	    }



	
}