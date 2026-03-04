package bt.ricb.ricb_api.dao.implementations;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.TransactionDao;
import bt.ricb.ricb_api.util.ToJSON;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionImpl implements TransactionDao {
	private static final String GET_POLICY_DETAILS = "SELECT a.product_code product_code,\r\n  a.policy_no policy_no,\r\n  TO_CHAR( a.policy_start_date, 'DD/MM/RRRR' ) incept_date,\r\n  TO_CHAR(a.policy_end_date, 'DD/MM/RRRR') maturity_date,\r\n  TO_CHAR( a.sum_assured, '9,999,999,999,999.99' ) sum_assured,\r\n  c.premium_per_instalment premium,\r\n  DECODE( a.mode_of_payment, 'M', 'MONTHLY', 'S', 'SSS', 'Q', 'QUARTERLY', 'H', 'HALF YEARLY', 'Y', 'YEARLY', 'O', 'SINGLE' ) AS p_mode,\r\n  DECODE( a.status_code, 'G', 'Active Policy', 'H', 'LAPSED', 'I', 'Lapsed Paid         \r\nUp', 'J', 'Forfieted', 'K', 'Matured', 'L', 'Closed- Maturity', 'M', 'Death         \r\nClaim Entered/Registered', 'MM', 'Surrender Claim Entered/', 'N', 'Closed-         \r\nDeath', 'P', 'Closed- Surrender', 'Q', 'Closed- Forfeiture', 'S', 'Closed-         \r\nClaim Declined', 'U', 'Closed- PTD', 'V', 'Cancelled', 'X', 'Rejected' ) POLSTATUS,\r\n  TO_CHAR (MIN (aa.due_date), 'DD/MM/YYYY') next_repay,\r\n  b.customer_name POLICY_HOLDER,\r\n  b.citizen_id CUST_CID,\r\n  a.policy_term term,\r\n  TO_CHAR (MAX (bb.due_date), 'DD/MM/YYYY') LAST_PAID_DATE\r\nFROM RICB_LI.tl_li_tr_policy_header@RICB_COM a,\r\n  RICB_COM.tl_in_mas_customer@RICB_COM b,\r\n  RICB_LI.tl_li_tr_premium_list@RICB_COM c,\r\n  (SELECT *\r\n  FROM RICB_LI.tl_li_tr_premium_list@RICB_COM\r\n  WHERE status_code = 'PENDING'\r\n  ) aa,\r\n  (SELECT *\r\n  FROM RICB_LI.tl_li_tr_premium_list@RICB_COM\r\n  WHERE status_code = 'PAID'\r\n  ) bb\r\nWHERE a.customer_code   = b.customer_code\r\nAND aa.policy_serial_no = a.serial_no\r\nAND bb.policy_serial_no = a.serial_no\r\nAND a.serial_no         = c.policy_serial_no\r\nAND c.status_code       = 'PENDING'\r\nAND b.citizen_id LIKE '%'\r\n  || ?\r\n  || '%'\r\nGROUP BY a.product_code,\r\n  a.policy_no,\r\n  a.sum_assured,\r\n  a.policy_start_date,\r\n  a.policy_end_date,\r\n  a.mode_of_payment,\r\n  a.status_code,\r\n  b.customer_name,\r\n  c.premium_per_instalment,\r\n  a.policy_term,\r\n  b.citizen_id";
	private static final String GET_RL_PAYMENT = "SELECT a.UNDERWRITING_YEAR,\r\n  a.STATUS_CODE\r\nFROM RICB_LI.TL_LI_TR_RURAL_MAPP_TRN_FINAL@RICB_COM a,\r\n  RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM b\r\nWHERE a.STATUS_CODE     ='PAID'\r\nOR b.status_code!       ='A'\r\nAND a.PERMANENT_HOUSE_NO=b.PERMANENT_HOUSE_NO\r\nAND a.PERMANENT_HOUSE_NO= ? \r\n";
	private static final String GET_ALL_CREDIT_DETAILS = "SELECT A.ACCOUNT_holder,\r\n\t  NVL(A.PVR_Citizenship_number, A.PVR_Registration_number) cid_no,\r\n\t  A.credit_id ,\r\n\t  A.credit_account_number account_no ,\r\n\t  TO_CHAR(A.sanction_amount, '999,999,999,999.99') sanc_amt ,\r\n\t  TO_CHAR(A.sanction_date,'dd/mm/rrrr') sanc_date ,\r\n\t  TO_CHAR(A.expiry_date,'dd/mm/rrrr') exp_dt ,\r\n\t  TO_CHAR(A.repayment_effective_date,'dd/mm/rrrr') repay_date ,\r\n\t  A.rate_of_interest int_rt ,\r\n\t  TO_CHAR(A.installment_amount, '999,999,999,999.99') emi ,\r\n\t  A.status STATUS ,\r\n\t  A.term ,\r\n\t  A.mode_of_repayment repay_mode ,\r\n\t  A.repayment_effective_date ,\r\n\t  TO_CHAR(B.INTEREST_OD_AMOUNT, '999,999,999,999.99') INTODAMT ,\r\n\t  get_nextrepaymentdt@RICB_COM(TO_CHAR(A.repayment_effective_date,'dd/mm/rrrr'),A.mode_of_repayment) next_repaymt,\r\n\t  B.LATE_FEE LATEFEE ,\r\n\t  B.OD_INDICATOR OD_DAYS\r\n\tFROM RICB_UWR.T_CID_LOAN_LISTS@RICB_COM A ,\r\n\t  RICB_UWR.V_CREDIT_PAYMENT_CYCLE@RICB_COM B\r\n\tWHERE credit_account_number IS NOT NULL\r\n\tAND A.credit_account_number  = '1232341234'\r\n\tAND UPPER(A.status) NOT LIKE 'CLOSED%'\r\n\tAND UPPER(A.status) NOT LIKE 'DECLINED%'\r\n\tAND A.CREDIT_ID                                      = B.CRDT_CREDIT_ID\r\n\tAND TO_CHAR(B.CREATED_DATE, 'DD/MM/RRRR HH24:MI:SS') =\r\n\t  (SELECT TO_CHAR(MAX(X.CREATED_DATE),'DD/MM/RRRR HH24:MI:SS')\r\n\t  FROM RICB_UWR.V_CREDIT_PAYMENT_CYCLE@RICB_COM X\r\n\t  WHERE X.CRDT_CREDIT_ID              = A.CREDIT_ID\r\n\t  AND x.transaction_type              = x.transaction_type\r\n\t  AND X.CRDT_CREDIT_PAYMENT_CYCLE_ID IS NULL\r\n\t  )\r\n";
	private static final String GET_OTIP_PLAN = "SELECT a.col_1 code,\r\n  a.col_6 premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1      ='T2'\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1 ";
	private static final String GET_OTIP_PLAN_JP = "SELECT a.col_1 code,\r\n  a.col_6 premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1      ='T2'\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1 ";
	private static final String GET_OTIP_PLAN_ASIAN = "SELECT a.col_1 code,\r\n  a.col_6 premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1     IN ('T2', 'T1','T3')\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1";
	private static final String GET_OTIP_PLAN_ALL = "SELECT a.col_1 code,\r\n  a.col_6 premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1     IN ('T2', 'T1')\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1\r\n";
	private static final String GET_GENERAL_POLICY_NO = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  c.PRODUCT_DESC policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,\r\n  TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status\r\nFROM RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,\r\n  RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b,\r\n  RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c\r\nWHERE b.citizen_id IN (?)\r\nAND a.customer_code =b.CUSTOMER_CODE\r\nAND a.product_code  =c.product_code\r\nAND a.status_code  IN ('G','F')\r\n";
	public static final String GET_MOTOR_TP_POLICY_NO = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO      = e.POLICY_SRL_NO\r\nAND e.CODE_CODE      = 8\r\nAND a.policy_end_date BETWEEN add_months(sysdate,-12) AND add_months(sysdate,3)\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I')\r\nAND b.citizen_id        IN (?)";
	private static final String GET_MOTOR_TP_ACTIVE_POLICY_NO = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code     = 'PR00000043'\r\nAND a.customer_code      = b.customer_code\r\nAND a.serial_no          = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO          = e.POLICY_SRL_NO\r\nAND e.CODE_CODE          = 8\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?)\r\nORDER BY a.policy_end_date,\r\n  a.policy_no ;\r\n";
	private static final String GET_MOTOR_TP_POLICY_DTL = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO      = e.POLICY_SRL_NO\r\nAND e.CODE_CODE      = 8\r\nAND a.policy_end_date BETWEEN add_months(sysdate,-12) AND add_months(sysdate,3)\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I')\r\nAND a.policy_no         IN (?) ";
	private static final String GET_OTIP_ACTIVE_POLICY_NO = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b\r\nWHERE a.product_code     = 'PR00000032'\r\nAND a.customer_code      = b.customer_code\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?) ";
	private static final String GET_ACTIVE_GI_POLICY = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  a.policy_no pol_no,\r\n  a.product_code,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b\r\nWHERE a.product_code    IN ('PR00000032','PR00000043')\r\nAND a.customer_code      = b.customer_code\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?)";
	private static final String GET_GENERAL_CLAIM_OTHERS = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  c.product_group_ri policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,\r\n  TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status,\r\n  d.registration_no reg_no\r\nFROM RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,\r\n  RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b,\r\n  RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c ,\r\n  RICB_GI.TL_GI_TR_MOTOR_DTL@RICB_COM d\r\nWHERE b.citizen_id   IN (?)\r\nAND a.customer_code   =b.CUSTOMER_CODE\r\nAND a.product_code    =c.product_code\r\nAND a.status_code    IN ('G','A','H')\r\nAND c.product_group_ri='MOTR'\r\nAND a.serial_no       =d.policy_hdr_srl_no";
	private static final String GET_GENERAL_CLAIM_TRACK = "SELECT a.policy_no POLICY_NO,\r\n  a.CLAIM_INTM_NO CLAIM_INTM_NO,\r\n  TO_CHAR(a.CLAIM_INTM_DATE, 'dd/mm/rrrr') CLAIM_INTM_DATE,\r\n  TO_CHAR(a.DATE_OF_LOSS, 'dd/mm/rrrr') DATE_OF_LOSS,\r\n  c.STATUS_DESC,\r\n  a.status_code,\r\n  b.INTIMATOR_CID\r\nFROM RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,\r\n  RICB_GI.CLAIMS_INTIMATION@RICB_COM b,\r\n  ricb_com.tl_in_mas_status@RICB_COM c\r\nWHERE a.policy_no  =b.policy_no\r\nAND b.INTIMATOR_CID= ?\r\nAND a.status_code  = c.status_code\r\nAND c.table_name   = 'tl_gi_tr_claims_header'\r\nGROUP BY a.policy_no,\r\n  a.CLAIM_INTM_NO,\r\n  a.CLAIM_INTM_DATE,\r\n  a.DATE_OF_LOSS,\r\n  c.STATUS_DESC,\r\n  b.INTIMATOR_CID,\r\n  a.status_code ;\r\n";
	private static final String GET_GEN_CLAIM_STATUS = "SELECT a.policy_no POLICY_NO,\r\n  a.status_code,\r\n  b.INTIMATOR_CID\r\nFROM RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,\r\n  RICB_GI.CLAIMS_INTIMATION@RICB_COM b,\r\n  ricb_com.tl_in_mas_status@RICB_COM c\r\nWHERE a.policy_no      =b.policy_no\r\nAND A.POLICY_NO        =?\r\nAND a.status_code      = c.status_code\r\nAND c.table_name       = 'tl_gi_tr_claims_header'\r\nAND a.status_code NOT IN ('G','X', 'F')\r\nGROUP BY a.policy_no,\r\n  b.INTIMATOR_CID,\r\n  a.status_code";
	private static final String GET_RURAL_LIFE_MEMBER = "SELECT b.MEMBER_NAME,\r\n  b.DATE_OF_BIRTH,\r\n  b.CITIZEN_ID,\r\n  DECODE(b.STATUS_CODE,'A','Active','X','Deceased','') status\r\nFROM RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM a,\r\n  RICB_LI.TL_LI_TR_RURAL_POL_DTL@RICB_COM b\r\nWHERE a.SERIAL_NO       = b.HDR_SERIAL_NO\r\nAND a.CITIZEN_ID        = ?\r\nAND a.UNDERWRITING_YEAR = ?\r\nAND b.STATUS_CODE       = 'A'\r\nORDER BY b.DATE_OF_BIRTH ";
	private static final String GET_OTIP_CUSTOMER = "SELECT CUSTOMER_NAME,\r\n  TO_CHAR(DATE_OF_BIRTH,'rrrr-mm-dd') DOB,\r\n  CITIZEN_ID,\r\n  PASSPORT_NO\r\nFROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM\r\nWHERE CITIZEN_ID LIKE ?";
	private static final String GET_ALL_GENERAL_DETAILS = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  c.PRODUCT_DESC policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,\r\n  TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium,\r\n  b.customer_name,\r\n  e.CODE_DESC,\r\n  m.MODEL_NAME vehicle_type,\r\n  DECODE(a.status_code,'G','Active','I','Expired','F','Active','H','Lapsed') STATUS,\r\n  d.REGISTRATION_NO\r\nFROM RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,\r\n  RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b,\r\n  RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_MOTOR_DTL@RICB_COM d,\r\n  ricb_gi.tl_gi_mas_code@RICB_COM e,\r\n  ricb_gi.tl_gi_mas_vehicle_model@RICB_COM m\r\nWHERE a.policy_no LIKE ?\r\nAND a.customer_code    =b.CUSTOMER_CODE\r\nAND a.product_code     =c.product_code\r\nAND a.serial_no        =d.policy_hdr_srl_no(+)\r\nAND d.VEHICLE_CATEGORY = e.CODE_CODE(+)\r\nAND d.MODEL            = m.MODEL_CODE(+)\r\nAND e.type_code(+)     = '84'\r\nAND a.status_code     IN ('G','F','I') \r\n";
	private static final String GET_ALL_MOTOR_TP_DETAILS = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'Motor Third Party' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.policy_start_date,'dd/mm/yyyy') inception_date,\r\n  TO_CHAR(a.policy_end_date,'dd/mm/yyyy') expiry_date,\r\n  e.code_value premium,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no,\r\n  d.CODE_DESC,\r\n  m.MODEL_NAME VEHICLE_TYPE\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e,\r\n  ricb_gi.tl_gi_mas_code@RICB_COM d,\r\n  ricb_gi.tl_gi_mas_vehicle_model@RICB_COM m\r\nWHERE a.product_code     = 'PR00000043'\r\nAND a.customer_code      = b.customer_code\r\nAND a.serial_no          = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO          = e.POLICY_SRL_NO\r\nAND e.CODE_CODE          = 8\r\nAND c.VEHICLE_CATEGORY   = d.CODE_CODE\r\nAND d.type_code          = '84'\r\nAND c.MODEL              = m.MODEL_CODE\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_end_date,\r\n  a.policy_no ";
	private static final String GET_MOTOR_TP_DETAILS = "SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'Motor Third Party' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.policy_start_date,'dd/mm/yyyy') inception_date,\r\n  TO_CHAR(a.policy_end_date,'dd/mm/yyyy') expiry_date,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no,\r\n  ABS(months_between(TO_DATE(policy_end_date,'dd/mm/rrrr'),TO_DATE(sysdate,'dd/mm/rrrr'))) months_diff,\r\n  NVL(a.renewed_policy_no,'renew') renewed_policy_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.status_code   IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_end_date,\r\n  a.policy_no";
	private static final String GET_GEN_REPORT_POLICY_DETAILS = "SELECT a.serial_no pol_sys_id,\r\n  a.policy_no pol_no,\r\n  a.product_code,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a\r\nWHERE a.product_code IN ('PR00000043','PR00000032')\r\nAND a.policy_no      IS NOT NULL\r\nAND a.status_code    IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_no";
	private static final String GET_TITLE = "SELECT *\r\nFROM ricb_com.tl_in_mas_code@RICB_COM\r\nWHERE type_code ='4'\r\nAND status_code = 'Active'\r\nORDER BY seq_no";
	private static final String GET_MSTATUS = "SELECT *\r\nFROM ricb_com.tl_in_mas_code@RICB_COM\r\nWHERE type_code = '2'\r\nAND status_code = 'Active'\r\nORDER BY seq_no";
	private static final String GET_CONTACT_TYPE = "SELECT DISTINCT pm.memberid memberid,\r\n  (lmmst.Title\r\n  || ' '\r\n  || lmmst.name) CUSTNAME,\r\n  lmmst.currentaddress CURRADDRESS,\r\n  lmmst.cityzenshipid cid,\r\n  lmmst.customerno custno,\r\n  TO_CHAR(pm.sumassured, '999,999,999,999.99') sumassr,\r\n  pm.policyno polno,\r\n  TO_CHAR(pm.policydate,'dd/mm/rrrr') poldate ,\r\n  TO_CHAR(pm.premiumamount, '999,999,999,999.99') preamt\r\nFROM RICB_FAS.lifeannuity_planformember@RICB_COM pm\r\nINNER JOIN RICB_FAS.lifeannuity_membermaster@RICB_COM lmmst\r\nON pm.memberid              =lmmst.id\r\nWHERE pm.memberannuitytypeid=1\r\nAND pm.policyno=?";
	private static final String GET_ALL_IMMEDIATE_DETAILS = "SELECT DISTINCT pm.memberid memberid,\r\n  (lmmst.Title\r\n  || ' '\r\n  || lmmst.name) CUSTNAME,\r\n  lmmst.currentaddress CURRADDRESS,\r\n  lmmst.cityzenshipid cid,\r\n  lmmst.customerno custno,\r\n  pm.sumassured sumassr,\r\n  pm.policyno polno,\r\n  pm.policydate poldate ,\r\n  pm.premiumamount preamt\r\nFROM RICB_FAS.lifeannuity_planformember@RICB_COM pm\r\nINNER JOIN RICB_FAS.lifeannuity_membermaster@RICB_COM lmmst\r\nON pm.memberid              =lmmst.id\r\nWHERE pm.memberannuitytypeid=1\r\nAND pm.membercitizenshipid  =?";
	private static final String GET_IMMEDIATE_POLICY_NO = ""
		      + "SELECT distinct pm.memberid memberid, "
		      + "                   (lmmst.Title  ' '  lmmst.name) CUSTNAME, "
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
	private static final String GET_GIS_NO = "SELECT * FROM RICB_UWR.V_PPF_MEMO_C_YEAR_BAL@RICB_COM WHERE citizenid=?";
	private static final String GET_PPF_NO = "SELECT pm.policyno\r\nFROM RICB_FAS.lifeannuity_planformember@RICB_COM pm\r\nINNER JOIN RICB_FAS.lifeannuity_membermaster@RICB_COM lmmst\\r\\n\r\nON pm.memberid         =lmmst.id\r\nWHERE pm.status        ='Active'\r\nAND lmmst.cityzenshipid=?";
	private static final String GET_ALL_DEFERRED_DETAILS = "SELECT B.Custname,\r\n  b.cityzenshipid,\r\n  TO_CHAR ( b.SUMASSURED, '999,999,999,999.99' ) SUMASSURED,\r\n  b.policyno ploicy_no,\r\n  TO_CHAR ( b.Effectivepremiumamount, '999,999,999,999.99' ) PREMIUMAMOUNT,\r\n  B.premiumtype,\r\n  TO_CHAR (b.POLICYDATE, 'dd/mm/rrrr') ploicy_date,\r\n  TO_CHAR (b.premiumclosingdate, 'dd/mm/rrrr') ploicy_vesting_date,\r\n  TO_CHAR (a.lastpremiumdate, 'dd/mm/rrrr') Paid_up_to_date,\r\n  CASE\r\n    WHEN TO_CHAR (A.lastpremiumdate, 'DD') = TO_CHAR (b.policydate, 'DD')\r\n    AND A.lastpremiumdate                 IS NOT NULL\r\n    THEN add_months (A.lastpremiumdate, 1)\r\n    WHEN TO_CHAR (A.lastpremiumdate, 'DD')                       <> TO_CHAR (b.policydate, 'DD')\r\n    AND ( ( TO_CHAR ( add_months (A.lastpremiumdate, 1), 'MON' ) <> 'FEB' )\r\n    OR ( TO_CHAR ( add_months (A.lastpremiumdate, 1), 'MON' )     = 'FEB'\r\n    AND TO_CHAR (b.policydate, 'DD')                             <= '28' ) )\r\n    AND A.lastpremiumdate                                        IS NOT NULL\r\n    THEN to_date ( TO_CHAR (b.policydate, 'DD')\r\n      || '-'\r\n      || TO_CHAR ( add_months (A.lastpremiumdate, 1), 'MON' )\r\n      || '-'\r\n      || TO_CHAR ( add_months (A.lastpremiumdate, 1), 'YYYY' ), 'dd-mon-yyyy' )\r\n    WHEN TO_CHAR (A.lastpremiumdate, 'DD')                  <> TO_CHAR (b.policydate, 'DD')\r\n    AND TO_CHAR ( add_months (A.lastpremiumdate, 1), 'MON' ) = 'FEB'\r\n    AND TO_CHAR (b.policydate, 'DD')                         > '28'\r\n    AND A.lastpremiumdate                                   IS NOT NULL\r\n    THEN to_date ( '28'\r\n      || '-'\r\n      || TO_CHAR ( add_months (A.lastpremiumdate, 1), 'MON' )\r\n      || '-'\r\n      || TO_CHAR ( add_months (A.lastpremiumdate, 1), 'YYYY' ), 'dd-mon-yyyy' )\r\n    WHEN A.lastpremiumdate IS NULL\r\n    THEN b.policydate\r\n  END next_due_months,\r\n  TRUNC ( months_between ( TO_CHAR (SYSDATE, 'DD-MON-YY'), ( add_months (A.lastpremiumdate, B.noofmonths) ) ), 0 ) + 1 deu_months,\r\n  b.effectivepremiumamount,\r\n  ( TRUNC ( months_between ( TO_CHAR (SYSDATE, 'DD-MON-YY'), ( add_months (A.lastpremiumdate, B.noofmonths) ) ), 0 ) + 1 ) * b.effectivepremiumamount AS total_payable\r\nFROM\r\n  (SELECT DISTINCT pm.memberid,\r\n    (lmmst.Title\r\n    || ' '\r\n    || lmmst.name) CUSTNAME,\r\n    lmmst.currentaddress CURRADDRESS,\r\n    lmmst.cityzenshipid,\r\n    lmmst.customerno,\r\n    pm.sumassured,\r\n    pm.policyno,\r\n    pm.policydate,\r\n    pm.premiumclosingdate,\r\n    plm.planname,\r\n    am.annuitytype,\r\n    NVL (prmst.noofmonths, 0) noofmonths,\r\n    prmst.premiumtype,\r\n    NVL (prmst.penaltydays, 0) penaltydays,\r\n    va.vestingage,\r\n    NVL (l_md.organizationcode, 'Direct') Agent,\r\n    ( pm.premiumamount + pm.insuraranceamount ) ANNUITYAMOUNT,\r\n    pm.effectivepremiumamount\r\n  FROM RICB_FAS.lifeannuity_planformember@RICB_COM pm\r\n  INNER JOIN RICB_FAS.lifeannuity_membermaster@RICB_COM lmmst\r\n  ON pm.memberid = lmmst.id\r\n  INNER JOIN RICB_FAS.lifeannuity_planmaster@RICB_COM plm\r\n  ON pm.memberplanid = plm.id\r\n  INNER JOIN RICB_FAS.lifeannuity_annuitytypemaster@RICB_COM am\r\n  ON pm.memberannuitytypeid = am.id\r\n  LEFT OUTER JOIN RICB_FAS.lifeannuity_vestingage@RICB_COM va\r\n  ON pm.membervestingageid = va.id\r\n  LEFT OUTER JOIN RICB_FAS.lifeannuity_premiumtypemaster@RICB_COM prmst\r\n  ON pm.memberpremiumtypeid = prmst.id\r\n  LEFT OUTER JOIN RICB_FAS.lifeannuity_memberdetails@RICB_COM l_md\r\n  ON pm.id                   = l_md.policyid\r\n  WHERE pm.isactive          = 1\r\n  AND pm.policydate         <= TO_CHAR (SYSDATE, 'DD-MON-YY')\r\n  AND pm.status              = 'Active'\r\n  AND pm.memberannuitytypeid = 2\r\n  ) B\r\nLEFT OUTER JOIN\r\n  (SELECT MAX(PremiumDate) lastpremiumdate,\r\n    policyno\r\n  FROM RICB_UWR.la_annuitypremium@RICB_COM\r\n  GROUP BY policyno\r\n  ) A\r\nON B.policyno        = A.policyno\r\nWHERE B.penaltydays <> 0\r\nORDER BY B.memberid";
	private static final String GET_DEFERRED_POLICY_NO = "SELECT pm.policyno\r\nFROM RICB_FAS.lifeannuity_planformember@RICB_COM pm\r\nINNER JOIN RICB_FAS.lifeannuity_membermaster@RICB_COM lmmst\r\nON pm.memberid         =lmmst.id\r\nWHERE pm.status        ='Active'\r\nAND lmmst.cityzenshipid=?";
	private static final String GET_OTIP_PLAN_FINAL = "select a.col_1 code, a.col_6 premium, b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53  and a.col_1='T2'  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1\r\n";
	private static final String GET_PPF_MEMO = "SELECT a.code PFNumber,\r\n\tb.designation desig,\r\n\tb.citizenid,\r\n\te.journaldate,\r\n\tTO_CHAR(e.fromdate,'Month, YYYY') due_month,\r\n\tamountmask(d.ercontribution) er_contr,\r\n\tamountmask(d.eecontribution) ee_contr,\r\n\te.rtype ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.ercontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) er_intr ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.eecontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) ee_intr\r\n\tFROM sale_customer a\r\n\tJOIN sale_investoreeorganisatndata b\r\n\tON a.id=b.customerid\r\n\tJOIN ppf_contributionflow d\r\n\tON d.ppfeeuid=a.code\r\n\tJOIN ppf_contributionheader e\r\n\tON e.id=d.contributionheaderid\r\n\tWHERE b.citizenid = ?\r\n\tAND b.isactiveee =1\r\n\tAND TO_CHAR(e.journaldate, 'YYYY') =TO_CHAR(SYSDATE, 'YYYY')\r\n\tORDER BY a.code,\r\n\tb.citizenid,\r\n\td.ercontribution,\r\n\td.eecontribution";
	private static final String GET_SEQ_ID = "select RICB_UWR.APP_TRANSACTION_SEQ.nextval code from dual ";
	private static final String getHousingDetails = "SELECT\r\n  HOUSEHOLD_NO,\r\n  HOUSE_CATEGORY,\r\n  SUBSIDY_PREM,\r\n  FAMILY_PREM Insureds_share,\r\n  (\r\n  SELECT\r\n    DZONG_NAME\r\n  FROM\r\n    RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM\r\n  WHERE\r\n    DZONG_CODE = A.DZONG_CODE) dzongkhag,\r\n  (\r\n  SELECT\r\n    GEWOG_NAME\r\n  FROM\r\n    RICB_COM.TL_IN_MAS_GEWOG@RICB_COM\r\n  WHERE\r\n    gewog_code = A.gewog_code) gewog,\r\n  village,\r\n  SUM_INSURED,\r\n  TOTAL_PREM premium,\r\n  HEAD_FAMILY_NAME,\r\n  CITIZEN_ID\r\nFROM\r\n  RICB_GI.TL_GI_TR_RURAL_POL_HDR@RICB_COM a\r\nWHERE\r\n  CITIZEN_ID = ?   and underwriting_year = ( select max(underwriting_year) from RICB_GI.TL_GI_TR_RURAL_POL_HDR@RICB_COM  where citizen_id = a.citizen_id)";
	private static final String getAllDzongkhags = "select DZONG_CODE,DZONG_NAME from RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM\r\n";
	private static final String getDzongkhagById = "select DZONG_CODE,DZONG_NAME from RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM where DZONG_CODE = ?\r\n";
	private static final String getAllGewogs = "select GEWOG_CODE, GEWOG_NAME, DZONG_CODE from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM\r\n";
	private static final String getGewogById = "select GEWOG_CODE, GEWOG_NAME, DZONG_CODE from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM where GEWOG_CODE = ?\r\n";
	private static final String getGewogsUnderDzoCode = "select GEWOG_CODE,GEWOG_NAME from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM where DZONG_CODE = ?";
	private static final String getAllProducts = "SELECT a.product_code\r\n       , a.product_desc\r\n       , a.table_number\r\n       , a.start_date\r\n       , a.min_entry_age_assured\r\n       , a.max_entry_age_assured\r\n       , a.maturity_age\r\n       , a.min_term\r\n       , a.max_term\r\n       , a.min_sum_assured\r\n       , a.max_sum_assured\r\n       , a.product_abbr_2\r\n       , a.product_abbr\r\n    FROM ricb_li.tl_li_mas_product@RICB_COM a \r\n   WHERE a.product_abbr_2 IN ('PHO-MO','TMN','MBP-NV-15','MBP-NV-20','MBP-NV-25','AN-NV') and a.status_code = 'A'";
	private static final String getThresholdSumAssured = "Select getThresholdSAMobile(?,?) as threshold from dual";
	private static final String getAgentCode = "select AGENT_CODE FROM RICB_COM.TL_IN_MAS_AGENT@RICB_COM\r\nwhere CITIZEN_ID = ?\r\n      AND STATUS_CODE = 'A'";
	private static final String getCustomerCode = "SELECT CUSTOMER_CODE FROM RICB_COM.TL_IN_MAS_CUSTOMER@RICB_COM WHERE CITIZEN_ID = ?";
	private static final String getPolicySeqId = "SELECT RICB_UWR.SEQ_POLICY_SERIAL.NEXTVAL AS code FROM dual";

	public JSONArray getRLImember(String cidNo, String uwYear) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT b.MEMBER_NAME,\r\n  b.DATE_OF_BIRTH,\r\n  b.CITIZEN_ID,\r\n  DECODE(b.STATUS_CODE,'A','Active','X','Deceased','') status\r\nFROM RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM a,\r\n  RICB_LI.TL_LI_TR_RURAL_POL_DTL@RICB_COM b\r\nWHERE a.SERIAL_NO       = b.HDR_SERIAL_NO\r\nAND a.CITIZEN_ID        = ?\r\nAND a.UNDERWRITING_YEAR = ?\r\nAND b.STATUS_CODE       = 'A'\r\nORDER BY b.DATE_OF_BIRTH ");
				pst.setString(1, cidNo);
				pst.setString(2, uwYear);
				rs = pst.executeQuery();
				System.out.println(rs);
				json = convertor.toJSONArray(rs);
				System.out.println(json);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return json;
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		return json;
	}

	public JSONArray getPolicyDetails(String cidNo, String type) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				if (type.equalsIgnoreCase("lifeinsurance")) {
					pst = conn.prepareStatement(
							"SELECT a.product_code product_code,\r\n  a.policy_no policy_no,\r\n  TO_CHAR( a.policy_start_date, 'DD/MM/RRRR' ) incept_date,\r\n  TO_CHAR(a.policy_end_date, 'DD/MM/RRRR') maturity_date,\r\n  TO_CHAR( a.sum_assured, '9,999,999,999,999.99' ) sum_assured,\r\n  c.premium_per_instalment premium,\r\n  DECODE( a.mode_of_payment, 'M', 'MONTHLY', 'S', 'SSS', 'Q', 'QUARTERLY', 'H', 'HALF YEARLY', 'Y', 'YEARLY', 'O', 'SINGLE' ) AS p_mode,\r\n  DECODE( a.status_code, 'G', 'Active Policy', 'H', 'LAPSED', 'I', 'Lapsed Paid         \r\nUp', 'J', 'Forfieted', 'K', 'Matured', 'L', 'Closed- Maturity', 'M', 'Death         \r\nClaim Entered/Registered', 'MM', 'Surrender Claim Entered/', 'N', 'Closed-         \r\nDeath', 'P', 'Closed- Surrender', 'Q', 'Closed- Forfeiture', 'S', 'Closed-         \r\nClaim Declined', 'U', 'Closed- PTD', 'V', 'Cancelled', 'X', 'Rejected' ) POLSTATUS,\r\n  TO_CHAR (MIN (aa.due_date), 'DD/MM/YYYY') next_repay,\r\n  b.customer_name POLICY_HOLDER,\r\n  b.citizen_id CUST_CID,\r\n  a.policy_term term,\r\n  TO_CHAR (MAX (bb.due_date), 'DD/MM/YYYY') LAST_PAID_DATE\r\nFROM RICB_LI.tl_li_tr_policy_header@RICB_COM a,\r\n  RICB_COM.tl_in_mas_customer@RICB_COM b,\r\n  RICB_LI.tl_li_tr_premium_list@RICB_COM c,\r\n  (SELECT *\r\n  FROM RICB_LI.tl_li_tr_premium_list@RICB_COM\r\n  WHERE status_code = 'PENDING'\r\n  ) aa,\r\n  (SELECT *\r\n  FROM RICB_LI.tl_li_tr_premium_list@RICB_COM\r\n  WHERE status_code = 'PAID'\r\n  ) bb\r\nWHERE a.customer_code   = b.customer_code\r\nAND aa.policy_serial_no = a.serial_no\r\nAND bb.policy_serial_no = a.serial_no\r\nAND a.serial_no         = c.policy_serial_no\r\nAND c.status_code       = 'PENDING'\r\nAND b.citizen_id LIKE '%'\r\n  || ?\r\n  || '%'\r\nGROUP BY a.product_code,\r\n  a.policy_no,\r\n  a.sum_assured,\r\n  a.policy_start_date,\r\n  a.policy_end_date,\r\n  a.mode_of_payment,\r\n  a.status_code,\r\n  b.customer_name,\r\n  c.premium_per_instalment,\r\n  a.policy_term,\r\n  b.citizen_id");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("generalinsurance")) {
					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  c.PRODUCT_DESC policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,\r\n  TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status\r\nFROM RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,\r\n  RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b,\r\n  RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c\r\nWHERE b.citizen_id IN (?)\r\nAND a.customer_code =b.CUSTOMER_CODE\r\nAND a.product_code  =c.product_code\r\nAND a.status_code  IN ('G','F')\r\n");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("motortppolicy")) {

					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO      = e.POLICY_SRL_NO\r\nAND e.CODE_CODE      = 8\r\nAND a.policy_end_date BETWEEN add_months(sysdate,-12) AND add_months(sysdate,3)\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I')\r\nAND b.citizen_id        IN (?)");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("motortpactivepolicy")) {

					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code     = 'PR00000043'\r\nAND a.customer_code      = b.customer_code\r\nAND a.serial_no          = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO          = e.POLICY_SRL_NO\r\nAND e.CODE_CODE          = 8\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?)\r\nORDER BY a.policy_end_date,\r\n  a.policy_no \r\n");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("otipactivepolicy")) {

					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b\r\nWHERE a.product_code     = 'PR00000032'\r\nAND a.customer_code      = b.customer_code\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?) ");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("genclaimothers")) {

					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  c.product_group_ri policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date,\r\n  TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status,\r\n  d.registration_no reg_no\r\nFROM RICB_GI.TL_GI_TR_POLICY_HEADER@RICB_COM a,\r\n  RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM b,\r\n  RICB_GI.TL_GI_MAS_PRODUCT@RICB_COM c ,\r\n  RICB_GI.TL_GI_TR_MOTOR_DTL@RICB_COM d\r\nWHERE b.citizen_id   IN (?)\r\nAND a.customer_code   =b.CUSTOMER_CODE\r\nAND a.product_code    =c.product_code\r\nAND a.status_code    IN ('G','A','H')\r\nAND c.product_group_ri='MOTR'\r\nAND a.serial_no       =d.policy_hdr_srl_no");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("genTrackclaim")) {

					pst = conn.prepareStatement(
							"SELECT a.policy_no POLICY_NO,\r\n  a.CLAIM_INTM_NO CLAIM_INTM_NO,\r\n  TO_CHAR(a.CLAIM_INTM_DATE, 'dd/mm/rrrr') CLAIM_INTM_DATE,\r\n  TO_CHAR(a.DATE_OF_LOSS, 'dd/mm/rrrr') DATE_OF_LOSS,\r\n  c.STATUS_DESC,\r\n  a.status_code,\r\n  b.INTIMATOR_CID\r\nFROM RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,\r\n  RICB_GI.CLAIMS_INTIMATION@RICB_COM b,\r\n  ricb_com.tl_in_mas_status@RICB_COM c\r\nWHERE a.policy_no  =b.policy_no\r\nAND b.INTIMATOR_CID= ?\r\nAND a.status_code  = c.status_code\r\nAND c.table_name   = 'tl_gi_tr_claims_header'\r\nGROUP BY a.policy_no,\r\n  a.CLAIM_INTM_NO,\r\n  a.CLAIM_INTM_DATE,\r\n  a.DATE_OF_LOSS,\r\n  c.STATUS_DESC,\r\n  b.INTIMATOR_CID,\r\n  a.status_code ;\r\n");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("generalactivepolicy")) {

					pst = conn.prepareStatement(
							"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  a.policy_no pol_no,\r\n  a.product_code,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(a.premium_amount, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b\r\nWHERE a.product_code    IN ('PR00000032','PR00000043')\r\nAND a.customer_code      = b.customer_code\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','F')\r\nAND b.citizen_id        IN (?)");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("checkpayment")) {

					pst = conn.prepareStatement(
							"SELECT a.UNDERWRITING_YEAR,\r\n  a.STATUS_CODE\r\nFROM RICB_LI.TL_LI_TR_RURAL_MAPP_TRN_FINAL@RICB_COM a,\r\n  RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM b\r\nWHERE a.STATUS_CODE     ='PAID'\r\nOR b.status_code!       ='A'\r\nAND a.PERMANENT_HOUSE_NO=b.PERMANENT_HOUSE_NO\r\nAND a.PERMANENT_HOUSE_NO= ? \r\n");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("getOtipCustomer")) {

					pst = conn.prepareStatement(
							"SELECT CUSTOMER_NAME,\r\n  TO_CHAR(DATE_OF_BIRTH,'rrrr-mm-dd') DOB,\r\n  CITIZEN_ID,\r\n  PASSPORT_NO\r\nFROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM\r\nWHERE CITIZEN_ID LIKE ?");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("deferredannuity")) {

					pst = conn.prepareStatement(
							"SELECT pm.policyno\r\nFROM RICB_FAS.lifeannuity_planformember pm\r\nINNER JOIN RICB_FAS.lifeannuity_membermaster lmmst\r\nON pm.memberid         =lmmst.id\r\nWHERE pm.status        ='Active'\r\nAND lmmst.cityzenshipid=?");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("immediateannuity")) {

					pst = conn.prepareStatement("SELECT distinct pm.memberid memberid,(lmmst.Title || ' ' || lmmst.name) CUSTNAME,                    lmmst.currentaddress CURRADDRESS,                    lmmst.cityzenshipid cid,                    lmmst.customerno custno,                    pm.sumassured sumassr,                    pm.policyno polno,                    pm.policydate poldate                    ,pm.premiumamount preamt                    from lifeannuity_planformember pm                    inner join lifeannuity_membermaster lmmst on pm.memberid=lmmst.id where pm.memberannuitytypeid=1 and pm.membercitizenshipid=?");
					pst.setString(1, cidNo);
				} else if (type.equalsIgnoreCase("ppfmemo")) {

			          pst = conn.prepareStatement(
			              "SELECT * FROM V_PPF_MEMO_C_YEAR_BAL WHERE citizenid=?");
			          pst.setString(1, cidNo);
			        } else if (type.equalsIgnoreCase("gis")) {

						pst = conn.prepareStatement(
								"SELECT\n"
								+ "			   pol.policyno polno, SC.CODE gisno, \n"
								+ "			   dpt.name || ' - ' || pol.name || ' (' || pol.policyno || ' )' BRANCHDEPARTMENT, \n"
								+ "			   sc.registeredname membername, \n"
								+ "			   ieog.citizenid CITIZENID, \n"
								+ "			   ieog.designation desig, \n"
								+ "			   to_char (ieog.joiningdate, 'dd/mm/rrrr') joindate, \n"
								+ "			   DECODE( \n"
								+ "			     ieog.groupid, \n"
								+ "			     '4', \n"
								+ "			     '200,000', \n"
								+ "			     '3', \n"
								+ "			     '300,000', \n"
								+ "			     '2', \n"
								+ "			     '400,000', \n"
								+ "			     '1', \n"
								+ "			     '500,000' \n"
								+ "			   ) sumassured, \n"
								+ "			   DECODE ( \n"
								+ "			     ieog.isactiveee, \n"
								+ "			     '1', \n"
								+ "			     'Active', \n"
								+ "			     '0', \n"
								+ "			     'Inactive' \n"
								+ "			   ) STATUS \n"
								+ "			 FROM \n"
								+ "			   gis_sale_customer SC \n"
								+ "			   INNER JOIN gis_investoreeorganisatndata ieog \n"
								+ "			     ON SC.id = ieog.customerid \n"
								+ "			   INNER JOIN gis_investorerlocation dpt \n"
								+ "			     ON ieog.departmentid = dpt.id \n"
								+ "			   INNER JOIN gis_policymaster pol \n"
								+ "			     ON pol.id = ieog.policyid \n"
								+ "			 WHERE ieog.citizenid = ?\n"
								+ "			   AND ieog.isactiveee = '1'");
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

	public JSONArray getGeneralInsuranceDtls(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getGeneralInsuranceConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.serial_no pol_sys_id,\r\n  a.policy_no pol_no,\r\n  a.product_code,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status\r\nFROM ricb_gi.tl_gi_tr_policy_header a\r\nWHERE a.product_code IN ('PR00000043','PR00000032')\r\nAND a.policy_no IS NOT NULL\r\nAND a.status_code IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_no");
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

	public JSONArray motorinsurancedetails(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'Motor Third Party' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  TO_CHAR(a.policy_start_date,'dd/mm/yyyy') inception_date,\r\n  TO_CHAR(a.policy_end_date,'dd/mm/yyyy') expiry_date,\r\n  (e.code_value * 1.05) premium,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no,\r\n  d.CODE_DESC,\r\n  m.MODEL_NAME VEHICLE_TYPE\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e,\r\n  ricb_gi.tl_gi_mas_code@RICB_COM d,\r\n  ricb_gi.tl_gi_mas_vehicle_model@RICB_COM m\r\nWHERE a.product_code     = 'PR00000043'\r\nAND a.customer_code      = b.customer_code\r\nAND a.serial_no          = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO          = e.POLICY_SRL_NO\r\nAND e.CODE_CODE          = 8\r\nAND c.VEHICLE_CATEGORY   = d.CODE_CODE\r\nAND d.type_code          = '84'\r\nAND c.MODEL              = m.MODEL_CODE\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_end_date,\r\n  a.policy_no ");
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

	public JSONArray motordetails(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'Motor Third Party' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.policy_start_date,'dd/mm/yyyy') inception_date,\r\n  TO_CHAR(a.policy_end_date,'dd/mm/yyyy') expiry_date,\r\n  DECODE(a.status_code,'G','Active','F','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no,\r\n  ABS(months_between(TO_DATE(policy_end_date,'dd/mm/rrrr'),TO_DATE(sysdate,'dd/mm/rrrr'))) months_diff,\r\n  NVL(a.renewed_policy_no,'renew') renewed_policy_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.status_code   IN ('G','I','F')\r\nAND a.policy_no LIKE ?\r\nORDER BY a.policy_end_date,\r\n  a.policy_no");
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

	public JSONArray mtpdetails(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.serial_no pol_sys_id,\r\n  b.citizen_id pol_civil_id,\r\n  b.customer_name assuredname,\r\n  'MTP' policycode,\r\n  a.policy_no pol_no,\r\n  TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured,\r\n  a.policy_start_date inception_date,\r\n  a.policy_end_date expiry_date,\r\n  TO_CHAR(e.code_value, '9,999,999,999,999.99')premium,\r\n  DECODE(a.status_code,'G','Active','I','Expired',a.status_code) status,\r\n  c.registration_no reg_no\r\nFROM ricb_gi.tl_gi_tr_policy_header@RICB_COM a,\r\n  ricb_gi.tl_gi_mas_customer@RICB_COM b,\r\n  ricb_gi.tl_gi_tr_motor_dtl@RICB_COM c,\r\n  RICB_GI.TL_GI_TR_PREMIUM_DTLS@RICB_COM e\r\nWHERE a.product_code = 'PR00000043'\r\nAND a.customer_code  = b.customer_code\r\nAND a.serial_no      = c.policy_hdr_srl_no\r\nAND a.SERIAL_NO      = e.POLICY_SRL_NO\r\nAND e.CODE_CODE      = 8\r\nAND a.policy_end_date BETWEEN add_months(sysdate,-12) AND add_months(sysdate,3)\r\nAND a.renewed_policy_no IS NULL\r\nAND a.status_code       IN ('G','I')\r\nAND a.policy_no         IN (?) ");
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

	public JSONArray checkrlpayment(String serialNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.UNDERWRITING_YEAR,\r\n  a.STATUS_CODE\r\nFROM RICB_LI.TL_LI_TR_RURAL_MAPP_TRN_FINAL@RICB_COM a,\r\n  RICB_LI.TL_LI_TR_RURAL_POL_HDR@RICB_COM b\r\nWHERE a.STATUS_CODE     ='PAID'\r\nOR b.status_code!       ='A'\r\nAND a.PERMANENT_HOUSE_NO=b.PERMANENT_HOUSE_NO\r\nAND a.PERMANENT_HOUSE_NO= ? \r\n");
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

	public JSONArray getCreditDetails(String accountNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT A.ACCOUNT_holder,\r\n\t  NVL(A.PVR_Citizenship_number, A.PVR_Registration_number) cid_no,\r\n\t  A.credit_id ,\r\n\t  A.credit_account_number account_no ,\r\n\t  TO_CHAR(A.sanction_amount, '999,999,999,999.99') sanc_amt ,\r\n\t  TO_CHAR(A.sanction_date,'dd/mm/rrrr') sanc_date ,\r\n\t  TO_CHAR(A.expiry_date,'dd/mm/rrrr') exp_dt ,\r\n\t  TO_CHAR(A.repayment_effective_date,'dd/mm/rrrr') repay_date ,\r\n\t  A.rate_of_interest int_rt ,\r\n\t  TO_CHAR(A.installment_amount, '999,999,999,999.99') emi ,\r\n\t  A.status STATUS ,\r\n\t  A.term ,\r\n\t  A.mode_of_repayment repay_mode ,\r\n\t  A.repayment_effective_date ,\r\n\t  TO_CHAR(B.INTEREST_OD_AMOUNT, '999,999,999,999.99') INTODAMT ,\r\n\t  get_nextrepaymentdt@RICB_COM(TO_CHAR(A.repayment_effective_date,'dd/mm/rrrr'),A.mode_of_repayment) next_repaymt,\r\n\t  B.LATE_FEE LATEFEE ,\r\n\t  B.OD_INDICATOR OD_DAYS\r\n\tFROM RICB_UWR.T_CID_LOAN_LISTS@RICB_COM A ,\r\n\t  RICB_UWR.V_CREDIT_PAYMENT_CYCLE@RICB_COM B\r\n\tWHERE credit_account_number IS NOT NULL\r\n\tAND A.credit_account_number  = '1232341234'\r\n\tAND UPPER(A.status) NOT LIKE 'CLOSED%'\r\n\tAND UPPER(A.status) NOT LIKE 'DECLINED%'\r\n\tAND A.CREDIT_ID                                      = B.CRDT_CREDIT_ID\r\n\tAND TO_CHAR(B.CREATED_DATE, 'DD/MM/RRRR HH24:MI:SS') =\r\n\t  (SELECT TO_CHAR(MAX(X.CREATED_DATE),'DD/MM/RRRR HH24:MI:SS')\r\n\t  FROM RICB_UWR.V_CREDIT_PAYMENT_CYCLE@RICB_COM X\r\n\t  WHERE X.CRDT_CREDIT_ID              = A.CREDIT_ID\r\n\t  AND x.transaction_type              = x.transaction_type\r\n\t  AND X.CRDT_CREDIT_PAYMENT_CYCLE_ID IS NULL\r\n\t  )\r\n");
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

	public JSONArray getotipplan(String days, String age) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			int number = Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {

				pst = conn.prepareStatement(
						"SELECT a.col_1 code,\r\n  (a.col_6 * 1.05) premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1      ='T2'\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1 ");
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

	public JSONArray getotipplanjp(String days, String age) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			int number = Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {

				pst = conn.prepareStatement(
						"SELECT a.col_1 code,\r\n  (a.col_6 * 1.05) premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1      ='T2'\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1 ");
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

	public JSONArray getotipplanasian(String days, String age) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			int number = Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {

				pst = conn.prepareStatement(
						"SELECT a.col_1 code,\r\n  (a.col_6 * 1.05) premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1     IN ('T2', 'T1','T3')\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1");
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

	public JSONArray getotipplanall(String days, String age) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			int number = Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {

				pst = conn.prepareStatement(
						"SELECT a.col_1 code,\r\n  (a.col_6 * 1.05) premium,\r\n  b.code_desc,\r\n  TO_CHAR(b.col_2, '9,999,999,999,999.99' ) suminsured\r\nFROM RICB_GI.TL_GI_MAS_CODE@RICB_COM a,\r\n  RICB_COM.TL_IN_MAS_CODE@RICB_COM b\r\nWHERE a.type_code=53\r\nAND a.col_1     IN ('T2', 'T1')\r\nAND ? BETWEEN a.col_2 AND a.col_3\r\nAND ? BETWEEN a.col_4 AND a.col_5\r\nAND a.col_1=b.col_1\r\n");
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

	public JSONArray getotipplanfinal(String days, String age, String plan) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			int number = Integer.parseInt(days);
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {

				pst = conn.prepareStatement(
						"select a.col_1 code, (a.col_6 * 1.05) premium, b.code_desc, to_char(b.col_2, '9,999,999,999,999.99' ) suminsured from RICB_GI.TL_GI_MAS_CODE@RICB_COM a,  RICB_COM.TL_IN_MAS_CODE@RICB_COM b where a.type_code=53  and a.col_1='T2'  and ? between a.col_2  and a.col_3  and ? between a.col_4  and a.col_5 and a.col_1=b.col_1\r\n");
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

	public JSONArray genclaimStatus(String accountNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.policy_no POLICY_NO,\r\n  a.status_code,\r\n  b.INTIMATOR_CID\r\nFROM RICB_GI.TL_GI_TR_CLAIMS_HEADER@RICB_COM a,\r\n  RICB_GI.CLAIMS_INTIMATION@RICB_COM b,\r\n  ricb_com.tl_in_mas_status@RICB_COM c\r\nWHERE a.policy_no      =b.policy_no\r\nAND A.POLICY_NO        =?\r\nAND a.status_code      = c.status_code\r\nAND c.table_name       = 'tl_gi_tr_claims_header'\r\nAND a.status_code NOT IN ('G','X', 'F')\r\nGROUP BY a.policy_no,\r\n  b.INTIMATOR_CID,\r\n  a.status_code");
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

	public JSONArray getGeneralPolicyDetails(String policyNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getGeneralInsuranceConnection();

			if (conn != null) {
				pst = conn.prepareStatement("SELECT a.serial_no pol_sys_id, " +
					    "b.citizen_id pol_civil_id, " +
					    "c.PRODUCT_DESC policycode, " +
					    "a.policy_no pol_no, " +
					    "TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sum_insured, " +
					    "TO_CHAR(a.POLICY_START_DATE,'dd/mm/rrrr') inception_date, " +
					    "TO_CHAR(a.POLICY_END_DATE,'dd/mm/rrrr') expiry_date, " +
					    "TO_CHAR(a.premium_amount, '9,999,999,999,999.99') premium, " +
					    "b.customer_name, " +
					    "e.CODE_DESC, " +
					    "m.MODEL_NAME vehicle_type, " +
					    "DECODE(a.status_code,'G','Active','I','Expired','F','Active','H','Lapsed') STATUS, " +
					    "d.REGISTRATION_NO " +
					    "FROM RICB_GI.TL_GI_TR_POLICY_HEADER a, " +
					    "RICB_GI.TL_GI_MAS_CUSTOMER b, " +
					    "RICB_GI.TL_GI_MAS_PRODUCT c, " +
					    "RICB_GI.TL_GI_TR_MOTOR_DTL d, " +
					    "ricb_gi.tl_gi_mas_code e, " +
					    "ricb_gi.tl_gi_mas_vehicle_model m " +
					    "WHERE a.policy_no LIKE ? " +
					    "AND a.customer_code    = b.CUSTOMER_CODE " +
					    "AND a.product_code     = c.product_code " +
					    "AND a.serial_no        = d.policy_hdr_srl_no(+) " +
					    "AND d.VEHICLE_CATEGORY = e.CODE_CODE(+) " +
					    "AND d.MODEL            = m.MODEL_CODE(+) " +
					    "AND e.type_code(+)     = '84' " +
					    "AND a.status_code     IN ('G','F','I')");
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

			if (conn != null) {
				pst = conn.prepareStatement("SELECT "
								+ "  B.Custname, "
								 
								+ "  b.cityzenshipid, "
								 
								+ "  to_char ( "
								 
								+ "    b.SUMASSURED, "
								 
								+ "    '999,999,999,999.99' "
								 
								+ "  ) SUMASSURED, "
								 
								+ "  b.policyno ploicy_no, "
								 
								+ "   to_char ( "
								 
								+ "    b.Effectivepremiumamount, "
								 
								+ "    '999,999,999,999.99' "
								 
								+ "  ) PREMIUMAMOUNT, "
								 
								+ "  B.premiumtype, "
								 
								+ "    to_char (b.POLICYDATE, 'dd/mm/rrrr') ploicy_date, "
								 
								+ "    to_char (b.premiumclosingdate, 'dd/mm/rrrr') ploicy_vesting_date, "
								 
								+ "  to_char (a.lastpremiumdate, 'dd/mm/rrrr') Paid_up_to_date, "
								 
								+ "  CASE "
								 
								+ "    WHEN to_char (A.lastpremiumdate, 'DD') = to_char (b.policydate, 'DD') "
								 
								+ "    AND A.lastpremiumdate IS NOT NULL "
								 
								+ "    THEN add_months (A.lastpremiumdate, 1) "
								 
								+ "    WHEN to_char (A.lastpremiumdate, 'DD') <> to_char (b.policydate, 'DD') "
								 
								+ "    AND ( "
								 
								+ "      ( "
								 
								+ "        to_char ( "
								 
								+ "          add_months (A.lastpremiumdate, 1), "
								 
								+ "          'MON' "
								 
								+ "        ) <> 'FEB' "
								 
								+ "      ) "
								 
								+ "      OR ( "
								 
								+ "        to_char ( "
								 
								+ "          add_months (A.lastpremiumdate, 1), "
								 
								+ "          'MON' "
								 
								+ "        ) = 'FEB' "
								 
								+ "        AND to_char (b.policydate, 'DD') <= '28' "
								 
								+ "      ) "
								 
								+ "    ) "
								 
								+ "    AND A.lastpremiumdate IS NOT NULL "
								 
								+ "    THEN to_date ( "
								 
								+ "      to_char (b.policydate, 'DD') || '-' || to_char ( "
								 
								+ "        add_months (A.lastpremiumdate, 1), "
								 
								+ "        'MON' "
								 
								+ "      ) || '-' || to_char ( "
								 
								+ "        add_months (A.lastpremiumdate, 1), "
								 
								+ "        'YYYY' "
								 
								+ "      ), "
								 
								+ "      'dd-mon-yyyy' "
								 
								+ "    ) "
								 
								+ "    WHEN to_char (A.lastpremiumdate, 'DD') <> to_char (b.policydate, 'DD') "
								 
								+ "    AND to_char ( "
								 
								+ "      add_months (A.lastpremiumdate, 1), "
								 
								+ "      'MON' "
								 
								+ "    ) = 'FEB' "
								 
								+ "    AND to_char (b.policydate, 'DD') > '28' "
								 
								+ "    AND A.lastpremiumdate IS NOT NULL "
								 
								+ "    THEN to_date ( "
								 
								+ "      '28' || '-' || to_char ( "
								 
								+ "        add_months (A.lastpremiumdate, 1), "
								 
								+ "        'MON' "
								 
								+ "      ) || '-' || to_char ( "
								 
								+ "        add_months (A.lastpremiumdate, 1), "
								 
								+ "        'YYYY' "
								 
								+ "      ), "
								 
								+ "      'dd-mon-yyyy' "
								 
								+ "    ) "
								 
								+ "    WHEN A.lastpremiumdate IS NULL "
								 
								+ "    THEN b.policydate "
								 
								+ "  END next_due_months, "
								 
								+ "  trunc ( "
								 
								+ "    months_between ( "
								 
								+ "      to_char (SYSDATE, 'DD-MON-YY'), "
								 
								+ "      ( "
								 
								+ "        add_months (A.lastpremiumdate, B.noofmonths) "
								 
								+ "      ) "
								 
								+ "    ), "
								 
								+ "    0 "
								 
								+ "  ) + 1 deu_months, "
								 
								+ "  b.effectivepremiumamount, "
								 
								+ "  ( "
								 
								+ "    trunc ( "
								 
								+ "      months_between ( "
								 
								+ "        to_char (SYSDATE, 'DD-MON-YY'), "
								 
								+ "        ( "
								 
								+ "          add_months (A.lastpremiumdate, B.noofmonths) "
								 
								+ "        ) "
								 
								+ "      ), "
								 
								+ "      0 "
								 
								+ "    ) + 1 "
								 
								+ "  ) * b.effectivepremiumamount AS total_payable "
								 
								+ "FROM "
								 
								+ "  (SELECT DISTINCT "
								 
								+ "    pm.memberid, "
								 
								+ "    (lmmst.Title || ' ' || lmmst.name) CUSTNAME, "
								 
								+ "    lmmst.currentaddress CURRADDRESS, "
								 
								+ "    lmmst.cityzenshipid, "
								 
								+ "    lmmst.customerno, "
								 
								+ "    pm.sumassured, "
								 
								+ "    pm.policyno, "
								 
								+ "    pm.policydate, "
								 
								+ "    pm.premiumclosingdate, "
								 
								+ "    plm.planname, "
								 
								+ "    am.annuitytype, "
								 
								+ "    nvl (prmst.noofmonths, 0) noofmonths, "
								 
								+ "    prmst.premiumtype, "
								 
								+ "    nvl (prmst.penaltydays, 0) penaltydays, "
								 
								+ "    va.vestingage, "
								 
								+ "    nvl (l_md.organizationcode, 'Direct') Agent, "
								 
								+ "    ( "
								 
								+ "      pm.premiumamount + pm.insuraranceamount "
								
								+ "    ) ANNUITYAMOUNT, "
								
								+ "    pm.effectivepremiumamount "
								
								+ "  FROM "
								
								+ "    lifeannuity_planformember pm "
								
								+ "    INNER JOIN lifeannuity_membermaster lmmst "
								
								+ "      ON pm.memberid = lmmst.id "
								
								+ "    INNER JOIN lifeannuity_planmaster plm "
								
								+ "      ON pm.memberplanid = plm.id "
								
								+ "    INNER JOIN lifeannuity_annuitytypemaster am "
								
								+ "      ON pm.memberannuitytypeid = am.id "
								
								+ "    LEFT OUTER JOIN lifeannuity_vestingage va "
								
								+ "      ON pm.membervestingageid = va.id "
								
								+ "    LEFT OUTER JOIN lifeannuity_premiumtypemaster prmst "
								
								+ "      ON pm.memberpremiumtypeid = prmst.id "
								
								+ "    LEFT OUTER JOIN lifeannuity_memberdetails l_md "
								
								+ "      ON pm.id = l_md.policyid "
								
								+ "  WHERE pm.isactive = 1 "
								
								+ "    AND pm.policydate <= to_char (SYSDATE, 'DD-MON-YY') "
								
								+ "    AND pm.status = 'Active' "
								
								+ "    AND pm.memberannuitytypeid = 2) B "
								
								+ "  LEFT OUTER JOIN "
								
								+ "    (SELECT "
								
								+ "      MAX(PremiumDate) lastpremiumdate, "
								
								+ "      policyno "
								
								+ "    FROM "
								
								+ "      la_annuitypremium "
								
								+ "    GROUP BY policyno) A "
							
								+ "    ON B.policyno = A.policyno "
						
								+ "WHERE B.penaltydays <> 0 "
								
								+ "  AND b.policyno = ? "
								+ "ORDER BY B.memberid");
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


	public JSONArray getImmediateDetails(String policyNo) throws Exception {
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    ToJSON convertor = new ToJSON();
	    JSONArray json = new JSONArray();

	    try {
	      conn = ConnectionManager.getOracleConnection();

	      if (conn != null) {
	        pst = conn.prepareStatement(
	            "SELECT DISTINCT pm.memberid memberid, "
	                + " "
	                + "                   (lmmst.Title || ' ' || lmmst.name) AS CUSTNAME, "
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
	                + "               AND pm.policyno=?");
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

	public JSONArray getppfmemo(String cidNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.code PFNumber,\r\n\tb.designation desig,\r\n\tb.citizenid,\r\n\te.journaldate,\r\n\tTO_CHAR(e.fromdate,'Month, YYYY') due_month,\r\n\tamountmask(d.ercontribution) er_contr,\r\n\tamountmask(d.eecontribution) ee_contr,\r\n\te.rtype ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.ercontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) er_intr ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.eecontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) ee_intr\r\n\tFROM sale_customer a\r\n\tJOIN sale_investoreeorganisatndata b\r\n\tON a.id=b.customerid\r\n\tJOIN ppf_contributionflow d\r\n\tON d.ppfeeuid=a.code\r\n\tJOIN ppf_contributionheader e\r\n\tON e.id=d.contributionheaderid\r\n\tWHERE b.citizenid = ?\r\n\tAND b.isactiveee =1\r\n\tAND TO_CHAR(e.journaldate, 'YYYY') =TO_CHAR(SYSDATE, 'YYYY')\r\n\tORDER BY a.code,\r\n\tb.citizenid,\r\n\td.ercontribution,\r\n\td.eecontribution");
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

	public JSONArray gettitle() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT *\r\nFROM ricb_com.tl_in_mas_code@RICB_COM\r\nWHERE type_code ='4'\r\nAND status_code = 'Active'\r\nORDER BY seq_no");
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

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT *\r\nFROM ricb_com.tl_in_mas_code@RICB_COM\r\nWHERE type_code = '2'\r\nAND status_code = 'Active'\r\nORDER BY seq_no");
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

			if (conn != null) {
				pst = conn.prepareStatement(
						"select * from ricb_com.tl_in_mas_code@RICB_COM where type_code = '3' and code_code != '2' AND status_code = 'Active' order by seq_no"
						);
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

			if (conn != null) {
				pst = conn.prepareStatement("select RICB_UWR.APP_TRANSACTION_SEQ.nextval code from dual ");
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

	public JSONArray getHousingDetailsAgainstCID(String cidNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT \r\n"
						+ "  A.HOUSEHOLD_NO,\r\n"
						+ "  A.HOUSE_CATEGORY,\r\n"
						+ "  A.SUBSIDY_PREM,\r\n"
						+ "  A.FAMILY_PREM AS Insureds_share,\r\n"
						+ "  A.DZONG_CODE,\r\n"
						+ "  A.GEWOG_CODE,\r\n"
						+ "  (SELECT \r\n"
						+ "    DZONG_NAME\r\n"
						+ "  FROM\r\n"
						+ "    RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM \r\n"
						+ "  WHERE DZONG_CODE = A.DZONG_CODE) AS dzongkhag,\r\n"
						+ "  (SELECT \r\n"
						+ "    GEWOG_NAME\r\n"
						+ "  FROM\r\n"
						+ "    RICB_COM.TL_IN_MAS_GEWOG@RICB_COM \r\n"
						+ "  WHERE GEWOG_CODE = A.GEWOG_CODE) AS gewog,\r\n"
						+ "  A.VILLAGE,\r\n"
						+ "  A.SUM_INSURED,\r\n"
						+ "  A.SERIAL_NO,\r\n"
						+ "  A.TOTAL_PREM AS premium,\r\n"
						+ "  A.COLLECTED_PREMIUM AS collectedPremium,\r\n"
						+ "  A.HEAD_FAMILY_NAME,\r\n"
						+ "  A.CITIZEN_ID \r\n"
						+ "FROM\r\n"
						+ "  RICB_GI.TL_GI_TR_RURAL_POL_HDR@RICB_COM A \r\n"
						+ "WHERE A.CITIZEN_ID = ? \r\n"
						+ "  AND A.UNDERWRITING_YEAR = \r\n"
						+ "  (SELECT \r\n"
						+ "    MAX(UNDERWRITING_YEAR) \r\n"
						+ "  FROM\r\n"
						+ "    RICB_GI.TL_GI_TR_RURAL_POL_HDR@RICB_COM \r\n"
						+ "  WHERE CITIZEN_ID = A.CITIZEN_ID)");
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				System.out.println(rs);
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

	public JSONArray getAllDzongkhangs() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();
		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select DZONG_CODE,DZONG_NAME from RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM\r\n");
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

	public JSONArray getAllGewogs() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select GEWOG_CODE, GEWOG_NAME, DZONG_CODE from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM\r\n");
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

	public JSONArray getGewogsUnderDzongkhags(String dzoCode) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select GEWOG_CODE,GEWOG_NAME from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM where DZONG_CODE = ?");
				pst.setString(1, dzoCode);
				rs = pst.executeQuery();
				System.out.println(rs);
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

	public JSONArray getDzongkhagByDzoCode(String dzoCode) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select DZONG_CODE,DZONG_NAME from RICB_COM.TL_IN_MAS_DZONGKHAG@RICB_COM where DZONG_CODE = ?\r\n");
				pst.setString(1, dzoCode);
				rs = pst.executeQuery();
				System.out.println(rs);
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

	public JSONArray getGewogByGewogCode(String gewogCode) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select GEWOG_CODE, GEWOG_NAME, DZONG_CODE from RICB_COM.TL_IN_MAS_GEWOG@RICB_COM where GEWOG_CODE = ?\r\n");
				pst.setString(1, gewogCode);
				rs = pst.executeQuery();
				System.out.println(rs);
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

	public JSONArray getPPFMemo(String cidNo) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.code PFNumber,\r\n\tb.designation desig,\r\n\tb.citizenid,\r\n\te.journaldate,\r\n\tTO_CHAR(e.fromdate,'Month, YYYY') due_month,\r\n\tamountmask(d.ercontribution) er_contr,\r\n\tamountmask(d.eecontribution) ee_contr,\r\n\te.rtype ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.ercontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) er_intr ,\r\n\t(SELECT amountmask(PPFINTEREST)\r\n\tFROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.eecontribution,e.rtype,TO_DATE(SYSDATE)) )\r\n\t) ee_intr\r\n\tFROM sale_customer a\r\n\tJOIN sale_investoreeorganisatndata b\r\n\tON a.id=b.customerid\r\n\tJOIN ppf_contributionflow d\r\n\tON d.ppfeeuid=a.code\r\n\tJOIN ppf_contributionheader e\r\n\tON e.id=d.contributionheaderid\r\n\tWHERE b.citizenid = ?\r\n\tAND b.isactiveee =1\r\n\tAND TO_CHAR(e.journaldate, 'YYYY') =TO_CHAR(SYSDATE, 'YYYY')\r\n\tORDER BY a.code,\r\n\tb.citizenid,\r\n\td.ercontribution,\r\n\td.eecontribution");
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				System.out.println(rs);
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

	public JSONArray getAllProduct() throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT a.product_code\r\n       , a.product_desc\r\n       , a.table_number\r\n       , a.start_date\r\n       , a.min_entry_age_assured\r\n       , a.max_entry_age_assured\r\n       , a.maturity_age\r\n       , a.min_term\r\n       , a.max_term\r\n       , a.min_sum_assured\r\n       , a.max_sum_assured\r\n       , a.product_abbr_2\r\n       , a.product_abbr\r\n    FROM ricb_li.tl_li_mas_product@RICB_COM a \r\n   WHERE a.product_abbr_2 IN ('PHO-MO','TMN','MBP-NV-15','MBP-NV-20','MBP-NV-25','AN-NV') and a.status_code = 'A'");
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

	public Map<String, Object> getSumAssuredThreshold(String cidNo, String prodCode) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Object> map = new HashMap<>();
		try {
			conn = ConnectionManager.getLifeConnection();

			if (conn != null) {
				// pst = conn.prepareStatement("Select getThresholdSAMobile(?,?) as threshold from dual");
				pst = conn.prepareStatement("Select RICB_LI.getThresholdSAMobile(?,?) as threshold from dual");
				pst.setString(1, cidNo);
				pst.setString(2, prodCode);

				rs = pst.executeQuery();
				if (rs.next()) {
					map.put("sumAssuredThreshold", rs.getString("threshold"));
				} else {
					map.put("sumAssuredThreshold", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		return map;
	}

	public Map<String, Object> isAgent(String cidNo) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Object> map = new HashMap<>();
		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select AGENT_CODE FROM RICB_COM.TL_IN_MAS_AGENT@RICB_COM\r\nwhere CITIZEN_ID = ?\r\n      AND STATUS_CODE = 'A'");
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				if (rs.next()) {
					map.put("agentCode", rs.getString("AGENT_CODE"));
					map.put("agentStatus", "Valid");
				} else {
					map.put("agentStatus", "Invalid");
					map.put("agentCode", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		return map;
	}

	public Map<String, Object> getCustomerCode(String cidNo) {
		Connection conn = null;
		PreparedStatement pst = null;

		ResultSet rs = null;
		Map<String, Object> map = new HashMap<>();
		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT CUSTOMER_CODE FROM RICB_COM.TL_IN_MAS_CUSTOMER@RICB_COM WHERE CITIZEN_ID = ?");
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				System.out.print(rs);
				if (rs.next()) {
					map.put("customerCode", rs.getString("CUSTOMER_CODE"));
				} else {
					map.put("customerCode", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		return map;
	}
	
	public Map<String, Object> getCustomerCodeForGeneralInsurance(String cidNo) {
		Connection conn = null;
		PreparedStatement pst = null;

		ResultSet rs = null;
		Map<String, Object> map = new HashMap<>();
		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"SELECT CUSTOMER_CODE from RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM where CITIZEN_ID = ?");
				pst.setString(1, cidNo);
				rs = pst.executeQuery();
				System.out.print(rs);
				if (rs.next()) {
					map.put("customerCode", rs.getString("CUSTOMER_CODE"));
				} else {
					map.put("customerCode", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}
		return map;
	}
	
	
}
