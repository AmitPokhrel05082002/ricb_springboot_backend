package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.AgencyUserEntity;
import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.models.RliCollectionDateEntity;
import bt.ricb.ricb_api.repository.AgencyUserRepository;
import bt.ricb.ricb_api.repository.PolicyRepository;
import bt.ricb.ricb_api.repository.RliCollectionDateRepository;
import bt.ricb.ricb_api.services.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

@RestController
@RequestMapping("/claims")
@CrossOrigin({ "*" })
public class ClaimController {

    @Autowired
    private ClaimService claimService;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private AgencyUserRepository userRepo;
    @Autowired
    private RliCollectionDateRepository configRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitClaim(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // ================= Validate File =================
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

            // ================= Parse JSON =================
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            FullClaimDTO dto = mapper.readValue(data, FullClaimDTO.class);

            // ================= Call Service =================
            Map<String, Object> result = claimService.submitClaim(dto, file);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Claim submitted successfully",
                    "data", result,
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", "Claim submission failed",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
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
    public ResponseEntity<?> getBranches() {
        try {
            return ResponseEntity.ok(claimService.getBranches());

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to fetch branches: " + e.getMessage());
        }
    }

    @GetMapping("/{cin}/track")
    public Map<String, Object> getClaimDetails(@PathVariable String cin) {

        return claimService.getClaimDetails(cin);
    }

    // ===== 1. Dashboard status count =====
    @GetMapping("/status-counts")
    public ResponseEntity<?> getClaimStatusCounts(Authentication authentication) {

        String username = authentication.getName();

        AgencyUserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                claimService.getClaimStatusCounts(user)
        );
    }

