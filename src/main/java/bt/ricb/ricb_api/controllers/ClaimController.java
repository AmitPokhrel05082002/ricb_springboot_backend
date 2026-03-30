package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.services.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitClaim(
//            @RequestPart("data") String data,
//            @RequestPart("file") MultipartFile file
//    ) {
//        try {
//            // ================= Validate File =================
//            if (file == null || file.isEmpty()) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "FAILED",
//                        "message", "No file uploaded. A ZIP file is required.",
//                        "timestamp", LocalDateTime.now()
//                ));
//            }
//
//            String fileName = file.getOriginalFilename();
//            if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "FAILED",
//                        "message", "Invalid file type. Only ZIP files are allowed.",
//                        "timestamp", LocalDateTime.now()
//                ));
//            }
//
//            long maxSizeBytes = 20L * 1024 * 1024;
//            if (file.getSize() > maxSizeBytes) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "FAILED",
//                        "message", "File size exceeds 20 MB.",
//                        "timestamp", LocalDateTime.now()
//                ));
//            }
//
//            // ================= Parse JSON =================
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
//            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//            FullClaimDTO dto = mapper.readValue(data, FullClaimDTO.class);
//
//            // ================= Call Service =================
//            Map<String, Object> result = claimService.submitClaim(dto, file);
//
//            return ResponseEntity.ok(Map.of(
//                    "status", "SUCCESS",
//                    "message", "Claim submitted successfully",
//                    "data", result,
//                    "timestamp", LocalDateTime.now()
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "status", "ERROR",
//                    "message", "Claim submission failed",
//                    "error", e.getMessage(),
//                    "timestamp", LocalDateTime.now()
//            ));
//        }
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitClaim(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file
    ) {

        Connection conn = null;
        PreparedStatement seqStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            // ================= FILE VALIDATION =================
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "No file uploaded. A ZIP file is required.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Invalid file type. Only ZIP files are allowed.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            long maxSizeBytes = 20L * 1024 * 1024;
            if (file.getSize() > maxSizeBytes) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "File size exceeds 20 MB.",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // ================= PARSE JSON =================
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            FullClaimDTO dto = mapper.readValue(data, FullClaimDTO.class);

            // ================= POLICY VALIDATION =================
            if (dto.getPolicies() == null || dto.getPolicies().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "At least one policy is required",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // ================= DB CONNECTION =================
            conn = ConnectionManager.getOracleConnection();
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status", "FAILED", "message", "Oracle DB connection failed"));
            }

            conn.setAutoCommit(false); // start transaction

            // ================= PREPARE VALUES =================
            String claimType = dto.getClaim().getClaimType();
            String claimIntimationBy = dto.getClaimant().getFullName();
            String dateOfDeath = dto.getClaim().getDateOfDeath().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String placeOfDeath = dto.getClaim().getPlaceOfDeath();
            String typeOfDeath = dto.getClaim().getDeathType();
            String branchCode = String.valueOf(dto.getClaim().getNearestBranchId());
            String causeOfDeath = dto.getClaim().getCauseOfDeath();
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            // ================= PREPARE INSERT STATEMENT =================
            String insertQuery = """
            INSERT INTO ricb_li.tl_li_tr_claims_header
            ( serial_no, claim_type, policy_no, policy_serial_no,
              claim_intm_date, claim_intm_by, claim_intm_relation,
              date_of_death, place_of_death, who_was_died,
              type_of_death, mode_of_intimation,
              claim_regn_no, claim_regn_date, status_code,
              prepared_by, prepared_on, prepared_time,
              branch_code, risk_commencement,
              cause_of_death, deceased_name )
            VALUES
            ( ?, ?, ?, ?,
              TO_DATE(?, 'dd-mm-yyyy'), ?, ?,
              TO_DATE(?, 'dd-mm-yyyy'), ?, 'P',
              ?, 'W', '',
              TO_DATE(?, 'dd-mm-yyyy'), 'A',
              'Web', TO_DATE(?, 'dd-mm-yyyy'), ?,
              ?, '',
              ?, ? )
        """;
            insertStmt = conn.prepareStatement(insertQuery);

            List<Long> serialNumbers = new ArrayList<>();

            // ================= LOOP OVER POLICIES =================
            for (PolicyDTO policy : dto.getPolicies()) {
                if (policy.getPolicyNumber() == null || policy.getPolicyNumber().trim().isEmpty()) {
                    conn.rollback();
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "FAILED",
                            "message", "policyNumber cannot be null or empty",
                            "timestamp", LocalDateTime.now()
                    ));
                }

                // Get new serial number for each policy
                seqStmt = conn.prepareStatement("SELECT ricb_li.sq_li_tr_claims_header.nextval FROM dual");
                rs = seqStmt.executeQuery();
                long serialNo = 0;
                if (rs.next()) serialNo = rs.getLong(1);
                serialNumbers.add(serialNo);

                insertStmt.setLong(1, serialNo);
                insertStmt.setString(2, claimType);
                insertStmt.setString(3, policy.getPolicyNumber());
                insertStmt.setNull(4, java.sql.Types.VARCHAR);

                insertStmt.setString(5, today);
                insertStmt.setString(6, claimIntimationBy);
                insertStmt.setNull(7, java.sql.Types.VARCHAR);

                insertStmt.setString(8, dateOfDeath);
                insertStmt.setString(9, placeOfDeath);
                insertStmt.setString(10, typeOfDeath);

                insertStmt.setString(11, today);
                insertStmt.setString(12, today);
                insertStmt.setString(13, time);

                insertStmt.setString(14, branchCode);
                insertStmt.setString(15, causeOfDeath);

                insertStmt.setNull(16, java.sql.Types.VARCHAR);

                insertStmt.executeUpdate();

                try { if (rs != null) rs.close(); } catch (Exception ignored) {}
                try { if (seqStmt != null) seqStmt.close(); } catch (Exception ignored) {}
            }

            // ================= CALL SERVICE =================
            Map<String, Object> result = claimService.submitClaim(dto, file);

            conn.commit();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "serialNumbers", serialNumbers,
                    "message", "Claims successfully recorded in the system",
                    "data", result,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", "Claim submission failed",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (seqStmt != null) seqStmt.close(); } catch (Exception ignored) {}
            try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    // Get all Dzongkhags
    @GetMapping("/dzongkhags")
    public List<DzongkhagDTO> getDzongkhagNames() {
        return claimService.getDzongkhagNames();
    }

    // Get Gewogs by Dzongkhag ID
    @GetMapping("/gewogs/{dzongkhagId}")
    public List<GewogDTO> getGewogs(@PathVariable Integer dzongkhagId) {
        return claimService.getGewogsByDzongkhag(dzongkhagId);
    }

    // Get Villages by Gewog ID
    @GetMapping("/villages/{gewogId}")
    public List<VillageDTO> getVillages(@PathVariable Integer gewogId) {
        return claimService.getVillagesByGewog(gewogId);
    }

    // Get all Banks
    @GetMapping("/banks")
    public List<BankDTO> getBanks() {
        return claimService.getBanks();
    }

    // Get all Branches
    @GetMapping("/branches")
    public List<BranchDTO> getBranches() {
        return claimService.getBranches();
    }


    @GetMapping("/{cin}/track")
    public Map<String, Object> getClaimDetails(@PathVariable String cin) {
        return claimService.getClaimDetails(cin);
    }

    // ===== 1. Dashboard status count =====
    @GetMapping("/status-counts")
    public ResponseEntity<Map<String, Long>> getClaimStatusCounts() {
        return ResponseEntity.ok(claimService.getClaimStatusCounts());
    }

    // ===== 2. Claim summaries for dashboard ===========
    @GetMapping("summaries")
    public ResponseEntity<List<ClaimSummaryDTO>> getAllClaimSummaries() {
        try {
            List<ClaimSummaryDTO> summaries = claimService.getAllClaimSummaries();
            return ResponseEntity.ok(summaries);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== 3. Full Claim Details =====
    @GetMapping("/{cin}")
    public ResponseEntity<ClaimResponseDRO> getFullClaim(@PathVariable String cin) {
        try {
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(cin);
            return ResponseEntity.ok(fullClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/resubmit")
    public ResponseEntity<ClaimEntity> resubmitClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.resubmitClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/reject")
    public ResponseEntity<ClaimEntity> rejectClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.rejectClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/verify")
    public ResponseEntity<ClaimEntity> verifyClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.verifyClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/approve")
    public ResponseEntity<ClaimEntity> approveClaim(@RequestBody ClaimActionDTO dto) {
        ClaimEntity updatedClaim = claimService.approveClaim(dto);
        return ResponseEntity.ok(updatedClaim);
    }

    @GetMapping("/download/{cin}")
    public ResponseEntity<ByteArrayResource> downloadClaim(@PathVariable String cin) {
        return claimService.downloadClaimFileByCin(cin);
    }

    @PostMapping(value = "/update-document/{cin}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateClaimDocument(
            @PathVariable String cin,
            @RequestPart("file") MultipartFile file) {

        try {

            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is required");
            }

            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".zip")) {
                return ResponseEntity.badRequest().body("Only ZIP files are allowed");
            }

            if (file.getSize() > 20 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size must be less than 20MB");
            }

            claimService.updateClaimDocumentByCin(cin, file);

            return ResponseEntity.ok("Document updated successfully!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/getPolicyDetails")
    public ResponseEntity<?> getPolicyDetails(@RequestParam("cid") String cid,
                                              @RequestParam("dob") String dob) {
        Connection conn = null;
        PreparedStatement dobPst = null;
        PreparedStatement policyPst = null;
        ResultSet dobRs = null;
        ResultSet policyRs = null;

        try {
            if (cid == null || cid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "cid parameter is required"));
            }

            if (dob == null || dob.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "dob parameter is required"));
            }

            conn = ConnectionManager.getOracleConnection();
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            String dobQuery = "SELECT a.DATE_OF_BIRTH " +
                    "FROM RICB_COM.TL_IN_MAS_CUSTOMER a " +
                    "WHERE a.CITIZEN_ID = ?";

            dobPst = conn.prepareStatement(dobQuery);
            dobPst.setString(1, cid.trim());
            dobRs = dobPst.executeQuery();

            if (!dobRs.next()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Citizen not found"));
            }

            java.sql.Date dbDob = dobRs.getDate("DATE_OF_BIRTH");
            if (dbDob == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "DOB not available for this citizen"));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);

            String formattedDbDob = sdf.format(dbDob);
            String inputDob = dob.trim();

            System.out.println("DB DOB: " + formattedDbDob);
            System.out.println("Input DOB: " + inputDob);

            if (!formattedDbDob.equals(inputDob)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "DOB does not match"));
            }

            String policyQuery = "SELECT * FROM V_CLAIMS_LI_POLICIES WHERE cid = ?";

            policyPst = conn.prepareStatement(policyQuery);
            policyPst.setString(1, cid.trim());
            policyRs = policyPst.executeQuery();

            JSONArray jsonArray = convertResultSetToJson(policyRs);

            if (jsonArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message",
                                "No Policies found for the given citizenship ID"));
            }

            return ResponseEntity.ok(jsonArray.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error occurred"));
        } finally {
            try {
                if (policyRs != null) policyRs.close();
                if (dobRs != null) dobRs.close();
                if (policyPst != null) policyPst.close();
                if (dobPst != null) dobPst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray convertResultSetToJson(ResultSet rs) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                obj.put(columnName, value != null ? value : JSONObject.NULL);
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    @PostMapping("/getGroupPolicyDetails")
    public ResponseEntity<?> getGroupPolicyDetails(@RequestParam("cid") String cid,
                                                   @RequestParam("orgCode") String orgCode,
                                                   @RequestParam("dob") String dob) {

        Connection conn = null;
        PreparedStatement dobPst = null;
        PreparedStatement policyPst = null;
        ResultSet dobRs = null;
        ResultSet policyRs = null;

        try {
            if (cid == null || cid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "cid parameter is required"));
            }

            if (orgCode == null || orgCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "orgCode parameter is required"));
            }

            if (dob == null || dob.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "dob parameter is required"));
            }

            conn = ConnectionManager.getOracleConnection();
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // ✅ DOB validation (same as first API)
            String dobQuery = "SELECT a.DATE_OF_BIRTH " +
                    "FROM RICB_COM.TL_IN_MAS_CUSTOMER a " +
                    "WHERE a.CITIZEN_ID = ?";

            dobPst = conn.prepareStatement(dobQuery);
            dobPst.setString(1, cid.trim());
            dobRs = dobPst.executeQuery();

            if (!dobRs.next()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Citizen not found"));
            }

            java.sql.Date dbDob = dobRs.getDate("DATE_OF_BIRTH");
            if (dbDob == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "DOB not available for this citizen"));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);

            String formattedDbDob = sdf.format(dbDob);
            String inputDob = dob.trim();

            System.out.println("DB DOB: " + formattedDbDob);
            System.out.println("Input DOB: " + inputDob);

            if (!formattedDbDob.equals(inputDob)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "DOB does not match"));
            }

            // ✅ Group Policy Query
            String policyQuery = "SELECT * FROM V_CLAIMS_GROUP_LI_POLICIES WHERE CID = ? AND ORG_CODE = ?";

            policyPst = conn.prepareStatement(policyQuery);
            policyPst.setString(1, cid.trim());
            policyPst.setString(2, orgCode.trim());
            policyRs = policyPst.executeQuery();

            JSONArray jsonArray = convertResultSetToJson(policyRs);

            if (jsonArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message",
                                "No Group Policies found for the given CID and ORG_CODE"));
            }

            return ResponseEntity.ok(jsonArray.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error occurred"));
        } finally {
            try {
                if (policyRs != null) policyRs.close();
                if (dobRs != null) dobRs.close();
                if (policyPst != null) policyPst.close();
                if (dobPst != null) dobPst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

//    @PostMapping("/insert-claim-header")
//    public ResponseEntity<?> insertClaimHeader(
//            @RequestParam String claimType,
//            @RequestParam String policyNo,
//            @RequestParam String policySerialNo,
//            @RequestParam String claimIntimationDate,
//            @RequestParam String claimIntimationBy,
//            @RequestParam String claimIntimationRelation,
//            @RequestParam String dateOfDeath,
//            @RequestParam String placeOfDeath,
//            @RequestParam String typeOfDeath,
//            @RequestParam String branchCode,
//            @RequestParam String causeOfDeath,
//            @RequestParam String deceasedName
//    ) {
//
//        Connection conn = null;
//        PreparedStatement seqStmt = null;
//        PreparedStatement insertStmt = null;
//        ResultSet rs = null;
//
//        try {
//            // ✅ FIXED HERE
//            conn = ConnectionManager.getOracleConnection();
//
//            if (conn == null) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Oracle DB connection failed");
//            }
//
//            // ================= 1. Get Serial No =================
//            String seqQuery = "SELECT ricb_li.sq_li_tr_claims_header.nextval FROM dual";
//            seqStmt = conn.prepareStatement(seqQuery);
//            rs = seqStmt.executeQuery();
//
//            long serialNo = 0;
//            if (rs.next()) {
//                serialNo = rs.getLong(1);
//            }
//
//            // ================= 2. Insert =================
//            String insertQuery = """
//        INSERT INTO ricb_li.tl_li_tr_claims_header
//        ( serial_no, claim_type, policy_no, policy_serial_no,
//          claim_intm_date, claim_intm_by, claim_intm_relation,
//          date_of_death, place_of_death, who_was_died,
//          type_of_death, mode_of_intimation,
//          claim_regn_no, claim_regn_date, status_code,
//          prepared_by, prepared_on, prepared_time,
//          branch_code, risk_commencement,
//          cause_of_death, deceased_name )
//        VALUES
//        ( ?, ?, ?, ?,
//          TO_DATE(?, 'dd-mm-yyyy'), ?, ?,
//          TO_DATE(?, 'dd-mm-yyyy'), ?, 'P',
//          ?, 'W', '',
//          TO_DATE(?, 'dd-mm-yyyy'), 'A',
//          'Web', TO_DATE(?, 'dd-mm-yyyy'), ?,
//          ?, '',
//          ?, ? )
//        """;
//
//            insertStmt = conn.prepareStatement(insertQuery);
//
//            String today = java.time.LocalDate.now()
//                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//
//            String time = java.time.LocalTime.now()
//                    .format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
//
//            insertStmt.setLong(1, serialNo);
//            insertStmt.setString(2, claimType);
//            insertStmt.setString(3, policyNo);
//            insertStmt.setString(4, policySerialNo);
//            insertStmt.setString(5, claimIntimationDate);
//            insertStmt.setString(6, claimIntimationBy);
//            insertStmt.setString(7, claimIntimationRelation);
//            insertStmt.setString(8, dateOfDeath);
//            insertStmt.setString(9, placeOfDeath);
//            insertStmt.setString(10, typeOfDeath);
//            insertStmt.setString(11, today);
//            insertStmt.setString(12, today);
//            insertStmt.setString(13, time);
//            insertStmt.setString(14, branchCode);
//            insertStmt.setString(15, causeOfDeath);
//            insertStmt.setString(16, deceasedName);
//
//            insertStmt.executeUpdate();
//
//            return ResponseEntity.ok(Map.of(
//                    "serialNo", serialNo,
//                    "message", "Inserted successfully"
//            ));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .body("Error: " + e.getMessage());
//
//        } finally {
//            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
//            try { if (seqStmt != null) seqStmt.close(); } catch (Exception ignored) {}
//            try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
//            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
//        }
//    }
}
