package bt.ricb.ricb_api.dao;

import java.util.Map;
import org.json.JSONArray;

public interface TransactionDao {
	JSONArray getPolicyDetails(String paramString1, String paramString2) throws Exception;

	JSONArray getRLImember(String paramString1, String paramString2) throws Exception;

	JSONArray getGeneralInsuranceDtls(String paramString) throws Exception;

	JSONArray motorinsurancedetails(String paramString) throws Exception;

	JSONArray motordetails(String paramString) throws Exception;

	JSONArray mtpdetails(String paramString) throws Exception;

	JSONArray checkrlpayment(String paramString) throws Exception;

	JSONArray getCreditDetails(String paramString) throws Exception;

	JSONArray getotipplan(String paramString1, String paramString2) throws Exception;

	JSONArray getotipplanjp(String paramString1, String paramString2) throws Exception;

	JSONArray getotipplanasian(String paramString1, String paramString2) throws Exception;

	JSONArray getotipplanall(String paramString1, String paramString2) throws Exception;

	JSONArray getotipplanfinal(String paramString1, String paramString2, String paramString3) throws Exception;

	JSONArray genclaimStatus(String paramString) throws Exception;

	JSONArray getGeneralPolicyDetails(String paramString) throws Exception;

	JSONArray getDeferredDetails(String paramString) throws Exception;

	JSONArray getImmediateDetails(String paramString) throws Exception;

	JSONArray getppfmemo(String paramString) throws Exception;

	JSONArray gettitle() throws Exception;

	JSONArray getMstatus() throws Exception;

	JSONArray getContactType() throws Exception;

	JSONArray getSeqId() throws Exception;

	JSONArray getHousingDetailsAgainstCID(String paramString) throws Exception;

	JSONArray getAllDzongkhangs() throws Exception;

	JSONArray getAllGewogs() throws Exception;

	JSONArray getDzongkhagByDzoCode(String paramString) throws Exception;

	JSONArray getGewogByGewogCode(String paramString) throws Exception;

	JSONArray getGewogsUnderDzongkhags(String paramString) throws Exception;

	JSONArray getPPFMemo(String paramString) throws Exception;

	JSONArray getAllProduct() throws Exception;

	Map<String, Object> getSumAssuredThreshold(String paramString1, String paramString2) throws Exception;

	Map<String, Object> isAgent(String paramString);

	Map<String, Object> getCustomerCode(String paramString);
	
	Map<String, Object> getCustomerCodeForGeneralInsurance(String paramString);

	
}