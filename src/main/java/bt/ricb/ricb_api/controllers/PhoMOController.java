package bt.ricb.ricb_api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin({ "*" })
@RequestMapping("/li")
public class PhoMOController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ================= calculatePhomoAge =================
    @GetMapping("/phomo-age")
    public Double calculatePhomoAge(@RequestParam int ageDifference) {
        System.out.println("hiii");
        String sql = "SELECT addition_to_young_age FROM life_insurance_phomo_age_tables WHERE age_difference = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{ageDifference}, Double.class);
    }

    @GetMapping("/occupation-rate")
    public List<Map<String, Object>> getAllOccupationRates() {

        String sql = """
        SELECT id, occupation, rate
        FROM occupation_rate
        ORDER BY occupation
        """;

        return jdbcTemplate.queryForList(sql);
    }

    // ================= calculatePremium =================
    @GetMapping("/premium")
    public Double calculatePremium(
            @RequestParam int product,
            @RequestParam String method,
            @RequestParam int term,
            @RequestParam double sumAssured,
            @RequestParam int age,
            @RequestParam String discountFlag) {

        String rateSql = "SELECT rate FROM life_insurance_rates WHERE life_insurance_product_id = ? AND term = ? AND age = ?";
        Double rate = jdbcTemplate.queryForObject(rateSql, new Object[]{product, term, age}, Double.class);

        if(rate == null) rate = 0.0;
        double adjustment = 0.0;

        if("Y".equalsIgnoreCase(discountFlag)) rate = rate - (rate * 0.05);

        if(product != 1) {
            switch (method.toLowerCase()) {
                case "yearly": rate -= 0.75; break;
                case "half": rate -= 0.50; break;
                case "monthly": rate += rate * 0.05; break;
            }

            if(product == 2) {
                if(sumAssured >= 100000 && sumAssured < 200000) adjustment = 1;
                else if(sumAssured >= 200000 && sumAssured < 300000) adjustment = 1.5;
                else if(sumAssured >= 300000) adjustment = 2;
            } else {
                if(sumAssured >= 25000 && sumAssured <= 49999) adjustment = 1;
                else if(sumAssured >= 50000 && sumAssured <= 99999) adjustment = 1.5;
                else if(sumAssured >= 100000) adjustment = 2;
            }

        } else { // product == 1
            if(sumAssured >= 150001 && sumAssured <= 300000) adjustment = 0.5;
            else if(sumAssured >= 300001) adjustment = 1;
            adjustment = (rate * adjustment) / 100;
            adjustment = round(adjustment, 2);
        }

        rate -= adjustment;
        rate = (rate * sumAssured) / 1000;
        return round(rate, 2);
    }

    // ================= getRate =================
    @GetMapping("/rate")
    public Double getRate(
            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age) {

        String sql = "SELECT rate FROM life_insurance_rates WHERE life_insurance_product_id = ? AND term = ? AND age = ?";
        Double rate = jdbcTemplate.queryForObject(sql, new Object[]{product, term, age}, Double.class);
        return rate != null ? rate : 0.0;
    }

    // ================= calculateAdjustment =================
    @GetMapping("/adjustment")
    public String calculateAdjustment(
            @RequestParam int product,
            @RequestParam String method,
            @RequestParam int term,
            @RequestParam double sumAssured,
            @RequestParam int age) {

        String sql = "SELECT rate FROM life_insurance_rates WHERE life_insurance_product_id = ? AND term = ? AND age = ?";
        Double rate = jdbcTemplate.queryForObject(sql, new Object[]{product, term, age}, Double.class);
        if(rate == null) rate = 0.0;

        double adjustment = 0.0;
        double saRebate = 0.0;

        if(product != 1) {
            switch (method.toLowerCase()) {
                case "yearly": adjustment = -0.75; break;
                case "half": adjustment = -0.50; break;
                case "monthly": adjustment = round(rate * 0.05, 2); break;
            }

            if(product == 2) {
                if(sumAssured >= 100000 && sumAssured < 200000) saRebate = 1;
                else if(sumAssured >= 200000 && sumAssured < 300000) saRebate = 1.5;
                else if(sumAssured >= 300000) saRebate = 2;
            } else {
                if(sumAssured >= 25000 && sumAssured <= 49999) saRebate = 1;
                else if(sumAssured >= 50000 && sumAssured <= 99999) saRebate = 1.5;
                else if(sumAssured >= 100000) saRebate = 2;
            }

        } else { // product == 1
            if(sumAssured >= 150001 && sumAssured <= 300000) saRebate = 0.5;
            else if(sumAssured >= 300001) saRebate = 1;

            saRebate = (rate * saRebate) / 100;
            saRebate = round(saRebate, 2);
        }

        return adjustment + "/" + saRebate;
    }

    // ================= calculateAB =================
    @GetMapping("/accident-rate")
    public Double calculateAB(@RequestParam int term, @RequestParam int age) {
        String sql = "SELECT rate FROM gplp_accident_rate WHERE term = ? AND age = ?";
        Double rate = jdbcTemplate.queryForObject(sql, new Object[]{term, age}, Double.class);
        return rate != null ? rate : 0.0;
    }

    @GetMapping("/me-rate")
    public Double getMeRate(
            @RequestParam int product,
            @RequestParam int childAge,
            @RequestParam int proposerAge) {

        String sql = """
        SELECT rate
        FROM life_insurance_rate_me_nv
        WHERE life_insurance_product_id = ?
        AND child_age = ?
        AND ? BETWEEN proposer_age_from
                AND proposer_age_to
        """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                childAge,
                proposerAge);

        return rate != null ? rate : 0.0;
    }

    @GetMapping("/me-rider-rate")
    public Double getRiderRate(
            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age) {

        String sql = """
            SELECT rate
            FROM term_rider_rate
            WHERE life_insurance_product_id = ?
            AND term = ?
            AND age = ?
            """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                term,
                age);

        return rate != null ? rate : 0.0;
    }

    @GetMapping("/me-adjustment")
    public String getMeAdjustment(

            @RequestParam int product,
            @RequestParam int childAge,
            @RequestParam int proposerAge,
            @RequestParam double sumAssured,
            @RequestParam String mode) {

        // ================= BASE RATE =================
        String sql = """
        SELECT rate
        FROM life_insurance_rate_me_nv
        WHERE life_insurance_product_id = ?
          AND child_age = ?
          AND ? BETWEEN proposer_age_from AND proposer_age_to
    """;

        BigDecimal adjustedRate = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                product,
                childAge,
                proposerAge
        );

        if (adjustedRate == null) adjustedRate = BigDecimal.ZERO;

        // ================= MODE =================
        switch (mode.toLowerCase()) {

            case "yearly":
                adjustedRate = adjustedRate.subtract(new BigDecimal("0.75"));
                break;

            case "half":
                adjustedRate = adjustedRate.subtract(new BigDecimal("0.50"));
                break;

            case "monthly":
                adjustedRate = adjustedRate.add(
                        adjustedRate.multiply(new BigDecimal("0.05"))
                );
                break;

            case "quarterly":
            case "si":
                break;
        }

        // ================= SA REBATE =================
        BigDecimal saRebate = BigDecimal.ZERO;

        if (sumAssured >= 100000) {
            saRebate = new BigDecimal("2");
        }

        adjustedRate = adjustedRate.setScale(2, RoundingMode.DOWN);
        saRebate = saRebate.setScale(2, RoundingMode.DOWN);

        return adjustedRate.toPlainString() + "/" + saRebate.toPlainString();
    }

    @GetMapping("/me-premium")
    public Map<String, Double> calculateMePremium(

            @RequestParam int product,
            @RequestParam int childAge,
            @RequestParam int proposerAge,
            @RequestParam double sumAssured,
            @RequestParam String mode,
            @RequestParam int occupationId,
            @RequestParam(defaultValue = "N") String discountFlag) {

        // ================= BASE RATE =================
        String sql = """
        SELECT rate
        FROM life_insurance_rate_me_nv
        WHERE life_insurance_product_id = ?
          AND child_age = ?
          AND ? BETWEEN proposer_age_from AND proposer_age_to
        """;

        BigDecimal baseRate = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                product,
                childAge,
                proposerAge);

        if (baseRate == null) baseRate = BigDecimal.ZERO;

        BigDecimal adjustedRate = baseRate;

        // ================= STAFF / AGENT =================
        if ("Y".equalsIgnoreCase(discountFlag)) {
            adjustedRate = adjustedRate.subtract(new BigDecimal("0.5"));
        }

        // ================= MODE =================
        switch (mode.toLowerCase()) {

            case "yearly":
                adjustedRate = adjustedRate.subtract(new BigDecimal("0.75"));
                break;

            case "half":
                adjustedRate = adjustedRate.subtract(new BigDecimal("0.50"));
                break;

            case "monthly":
                adjustedRate = adjustedRate.add(
                        adjustedRate.multiply(new BigDecimal("0.05"))
                );
                break;

            case "quarterly":
            case "si":
                break;
        }

        // ================= SA REBATE =================
        if (sumAssured >= 100000) {
            adjustedRate = adjustedRate.subtract(new BigDecimal("2"));
        }

        // ================= OCCUPATION RATE =================
        String occSql = """
        SELECT rate
        FROM occupation_rate
        WHERE id = ?
        """;

        BigDecimal occRate = jdbcTemplate.queryForObject(
                occSql,
                BigDecimal.class,
                occupationId);

        if (occRate == null) occRate = BigDecimal.ZERO;

        BigDecimal basePremium =
                adjustedRate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        // ================= OCCUPATION PREMIUM =================
        BigDecimal occupationPremium =
                occRate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        // ================= TOTAL =================
        BigDecimal totalPremium =
                basePremium.add(occupationPremium);

        Map<String, Double> response = new HashMap<>();
        response.put("baseRate", baseRate.doubleValue());
        response.put("adjustedRate", adjustedRate.doubleValue());
        response.put("basePremium", basePremium.doubleValue());
        response.put("occupationPremium", occupationPremium.doubleValue());
        response.put("totalPremium", totalPremium.doubleValue());

        return response;
    }

    @GetMapping("/me-term-rider")
    public Double calculateTermRider(
            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age,
            @RequestParam double sumAssured) {

        String sql = """
        SELECT rate
        FROM term_rider_rate
        WHERE life_insurance_product_id = ?
        AND term = ?
        AND age = ?
        """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                term,
                age);

        if(rate == null) return 0.0;

        double riderPremium =
                rate *
                        (sumAssured * 0.25) /
                        1000;

        return round(riderPremium, 2);
    }


    @GetMapping("/tmn-rate")
    public Double getTmnRate(
            @RequestParam int product,
            @RequestParam int policyTerm,
            @RequestParam int premiumTerm,
            @RequestParam int age) {

        String sql = """
        SELECT rate
        FROM life_insurance_rate_tmn
        WHERE life_insurance_product_id = ?
        AND policy_term = ?
        AND premium_term = ?
        AND ? BETWEEN age_from AND age_to
    """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                policyTerm,
                premiumTerm,
                age
        );

        return rate != null ? rate : 0.0;
    }

    @GetMapping("/tmn-premium")
    public Map<String, Double> calculateTmnPremium(

            @RequestParam int product,
            @RequestParam int policyTerm,
            @RequestParam int premiumTerm,
            @RequestParam int age,
            @RequestParam double sumAssured,
            @RequestParam String mode,
            @RequestParam int occupationId,
            @RequestParam(defaultValue = "N") String discountFlag) {

        // ================= BASE RATE =================
        String sql = """
        SELECT rate
        FROM life_insurance_rate_tmn
        WHERE life_insurance_product_id = ?
          AND policy_term = ?
          AND premium_term = ?
          AND ? BETWEEN age_from AND age_to
    """;

        BigDecimal rate = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                product,
                policyTerm,
                premiumTerm,
                age);

        if (rate == null) rate = BigDecimal.ZERO;

        // keep original base rate for transparency
        BigDecimal baseRate = rate;

        // ================= STAFF / AGENT (-3%) =================
        if ("Y".equalsIgnoreCase(discountFlag)) {
            rate = rate.subtract(rate.multiply(new BigDecimal("0.03")));
        }

        // ================= MODE ADJUSTMENT (Single - 0.25%) =================
        if ("Single".equalsIgnoreCase(mode) && sumAssured > 500000) {
            rate = rate.subtract(rate.multiply(new BigDecimal("0.0025")));
        }

        // ================= SA REBATE (-1% if SA > 500,000) =================
        if (sumAssured > 500000) {
            rate = rate.subtract(rate.multiply(new BigDecimal("0.01")));
        }

        // ================= OCCUPATION RATE =================
        String occSql = """
        SELECT rate
        FROM occupation_rate
        WHERE id = ?
    """;

        BigDecimal occRate = jdbcTemplate.queryForObject(
                occSql,
                BigDecimal.class,
                occupationId);

        if (occRate == null) occRate = BigDecimal.ZERO;

        // ================= PREMIUM CALCULATION =================
        BigDecimal basePremium =
                rate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        BigDecimal occupationPremium =
                occRate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        BigDecimal totalPremium =
                basePremium.add(occupationPremium);

        // ================= RESPONSE =================
        Map<String, Double> response = new HashMap<>();
        response.put("baseRate", baseRate.doubleValue());
        response.put("adjustedRate", rate.doubleValue());
        response.put("basePremium", basePremium.doubleValue());
        response.put("occupationPremium", occupationPremium.doubleValue());
        response.put("totalPremium", totalPremium.doubleValue());

        return response;
    }

    @GetMapping("/tmn-adjustment")
    public String getTmnAdjustment(

            @RequestParam int product,
            @RequestParam int policyTerm,
            @RequestParam int premiumTerm,
            @RequestParam int age,
            @RequestParam double sumAssured,
            @RequestParam String mode) {

        // ================= BASE RATE =================
        String sql = """
        SELECT rate
        FROM life_insurance_rate_tmn
        WHERE life_insurance_product_id = ?
          AND policy_term = ?
          AND premium_term = ?
          AND ? BETWEEN age_from AND age_to
    """;

        BigDecimal adjustedRate = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                product,
                policyTerm,
                premiumTerm,
                age
        );

        if (adjustedRate == null) adjustedRate = BigDecimal.ZERO;

        // keep base rules consistent with premium logic
        BigDecimal baseRate = adjustedRate;

        // ================= MODE ADJUSTMENT =================
        if ("single".equalsIgnoreCase(mode) && sumAssured > 500000) {
            adjustedRate = adjustedRate.subtract(
                    adjustedRate.multiply(new BigDecimal("0.0025"))
            );
        }

        // ================= SA REBATE =================
        BigDecimal saRebate = BigDecimal.ZERO;

        if (sumAssured > 500000) {
            saRebate = new BigDecimal("1");
        }

        adjustedRate = adjustedRate.setScale(2, RoundingMode.DOWN);
        saRebate = saRebate.setScale(2, RoundingMode.DOWN);

        return adjustedRate.toPlainString() + "/" + saRebate.toPlainString();
    }

    @GetMapping("/ktn-rate")
    public Double getKtnRate(

            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age,
            @RequestParam String isRegular) {

        String sql = """
        SELECT rate
        FROM life_insurance_rate_ktn
        WHERE life_insurance_product_id = ?
          AND term = ?
          AND age = ?
          AND is_regular = ?
        """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                term,
                age,
                isRegular
        );

        return rate != null ? rate : 0.0;
    }

    @GetMapping("/ktn-CI-rate")
    public Double getCIRate(

            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age,
            @RequestParam String isRegular) {

        String sql = """
        SELECT rate
        FROM critical_illness_rate
        WHERE life_insurance_product_id = ?
          AND term = ?
          AND age = ?
          AND is_regular = ?
        """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                term,
                age,
                isRegular
        );

        return rate != null ? rate : 0.0;
    }

    @GetMapping("/ktn-CI-premium")
    public Map<String, Double> calculateCIPremium(

            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age,
            @RequestParam String isRegular,
            @RequestParam double sumAssured) {

        // ================= CI RATE =================
        String sql = """
        SELECT rate
        FROM critical_illness_rate
        WHERE life_insurance_product_id = ?
          AND term = ?
          AND age = ?
          AND is_regular = ?
        """;

        Double rate = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                product,
                term,
                age,
                isRegular);

        if (rate == null) {
            rate = 0.0;
        }

        // ================= CI COVERAGE =================
        double fiftyPercentSA = sumAssured * 0.50;
        double ciCoverage = Math.min(fiftyPercentSA, 2_000_000);

        // ================= CI PREMIUM =================
        BigDecimal ciPremium =
                BigDecimal.valueOf(rate)
                        .multiply(BigDecimal.valueOf(ciCoverage))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        // ================= RESPONSE =================
        Map<String, Double> response = new HashMap<>();
        response.put("rate", rate);
        response.put("ciCoverage", ciCoverage);
        response.put("ciPremium", ciPremium.doubleValue());

        return response;
    }

    @GetMapping("/ktn-premium")
    public Map<String, Double> calculateKtnPremium(

            @RequestParam int product,
            @RequestParam int term,
            @RequestParam int age,
            @RequestParam double sumAssured,
            @RequestParam String isRegular,
            @RequestParam int occupationId,
            @RequestParam(defaultValue = "N") String discountFlag) {

        // ================= BASE RATE =================
        String sql = """
        SELECT rate
        FROM life_insurance_rate_ktn
        WHERE life_insurance_product_id = ?
          AND term = ?
          AND age = ?
          AND is_regular = ?
    """;

        BigDecimal rate = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                product,
                term,
                age,
                isRegular
        );

        if (rate == null) rate = BigDecimal.ZERO;

        BigDecimal adjustedRate = rate;

        // ================= EMPLOYEE / AGENT (-5%) =================
        if ("Y".equalsIgnoreCase(discountFlag)) {
            adjustedRate = adjustedRate.subtract(
                    adjustedRate.multiply(new BigDecimal("0.05"))
            );
        }

        // ================= BASE PREMIUM =================
        BigDecimal basePremium =
                adjustedRate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        // ================= OCCUPATION PREMIUM =================
        String occSql = """
        SELECT rate
        FROM occupation_rate
        WHERE id = ?
    """;

        BigDecimal occRate = jdbcTemplate.queryForObject(
                occSql,
                BigDecimal.class,
                occupationId
        );

        if (occRate == null) occRate = BigDecimal.ZERO;

        BigDecimal occupationPremium =
                occRate.multiply(BigDecimal.valueOf(sumAssured))
                        .divide(BigDecimal.valueOf(1000), 2, RoundingMode.DOWN);

        // ================= TOTAL PREMIUM =================
        BigDecimal totalPremium =
                basePremium.add(occupationPremium);

        // ================= RESPONSE =================
        Map<String, Double> response = new HashMap<>();
        response.put("baseRate", rate.doubleValue());
        response.put("adjustedRate", adjustedRate.doubleValue());
        response.put("basePremium", basePremium.doubleValue());
        response.put("occupationPremium", occupationPremium.doubleValue());
        response.put("totalPremium", totalPremium.doubleValue());
        return response;
    }

    // ================= Helper Method =================
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}