package bt.ricb.ricb_api.controllers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bt.ricb.ricb_api.config.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

@CrossOrigin({ "*" })
@RestController
public class apiController {
	
	//Life Insurance
	@GetMapping("/viewLifeInsurance")
	public ResponseEntity<String> getPolicyDetailsAgainstCid(
	        @RequestParam("cid") String cid) {
	    System.out.print("HIIII");
	    JSONArray json = new JSONArray();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    try {
	        // Get database connection
	        conn = ConnectionManager.getOracleConnection();
	        
	        if (conn != null) {
	            // SQL query from your PHP code
	            String query = "SELECT distinct a.policy_no policy_no, b.customer_name name, b.citizen_id CUST_CID, " +
	                    "to_char(a.sum_assured, '9,999,999,999,999.99') sum_assured, " +
	                    "to_char(c.premium_per_instalment, '9,999,999,999,999.99') premium, " +
	                    "DECODE(a.status_code,'G','Active Policy','H','LAPSED', 'I', 'Lapsed Paid Up','J', 'Forfieted'," +
	                    "'K','Matured','L','Closed- Maturity','M','Death Claim Entered/Registered','MM','Surrender Claim Entered/'," +
	                    "'N','Closed- Death','P','Closed- Surrender','Q','Closed- Forfeiture','S','Closed- Claim Declined'," +
	                    "'U','Closed- PTD','V','Cancelled', 'X','Rejected') polstatus, " +
	                    "to_char(a.POLICY_START_DATE,'dd/mm/rrrr') incept_date, " +
	                    "to_char(a.POLICY_END_DATE,'dd/mm/rrrr') maturitydate, " +
	                    "(SELECT to_char(MIN(due_date),'dd/mm/rrrr') " +
	                    "FROM tl_li_tr_premium_list WHERE policy_serial_no = a.serial_no " +
	                    "AND status_code = 'PENDING' AND payment_date IS NULL GROUP BY policy_serial_no) nextrepay " +
	                    "from tl_li_tr_policy_header a, tl_in_mas_customer b, tl_li_tr_premium_list c " +
	                    "where a.customer_code = b.customer_code " +
	                    "and a.status_code in ('G','H','T','TT') " +
	                    "and a.serial_no = c.policy_serial_no " +
	                    "and a.policy_no IN (select policy_no from tl_li_tr_policy_header a, tl_in_mas_customer b " +
	                    "where a.customer_code=b.customer_code and b.CITIZEN_ID=?) " +
	                    "order by a.policy_no";
	            
	            // Prepare and execute query
	            pst = conn.prepareStatement(query);
	            pst.setString(1, cid);
	            rs = pst.executeQuery();
	            
	            // Convert result to JSON
	            json = new ToJSON().toJSONArray(rs);
	            
	            return ResponseEntity.ok().body(json.toString());
	        } else {
	            return ResponseEntity.status(500).body("Unable to establish database connection");
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Server was not able to process your request");
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	            if (pst != null) pst.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	
	//General Insurance
	@GetMapping("/getGiPolicyDetailsAgainstCid")
	public ResponseEntity<String> getGiPolicyDetailsAgainstCid(
	        @RequestParam("cid") String cid) {
	    
	    JSONArray json = new JSONArray();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    
	    try {
	        // Get database connection
	        conn = ConnectionManager.getLifeConnection();
	        
	        if (conn != null) {
	            // SQL query from your PHP code
	            String query = "SELECT a.serial_no pol_sys_id, b.citizen_id pol_civil_id, b.customer_name assuredname, " +
	                    "c.product_group_ri pol_dept_code, a.policy_no pol_no, " +
	                    "to_char(a.sum_assured, '9,999,999,999,999.99') pol_si_lc_3, " +
	                    "to_char(a.POLICY_START_DATE,'dd/mm/rrrr') pol_fm_dt, " +
	                    "to_char(a.POLICY_END_DATE,'dd/mm/rrrr') pol_to_dt, " +
	                    "to_char(a.premium_amount, '9,999,999,999,999.99') pol_prem_lc_1, " +
	                    "Decode(a.status_code,'G','Active','I','Expired','A','Proposed','H','Lapsed') status " +
	                    "from RICB_GI.TL_GI_TR_POLICY_HEADER a, " +
	                    "RICB_GI.TL_GI_MAS_CUSTOMER b, RICB_GI.TL_GI_MAS_PRODUCT c " +
	                    "where b.citizen_id = ? " +
	                    "and a.customer_code = b.CUSTOMER_CODE " +
	                    "and a.product_code = c.product_code";
	            
	            // Prepare and execute query
	            pst = conn.prepareStatement(query);
	            pst.setString(1, cid);
	            rs = pst.executeQuery();
	            
	            // Convert result to JSON
	            json = new ToJSON().toJSONArray(rs);
	            
	            return ResponseEntity.ok().body(json.toString());
	        } else {
	            return ResponseEntity.status(500).body("Unable to establish database connection");
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Server was not able to process your request");
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	            if (pst != null) pst.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	@GetMapping("/getPpfMemo")
	public ResponseEntity<Map<String, Object>> getPpfMemo(
	        @RequestParam("ID") String ppfNo,
	        @RequestParam("name") String name,
	        @RequestParam("joindt") String joinDate,
	        @RequestParam("deptnm") String departmentName,
	        @RequestParam("desig") String designation,
	        @RequestParam("uid") String uid) {
	    

	    Map<String, Object> response = new LinkedHashMap<>();
	    
	    try {
	        // 1. Get database connection
	        Connection conn = ConnectionManager.getOracleConnection();
	        
	        if (conn == null) {
	            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Database connection failed"));
	        }

	        // 2. Header information
	        Map<String, Object> header = new LinkedHashMap<>();
	        header.put("institution", "Royal Insurance Corporation of Bhutan Limited");
	        header.put("scheme", "PRIVATE PROVIDENT FUND SCHEME");
	        header.put("runDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
	        header.put("reportTitle", "Memo of Account for the period till: " + 
	            new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
	        
	        // 3. Employee details
	        Map<String, String> employeeInfo = new LinkedHashMap<>();
	        employeeInfo.put("name", name);
	        employeeInfo.put("ppfAccountNo", ppfNo);
	        employeeInfo.put("designation", designation);
	        employeeInfo.put("department", departmentName);
	        employeeInfo.put("joiningDate", joinDate);
	        
	        // 4. Get opening balance
	        Map<String, String> openingBalance = getPpfOpeningBalance(conn, ppfNo, uid);
	        
	        // 5. Get current year balance
	        Map<String, String> currentYearBalance = getPpfCurrentYearBalance(conn, ppfNo, uid);
	        
	        // 6. Calculate closing balance
	        Map<String, String> closingBalance = calculateClosingBalance(openingBalance, currentYearBalance);
	        
	        // 7. Get monthly contributions
	        List<Map<String, String>> monthlyContributions = getMonthlyContributions(conn, uid);
	        
	        // 8. Build response
	        response.put("header", header);
	        response.put("employeeInfo", employeeInfo);
	        response.put("openingBalance", openingBalance);
	        response.put("currentYearBalance", currentYearBalance);
	        response.put("closingBalance", closingBalance);
	        response.put("monthlyContributions", monthlyContributions);
	        response.put("currentYearTotal", currentYearBalance);
	        
	        return ResponseEntity.ok().body(response);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(Collections.singletonMap("error", "Server error processing request"));
	    }
	}

	// Helper methods
	private Map<String, String> getPpfOpeningBalance(Connection conn, String ppfNo, String uid) throws SQLException {
	    String query = "SELECT * FROM V_PPF_MEMO_OPENING_BAL WHERE PFNumber=? and citizenid=?";
	    try (PreparedStatement pst = conn.prepareStatement(query)) {
	        pst.setString(1, ppfNo);
	        pst.setString(2, uid);
	        ResultSet rs = pst.executeQuery();
	        
	        if (rs.next()) {
	            Map<String, String> result = new LinkedHashMap<>();
	            result.put("employeeContribution", rs.getString("empe_con"));
	            result.put("employerContribution", rs.getString("empr_con"));
	            result.put("employeeInterest", rs.getString("opeei"));
	            result.put("employerInterest", rs.getString("operi"));
	            return result;
	        }
	    }
	    return Collections.emptyMap();
	}

	private Map<String, String> getPpfCurrentYearBalance(Connection conn, String ppfNo, String uid) throws SQLException {
	    String query = "SELECT * FROM V_PPF_MEMO_C_YEAR_BAL WHERE PFNumber=? and citizenid=?";
	    try (PreparedStatement pst = conn.prepareStatement(query)) {
	        pst.setString(1, ppfNo);
	        pst.setString(2, uid);
	        ResultSet rs = pst.executeQuery();
	        
	        if (rs.next()) {
	            Map<String, String> result = new LinkedHashMap<>();
	            result.put("employeeContribution", rs.getString("eecontribution"));
	            result.put("employerContribution", rs.getString("ercontribution"));
	            result.put("employeeInterest", rs.getString("ee_int"));
	            result.put("employerInterest", rs.getString("er_int"));
	            return result;
	        }
	    }
	    return Collections.emptyMap();
	}

	private Map<String, String> calculateClosingBalance(Map<String, String> opening, Map<String, String> currentYear) {
	    Map<String, String> closing = new LinkedHashMap<>();
	    
	    try {
	        BigDecimal empCont = new BigDecimal(opening.get("employeeContribution"))
	                          .add(new BigDecimal(currentYear.get("employeeContribution")));
	        BigDecimal empCont1 = new BigDecimal(opening.get("employerContribution"))
	                          .add(new BigDecimal(currentYear.get("employerContribution")));
	        BigDecimal empInt = new BigDecimal(opening.get("employeeInterest"))
	                          .add(new BigDecimal(currentYear.get("employeeInterest")));
	        BigDecimal empInt2 = new BigDecimal(opening.get("employerInterest"))
	                          .add(new BigDecimal(currentYear.get("employerInterest")));
	        
	        closing.put("employeeContribution", empCont.toString());
	        closing.put("employerContribution", empCont1.toString());
	        closing.put("employeeInterest", empInt.toString());
	        closing.put("employerInterest", empInt2.toString());
	        
	    } catch (Exception e) {
	        // Handle number format exceptions
	    }
	    
	    return closing;
	}

	private List<Map<String, String>> getMonthlyContributions(Connection conn, String uid) throws SQLException {
	    List<Map<String, String>> contributions = new ArrayList<>();
	    
	    String query = "SELECT a.code PFNumber, b.designation desig, b.citizenid, e.journaldate, " +
	            "to_char(e.fromdate,'Month, YYYY') due_month, amountmask(d.ercontribution) er_contr, " +
	            "amountmask(d.eecontribution) ee_contr, e.rtype, " +
	            "(SELECT amountmask(PPFINTEREST) FROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.ercontribution,e.rtype,TO_DATE(sysdate)))) er_intr, " +
	            "(SELECT amountmask(PPFINTEREST) FROM TABLE(PPF_GETPPFINTEREST(e.journaldate,d.eecontribution,e.rtype,TO_DATE(sysdate)))) ee_intr " +
	            "from sale_customer a " +
	            "join sale_investoreeorganisatndata b on a.id=b.customerid " +
	            "join ppf_contributionflow d on d.ppfeeuid=a.code " +
	            "join ppf_contributionheader e on e.id=d.contributionheaderid " +
	            "where b.citizenid =? and b.isactiveee=1 and to_char(e.journaldate, 'YYYY') =to_char(sysdate, 'YYYY') " +
	            "order by e.journaldate";
	    
	    try (PreparedStatement pst = conn.prepareStatement(query)) {
	        pst.setString(1, uid);
	        ResultSet rs = pst.executeQuery();
	        
	        while (rs.next()) {
	            Map<String, String> monthly = new LinkedHashMap<>();
	            monthly.put("dueMonth", rs.getString("due_month"));
	            monthly.put("employeeContribution", rs.getString("ee_contr"));
	            monthly.put("employerContribution", rs.getString("er_contr"));
	            monthly.put("employeeInterest", rs.getString("ee_intr"));
	            monthly.put("employerInterest", rs.getString("er_intr"));
	            contributions.add(monthly);
	        }
	    }
	    
	    return contributions;
	}
	
	//ppf account
	@GetMapping("/getPpfAccountDetails")
	public ResponseEntity<?> getPpfAccountDetails(
	        @RequestParam("cid") String citizenId) {

	    JSONArray jsonResponse = new JSONArray();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        // Validate input
	        if (citizenId == null || citizenId.trim().isEmpty()) {
	            return ResponseEntity.badRequest()
	                    .body(Collections.singletonMap("error", "CID parameter is required"));
	        }

	        // Get database connection
	        conn = ConnectionManager.getOracleConnection();
	        
	        if (conn == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Collections.singletonMap("error", "Database connection failed"));
	        }

	        // SQL query from PHP code
	        String query = "SELECT INITCAP(b.designation) desig, INITCAP(a.registeredname) r_name, " +
	                      "decode(b.isactiveee, 1, 'Active') status, a.code ppf_no, " +
	                      "to_char(b.ppfjoiningdate,'dd/mm/rrrr') ppf_joindt, b.citizenid, " +
	                      "INITCAP(c.description || c.name) dept_name " +
	                      "FROM sale_customer a " +
	                      "JOIN sale_investoreeorganisatndata b ON a.id=b.customerid " +
	                      "JOIN sale_investorerlocation c ON b.investorerlocationid=c.id " +
	                      "WHERE b.citizenid =? AND b.isactiveee=1 " +
	                      "ORDER BY a.registeredname, a.code, b.ppfjoiningdate, b.citizenid";

	        // Prepare and execute query
	        pst = conn.prepareStatement(query);
	        pst.setString(1, citizenId);
	        rs = pst.executeQuery();

	        // Convert result to JSON
	        ToJSON converter = new ToJSON();
	        jsonResponse = converter.toJSONArray(rs);

	        if (jsonResponse.length() == 0) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("error", "No PPF account found for the given CID"));
	        }

	        return ResponseEntity.ok().body(jsonResponse.toString());

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Database error occurred"));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Server error occurred"));
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	            if (pst != null) pst.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	//Group Insurance
	@GetMapping("/getGroupInsuranceDetails")
	public ResponseEntity<Map<String, Object>> getGroupInsuranceDetails(
	        @RequestParam("cid") String citizenId) {

	    Map<String, Object> response = new LinkedHashMap<>();
	    
	    try {
	        // Validate input
	        if (citizenId == null || citizenId.trim().isEmpty()) {
	            return ResponseEntity.badRequest()
	                    .body(Collections.singletonMap("error", "CID parameter is required"));
	        }

	        // Get database connection
	        Connection conn = ConnectionManager.getOracleConnection();
	        
	        if (conn == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Collections.singletonMap("error", "Database connection failed"));
	        }

	        // 1. Get basic policy information
	        String policyQuery = "SELECT SC.CODE gisno, " +
	                "dpt.name||' - '||pol.name||' ('||pol.policyno||' )' BRANCHDEPARTMENT, " +
	                "sc.registeredname membername, ieog.citizenid CITIZENID, " +
	                "ieog.designation desig, to_char(ieog.GISJOININGDATE,'dd/mm/rrrr') joindate, " +
	                "decode(ieog.groupid, '4','200,000', '3', '300,000', '2', '400,000', '1', '500,000') sumassured, " +
	                "decode(ieog.isactiveee, '1', 'Active', '0', 'Inactive') status " +
	                "FROM gis_sale_customer SC " +
	                "INNER JOIN gis_investoreeorganisatndata ieog on SC.id=ieog.customerid " +
	                "INNER JOIN gis_investorerlocation dpt on ieog.departmentid=dpt.id " +
	                "INNER JOIN gis_policymaster pol on pol.id=ieog.policyid " +
	                "WHERE ieog.citizenid=?";

	        List<Map<String, String>> policies = new ArrayList<>();
	        try (PreparedStatement pst = conn.prepareStatement(policyQuery)) {
	            pst.setString(1, citizenId);
	            ResultSet rs = pst.executeQuery();
	            
	            while (rs.next()) {
	                Map<String, String> policy = new LinkedHashMap<>();
	                policy.put("gisno", rs.getString("gisno"));
	                policy.put("branchDepartment", rs.getString("BRANCHDEPARTMENT"));
	                policy.put("memberName", rs.getString("membername"));
	                policy.put("citizenId", rs.getString("CITIZENID"));
	                policy.put("designation", rs.getString("desig"));
	                policy.put("joinDate", rs.getString("joindate"));
	                policy.put("sumAssured", rs.getString("sumassured"));
	                policy.put("status", rs.getString("status"));
	                policies.add(policy);
	            }
	        }

	        if (policies.isEmpty()) {
	            response.put("message", "No Group Insurance holding found for the given CID");
	            return ResponseEntity.ok().body(response);
	        }

	        // 2. Get opening balance - Fixed syntax error in original query
	        String openingBalanceQuery = "SELECT nvl(YTD.CLCONTRIBUTION,0) opcontri, " +
	                "nvl(YTD.CLSAVING,0) opsaving, " +
	                "nvl(YTD.CLINTEREST,0) opinterest " +
	                "FROM GIS_YEARTRANSACTIONDETAILS YTD " +
	                "WHERE ppfeeuid=(SELECT code FROM gis_sale_customer GSC WHERE id=" +
	                "(SELECT customerid FROM gis_investoreeorganisatndata WHERE citizenid=? GROUP BY customerid)) " +
	                "AND YTD.year= to_char(ADD_MONTHS(sysdate,-12),'YYYY')";

	        Map<String, BigDecimal> openingBalance = new LinkedHashMap<>();
	        try (PreparedStatement pst = conn.prepareStatement(openingBalanceQuery)) {
	            pst.setString(1, citizenId);
	            ResultSet rs = pst.executeQuery();
	            
	            if (rs.next()) {
	                openingBalance.put("contribution", rs.getBigDecimal("opcontri"));
	                openingBalance.put("saving", rs.getBigDecimal("opsaving"));
	                openingBalance.put("interest", rs.getBigDecimal("opinterest"));
	                openingBalance.put("total", rs.getBigDecimal("opsaving").add(rs.getBigDecimal("opinterest")));
	            }
	        }

	        // 3. Get current year contributions - Fixed syntax errors in original query
	        String contributionsQuery = "SELECT to_char(L_AP.referencedate,'dd/mm/rrrr') recdate, " +
	                "CH.journalreference recno, " +
	                "CASE WHEN LAST_DAY(L_AP.FromDate)=LAST_DAY(L_AP.ToDate) THEN " +
	                "to_char(L_AP.ToDate, 'MON''YY') " +
	                "ELSE to_char(L_AP.FromDate, 'MON''YY') ||'-'||to_char(L_AP.ToDate, 'MON''YY') END recmonth, " +
	                "l_ap.contribution monthlycontri, " +
	                "decode(nvl(L_AP.Is_Excess,0),1,l_ap.contribution, L_AP.saving) preamount, " +
	                "(SELECT Gisinterest FROM TABLE(Gis_Getgisinterest(L_Ap.Referencedate, " +
	                "decode(nvl(L_AP.Is_Excess,0),1,0, L_AP.saving), " +
	                "L_AP.TYPEID, CH.rtype, NVL2(SC.REFUNDDATE, SC.REFUNDDATE-1, sysdate)))) interest " +
	                "FROM gis_sale_customer SC " +
	                "INNER JOIN gis_investoreeorganisatndata ieog ON SC.id=ieog.customerid " +
	                "INNER JOIN gis_investorerlocation dpt ON ieog.departmentid=dpt.id " +
	                "INNER JOIN gis_policymaster pol ON pol.id=ieog.policyid " +
	                "INNER JOIN gis_group gg ON gg.id=ieog.groupid " +
	                "LEFT OUTER JOIN common_lov cl ON cast(SC.refundtype as varchar2(10))=cl.value AND cl.companyid=10 AND cl.name ='GISRefundERRequests' " +
	                "LEFT OUTER JOIN GIS_YEARTRANSACTIONDETAILS YTD ON SC.CODE=ytd.ppfeeuid AND to_char(YTD.year)=to_char(sysdate,'YYYY')-1 " +
	                "LEFT OUTER JOIN Gis_Contributionflow L_Ap ON Sc.Code=L_Ap.Ppfeeuid " +
	                "AND L_AP.referencedate BETWEEN TRUNC(sysdate,'YEAR') AND sysdate " +
	                "AND L_AP.REFERENCENUMBER IS NOT NULL " +
	                "LEFT OUTER JOIN GIS_CONTRIBUTIONHEADER CH ON L_AP.CONTRIBUTIONHEADERID=CH.ID " +
	                "WHERE ieog.joiningdate=(SELECT max(joiningdate) FROM gis_investoreeorganisatndata WHERE customerid=SC.id) " +
	                "AND SC.CODE =(SELECT code FROM gis_sale_customer GSC WHERE id IN " +
	                "(SELECT customerid FROM gis_investoreeorganisatndata WHERE citizenid=?))";

	        List<Map<String, Object>> contributions = new ArrayList<>();
	        BigDecimal totalContribution = BigDecimal.ZERO;
	        BigDecimal totalSaving = BigDecimal.ZERO;
	        BigDecimal totalInterest = BigDecimal.ZERO;
	        
	        try (PreparedStatement pst = conn.prepareStatement(contributionsQuery)) {
	            pst.setString(1, citizenId);
	            ResultSet rs = pst.executeQuery();
	            
	            int count = 1;
	            while (rs.next()) {
	                Map<String, Object> contribution = new LinkedHashMap<>();
	                contribution.put("slNo", count++);
	                contribution.put("forMonth", rs.getString("recmonth"));
	                contribution.put("receiptDate", rs.getString("recdate"));
	                contribution.put("receiptNo", rs.getString("recno"));
	                
	                BigDecimal monthlyContri = rs.getBigDecimal("monthlycontri");
	                BigDecimal preAmount = rs.getBigDecimal("preamount");
	                BigDecimal interest = rs.getBigDecimal("interest");
	                
	                contribution.put("monthlyContribution", monthlyContri);
	                contribution.put("saving", preAmount);
	                contribution.put("interest", interest);
	                
	                totalContribution = totalContribution.add(monthlyContri);
	                totalSaving = totalSaving.add(preAmount);
	                totalInterest = totalInterest.add(interest);
	                
	                contributions.add(contribution);
	            }
	        }

	        // Build response
	        response.put("policies", policies);
	        response.put("openingBalance", openingBalance);
	        response.put("currentYearContributions", contributions);
	        
	        Map<String, BigDecimal> totals = new LinkedHashMap<>();
	        totals.put("totalContribution", totalContribution);
	        totals.put("totalSaving", totalSaving);
	        totals.put("totalInterest", totalInterest);
	        response.put("totals", totals);

	        return ResponseEntity.ok().body(response);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Database error occurred: " + e.getMessage()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Server error occurred"));
	    }
	}
	
	//Annuity
	@GetMapping("/getLifeAnnuityPolicies")
	public ResponseEntity<?> getLifeAnnuityPolicies(
	        @RequestParam("cid") String citizenId) {

	    JSONArray jsonResponse = new JSONArray();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    try {
	        // Validate input
	        if (citizenId == null || citizenId.trim().isEmpty()) {
	            return ResponseEntity.badRequest()
	                    .body(Collections.singletonMap("error", "CID parameter is required"));
	        }

	        // Get database connection
	        conn = ConnectionManager.getOracleConnection();
	        
	        if (conn == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Collections.singletonMap("error", "Database connection failed"));
	        }

	        // SQL query from PHP code with improved formatting
	        String query = "SELECT B.Custname, b.cityzenshipid, " +
	                "amountmask(b.SUMASSURED) SUMASSURED, b.policyno ploicy_no, " +
	                "b.planname, b.annuitytype, amountmask(b.Effectivepremiumamount) PREMIUMAMOUNT, " +
	                "B.premiumtype, B.Agent, to_char(b.POLICYDATE,'dd/mm/rrrr') ploicy_date, " +
	                "to_char(a.lastpremiumdate,'dd/mm/rrrr') close_date, " +
	                "CASE " +
	                "  WHEN to_char(A.lastpremiumdate,'DD') = to_char(b.policydate,'DD') AND A.lastpremiumdate IS NOT NULL THEN add_months(A.lastpremiumdate, 1) " +
	                "  WHEN to_char(A.lastpremiumdate,'DD') <> to_char(b.policydate,'DD') AND " +
	                "    ((to_char(add_months(A.lastpremiumdate, 1),'MON') <> 'FEB') OR " +
	                "    (to_char(add_months(A.lastpremiumdate, 1),'MON') = 'FEB' AND to_char(b.policydate,'DD') <= '28')) " +
	                "    AND A.lastpremiumdate IS NOT NULL THEN " +
	                "    to_date(to_char(b.policydate,'DD')||'-'||to_char(add_months(A.lastpremiumdate, 1),'MON')||'-'||" +
	                "    to_char(add_months(A.lastpremiumdate, 1),'YYYY'),'dd-mon-yyyy') " +
	                "  WHEN to_char(A.lastpremiumdate,'DD') <> to_char(b.policydate,'DD') AND " +
	                "    to_char(add_months(A.lastpremiumdate, 1),'MON') = 'FEB' AND " +
	                "    to_char(b.policydate,'DD') > '28' AND A.lastpremiumdate IS NOT NULL THEN " +
	                "    to_date('28'||'-'||to_char(add_months(A.lastpremiumdate, 1),'MON')||'-'||" +
	                "    to_char(add_months(A.lastpremiumdate, 1),'YYYY'),'dd-mon-yyyy') " +
	                "  WHEN A.lastpremiumdate IS NULL THEN b.policydate " +
	                "END next_due_months, " +
	                "trunc(months_between(sysdate, add_months(A.lastpremiumdate, B.noofmonths)),0)+1 deu_months, " +
	                "b.effectivepremiumamount, " +
	                "(trunc(months_between(sysdate, add_months(A.lastpremiumdate, B.noofmonths)),0)+1)*b.effectivepremiumamount as total_payable " +
	                "FROM ( " +
	                "  SELECT DISTINCT pm.memberid, " +
	                "    (lmmst.Title || ' ' || lmmst.name) CUSTNAME, " +
	                "    lmmst.currentaddress CURRADDRESS, " +
	                "    lmmst.cityzenshipid, lmmst.customerno, pm.sumassured, " +
	                "    pm.policyno, pm.policydate, plm.planname, am.annuitytype, " +
	                "    NVL(prmst.noofmonths,0) noofmonths, prmst.premiumtype, " +
	                "    NVL(prmst.penaltydays,0) penaltydays, va.vestingage, " +
	                "    NVL(l_md.organizationcode,'Direct') Agent, " +
	                "    (pm.premiumamount+pm.insuraranceamount) ANNUITYAMOUNT, " +
	                "    pm.effectivepremiumamount " +
	                "  FROM lifeannuity_planformember pm " +
	                "  INNER JOIN lifeannuity_membermaster lmmst ON pm.memberid=lmmst.id " +
	                "  INNER JOIN lifeannuity_planmaster plm ON pm.memberplanid=plm.id " +
	                "  INNER JOIN lifeannuity_annuitytypemaster am ON pm.memberannuitytypeid=am.id " +
	                "  LEFT OUTER JOIN lifeannuity_vestingage va ON pm.membervestingageid=va.id " +
	                "  LEFT OUTER JOIN lifeannuity_premiumtypemaster prmst ON pm.memberpremiumtypeid=prmst.id " +
	                "  LEFT OUTER JOIN lifeannuity_memberdetails l_md ON pm.id=l_md.policyid " +
	                "  WHERE pm.isactive=1 " +
	                "    AND pm.policydate<=sysdate " +
	                "    AND pm.status='Active' " +
	                "    AND pm.memberannuitytypeid=2 " +
	                ") B " +
	                "LEFT OUTER JOIN ( " +
	                "  SELECT MAX(PremiumDate) lastpremiumdate, policyno " +
	                "  FROM la_annuitypremium " +
	                "  GROUP BY policyno " +
	                ") A ON B.policyno=A.policyno " +
	                "WHERE B.penaltydays <> 0 AND b.cityzenshipid=? " +
	                "ORDER BY B.memberid";

	        // Prepare and execute query
	        pst = conn.prepareStatement(query);
	        pst.setString(1, citizenId);
	        rs = pst.executeQuery();

	        // Convert result to JSON
	        ToJSON converter = new ToJSON();
	        jsonResponse = converter.toJSONArray(rs);

	        if (jsonResponse.length() == 0) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("message", "No life annuity policies found for the given CID"));
	        }

