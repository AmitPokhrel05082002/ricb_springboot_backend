package bt.ricb.ricb_api.dao;

import org.json.JSONArray;

public interface MasterDao {
	JSONArray getAllDiscountLoad() throws Exception;

	JSONArray getAllCovers() throws Exception;

	JSONArray getCoverByProduct(String paramString) throws Exception;
}
