package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.dao.MasterDao;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin({ "*" })
@RequestMapping({ "master" })
@RestController
public class MasterApi {
	@Autowired
	private MasterDao masterDao;

	@GetMapping({ "getAllDiscountLoad" })
	public ResponseEntity<String> getAllDiscountLoad() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.masterDao.getAllDiscountLoad();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getAllCovers" })
	public ResponseEntity<String> getAllCovers() throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.masterDao.getAllCovers();
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}

	@GetMapping({ "getCoverByProduct" })
	public ResponseEntity<String> getCoverByProduct(@RequestParam("productCode") String productCode) throws Exception {
		JSONArray jsonData = new JSONArray();
		try {
			jsonData = this.masterDao.getCoverByProduct(productCode);
			return ResponseEntity.ok().body(jsonData.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Server was not able to process your request");
		}
	}
}