	        return ResponseEntity.ok().body(jsonResponse.toString());

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Database error occurred: " + e.getMessage()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Server error occurred"));
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	            if (pst != null) pst.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	@GetMapping("/getLapsedPolicyDetails")
	public ResponseEntity<?> getPolicyDetails(@RequestParam("cid") String citizenId) {
	    JSONArray jsonResponse = new JSONArray();
	    Connection conn = null;
	    PreparedStatement pst1 = null;
	    PreparedStatement pst2 = null;
	    ResultSet rs1 = null;
	    ResultSet rs2 = null;

	    try {
	        if (citizenId == null || citizenId.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "CID parameter is required"));
	        }

	        conn = ConnectionManager.getLifeConnection();
	        if (conn == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Collections.singletonMap("error", "Database connection failed"));
	        }

	        // Step 1: Fetch agent_code from citizen_id
	        String getAgentQuery = "SELECT agent_code FROM tl_in_mas_agent WHERE citizen_id = ?";
	        pst1 = conn.prepareStatement(getAgentQuery);
	        pst1.setString(1, citizenId);
	        rs1 = pst1.executeQuery();

	        String agentCode = null;
	        if (rs1.next()) {
	            agentCode = rs1.getString("agent_code");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("error", "Agent not found for given CID"));
	        }

	        // Step 2: Run the main complex query with proper placeholder
	        String query =
	        	    "SELECT a.serial_no srl, a.policy_no polno, a.underwriting_year, " +
	        	    "TO_CHAR(a.sum_assured, '9,999,999,999,999.99') sumassured, " +
	        	    "a.sum_assured total, a.agent_code agcode, " +
	        	    "TO_CHAR(a.policy_start_date, 'DD/MM/RRRR') as stdate, " +
	        	    "TO_CHAR(a.policy_end_date, 'DD/MM/RRRR') as exptdt, " +
	        	    "DECODE(a.mode_of_payment, 'M', 'MONTHLY', 'S', 'SSS', 'Q', 'QUARTERLY', " +
	        	    "'H', 'HALF-YEARLY', 'Y', 'YEARLY', 'O', 'SINGLE') as mop, " +
	        	    "DECODE(a.status_code, 'G', 'Active Policy', 'H', 'LAPSED', 'I', 'Lapsed Paid Up', " +
	        	    "'J', 'Forfieted', 'K', 'Matured', 'L', 'Closed- Maturity', 'M', 'Death Claim Entered/Registered', " +
	        	    "'MM', 'Surrender Claim Entered/', 'N', 'Closed- Death', 'P', 'Closed- Surrender', " +
	        	    "'Q', 'Closed- Forfeiture', 'S', 'Closed- Claim Declined', 'U', 'Closed- PTD', " +
	        	    "'V', 'Cancelled', 'X', 'Rejected') status, " +
	        	    "b.customer_name name, b.con_mobile_no1 cust_mobile_no, " +
	        	    "c.premium_per_instalment premium, " +
	        	    "TO_CHAR(MAX(aa.due_date), 'DD/MM/YYYY') last, " +
	        	    "d.branch_name branch " +
	        	    "FROM RICB_LI.tl_li_tr_policy_header a, " +
	        	    "tl_in_mas_customer b, " +
	        	    "RICB_LI.tl_li_tr_premium_list c, " +
	        	    "(SELECT * FROM RICB_LI.tl_li_tr_premium_list WHERE status_code = 'PAID') aa, " +
	        	    "tl_in_mas_branch d " +
	        	    "WHERE a.customer_code = b.customer_code " +
	        	    "AND aa.policy_serial_no = a.serial_no " +
	        	    "AND a.serial_no = c.policy_serial_no " +
	        	    "AND a.agent_code = ? " +
	        	    "AND c.status_code = 'PENDING' " +
	        	    "AND a.status_code = 'H' " +
	        	    "AND d.branch_code = a.branch_code " +
	        	    "GROUP BY a.policy_no, a.underwriting_year, a.sum_assured, a.policy_start_date, " +
	        	    "a.policy_end_date, a.mode_of_payment, a.status_code, b.customer_name, " +
	        	    "b.con_mobile_no1, c.premium_per_instalment, a.agent_code, d.branch_name, " +
	        	    "a.serial_no " +
	        	    "ORDER BY a.policy_start_date";

	        pst2 = conn.prepareStatement(query);
	        pst2.setString(1, agentCode);
	        rs2 = pst2.executeQuery();

	        ToJSON converter = new ToJSON();
	        jsonResponse = converter.toJSONArray(rs2);

	        if (jsonResponse.length() == 0) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("error", "No policy details found for the given agent"));
	        }

	        return ResponseEntity.ok(jsonResponse.toString());

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
	            if (rs2 != null) rs2.close();
	            if (rs1 != null) rs1.close();
	            if (pst2 != null) pst2.close();
	            if (pst1 != null) pst1.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	

    @GetMapping("/getLapsedAnnuityDetails")
    public ResponseEntity<?> getLapsedAnnuityDetails(@RequestParam("cid") String citizenId) {
        JSONArray jsonResponse = new JSONArray();
        Connection conn = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        try {
            if (citizenId == null || citizenId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "CID parameter is required"));
            }

            conn = ConnectionManager.getAnnuityLapsedConnection(); // Assuming you have this connection
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // Step 1: Get agent code from citizen_id
//            String getAgentQuery = "SELECT agentcode FROM ricb_fas.lifeannuity_agent WHERE citizenid = ?";
//            pst1 = conn.prepareStatement(getAgentQuery);
//            pst1.setString(1, citizenId);
//            rs1 = pst1.executeQuery();
//
//            String agentCode = null;
//            if (rs1.next()) {
//            	agentCode = rs1.getString("agentcode");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Collections.singletonMap("error", "Agent not found for given CID"));
//            }

            // Step 2: Main complex query for lapsed annuity details
            String query = """
                SELECT
    b.policyno polno
    ,B.Custname
    ,B.curraddress currentaddress
    ,b.planname pname
    ,b.annuitytype annutype
    ,b.phoneno phoneno
    ,B.premiumtype prmtype
    ,b.effectivepremiumamount effprmamt
    ,to_char(A.lastpremiumdate, 'dd/mm/yyyy') custpredate
    ,trunc( months_between( sysdate 
    , (add_months(A.lastpremiumdate, B.noofmonths))   ),0)+1 duemonths  
    from
    (SELECT distinct pm.memberid,
(lmmst.Title || ' ' || lmmst.name) AS CUSTNAME,
        lmmst.currentaddress CURRADDRESS,
        pm.policyno,
        pm.policydate,
        plm.planname, 
        am.annuitytype,
        nvl(prmst.noofmonths,0) noofmonths,
        prmst.premiumtype,
        nvl(prmst.penaltydays,0) penaltydays,
        va.vestingage
        ,lmmst.phoneno
        ,pm.effectivepremiumamount
        
        from ricb_fas.lifeannuity_planformember pm
        inner join ricb_fas.lifeannuity_membermaster lmmst
        on pm.memberid=lmmst.id
        inner join ricb_fas.lifeannuity_planmaster plm
        on pm.memberplanid=plm.id
        inner join ricb_fas.lifeannuity_annuitytypemaster am
        on pm.memberannuitytypeid=am.id
        left outer join ricb_fas.lifeannuity_vestingage va
        on pm.membervestingageid=va.id
        left outer join ricb_fas.lifeannuity_premiumtypemaster prmst
        on pm.memberpremiumtypeid=prmst.id
        left outer join ricb_fas.lifeannuity_memberdetails l_md
        on pm.id=l_md.policyid
        where pm.isactive=1  
        and pm.policydate<=sysdate
        and  pm.status='Active'
        and  pm.memberannuitytypeid=2
        and l_md.organizationcode=(SELECT agentcode from ricb_fas.lifeannuity_agent where citizenid=?)
    )B
    left outer join
    (
        SELECT MAX(PremiumDate) lastpremiumdate,policyno from ricb_fas.la_annuitypremium group by policyno
    )A
    on B.policyno=A.policyno
    where add_months(A.lastpremiumdate, B.noofmonths)+B.penaltydays <sysdate
    and B.penaltydays <> 0
    order by B.memberid
            """;

            pst2 = conn.prepareStatement(query);
            pst2.setString(1, citizenId);
            rs2 = pst2.executeQuery();

            ToJSON converter = new ToJSON();
            jsonResponse = converter.toJSONArray(rs2);

            if (jsonResponse.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No lapsed annuity details found for the given agent"));
            }

            return ResponseEntity.ok(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error occurred: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error occurred: " + e.getMessage()));
        } finally {
            try {
                if (rs2 != null) rs2.close();
                if (rs1 != null) rs1.close();
                if (pst2 != null) pst2.close();
                if (pst1 != null) pst1.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @GetMapping("/getLifeAnnuityDetails")
    public ResponseEntity<?> getLifeAnnuityDetails(@RequestParam("cid") String citizenId) {
        JSONArray jsonResponse = new JSONArray();
        Connection conn = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        try {
            if (citizenId == null || citizenId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "CID parameter is required"));
            }

            conn = ConnectionManager.getAnnuityLapsedConnection();
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // Step 1: Fetch agent_code from citizen_id
//            String getAgentQuery = "SELECT agent_code FROM tl_in_mas_agent WHERE citizen_id = ?";
//            pst1 = conn.prepareStatement(getAgentQuery);
//            pst1.setString(1, citizenId);
//            rs1 = pst1.executeQuery();

//            String agentCode = null;
//            if (rs1.next()) {
//                agentCode = rs1.getString("agent_code");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Collections.singletonMap("error", "Agent not found for given CID"));
//            }

            // Step 2: Run the main complex query for Life Annuity details
            String query = """
                SELECT 
                A.POLICYNO ploicy_no
            ,to_char(A.POLICYDATE,'dd/mm/rrrr') ploicy_date
            ,to_char(A.PREMIUMCLOSINGDATE,'dd/mm/rrrr') close_date
            ,amountmask(A.SUMASSURED) assured                        
            ,A.Effectivepremiumamount totpremium
            ,amountmask(A.Effectivepremiumamount) premium
            ,amountmask(A.INSURARANCEAMOUNT) insuranceamt
            ,A.INSURARANCEAMOUNT totinsurance
            ,Decode(A.ISACTIVE,1,'Active','Not Active') status
            ,a.annuityamount annuityamt
            ,B.CURRENTADDRESS
            ,B.name name
            ,c.organizationcode agcode
            ,b.phoneno phone
            ,d.name branch
            ,e.vestingage vestingage
    
        FROM    
                RICB_FAS.LIFEANNUITY_PLANFORMEMBER a 
            ,RICB_FAS.LIFEANNUITY_MEMBERMASTER b
            ,RICB_FAS.lifeannuity_memberdetails c
            ,RICB_FAS.company_os d
            ,RICB_FAS.lifeannuity_vestingage e
            WHERE a.memberid=b.id 
            and b.id=c.memberid
            -- AND b.id=c.id  
            
            AND a.membervestingageid=e.id
            
            AND a.osid= d.id
            AND c.organizationcode =(SELECT agentcode FROM RICB_FAS.lifeannuity_agent WHERE citizenid=?)
            """;

            pst2 = conn.prepareStatement(query);
            pst2.setString(1, citizenId);
            rs2 = pst2.executeQuery();

            ToJSON converter = new ToJSON();
            jsonResponse = converter.toJSONArray(rs2);

            if (jsonResponse.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No Life Annuity business found for the given agent"));
            }

            return ResponseEntity.ok(jsonResponse.toString());

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
                if (rs2 != null) rs2.close();
                if (rs1 != null) rs1.close();
                if (pst2 != null) pst2.close();
                if (pst1 != null) pst1.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @GetMapping("/getGeneralInsuranceDetails")
    public ResponseEntity<?> getGeneralInsuranceDetails(@RequestParam("cid") String citizenId) {
        JSONArray jsonResponse = new JSONArray();
        Connection conn = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        try {
            if (citizenId == null || citizenId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "CID parameter is required"));
            }

            conn = ConnectionManager.getGeneralInsuranceConnection(); // Assuming you have this connection method
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // Step 1: Fetch agent_code from citizen_id
//            String getAgentQuery = "SELECT agent_code FROM TL_GI_MAS_AGENT WHERE citizen_id = ?";
//            pst1 = conn.prepareStatement(getAgentQuery);
//            pst1.setString(1, citizenId);
//            rs1 = pst1.executeQuery();
//
//            String agentCode = null;
//            if (rs1.next()) {
//                agentCode = rs1.getString("agent_code");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Collections.singletonMap("error", "Agent not found for given CID"));
//            }

            // Step 2: Run the main complex query for General Insurance details
            String query = """
                SELECT a.agent_code pol_src_code, a.serial_no pol_sys_id,c.product_group_ri pol_dept_code, a.policy_no pol_no, 
    to_char(a.sum_assured, '9,999,999,999,999.99') sumassured,to_char(a.POLICY_START_DATE,'dd/mm/rrrr') pol_fm_dt,
    to_char(a.POLICY_END_DATE,'dd/mm/rrrr') pol_to_dt,    
        a.policy_no pol_no, to_char(a.premium_amount, '9,999,999,999,999.99') pol_prem_lc_1,B.CON_MOBILE_NO1 phone, b.customer_name name,
    Decode(a.status_code,'G','Active','I','Expired','H','Lapsed') status 
    from TL_GI_TR_POLICY_HEADER a
    ,TL_GI_MAS_CUSTOMER b, TL_GI_MAS_PRODUCT c, TL_GI_MAS_ORGANISATION d
        where  A.AGENT_CODE=(SELECT agent_code from TL_GI_MAS_AGENT  where citizen_id=? or AGENT_CODE = ? ) 
        and a.customer_code=b.CUSTOMER_CODE 
        and a.product_code=c.product_code and a.status_code in ('G','H')             
        union             
        SELECT a.agent_code pol_src_code, a.serial_no pol_sys_id,c.product_group_ri pol_dept_code, a.policy_no pol_no, 
    to_char(a.sum_assured, '9,999,999,999,999.99') sumassured,to_char(a.POLICY_START_DATE,'dd/mm/rrrr') pol_fm_dt,
    to_char(a.POLICY_END_DATE,'dd/mm/rrrr') pol_to_dt,    
        a.policy_no pol_no, to_char(a.premium_amount, '9,999,999,999,999.99') pol_prem_lc_1,d.off_mobile_no1 phone, d.orgn_name name,
    Decode(a.status_code,'G','Active','I','Expired','H','Lapsed') status from TL_GI_TR_POLICY_HEADER a
    ,TL_GI_MAS_PRODUCT c, TL_GI_MAS_ORGANISATION d
        where  A.AGENT_CODE=(SELECT agent_code from TL_GI_MAS_AGENT  where citizen_id=? or AGENT_CODE = ?) 
        and a.customer_code=d.orgn_code
        and a.product_code=c.product_code and a.status_code in ('G','H')
            """;

            pst2 = conn.prepareStatement(query);
            pst2.setString(1, citizenId);
            pst2.setString(2, citizenId);
            pst2.setString(3, citizenId);
            pst2.setString(4, citizenId);
            rs2 = pst2.executeQuery();

            ToJSON converter = new ToJSON();
            jsonResponse = converter.toJSONArray(rs2);

            if (jsonResponse.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No General Insurance business found for the given agent"));
            }

            return ResponseEntity.ok(jsonResponse.toString());

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
                if (rs2 != null) rs2.close();
                if (rs1 != null) rs1.close();
                if (pst2 != null) pst2.close();
                if (pst1 != null) pst1.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @GetMapping("/getLifeInsuranceDetails")
    public ResponseEntity<?> getLifeInsuranceDetails(@RequestParam("cid") String citizenId, 
                                                    @RequestParam("year") String year) {
        JSONObject jsonResponse = new JSONObject();
        Connection conn = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        PreparedStatement pst3 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        try {
            if (citizenId == null || citizenId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "CID parameter is required"));
            }
            
            if (year == null || year.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Year parameter is required"));
            }

            conn = ConnectionManager.getLifeConnection(); // Assuming you have this connection method
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            // Step 1: Fetch agent_code from citizen_id
            String getAgentQuery = "SELECT agent_code FROM tl_in_mas_agent WHERE citizen_id = ?";
            pst1 = conn.prepareStatement(getAgentQuery);
            pst1.setString(1, citizenId);
            rs1 = pst1.executeQuery();

            String agentCode = null;
            if (rs1.next()) {
                agentCode = rs1.getString("agent_code");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Agent not found for given CID"));
            }

            // Step 2: Fetch Regular Life Insurance Policies
            String regularPoliciesQuery = """
                SELECT a.policy_no polno, a.underwriting_year, TO_CHAR(a.sum_assured, '9,999,999,999,999.99')  sumassured, a.sum_assured total, a.agent_code agcode, 
            TO_CHAR(a.policy_start_date,'DD/MM/RRRR') as stdate, TO_CHAR(a.policy_end_date ,'DD/MM/RRRR')as exptdt, decode(a.mode_of_payment,'M','MONTHLY','S','SSS','Q','QUARTERLY','H','HALF-YEARLY','Y','YEARLY', 'O','SINGLE') as mop,
            DECODE(a.status_code,'G','Active Policy','H','LAPSED', 'I', 'Lapsed Paid Up','J', 'Forfieted','K','Matured','L','Closed- Maturity','M','Death Claim Entered/Registered','MM','Surrender Claim Entered/','N','Closed- Death','P','Closed- Surrender','Q','Closed- Forfeiture','S','Closed- Claim Declined','U','Closed- PTD','V','Cancelled', 'X','Rejected') status,
            b.customer_name name,b.con_mobile_no1 mobno,  c.premium_per_instalment premium ,
            TO_CHAR(max(aa.due_date),'DD/MM/YYYY') lastpaiddate 
            ,d.branch_name branch
            from RICB_LI.tl_li_tr_policy_header a, tl_in_mas_customer b, RICB_LI.tl_li_tr_premium_list c 
            ,(select * from RICB_LI.tl_li_tr_premium_list where status_code='PAID')aa
            ,tl_in_mas_branch d  
            where a.customer_code=b.customer_code 
            and aa.policy_serial_no=a.serial_no
            and a.serial_no=c.policy_serial_no 
            and a.underwriting_year= ? 
            and  a.agent_code = ?
            and c.status_code='PENDING'
            and d.branch_code=a.branch_code
            group by a.policy_no, a.underwriting_year, a.sum_assured, a.policy_start_date, a.policy_end_date,a.mode_of_payment, a.status_code,
            b.customer_name, b.con_mobile_no1, c.premium_per_instalment, a.agent_code,d.branch_name
            order by a.policy_start_date

            """;

            pst2 = conn.prepareStatement(regularPoliciesQuery);
            pst2.setString(1, year);
            pst2.setString(2, agentCode);
            rs2 = pst2.executeQuery();

            ToJSON converter = new ToJSON();
            JSONArray regularPolicies = converter.toJSONArray(rs2);

            

            // Prepare response
            jsonResponse.put("regularPolicies", regularPolicies);
            jsonResponse.put("agentCode", agentCode);
            jsonResponse.put("year", year);

            return ResponseEntity.ok(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error occurred: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error occurred: " + e.getMessage()));
        } finally {
            try {
                if (rs3 != null) rs3.close();
                if (rs2 != null) rs2.close();
                if (rs1 != null) rs1.close();
                if (pst3 != null) pst3.close();
                if (pst2 != null) pst2.close();
                if (pst1 != null) pst1.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/life-annuity")
    public ResponseEntity<?> getLifeAnnuity(@RequestParam("cid") String cid) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            if (cid == null || cid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "cid parameter is required"));
            }

            conn = ConnectionManager.getOracleConnection();
            if (conn == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Database connection failed"));
            }

            String query =
                    "SELECT B.Custname, " +
                            "       b.cityzenshipid, " +
                            "       amountmask(b.SUMASSURED) SUMASSURED, " +
                            "       b.policyno ploicy_no, " +
                            "       b.planname, " +
                            "       b.annuitytype, " +
                            "       amountmask(b.Effectivepremiumamount) PREMIUMAMOUNT, " +
                            "       B.premiumtype, " +
                            "       B.Agent, " +
                            "       to_char(b.POLICYDATE,'dd/mm/rrrr') ploicy_date, " +
                            "       to_char(a.lastpremiumdate,'dd/mm/rrrr') close_date, " +
                            "       to_char( " +
                            "           case  " +
                            "               when to_char(A.lastpremiumdate,'DD') = to_char(b.policydate,'DD') and A.lastpremiumdate is not null " +
                            "                   then add_months(A.lastpremiumdate, 1)  " +
                            "               when to_char(A.lastpremiumdate,'DD') <> to_char(b.policydate,'DD') " +
                            "                    and ((to_char(add_months(A.lastpremiumdate, 1),'MON') <> 'FEB') " +
                            "                    or (to_char(add_months(A.lastpremiumdate, 1),'MON') = 'FEB' and to_char(b.policydate,'DD') <= '28')) " +
                            "                    and A.lastpremiumdate is not null " +
                            "                   then to_date(to_char(b.policydate,'DD') || '-' || " +
                            "                                to_char(add_months(A.lastpremiumdate, 1),'MON') || '-' || " +
                            "                                to_char(add_months(A.lastpremiumdate, 1),'YYYY'), 'dd-mon-yyyy') " +
                            "               when to_char(A.lastpremiumdate,'DD') <> to_char(b.policydate,'DD') " +
                            "                    and to_char(add_months(A.lastpremiumdate, 1),'MON') = 'FEB' " +
                            "                    and to_char(b.policydate,'DD') > '28' " +
                            "                    and A.lastpremiumdate is not null " +
                            "                   then to_date('28' || '-' || " +
                            "                                to_char(add_months(A.lastpremiumdate, 1),'MON') || '-' || " +
                            "                                to_char(add_months(A.lastpremiumdate, 1),'YYYY'), 'dd-mon-yyyy') " +
                            "               when A.lastpremiumdate is null then b.policydate " +
                            "           end, 'dd/mm/yyyy') next_due_months, " +
                            "       trunc(months_between(sysdate, add_months(A.lastpremiumdate, B.noofmonths)), 0) + 1 deu_months, " +
                            "       b.effectivepremiumamount, " +
                            "       (trunc(months_between(sysdate, add_months(A.lastpremiumdate, B.noofmonths)), 0) + 1) * b.effectivepremiumamount as total_payable " +
                            "FROM ( " +
                            "       select distinct pm.memberid, " +
                            "              (lmmst.Title || ' ' || lmmst.name) CUSTNAME, " +
                            "              lmmst.currentaddress CURRADDRESS, " +
                            "              lmmst.cityzenshipid, " +
                            "              lmmst.customerno, " +
                            "              pm.sumassured, " +
                            "              pm.policyno, " +
                            "              pm.policydate, " +
                            "              plm.planname, " +
                            "              am.annuitytype, " +
                            "              nvl(prmst.noofmonths,0) noofmonths, " +
                            "              prmst.premiumtype, " +
                            "              nvl(prmst.penaltydays,0) penaltydays, " +
                            "              va.vestingage, " +
                            "              nvl(l_md.organizationcode,'Direct') Agent, " +
                            "              (pm.premiumamount + pm.insuraranceamount) ANNUITYAMOUNT, " +
                            "              pm.effectivepremiumamount " +
                            "       from lifeannuity_planformember pm " +
                            "       inner join lifeannuity_membermaster lmmst on pm.memberid = lmmst.id " +
                            "       inner join lifeannuity_planmaster plm on pm.memberplanid = plm.id " +
                            "       inner join lifeannuity_annuitytypemaster am on pm.memberannuitytypeid = am.id " +
                            "       left outer join lifeannuity_vestingage va on pm.membervestingageid = va.id " +
                            "       left outer join lifeannuity_premiumtypemaster prmst on pm.memberpremiumtypeid = prmst.id " +
                            "       left outer join lifeannuity_memberdetails l_md on pm.id = l_md.policyid " +
                            "       where pm.isactive = 1 " +
                            "         and pm.policydate <= sysdate " +
                            "         and pm.status = 'Active' " +
                            "         and pm.memberannuitytypeid = 2 " +
                            "     ) B " +
                            "left outer join ( " +
                            "       select MAX(PremiumDate) lastpremiumdate, policyno " +
                            "       from la_annuitypremium " +
                            "       group by policyno " +
                            "     ) A on B.policyno = A.policyno " +
                            "where B.penaltydays <> 0 " +
                            "  and B.cityzenshipid = ? " +
                            "order by B.memberid";

            pst = conn.prepareStatement(query);
            pst.setString(1, cid);

            rs = pst.executeQuery();

            JSONArray jsonArray = convertResultSetToJson(rs);

            if (jsonArray.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message",
                                "No Life Annuity Business found for the given citizenship ID"));
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
                if (rs != null) rs.close();
                if (pst != null) pst.close();
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
}

// Utility class for JSON conversion (if not already exists)
class ToJSON {
    public JSONArray toJSONArray(ResultSet rs) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i).toUpperCase();
                Object columnValue = rs.getObject(i);
                jsonObject.put(columnName, columnValue != null ? columnValue.toString() : "");
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
