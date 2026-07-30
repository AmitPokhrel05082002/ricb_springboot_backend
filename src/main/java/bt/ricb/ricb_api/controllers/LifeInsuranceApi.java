package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.config.ConnectionManager;
import bt.ricb.ricb_api.dao.LifeInsuranceDao;
import bt.ricb.ricb_api.models.DTOs.PolicyIssuedSmsRequest;
import bt.ricb.ricb_api.models.DTOs.UnderwritingEmailRequest;
import bt.ricb.ricb_api.models.FamilyDetailsDto;
import bt.ricb.ricb_api.models.LifeInsuranceMainDto;
import bt.ricb.ricb_api.models.NomineeDto;
import bt.ricb.ricb_api.models.PolicyCoverDto;
import bt.ricb.ricb_api.models.PolicyDiscountLoadDTO;
import bt.ricb.ricb_api.models.PolicyDto;
import bt.ricb.ricb_api.models.PolicyPremiumDto;
import bt.ricb.ricb_api.repository.UnderwritingOfficerRepository;
import bt.ricb.ricb_api.services.ApiService;
import bt.ricb.ricb_api.services.EmailService;
import bt.ricb.ricb_api.services.MinioService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import bt.ricb.ricb_api.models.UnderwritingOfficerEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin({ "*" })
@RequestMapping({ "life" })
@RestController
public class LifeInsuranceApi {
    @Autowired
    private final MinioService minioService;
    private final LifeInsuranceDao lifeInsuarance;
    private final RestTemplate restTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private UnderwritingOfficerRepository underwritingOfficerRepository;

