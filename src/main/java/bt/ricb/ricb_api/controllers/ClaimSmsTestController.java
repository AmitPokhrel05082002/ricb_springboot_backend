package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class ClaimSmsTestController {

    @Autowired
    private ApiService apiService;

    // ✅ POST endpoint for Tcell
    @PostMapping("/tcell")
    public ResponseEntity<String> sendTcellSms(
            @RequestParam String mobile,
            @RequestParam String message) {

        try {
            // ✅ Ensure Tcell number starts with 77
            if (!mobile.startsWith("77")) {
                return ResponseEntity.badRequest()
                        .body("Invalid Tcell number. It should start with 77.");
            }

            // ✅ Prepend country code if not already
            if (!mobile.startsWith("975")) {
                mobile = "975" + mobile;
            }

            // ✅ Call the correct Tcell service
            ResponseEntity<String> response =
                    apiService.sendSmsTcell(message, mobile);

            // Optional: parse response
            String body = response.getBody();
            String[] parts = body != null ? body.split(",") : new String[]{"N/A"};
            String status = parts[0];
            String messageId = parts.length > 1 ? parts[1] : "N/A";

            return ResponseEntity.ok("Status: " + status + ", Message ID: " + messageId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error sending SMS: " + e.getMessage());
        }
    }

    @PostMapping("/bmobile")
    public ResponseEntity<String> sendBMobileSms(
            @RequestParam String mobile,
            @RequestParam String message) {

        try {
            // Ensure B-Mobile number starts with 17
            if (!mobile.startsWith("17")) {
                return ResponseEntity.badRequest()
                        .body("Invalid B-Mobile number. It should start with 17.");
            }

            ResponseEntity<String> response =
                    apiService.sendSms(message, mobile);

            // Optional: parse response (if gateway returns status,messageId)
            String body = response.getBody();
            String[] parts = body != null ? body.split(",") : new String[]{"N/A"};
            String status = parts[0];
            String messageId = parts.length > 1 ? parts[1] : "N/A";

            return ResponseEntity.ok("Status: " + status + ", Message ID: " + messageId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error sending SMS: " + e.getMessage());
        }
    }
}