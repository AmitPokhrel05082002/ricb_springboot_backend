package bt.ricb.ricb_api.dao;

import bt.ricb.ricb_api.models.CustomerDto;
import bt.ricb.ricb_api.models.PolicyReqDto;
import java.util.List;
import org.json.JSONArray;

public interface PolicyDao {
	Integer insertNyekorDtiPolicy(PolicyReqDto paramPolicyReqDto, List<CustomerDto> paramList) throws Exception;

	JSONArray getdtiNyekkorDetailsAgainstCid(String paramString1, String paramString2) throws Exception;

	JSONArray getPolciciesAgainstCid(String paramString) throws Exception;

	JSONArray getUserDetailsAgainstTpn(String paramString) throws Exception;
}