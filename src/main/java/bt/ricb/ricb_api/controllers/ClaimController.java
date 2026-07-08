package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.models.AgencyUserEntity;
import bt.ricb.ricb_api.models.ClaimEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.models.RliCollectionDateEntity;
import bt.ricb.ricb_api.repository.AgencyUserRepository;
import bt.ricb.ricb_api.repository.PolicyHolderRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
    @Autowired
    private PolicyHolderRepository policyHolderRepository;

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

            // ================= USER =================
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
                        "message", "No policies selected for rejection"
                ));
            }


            // ================= CLAIM DATA =================
            ClaimResponseDRO fullClaim = claimService.getFullClaimByCin(dto.getCin());

            ClaimDTO claim = fullClaim.getClaim();
            ClaimantDTO claimant = fullClaim.getClaimant();
            PolicyHolderDTO policyHolder = fullClaim.getPolicyHolder();


            List<PolicyDTO> policies = fullClaim.getPolicies()
                    .stream()
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



            // ================= SQL =================
            String insertQuery = """
        INSERT INTO ricb_li.tl_li_tr_claims_header
        (
          serial_no,claim_type,policy_no,policy_serial_no,claim_intm_date,
          claim_intm_by,claim_intm_relation,date_of_death,place_of_death,
          who_was_died,type_of_death,mode_of_intimation, claim_regn_no,
          claim_regn_date,status_code, prepared_by,prepared_on,prepared_time,
          branch_code,risk_commencement, cause_of_death,deceased_name,
          deceased_cid
        )
        VALUES
        (
          ?, ?, ?, ?,
          ?, ?, ?,?, ?, ?,
          ?, 'W', '',?, 'Z',
          'Web', ?, ?,
          ?, '',
          ?, ?, ?
        )
        """;


            insertStmt = conn.prepareStatement(insertQuery);


            List<Long> serialNumbers = new ArrayList<>();

            LocalDate todayDate = LocalDate.now();

            String time =
                    LocalTime.now()
                            .format(DateTimeFormatter.ofPattern("HHmmss"));



            // ================= LOOP =================
            for (PolicyDTO policy : policies) {


                // ================= SEQUENCE =================
                seqStmt = conn.prepareStatement(
                        "SELECT ricb_li.sq_li_tr_claims_header.nextval FROM dual"
                );

                rs = seqStmt.executeQuery();

                long serialNo = rs.next() ? rs.getLong(1) : 0;

                serialNumbers.add(serialNo);

                // ================= INSERT =================
                insertStmt.setLong(1, serialNo);
                insertStmt.setString(2, claim.getClaimType());
                insertStmt.setString(3, policy.getPolicyNumber());
                insertStmt.setObject(4, policy.getPolicySerialNumber());
                insertStmt.setObject(5, claim.getCreatedAt());
                insertStmt.setString(6, claimant.getFullName());
                insertStmt.setString(7, claimant.getRelation());
                insertStmt.setObject(8, claim.getDateOfDeath());
                insertStmt.setString(9, claim.getPlaceOfDeath());
                String whoWasDied =
                        ("DB".equalsIgnoreCase(claim.getClaimType()) ||
                                "DBR".equalsIgnoreCase(claim.getClaimType()))
                                ? "P"
                                : "A";
                insertStmt.setString(10, whoWasDied);
                insertStmt.setString(11, claim.getDeathType());
                insertStmt.setObject(12, todayDate);
                insertStmt.setObject(13, todayDate);
                insertStmt.setString(14, time);
                insertStmt.setString(15, claim.getNearestBranchId());
                insertStmt.setString(16, claim.getCauseOfDeath());
                insertStmt.setString(17, policy.getPolicyHolderName());

                // deceased CID
                String deceasedCid =
                        Optional.ofNullable(policyHolder)
                                .map(PolicyHolderDTO::getCid)
                                .orElse(null);
                insertStmt.setString(18, deceasedCid);
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
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {}

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
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

            // ================= USER =================
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
            PolicyHolderDTO policyHolder = fullClaim.getPolicyHolder();

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

            // ================= SQL =================
            String insertQuery = """
            INSERT INTO ricb_li.tl_li_tr_claims_header
            ( serial_no, claim_type, policy_no, policy_serial_no,
              claim_intm_date, claim_intm_by, claim_intm_relation,
              date_of_death, place_of_death, who_was_died,
              type_of_death, mode_of_intimation,
              claim_regn_no, claim_regn_date, status_code,
              prepared_by, prepared_on, prepared_time,
              branch_code, risk_commencement,
              cause_of_death, deceased_name, deceased_cid )
            VALUES
            ( ?, ?, ?, ?,
              ?, ?, ?,
              ?, ?, ?,
              ?, 'W', '',
              ?, 'A',
              'Web', ?, ?,
              ?, '',
              ?, ?, ? )
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

                insertStmt.setLong(1, serialNo);
                insertStmt.setString(2, claim.getClaimType());
                insertStmt.setString(3, policy.getPolicyNumber());
                insertStmt.setObject(4, policy.getPolicySerialNumber());
                insertStmt.setObject(5, claim.getCreatedAt());
                insertStmt.setString(6, claimant.getFullName());
                insertStmt.setString(7, claimant.getRelation());
                insertStmt.setObject(8, claim.getDateOfDeath());
                insertStmt.setString(9, claim.getPlaceOfDeath());
                String whoWasDied =
                        ("DB".equalsIgnoreCase(claim.getClaimType()) ||
                                "DBR".equalsIgnoreCase(claim.getClaimType()))
                                ? "P"
                                : "A";
                insertStmt.setString(10, whoWasDied);
                insertStmt.setString(11, claim.getDeathType());
                insertStmt.setObject(12, todayDate);
                insertStmt.setObject(13, todayDate);
                insertStmt.setString(14, time);
                insertStmt.setString(15, claim.getNearestBranchId());
                insertStmt.setString(16, claim.getCauseOfDeath());
                insertStmt.setString(17, policy.getPolicyHolderName());
                // ================= DECEASED CID (SAFE) =================
                String deceasedCid = Optional.ofNullable(policyHolder)
                        .map(PolicyHolderDTO::getCid)
                        .orElse(null);

                insertStmt.setString(18, deceasedCid);

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
                    "SELECT SERIAL_NO,POLICY_NO, POLICY_START_DATE, BRANCH_CODE, COLLECTION_DATE " +
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
                String SERIAL_NO = rs.getString("SERIAL_NO");
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
                policyObj.put("SERIAL_NO", SERIAL_NO);
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

                if (policyRepository.existsByCidAndPolicyNumber(cid, policyNo) > 0) {
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

            // ================= VALIDATION =================
            if (cid == null || cid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "cid parameter is required"));
            }

            if (orgCode == null || orgCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "orgCode parameter is required"));
            }

            // ================= DB CONNECTION =================
            conn = ConnectionManager.getOracleConnectionforims();

            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // ================= MAIN QUERY =================
            String policyQuery =
                    "SELECT * FROM V_CLAIMS_GROUP_LI_POLICIES " +
                            "WHERE CID = ? AND ORG_CODE = ?";

            policyPst = conn.prepareStatement(policyQuery);
            policyPst.setString(1, cid.trim());
            policyPst.setString(2, orgCode.trim());

            policyRs = policyPst.executeQuery();

            JSONArray resultArray = new JSONArray();
            List<String> existingCids = new ArrayList<>();

            ResultSetMetaData metaData = policyRs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // ================= LOOP RESULT =================
            while (policyRs.next()) {

                String policyCid = policyRs.getString("CID");

                // ================= CID CHECK USING JPA (FIXED) =================
                String policyNumber = policyRs.getString("POLICY_NO");

                long exists = policyRepository.existsByCidAndPolicyNumber(
                        policyCid,
                        policyNumber
                );

                if (exists > 0) {
                    existingCids.add(policyCid + "-" + policyNumber);
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

            // ================= RESPONSE HANDLING =================
            if (resultArray.length() == 0 && !existingCids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap(
                                "message",
                                "Claim has already been processed for the provided CID. Contact 1818 for clarification."
                        ));
            }

            if (resultArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap(
                                "message",
                                "Claimable policies were not found for the provided CID and orgCode."
                        ));
            }

            return ResponseEntity.ok(resultArray.toString());

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
                if (policyPst != null) policyPst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/rural-claim")
    public ResponseEntity<?> createRuralClaim(@RequestBody RuralClaimDTO dto) {

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement seqPs = null;
        ResultSet rs = null;

        try {

            // ================= VALIDATION =================
            if (dto.getCitizenId() == null || dto.getCitizenId().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Citizen ID required"
                ));
            }

            if (dto.getDateOfDeath() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Date of Death required"
                ));
            }

            if (dto.getBranchCode() == null || dto.getBranchCode().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILED",
                        "message", "Branch Code required"
                ));
            }

            // ================= SYSTEM VALUES =================
            String preparedBy = SecurityContextHolder.getContext().getAuthentication().getName();
            LocalDate preparedOn = LocalDate.now();

            String preparedTime = LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // ================= API CALL =================
            String url = "https://apps.ricb.bt/rliHouseholdDetails.php?cid=" + dto.getCitizenId();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            Map<String, Object> body = response.getBody();

            Map<String, Object> details =
                    (Map<String, Object>) body.get("eligibleMemberCountDetails");

            List<Map<String, Object>> list =
                    (List<Map<String, Object>>) details.get("eligibleMemberCountDetail");

            Map<String, Object> data = list.get(0);

            String householdNo = data.get("Household_number") != null
                    ? data.get("Household_number").toString()
                    : null;

            String prevHouseholdNo = data.get("Previous_HH_No") != null
                    ? data.get("Previous_HH_No").toString()
                    : null;

            java.sql.Date censusDate = null;

            if (data.get("Census_Transfer_Date") != null) {
                try {
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    LocalDateTime ldt =
                            LocalDateTime.parse(data.get("Census_Transfer_Date").toString(), formatter);

                    censusDate = java.sql.Date.valueOf(ldt.toLocalDate());
                } catch (Exception e) {
                    censusDate = null;
                }
            }

            // ================= DB =================
            conn = ConnectionManager.getOracleConnectionforims();
            conn.setAutoCommit(false);

            seqPs = conn.prepareStatement(
                    "SELECT ricb_li.sq_li_tr_rural_2026_b01.nextval FROM dual"
            );

            rs = seqPs.executeQuery();
            rs.next();
            long serialNo = rs.getLong(1);

            // ================= SQL =================
            String sql = """
            INSERT INTO ricb_li.TL_LI_TR_RURAL_2026_CLM
            (
                SERIAL_NO,
                CITIZEN_ID,
                HOUSEHOLD_NO,
                PREV_HH_NO,
                CENSUS_TRF_DATE,
                DATE_OF_DEATH,
                BRANCH_CODE,
                PREPARED_BY,
                PREPARED_ON,
                PREPARED_TIME,
                STATUS_CODE,
                REMARKS,
                POLICY_SERIAL_NO,
                POLICY_NO,
                DECEASED_NAME,
                CAUSE_OF_DEATH,
                DEATH_REPORTING_APPL_NO,
                DEATH_PLACE,
                DEATH_TYPE,
                CLAIM_INTM_DATE,
                CLAIM_INTM_BY,
                CLAIM_INTM_RELATION,
                MODE_OF_INTIMATION,
                CLAIM_AMOUNT,
                WHO_WAS_DIED
            )
            VALUES
            (
                ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?,
                ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                'W',
                30000,
                'A'
            )
        """;

            ps = conn.prepareStatement(sql);

            // ================= MAPPING =================
            ps.setLong(1, serialNo);
            ps.setString(2, dto.getCitizenId());
            ps.setString(3, householdNo);
            ps.setString(4, prevHouseholdNo);
            ps.setDate(5, censusDate);

            ps.setDate(6, java.sql.Date.valueOf(dto.getDateOfDeath()));
            ps.setString(7, dto.getBranchCode());

            // SYSTEM FIELDS
            ps.setString(8, preparedBy);
            ps.setDate(9, java.sql.Date.valueOf(preparedOn));
            ps.setString(10, preparedTime);

            ps.setString(11, dto.getStatusCode());
            ps.setString(12, "Web");

            ps.setLong(13, dto.getPolicySerialNo());
            ps.setString(14, dto.getPolicyNo());

            ps.setString(15, dto.getDeceasedName());
            ps.setString(16, dto.getCauseOfDeath());

            ps.setString(17, dto.getDeathReportingApplNo());
            ps.setString(18, dto.getDeathPlace());
            ps.setString(19, dto.getDeathType());

            if (dto.getClaimIntmDate() != null) {
                ps.setDate(20, java.sql.Date.valueOf(dto.getClaimIntmDate()));
            } else {
                ps.setNull(20, Types.DATE);
            }

            ps.setString(21, dto.getClaimIntmBy());
            ps.setString(22, dto.getClaimIntmRelation());

            // ================= EXECUTE =================
            ps.executeUpdate();
            conn.commit();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Rural claim inserted successfully",
                    "serialNo", serialNo
            ));

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {}

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "ERROR",
                            "message", e.getMessage()
                    ));

        } finally {

            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (seqPs != null) seqPs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}