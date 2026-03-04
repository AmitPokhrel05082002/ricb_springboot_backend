package bt.ricb.ricb_api.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import bt.ricb.ricb_api.config.ConnectionManager;


@RestController
@RequestMapping("/api/customer")
@CrossOrigin({ "*" })
public class CustomerSearchController {

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomerByCitizenId(@RequestParam String citizenId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Map<String, Object> response = new HashMap<>();

        try {
            conn = ConnectionManager.getGeneralInsuranceConnection();

            String sql = "SELECT CUSTOMER_CODE FROM TL_GI_MAS_CUSTOMER WHERE CITIZEN_ID = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, citizenId);

            rs = pst.executeQuery();

            if (rs.next()) {
                String customerCode = rs.getString("CUSTOMER_CODE");
                response.put("success", true);
                response.put("customerCode", customerCode);
                response.put("message", "Customer found successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("customerCode", null);
                response.put("message", "No customer found with the provided citizen ID");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (SQLException e) {
            System.err.println("Database error while searching customer: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Database error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            System.err.println("Unexpected error while searching customer: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            ConnectionManager.close(conn, rs, pst);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomerByCitizenIdPost(@RequestBody Map<String, String> request) {
        String citizenId = request.get("citizenId");
        
        if (citizenId == null || citizenId.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Citizen ID is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        return searchCustomerByCitizenId(citizenId.trim());
    }
}