    // ===== 2. Claim summaries for dashboard ===========
    @GetMapping("/summaries")
    public ResponseEntity<List<ClaimSummaryDTO>> getAllClaimSummaries(
            Authentication authentication) {

        try {

            String username = authentication.getName();

            AgencyUserEntity user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<ClaimSummaryDTO> summaries =
                    claimService.getAllClaimSummaries(user);

            return ResponseEntity.ok(summaries);

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // ===== 3. Full Claim Details =====
    @GetMapping("/details/{cin}")
    public ResponseEntity<ClaimResponseDRO> getFullClaim(@PathVariable String cin) {
        try {
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(cin);
            return ResponseEntity.ok(fullClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/resubmit")
    public ResponseEntity<ClaimEntity> resubmitClaim(
            @RequestBody ClaimCompleteDTO dto,
            Authentication authentication
    ) {
        ClaimEntity updatedClaim = claimService.resubmitClaim(
                dto,
                authentication.getName()
        );

        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeClaim(
            @RequestBody ClaimCompleteDTO dto,
            Authentication authentication
    ) {

        // ================= USER (FIXED) =================
        String username = authentication.getName();

        AgencyUserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ================= SERVICE =================
        ClaimEntity updatedClaim =
                claimService.completeClaim(dto, user);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Claim completed successfully"
        ));
    }

    @PostMapping(value = "/reject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> rejectClaim(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication
    ) {

        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement seqStmt = null;
        ResultSet rs = null;

        try {

            // ================= USER (FIXED) =================
            String username = authentication.getName();

            AgencyUserEntity user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String actionedBy = String.valueOf(user.getId()); // FIXED

            // ================= DTO =================
            ObjectMapper mapper = new ObjectMapper();
            ClaimActionDTO dto = mapper.readValue(data, ClaimActionDTO.class);

            if (dto.getPolicyNumbers() == null || dto.getPolicyNumbers().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "No policies selected for rejection"
                ));
            }

            // ================= GET CLAIM DATA =================
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(dto.getCin());

            ClaimDTO claim = fullClaim.getClaim();
            ClaimantDTO claimant = fullClaim.getClaimant();

            List<PolicyDTO> policies = fullClaim.getPolicies().stream()
                    .filter(p -> dto.getPolicyNumbers().contains(p.getPolicyNumber()))
                    .toList();

            if (policies.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Selected policies not found"
                ));
            }

            // ================= ORACLE CONNECTION =================
            conn = ConnectionManager.getOracleConnectionforims();
            conn.setAutoCommit(false);

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
              TO_DATE(?, 'dd-mm-yyyy'), 'Z',
              'Web', TO_DATE(?, 'dd-mm-yyyy'), ?,
              ?, '',
              ?, ? )
        """;

            insertStmt = conn.prepareStatement(insertQuery);

            List<Long> serialNumbers = new ArrayList<>();

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            for (PolicyDTO policy : policies) {

                seqStmt = conn.prepareStatement(
                        "SELECT ricb_li.sq_li_tr_claims_header.nextval FROM dual"
                );

                rs = seqStmt.executeQuery();

                long serialNo = rs.next() ? rs.getLong(1) : 0;
                serialNumbers.add(serialNo);

                insertStmt.setLong(1, serialNo);
                insertStmt.setString(2, claim.getClaimType());
                insertStmt.setString(3, policy.getPolicyNumber());
                insertStmt.setObject(4, policy.getPolicySerialNumber());

                insertStmt.setString(5,
                        policy.getIntimationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                );

                insertStmt.setString(6, claimant.getFullName());
                insertStmt.setString(7, claimant.getRelation());

                insertStmt.setString(8,
                        claim.getDateOfDeath().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                );

                insertStmt.setString(9, claim.getPlaceOfDeath());
                insertStmt.setString(10, claim.getDeathType());

                insertStmt.setString(11, "");
                insertStmt.setString(12, today);
                insertStmt.setString(13, time);

                insertStmt.setString(14, claim.getNearestBranchId());
                insertStmt.setString(15, claim.getCauseOfDeath());
                insertStmt.setString(16, policy.getPolicyHolderName());

                insertStmt.executeUpdate();

                rs.close();
                seqStmt.close();
            }

            conn.commit();

            // ================= SERVICE CALL =================
            claimService.rejectPolicies(dto, file, actionedBy);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Selected policies rejected successfully",
                    "serialNumbers", serialNumbers
            ));

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {}

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));

        } finally {

            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (seqStmt != null) seqStmt.close(); } catch (Exception ignored) {}
            try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @PostMapping(value = "/approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> approveClaim(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication
    ) {

        Connection conn = null;
        PreparedStatement seqStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {

            // ================= USER FIX =================
            String username = authentication.getName();

            AgencyUserEntity user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String actionedBy = String.valueOf(user.getId());

            // ================= DTO =================
            ObjectMapper mapper = new ObjectMapper();
            ClaimActionDTO dto = mapper.readValue(data, ClaimActionDTO.class);

            if (dto.getPolicyNumbers() == null || dto.getPolicyNumbers().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "No policies selected"
                ));
            }

            // ================= CLAIM DATA =================
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(dto.getCin());

            ClaimDTO claim = fullClaim.getClaim();
            ClaimantDTO claimant = fullClaim.getClaimant();

            List<PolicyDTO> policies = fullClaim.getPolicies().stream()
                    .filter(p -> dto.getPolicyNumbers().contains(p.getPolicyNumber()))
                    .toList();

            if (policies.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Selected policies not found"
                ));
            }

            // ================= DB CONNECTION =================
            conn = ConnectionManager.getOracleConnectionforims();
            conn.setAutoCommit(false);

            // ================= SQL (NO TO_DATE) =================
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
              ?, ?, ?,
              ?, ?, 'P',
              ?, 'W', '',
              ?, 'A',
              'Web', ?, ?,
              ?, '',
              ?, ? )
        """;

            insertStmt = conn.prepareStatement(insertQuery);

            List<Long> serialNumbers = new ArrayList<>();

            LocalDate todayDate = LocalDate.now();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            // ================= LOOP =================
            for (PolicyDTO policy : policies) {

                seqStmt = conn.prepareStatement(
                        "SELECT ricb_li.sq_li_tr_claims_header.nextval FROM dual"
                );

                rs = seqStmt.executeQuery();
                long serialNo = rs.next() ? rs.getLong(1) : 0;

                serialNumbers.add(serialNo);

                // ================= SAFE DATE HANDLING =================
                insertStmt.setLong(1, serialNo);
                insertStmt.setString(2, claim.getClaimType());
                insertStmt.setString(3, policy.getPolicyNumber());
                insertStmt.setObject(4, policy.getPolicySerialNumber());

                // ✔ FIXED (NO STRING FORMAT)
                insertStmt.setObject(5, policy.getIntimationDate());
                insertStmt.setString(6, claimant.getFullName());
                insertStmt.setString(7, claimant.getRelation());

                insertStmt.setObject(8, claim.getDateOfDeath());
                insertStmt.setString(9, claim.getPlaceOfDeath());
                insertStmt.setString(10, claim.getDeathType());

                insertStmt.setString(11, "");
                insertStmt.setObject(12, todayDate);
                insertStmt.setString(13, time);

                insertStmt.setString(14, claim.getNearestBranchId());
                insertStmt.setString(15, claim.getCauseOfDeath());
                insertStmt.setString(16, policy.getPolicyHolderName());

                insertStmt.executeUpdate();

                rs.close();
                seqStmt.close();
            }

            conn.commit();

            // ================= SERVICE CALL =================
            claimService.approvePolicies(dto, file, actionedBy);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Selected policies approved successfully",
                    "serialNumbers", serialNumbers
            ));

        } catch (Exception e) {

            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));

        } finally {

            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (seqStmt != null) seqStmt.close(); } catch (Exception ignored) {}
            try { if (insertStmt != null) insertStmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @GetMapping("/documents/{cin}")
    public ResponseEntity<List<ClaimDocumentsDTO>> getClaimDocuments(
            @PathVariable String cin
    ) {
        return ResponseEntity.ok(
                claimService.getClaimDocuments(cin)
        );
    }

    @GetMapping("/document/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadSingleDocument(
            @PathVariable Integer documentId
    ) {
        return claimService.downloadSingleDocument(documentId);
    }

    @PostMapping(value = "/update-document/{cin}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateClaimDocument(
            @PathVariable String cin,
            @RequestPart("files") List<MultipartFile> files
    ) {

        try {

            claimService.updateClaimDocumentByCin(cin, files);

            return ResponseEntity.ok("Documents uploaded successfully!");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body("Something went wrong");
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
            // ✅ Validate inputs
            if (cid == null || cid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "cid parameter is required"));
            }

            if (dob == null || dob.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "dob parameter is required"));
            }

            conn = ConnectionManager.getOracleConnectionforims();

            // ✅ Step 1: Validate DOB
            String dobQuery = "SELECT DATE_OF_BIRTH FROM RICB_COM.TL_IN_MAS_CUSTOMER WHERE CITIZEN_ID = ?";
            dobPst = conn.prepareStatement(dobQuery);
            dobPst.setString(1, cid.trim());
            dobRs = dobPst.executeQuery();

            if (!dobRs.next()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Citizen not found"));
            }

            java.sql.Date dbDob = dobRs.getDate("DATE_OF_BIRTH");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            if (!sdf.format(dbDob).equals(dob.trim())) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "DOB does not match"));
            }

            // ✅ Step 2: Fetch ALL columns (original structure)
            String policyQuery = "SELECT * FROM V_CLAIMS_LI_POLICIES WHERE CID = ?";
            policyPst = conn.prepareStatement(policyQuery);
            policyPst.setString(1, cid.trim());
            policyRs = policyPst.executeQuery();

            JSONArray resultArray = new JSONArray();
            List<String> existingPolicies = new ArrayList<>();

            ResultSetMetaData metaData = policyRs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (policyRs.next()) {

                String policyNo = policyRs.getString("POLICY_NO");

                // ✅ Check if exists in your DB
                if (policyRepository.existsByPolicyNumber(policyNo)) {
                    existingPolicies.add(policyNo);
                    continue; // skip existing
                }

                // ✅ Build ORIGINAL JSON dynamically
                JSONObject obj = new JSONObject();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = policyRs.getObject(i);
                    obj.put(columnName, value);
                }

                resultArray.put(obj);
            }

            // ✅ If no new policies
            if (resultArray.length() == 0 && !existingPolicies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("message", "Claim has already been processed for the provided details. Contact 1818 for clarification."));
            }

            if (resultArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Claimable policies were not found for the provided details."));
            }

            // ✅ Return SAME structure as Oracle
            return ResponseEntity.ok(resultArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
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

    @PostMapping("/getRuralLifePolicies")
    public ResponseEntity<?> getRuralLifePoliciesNew(
            @RequestParam("cid") String cid,
            @RequestParam(value = "dod", required = false) String dod) {

        if (cid == null || cid.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "cid parameter is required"));
        }

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            Date dateOfDeath = null;
            Integer dodYear = null;

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (dod != null && !dod.trim().isEmpty()) {
                try {
                    dateOfDeath = inputDateFormat.parse(dod.trim());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateOfDeath);
                    dodYear = cal.get(Calendar.YEAR);

                } catch (ParseException e) {
                    return ResponseEntity.badRequest()
                            .body(Collections.singletonMap(
                                    "error",
                                    "Invalid DOD format. Use dd-MM-yyyy"
                            ));
                }
            }

            String apiUrl = "http://apps.ricb.bt/rliHouseholdDetails.php?cid=" + cid.trim();

            HttpURLConnection connHttp = (HttpURLConnection) new URL(apiUrl).openConnection();
            connHttp.setRequestMethod("GET");
            connHttp.setConnectTimeout(10000);
            connHttp.setReadTimeout(10000);

            if (connHttp.getResponseCode() != 200) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Collections.singletonMap("error", "Failed to fetch GovTech data"));
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connHttp.getInputStream())
            );

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            JSONArray memberArray = jsonResponse
                    .getJSONObject("eligibleMemberCountDetails")
                    .getJSONArray("eligibleMemberCountDetail");

            if (memberArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "No household data found"));
            }

            JSONObject member = memberArray.getJSONObject(0);

            String householdNo = member.optString("Household_number", null);
            String previousHHNo = member.optString("Previous_HH_No", null);
            String censusTransferDateStr = member.optString("Census_Transfer_Date", null);

            String finalHouseholdNo = householdNo;

            Date effectiveDate = configRepository.findAll()
                    .stream()
                    .findFirst()
                    .map(RliCollectionDateEntity::getEffectiveDate)
                    .orElse(null);

            if (censusTransferDateStr != null
                    && !censusTransferDateStr.equalsIgnoreCase("null")
                    && !censusTransferDateStr.trim().isEmpty()
                    && effectiveDate != null) {

                Date censusTransferDate = dbDateFormat.parse(censusTransferDateStr);

                if (effectiveDate.before(censusTransferDate)) {
                    if (previousHHNo != null && !previousHHNo.trim().isEmpty()) {
                        finalHouseholdNo = previousHHNo;
                    }
                }
            }

            conn = ConnectionManager.getOracleConnectionforims();

            StringBuilder queryBuilder = new StringBuilder(
                    "SELECT POLICY_NO, POLICY_START_DATE, BRANCH_CODE, COLLECTION_DATE " +
                            "FROM TL_LI_TR_RURAL_POL_HDR " +
                            "WHERE PRESENT_HOUSEHOLD_NO = ? " +
                            "AND STATUS_CODE = 'D' "
            );

            // ✅ ONLY DOD YEAR FILTER (THIS IS YOUR FIX)
            if (dodYear != null) {
                queryBuilder.append(" AND UNDERWRITING_YEAR = ? ");
            }

            String query = queryBuilder.toString();
            pst = conn.prepareStatement(query);

            int index = 1;

            pst.setString(index++, finalHouseholdNo);

            if (dodYear != null) {
                pst.setString(index++, String.valueOf(dodYear));
            }

            rs = pst.executeQuery();

            List<Map<String, Object>> filteredPolicies = new ArrayList<>();
            List<Map<String, Object>> policyList = new ArrayList<>();
            List<String> existingPolicies = new ArrayList<>();

            while (rs.next()) {

                String policyNo = rs.getString("POLICY_NO");
                String collectionDateStr = rs.getString("COLLECTION_DATE");

                if (dateOfDeath != null && collectionDateStr != null) {
                    try {
                        Date collectionDate = dbDateFormat.parse(collectionDateStr);

                        if (collectionDate.compareTo(dateOfDeath) >= 0) {
                            continue;
                        }
                    } catch (ParseException e) {
                        continue;
                    }
                }

                Map<String, Object> policyObj = new HashMap<>();
                policyObj.put("POLICY_NO", policyNo);
                policyObj.put("POLICY_START_DATE", rs.getString("POLICY_START_DATE"));
                policyObj.put("BRANCH_CODE", rs.getString("BRANCH_CODE"));
                policyObj.put("HOUSEHOLD_NO", finalHouseholdNo);
                policyObj.put("COLLECTION_DATE", collectionDateStr);
                policyObj.put("SA", 30000);
                policyObj.put("STATUS", "Active");

                if (dateOfDeath != null) {
                    policyObj.put("DOD", inputDateFormat.format(dateOfDeath));
                }

                filteredPolicies.add(policyObj);
            }

            for (Map<String, Object> policy : filteredPolicies) {
                String policyNo = (String) policy.get("POLICY_NO");

                if (policyRepository.existsByPolicyNumber(policyNo)) {
                    existingPolicies.add(policyNo);
                } else {
                    policyList.add(policy);
                }
            }

            if (filteredPolicies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Claimable policies were not found for the provided details."));
            }

            if (policyList.isEmpty() && !existingPolicies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("message", "Claim has already been processed for the provided details. Contact 1818 for clarification."));
            }

            if (!policyList.isEmpty() && !existingPolicies.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Partial success");
                result.put("newPolicies", policyList);
                result.put("existingPolicies", existingPolicies);
                result.put("existingCount", existingPolicies.size());
                return ResponseEntity.ok(result);
            }

            return ResponseEntity.ok(policyList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));

        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @PostMapping("/getGroupPolicyDetails")
    public ResponseEntity<?> getGroupPolicyDetails(@RequestParam("cid") String cid,
                                                   @RequestParam("orgCode") String orgCode) {

        Connection conn = null;
        PreparedStatement policyPst = null;
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

            conn = ConnectionManager.getOracleConnectionforims();

            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            String policyQuery =
                    "SELECT * FROM V_CLAIMS_GROUP_LI_POLICIES " +
                            "WHERE CID = ? AND ORG_CODE = ?";

            policyPst = conn.prepareStatement(policyQuery);

            policyPst.setString(1, cid.trim());
            policyPst.setString(2, orgCode.trim());

            policyRs = policyPst.executeQuery();

            JSONArray resultArray = new JSONArray();
            List<String> existingPolicies = new ArrayList<>();

            ResultSetMetaData metaData = policyRs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (policyRs.next()) {

                String policyNo = policyRs.getString("POLICY_NO");


                if (policyRepository.existsByPolicyNumber(policyNo)) {
                    existingPolicies.add(policyNo);
                    continue;
                }

                JSONObject obj = new JSONObject();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = policyRs.getObject(i);

                    obj.put(columnName, value);
                }

                resultArray.put(obj);
            }

            if (resultArray.length() == 0 && !existingPolicies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap(
                                "message",
                                "Claim has already been processed for the provided details. Contact 1818 for clarification."
                        ));
            }

            if (resultArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap(
                                "message",
                                "Claimable policies were not found for the provided details."
                        ));
            }
            return ResponseEntity.ok(resultArray.toString());

        } catch (SQLException e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap(
                            "error",
                            "Database error occurred"
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap(
                            "error",
                            "Server error occurred"
                    ));

        } finally {

            try {
                if (policyRs != null) policyRs.close();
                if (policyPst != null) policyPst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
