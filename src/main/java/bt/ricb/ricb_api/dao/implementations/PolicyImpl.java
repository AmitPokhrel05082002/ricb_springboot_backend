package bt.ricb.ricb_api.dao.implementations;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.PolicyDao;
import bt.ricb.ricb_api.models.CustomerDto;
import bt.ricb.ricb_api.models.PolicyReqDto;
import bt.ricb.ricb_api.util.ToJSON;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

@Repository
public class PolicyImpl implements PolicyDao {
	private static final String insertPolicyDetails = "insert into RICB_UWR.dti_nyekor_tbl(CARRIER_TYPE,CARRIER_NO,START_DATE,END_DATE,ORIGIN_PLACE,DESTINATION,DURATION,OPERATOR_NAME,CUSTOMER_CODE,PRODUCT_CODE,PROPOSER_CID,PASSPORT,PROPOSAL_DATE,UNDERWRITING_YEAR)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String insertPolicyCustomer = "insert into RICB_UWR.POLICY_CUSTOMER(POLICYID,CID,ASSURED_NAME,GENDER,PHONE_NUMBER,DOB,NOMINEE_CID,RELATION,PASSPORT_NO,ASSIGNEE_NAME,SUM_INSURED) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	private static final String dtiNyekkorDetailsAgaintsCID = "select * from policy_customer cus left join dti_nyekor_tbl main on cus.policyid = main.id where cus.cid = ? and product_code = ?";
	private static final String getPolicyAgainstCid = "select * from policy_customer where cid = ?";
	private static final String getUsdrDetailsAgainstTpn = "select * from RICB_GI.TL_GI_MAS_ORGANISATION@RICB_COM where TPN_NO = ?";

	public Integer insertNyekorDtiPolicy(PolicyReqDto policyDetails, List<CustomerDto> customers) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Integer generatedId = null;

		try {
			conn = ConnectionManager.getOracleConnection();
			if (conn != null) {

				pst = conn.prepareStatement(
						"insert into RICB_UWR.dti_nyekor_tbl(CARRIER_TYPE,CARRIER_NO,START_DATE,END_DATE,ORIGIN_PLACE,DESTINATION,DURATION,OPERATOR_NAME,CUSTOMER_CODE,PRODUCT_CODE,PROPOSER_CID,PASSPORT,PROPOSAL_DATE,UNDERWRITING_YEAR)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new String[] { "id" });
				pst.setString(1, policyDetails.getCarrierType());
				pst.setString(2, policyDetails.getCarrierNo());
				pst.setString(3, policyDetails.getStartDate());
				pst.setString(4, policyDetails.getEndDate());
				pst.setString(5, policyDetails.getOriginPlace());
				pst.setString(6, policyDetails.getDestination());
				pst.setInt(7, policyDetails.getDuration().intValue());
				pst.setString(8, policyDetails.getOperatorName());
				pst.setString(9, policyDetails.getCustomerCode());
				pst.setString(10, policyDetails.getProductCode());
				pst.setString(11, policyDetails.getProposerCid());
				pst.setString(12, policyDetails.getPassport());
				pst.setDate(13, policyDetails.getProposalDate());
				pst.setString(14, policyDetails.getUnderwritingYear());
				int rowsAffected = pst.executeUpdate();
				if (rowsAffected > 0) {

					rs = pst.getGeneratedKeys();

					if (rs.next()) {
						generatedId = Integer.valueOf(rs.getInt(1));
						for (int i = 0; i < customers.size(); i++) {
							pst1 = conn.prepareStatement(
									"insert into RICB_UWR.POLICY_CUSTOMER(POLICYID,CID,ASSURED_NAME,GENDER,PHONE_NUMBER,DOB,NOMINEE_CID,RELATION,PASSPORT_NO,ASSIGNEE_NAME,SUM_INSURED) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
							pst1.setInt(1, generatedId.intValue());
							pst1.setString(2, ((CustomerDto) customers.get(i)).getCid());
							pst1.setString(3, ((CustomerDto) customers.get(i)).getAssuredName());
							pst1.setString(4, ((CustomerDto) customers.get(i)).getGender());
							pst1.setString(5, ((CustomerDto) customers.get(i)).getPhoneNo());
							pst1.setString(6, ((CustomerDto) customers.get(i)).getDob());
							pst1.setString(7, ((CustomerDto) customers.get(i)).getNomineeCid());
							pst1.setString(8, ((CustomerDto) customers.get(i)).getRelation());
							pst1.setString(9, ((CustomerDto) customers.get(i)).getPassportNo());
							pst1.setString(10, ((CustomerDto) customers.get(i)).getAssigneeName());
							pst1.setString(11, ((CustomerDto) customers.get(i)).getSumInsured());
							rowsAffected = pst1.executeUpdate();
							rowsAffected++;
						}
					}
				} else {
					System.out.println("No data inserted.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, rs, pst);
		}

		return generatedId;
	}

	public JSONArray getdtiNyekkorDetailsAgainstCid(String cid, String productCode) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(
						"select * from policy_customer cus left join dti_nyekor_tbl main on cus.policyid = main.id where cus.cid = ? and product_code = ?");
				pst.setString(1, cid);
				pst.setString(2, productCode);
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

	public JSONArray getPolciciesAgainstCid(String cid) throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement("select * from policy_customer where cid = ?");
				pst.setString(1, cid);
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

	public JSONArray getUserDetailsAgainstTpn(String tpn) throws Exception {
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
					    + "    NVL(\r\n"
					    + "        (SELECT CUSTOMER_CODE FROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM a WHERE a.CITIZEN_ID = :1),\r\n"
					    + "        (SELECT ORGN_CODE FROM RICB_GI.TL_GI_MAS_ORGANISATION@RICB_COM b WHERE b.TPN_NO = :2)\r\n"
					    + "    ) AS Customer_code, \r\n"
					    + "    NVL(\r\n"
					    + "        (SELECT CUSTOMER_NAME FROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM a WHERE a.CITIZEN_ID = :3),\r\n"
					    + "        (SELECT ORGN_NAME FROM RICB_GI.TL_GI_MAS_ORGANISATION@RICB_COM b WHERE b.TPN_NO = :4)\r\n"
					    + "    ) AS Customer_name, \r\n"
					    + "    NVL(\r\n"
					    + "        (SELECT RESI_ADDRESS_1 || ',' || RESI_ADDRESS_2 FROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM a WHERE a.CITIZEN_ID = :5),\r\n"
					    + "        (SELECT OFF_ADDRESS_1 || ',' || OFF_ADDRESS_2 FROM RICB_GI.TL_GI_MAS_ORGANISATION@RICB_COM b WHERE b.TPN_NO = :6)\r\n"
					    + "    ) AS ADDRESS, \r\n"
					    + "    CASE \r\n"
					    + "        WHEN EXISTS (\r\n"
					    + "            SELECT 1 \r\n"
					    + "            FROM RICB_GI.TL_GI_MAS_CUSTOMER@RICB_COM a \r\n"
					    + "            WHERE a.CITIZEN_ID = :7 \r\n"
					    + "        ) THEN 'I'\r\n"
					    + "        ELSE 'O'\r\n"
					    + "    END AS insured_type\r\n"
					    + "FROM dual\r\n"
					    + ""               // <--- no semicolon here
					);

				pst.setString(1, tpn);
				pst.setString(2, tpn);
				pst.setString(3, tpn);
				pst.setString(4, tpn);
				pst.setString(5, tpn);
				pst.setString(6, tpn);
				pst.setString(7, tpn);
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
}
