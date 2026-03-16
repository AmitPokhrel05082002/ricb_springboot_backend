//package bt.ricb.ricb_api.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class PolicyService {
//
//    @Autowired
//    @Qualifier("oracleJdbcTemplate")
//    private JdbcTemplate oracleJdbcTemplate;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public Map<String,Object> getPolicies(String cid, String orgCode){
//
//        Map<String,Object> response = new HashMap<>();
//
//        // 1️⃣ Individual Policies
//        String individualSql = "select * from V_CLAIMS_LI_POLICIES where cid=?";
//        List<Map<String,Object>> individualPolicies =
//                oracleJdbcTemplate.queryForList(individualSql, cid);
//
//        response.put("individualPolicies", individualPolicies);
//
//
//        // 2️⃣ Call GovTech API to get Household Number
//        String apiUrl = "https://apps.ricb.bt/rliHouseholdDetails.php?cid=" + cid;
//
//        Map apiResponse = restTemplate.getForObject(apiUrl, Map.class);
//
//        String householdNo = null;
//
//        try {
//            Map eligibleMemberCountDetails =
//                    (Map) apiResponse.get("eligibleMemberCountDetails");
//
//            List<Map> details =
//                    (List<Map>) eligibleMemberCountDetails.get("eligibleMemberCountDetail");
//
//            householdNo = (String) details.get(0).get("Household_number");
//
//        } catch (Exception e){
//            householdNo = null;
//        }
//
//        response.put("householdNo", householdNo);
//
//
//        // 3️⃣ RLI Policies
//        if(householdNo != null){
//
//            String rliSql = """
//                    select POLICY_NO
//                    from TL_LI_TR_RURAL_POL_HDR
//                    where PRESENT_HOUSEHOLD_NO = ?
//                    and status_code = 'D'
//                    and UNDERWRITING_YEAR = TO_CHAR(sysdate,'yyyy')
//                    """;
//
//            List<Map<String,Object>> rliPolicies =
//                    oracleJdbcTemplate.queryForList(rliSql, householdNo);
//
//            response.put("rliPolicies", rliPolicies);
//        }
//
//
//        // 4️⃣ Group Policies
//        if(orgCode != null){
//
//            String groupSql =
//                    "select * from V_CLAIMS_GROUP_LI_POLICIES where CID=? and ORG_CODE=?";
//
//            List<Map<String,Object>> groupPolicies =
//                    oracleJdbcTemplate.queryForList(groupSql, cid, orgCode);
//
//            response.put("groupPolicies", groupPolicies);
//        }
//
//        return response;
//    }
//
//    public Map<String, Object> getAllPolicies() {
//
//        Map<String, Object> response = new HashMap<>();
//
//        // 1️⃣ Individual Policies
//        String individualSql = "select * from V_CLAIMS_LI_POLICIES";
//        List<Map<String, Object>> individualPolicies =
//                oracleJdbcTemplate.queryForList(individualSql);
//
//        response.put("individualPolicies", individualPolicies);
//
//        // 2️⃣ RLI Policies
//        String rliSql = """
//            select POLICY_NO, PRESENT_HOUSEHOLD_NO
//            from TL_LI_TR_RURAL_POL_HDR
//            where status_code = 'D'
//            and UNDERWRITING_YEAR = TO_CHAR(sysdate,'yyyy')
//            """;
//
//        List<Map<String, Object>> rliPolicies =
//                oracleJdbcTemplate.queryForList(rliSql);
//
//        response.put("rliPolicies", rliPolicies);
//
//        // 3️⃣ Group Policies
//        String groupSql = "select * from V_CLAIMS_GROUP_LI_POLICIES";
//
//        List<Map<String, Object>> groupPolicies =
//                oracleJdbcTemplate.queryForList(groupSql);
//
//        response.put("groupPolicies", groupPolicies);
//
//        return response;
//    }
//}