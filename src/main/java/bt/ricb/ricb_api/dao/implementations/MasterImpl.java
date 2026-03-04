package bt.ricb.ricb_api.dao.implementations;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.MasterDao;
import bt.ricb.ricb_api.util.ToJSON;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

@Repository
public class MasterImpl implements MasterDao {
	public JSONArray getAllDiscountLoad() throws Exception {
		String sql = "select * from ricb_li.TL_LI_MAS_COVER_DETAILS@ricb_com";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(sql);
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

	public JSONArray getAllCovers() throws Exception {
		String sql = "select * from ricb_li.TL_LI_MAS_COVER_DETAILS@ricb_com";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(sql);
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

	public JSONArray getCoverByProduct(String prodCode) throws Exception {
		String sql = "select * from ricb_li.TL_LI_MAS_COVER_DETAILS@ricb_com where product_code = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		ToJSON convertor = new ToJSON();
		JSONArray json = new JSONArray();

		try {
			conn = ConnectionManager.getOracleConnection();

			if (conn != null) {
				pst = conn.prepareStatement(sql);
				pst.setString(1, prodCode);
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