    public LifeInsuranceApi(MinioService minioService,
                            LifeInsuranceDao lifeInsurance,
                            RestTemplate restTemplate) {
        this.minioService = minioService;
        this.lifeInsuarance = lifeInsurance;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/underwriting-review")
    public ResponseEntity<String> sendUnderwritingReviewEmail(
            @RequestBody UnderwritingEmailRequest request) {

        try {
            UnderwritingOfficerEntity officer =
                    underwritingOfficerRepository
                            .findByBranchCode(request.getBranchCode())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "No underwriter found for branch code: "
                                                    + request.getBranchCode()
                                    )
                            );


            String underwriterEmail = officer.getEmail();

            String underwriterSubject =
                    "Life Insurance Proposal - Further Underwriting Review Required";


            String underwriterBody =
                    "Dear Branch Manager" + ",\n\n" +
                            "The online proposal mentioned below requires further underwriting review " +
                            "and has not been accepted online due to medical requirements or exceeding " +
                            "the applicable threshold limit.\n\n" +

                            "Proposal No.: " + request.getProposalNo() + "\n" +
                            "Proposer Name: " + request.getProposerName() + "\n" +
                            "Proposal CID No.: " + request.getCidNo() + "\n" +
                            "Contact No.: " + request.getContactNo() + "\n" +
                            "Proposal Date: " + request.getProposalDate() + "\n" +
                            "Product: " + request.getProduct() + "\n" +
                            "Reason: " + request.getReason() + "\n\n" +

                            "Follow up with the client and arrange the required documents/medical " +
                            "examinations to proceed further.\n\n" +

                            "This is a system-generated notification. Please do not reply.\n\n";

            emailService.sendEmail(
                    underwriterEmail,
                    underwriterSubject,
                    underwriterBody,
                    null
            );

            if (request.getEmailAddress() != null &&
                    !request.getEmailAddress().isBlank()) {


                String customerSubject =
                        "Life Insurance Proposal - Further Assessment Required";


                String customerBody =
                        "Dear " + request.getProposerName() + ",\n\n" +
                                "Your online life insurance proposal requires further assessment " +
                                "before acceptance.\n\n" +
                                "Our official will contact you soon for further requirements.\n\n" +
                                "Thank you.\n\n";


                emailService.sendEmail(
                        request.getEmailAddress(),
                        customerSubject,
                        customerBody,
                        null
                );
            }


            return ResponseEntity.ok("Emails sent successfully.");


        } catch (MessagingException | IOException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send emails: " + e.getMessage());


        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/policy-issued-sms")
    public ResponseEntity<String> sendPolicyIssuedSms(
            @RequestBody PolicyIssuedSmsRequest request) {

        try {

            String mobile = request.getMobileNumber();

            if (mobile == null || mobile.isBlank()) {
                return ResponseEntity.badRequest().body("Mobile number is required.");
            }

            String message =
                    "Dear " + request.getName() +
                            ", your online life insurance proposal is accepted and issued with Policy No. " +
                            request.getPolicyNumber() +
                            ". Thanks for choosing RICBL.";

            if (mobile.startsWith("17")) {

                apiService.sendSms(message, mobile);

            } else if (mobile.startsWith("77")) {

                apiService.sendSmsTcell(message, mobile);

            } else {

                return ResponseEntity.badRequest()
                        .body("Unsupported mobile number.");
            }

            return ResponseEntity.ok("SMS sent successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send SMS: " + e.getMessage());
        }
    }

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
    public ResponseEntity<?> uploadMultipleLifeInsuranceDocuments(

            @RequestParam("files") MultipartFile[] files,
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

        List<Map<String, Object>> uploadedFiles = new ArrayList<>();

        try {

            conn = ConnectionManager.fileUploadLifeInsurance();

            for (MultipartFile file : files) {

                // ================= FILE VALIDATION =================
                String originalFileName = file.getOriginalFilename();

                if (originalFileName == null || !originalFileName.contains(".")) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Invalid file : " + originalFileName
                    ));
                }

                String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

                if (!extension.equals(".pdf") && !extension.equals(".docx")) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Only PDF and DOCX files are allowed."
                    ));
                }

                long maxSize = 5 * 1024 * 1024;

                if (file.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", originalFileName + " exceeds 5MB."
                    ));
                }

                // ================= GET SERIAL NO =================
                String seqQuery = "SELECT SEQ_T_DMS_METADATA_DETAILS.NEXTVAL AS srl_no FROM dual";

                ps = conn.prepareStatement(seqQuery);
                rs = ps.executeQuery();

                long serialNo = 0;

                if (rs.next()) {
                    serialNo = rs.getLong("srl_no");
                }

                rs.close();
                ps.close();

                // ================= UPLOAD TO MINIO =================
                String objectPath = minioService.uploadFile(file);

                // ================= GENERATED FILE NAME =================
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

                String generatedFileName =
                        policyNo + "_DOC_" +
                                LocalDateTime.now().format(formatter) +
                                extension;

                // ================= FILE SIZE =================
                double sizeMB = (double) file.getSize() / (1024 * 1024);

                BigDecimal fileSize =
                        new BigDecimal(sizeMB).setScale(2, RoundingMode.HALF_UP);

                String fileType = extension.replace(".", "");

                // ================= INSERT =================
                String insertQuery =
                        "INSERT INTO RICB_EIS.T_DMS_METADATA_DETAILS (" +
                                "SERIAL_NO, DEPT_CODE, DOC_TYPE, DOC_CAT_CODE, DOC_SUB_CAT_CODE," +
                                "BRANCH_CODE, VALIDITY_DATE, REMARKS," +
                                "MD_01, MD_02, MD_03, MD_04, MD_05, MD_06," +
                                "FILE_NAME_SYS, FILE_NAME_GEN, FILE_SIZE, FILE_TYPE," +
                                "VERSION_NO, FILE_PATH, UPLOAD_BY, UPLOAD_DATE," +
                                "UPLOAD_TIME, STATUS)" +
                                " VALUES (" +
                                "?, 'D003','C','1',?,'B001'," +
                                "TO_DATE(?,'YYYY-MM-DD')," +
                                "'Web'," +
                                "?,?,?,?,?,?,?," +
                                "?,?,?, '1', ?, " +
                                "'Web',SYSDATE,TO_CHAR(SYSDATE,'HH24:MI:SS'),'A')";

                ps = conn.prepareStatement(insertQuery);

                ps.setLong(1, serialNo);
                ps.setString(2, docSubCatCode);
                ps.setString(3, validityDate);

                // Metadata
                ps.setString(4, policyNo);
                ps.setString(5, policyStartDate);
                ps.setString(6, policyEndDate);
                ps.setString(7, custName);
                ps.setString(8, custCid);
                ps.setString(9, productCode);

                // File Details
                ps.setString(10, objectPath);
                ps.setString(11, generatedFileName);
                ps.setDouble(12, fileSize.doubleValue());
                ps.setString(13, fileType);
                ps.setString(14, objectPath);

                System.out.println(insertQuery);
                ps.executeUpdate();

                ps.close();

                Map<String, Object> result = new HashMap<>();
                result.put("serial_no", serialNo);
                result.put("file_name", generatedFileName);
                result.put("file_path", objectPath);

                uploadedFiles.add(result);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("total_uploaded", uploadedFiles.size());
            response.put("files", uploadedFiles);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    )
            );

        } finally {

            ConnectionManager.close(conn, rs, ps);

        }
    }

    @GetMapping("/lifeInsurance_cert_online")
    public ResponseEntity<String> getLifeInsuranceCertificate(
            @RequestParam("policyNo") String policyNo) {

        try {
            String url = "https://apps.ricb.bt/san/report/lifeInsurance_cert_online.php?polNo=" + policyNo;

            String response = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching certificate: " + e.getMessage());
        }
    }
}