package bt.ricb.ricb_api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    // ================= Helper Method =================
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}