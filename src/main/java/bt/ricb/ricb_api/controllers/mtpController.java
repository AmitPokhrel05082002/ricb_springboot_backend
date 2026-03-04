package bt.ricb.ricb_api.controllers;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.PolicyInfo;
import bt.ricb.ricb_api.models.RegistrationType;
import bt.ricb.ricb_api.models.VehicleModel;
import bt.ricb.ricb_api.models.VehicleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin({ "*" })
public class mtpController {
	
	@Autowired
    private EntityManager entityManager;
	
	@GetMapping("/registrationType_gst")
	public List<RegistrationType> fetchRegistrationTypes() {
	    List<RegistrationType> registrationTypes = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        conn = ConnectionManager.getGeneralInsuranceConnection(); // Replace with your actual Oracle connection logic

	        String sql = "SELECT CODE_CODE, CODE_DESC " +
	                     "FROM ricb_gi.tl_gi_mas_code " +
	                     "WHERE type_code = '84' AND status_code = 'Active' AND code_code IN ('4','7')" +
	                     "ORDER BY seq_no";

	        pst = conn.prepareStatement(sql);
	        rs = pst.executeQuery();

	        while (rs.next()) {
	            RegistrationType rt = new RegistrationType();
	            rt.setRegistrationTypeCode(rs.getString("CODE_CODE"));
	            rt.setRegistrationTypeDesc(rs.getString("CODE_DESC"));
	            registrationTypes.add(rt);
	        }

	    } catch (Exception e) {
	        System.err.println("Failed to fetch registration types: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }

	    return registrationTypes;
	}
	
	@GetMapping("/vehicleType_gst")
	public List<VehicleType> fetchVehicleTypes() {
	    List<VehicleType> vehicleTypes = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        conn = ConnectionManager.getLifeConnection(); // Ensure this accesses the correct Oracle DB

	        String sql = "SELECT CODE_CODE, CODE_DESC " +
	                     "FROM ricb_com.tl_in_mas_code " +
	                     "WHERE type_code = '39' AND status_code = 'Active' and code_code != '5' " +
	                     "ORDER BY CODE_DESC";

	        pst = conn.prepareStatement(sql);
	        rs = pst.executeQuery();

	        while (rs.next()) {
	            VehicleType vt = new VehicleType();
	            vt.setVehicleTypeCode(rs.getString("CODE_CODE"));
	            vt.setVehicleTypeName(rs.getString("CODE_DESC"));
	            vehicleTypes.add(vt);
	        }

	    } catch (Exception e) {
	        System.err.println("Failed to fetch vehicle types: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }

	    return vehicleTypes;
	}
	
	@GetMapping("/checkEnginePolicy_gst/{engineNo}")
	public ResponseEntity<List<PolicyInfo>> checkActivePolicyByEngine(@PathVariable String engineNo) {
	    List<PolicyInfo> policyList = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        conn = ConnectionManager.getGeneralInsuranceConnection();

	        String sql = "SELECT b.policy_no " +
	                    "FROM RICB_GI.TL_GI_TR_MOTOR_DTL a " +
	                    "INNER JOIN RICB_GI.TL_GI_TR_POLICY_HEADER b " +
	                    "ON a.POLICY_HDR_SRL_NO = b.serial_no " +
	                    "WHERE a.ENGINE_NO = ? " +
	                    "AND b.status_code IN ('G','F')";

	        pst = conn.prepareStatement(sql);
	        pst.setString(1, engineNo);

	        rs = pst.executeQuery();

	        while (rs.next()) {
	            PolicyInfo policy = new PolicyInfo();
	            policy.setPolicyNo(rs.getString("policy_no"));
	            policyList.add(policy);
	        }

	        return ResponseEntity.ok(policyList);

	    } catch (Exception e) {
	        System.err.println("Failed to fetch policy for engine number: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(new ArrayList<>());
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }
	}
	
	@GetMapping("/vehicleModel_gst")
	public List<VehicleModel> fetchVehicleModels() {
	    List<VehicleModel> vehicleModels = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        conn = ConnectionManager.getGeneralInsuranceConnection(); // Use your configured Oracle connection

	        String sql = "SELECT a.mfg_code, " +
                    "       b.cm_name, " +
                    "       a.model_code, " +
                    "case WHEN A.MODEL_FUEL != 'E' THEN a.model_cc\n" +
                    "	 ELSE a.MODEL_KW END AS cc, " +
                    "       a.model_name, " +
                    "       a.model_variant, " +
                    "       a.model_cc AS cc, " +
                    "       a.model_fuel AS fuel, " +
                    "       a.weight_capacity AS weight, " +
                    "       a.passenger_capacity " +
                    "FROM ricb_gi.tl_gi_mas_vehicle_model a, " +
                    "     ricb_gi.tl_gi_mas_vehicle_manufacturer b " +
                    "WHERE a.mfg_code = b.cm_code and a.MFG_CODE NOT IN ('ISUZU','MAHINDRA') and b.STATUS_CODE = 'A' and a.STATUS_CODE = 'A'";

	        pst = conn.prepareStatement(sql);
	        rs = pst.executeQuery();

	        while (rs.next()) {
	            VehicleModel vm = new VehicleModel();
	            vm.setMfgCode(rs.getString("mfg_code"));
	            vm.setCmName(rs.getString("cm_name"));
	            vm.setModelCode(rs.getString("model_code"));
	            vm.setModelName(rs.getString("model_name"));
	            vm.setModelVariant(rs.getString("model_variant"));
	            vm.setCc(rs.getString("cc"));
	            vm.setFuel(rs.getString("fuel"));
	            vm.setWeight(rs.getString("weight"));
	            vm.setPassengerCapacity(rs.getString("passenger_capacity"));
	            vehicleModels.add(vm);
	        }

	    } catch (Exception e) {
	        System.err.println("Failed to fetch vehicle models: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        ConnectionManager.close(conn, rs, pst);
	    }

	    return vehicleModels;
	}



}
