package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.LifeInsuranceDao;
import bt.ricb.ricb_api.models.FamilyDetailsDto;
import bt.ricb.ricb_api.models.LifeInsuranceMainDto;
import bt.ricb.ricb_api.models.NomineeDto;
import bt.ricb.ricb_api.models.PolicyCoverDto;
import bt.ricb.ricb_api.models.PolicyDiscountLoadDTO;
import bt.ricb.ricb_api.models.PolicyDto;
import bt.ricb.ricb_api.models.PolicyPremiumDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin({ "*" })
@RequestMapping({ "life" })
@RestController
public class LifeInsuranceApi {
    @Autowired
    private LifeInsuranceDao lifeInsuarance;

    @PostMapping({"/insuranceMainDetails"})
    public ResponseEntity<String> lifeInsuranceMain(@RequestBody LifeInsuranceMainDto insuranceDetails) {
        try {
            this.lifeInsuarance.lifeInsuranceMain(insuranceDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertDiscLoadDetails"})
    public ResponseEntity<String> insertDiscLoadDetails(@RequestBody PolicyDiscountLoadDTO discLoadDetails) {
        try {
            this.lifeInsuarance.insertDiscLoadDetails(discLoadDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertCoverDetails"})
    public ResponseEntity<String> insertCoverDetails(@RequestBody PolicyCoverDto coverDetails) {
        try {
            this.lifeInsuarance.insertCoverDetails(coverDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertNomineeDetails"})
    public ResponseEntity<String> insertNomineeDetails(@RequestBody NomineeDto nomineeDetails) {
        try {
            this.lifeInsuarance.insertNomineeDetails(nomineeDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertFamilyDetails"})
    public ResponseEntity<String> insertFamilyDetails(@RequestBody FamilyDetailsDto familyDetails) {
        try {
            this.lifeInsuarance.insertFamilyDetails(familyDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertPremiumDetails"})
    public ResponseEntity<String> insertPremiumDetails(@RequestBody PolicyPremiumDto premiumDetails) {
        try {
            this.lifeInsuarance.insertPremiumDetails(premiumDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping({"/insertPolicyDetails"})
    public ResponseEntity<String> insertPolicyDetails(@RequestBody PolicyDto policyDetails) {
        try {
            this.lifeInsuarance.insertPolicyDetails(policyDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> fileUploadLifeInsurance(
            @RequestParam("file") MultipartFile file,
            @RequestParam("doc_sub_cat_code") String docSubCatCode,
            @RequestParam("validity_date") String validityDate,
            @RequestParam("policy_no") String policyNo,
            @RequestParam("policy_start_date") String policyStartDate,
            @RequestParam("policy_end_date") String policyEndDate,
            @RequestParam("cust_name") String custName,
            @RequestParam("cust_cid") String custCid,
            @RequestParam("product_code") String productCode
    ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // ================= FILE VALIDATION =================
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

            if (!extension.equals(".pdf") && !extension.equals(".docx")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Only PDF and DOCX files are allowed");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Check max file size (5 MB)
            long maxSize = 5 * 1024 * 1024; // 5 MB in bytes
            if (file.getSize() > maxSize) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "File size exceeds maximum limit of 5 MB");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // ================= DB CONNECTION =================
            conn = ConnectionManager.fileUploadLifeInsurance();

            // ================= SERIAL_NO =================
            String seqQuery = "SELECT SEQ_T_DMS_METADATA_DETAILS.nextval + 1 AS srl_no FROM dual";
            ps = conn.prepareStatement(seqQuery);
            rs = ps.executeQuery();

            long serialNo = 0;
            if (rs.next()) {
                serialNo = rs.getLong("srl_no");
            }

            rs.close();
            ps.close();

            // ================= SAVE FILE =================
            String uploadDir = "D:/ricbdoc/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String systemFileName = UUID.randomUUID().toString() + extension;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            String fileNameGen = policyNo + "_DOC_" + LocalDateTime.now().format(formatter) + extension;

            String filePath = uploadDir + systemFileName;
            file.transferTo(new File(filePath));

// Convert file size to MB (2 decimal places)
            double fileSizeMB = (double) file.getSize() / (1024 * 1024);
            BigDecimal fileSizeRounded = new BigDecimal(fileSizeMB).setScale(2, RoundingMode.HALF_UP);

            String fileType = extension.replace(".", "");

            // ================= INSERT QUERY =================
            String insertQuery = "INSERT INTO RICB_EIS.T_DMS_METADATA_DETAILS (" +
                    "SERIAL_NO, DEPT_CODE, DOC_TYPE, DOC_CAT_CODE, DOC_SUB_CAT_CODE, BRANCH_CODE, VALIDITY_DATE, " +
                    "REMARKS, MD_01, MD_02, MD_03, MD_04, MD_05, MD_06, " +
                    "FILE_NAME_SYS, FILE_NAME_GEN, FILE_SIZE, FILE_TYPE, VERSION_NO, FILE_PATH, " +
                    "UPLOAD_BY, UPLOAD_DATE, UPLOAD_TIME, STATUS) " +
                    "VALUES (?, 'D003', 'C', '1', ?, 'B001', TO_DATE(?, 'YYYY-MM-DD'), " +
                    "'Web', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '1', ?, " +
                    "'Web', SYSDATE, TO_CHAR(SYSDATE, 'HH24:MI:SS'), 'A')";

            ps = conn.prepareStatement(insertQuery);
            ps.setLong(1, serialNo);
            ps.setString(2, docSubCatCode);
            ps.setString(3, validityDate);

            // MD fields
            ps.setString(4, policyNo);
            ps.setString(5, policyStartDate);
            ps.setString(6, policyEndDate);
            ps.setString(7, custName);
            ps.setString(8, custCid);
            ps.setString(9, productCode);

            ps.setString(10, systemFileName);
            ps.setString(11, fileNameGen);
            ps.setDouble(12, fileSizeRounded.doubleValue());;
            ps.setString(13, fileType);
            ps.setString(14, filePath);

            ps.executeUpdate();

            // ================= RESPONSE =================
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "File uploaded & data inserted successfully");
            response.put("serial_no", serialNo);
            response.put("file_name_sys", systemFileName);
            response.put("file_name_gen", fileNameGen);
            response.put("file_size", fileSizeRounded.doubleValue());
            response.put("file_type", fileType);
            response.put("file_path", filePath);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } finally {
            ConnectionManager.close(conn, rs, ps);
        }
    }
}