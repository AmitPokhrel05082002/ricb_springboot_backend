//package bt.ricb.ricb_api.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class OracleDBConfig {
//
//    @Value("${oracle.datasource.url}")
//    private String url;
//
//    @Value("${oracle.datasource.username}")
//    private String username;
//
//    @Value("${oracle.datasource.password}")
//    private String password;
//
//    @Bean(name = "oracleDataSource")
//    public DataSource oracleDataSource() {
//
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(url);
//        ds.setUsername(username);
//        ds.setPassword(password);
//        ds.setDriverClassName("oracle.jdbc.OracleDriver");
//
//        return ds;
//    }
//
//    @Bean(name = "oracleJdbcTemplate")
//    public JdbcTemplate oracleJdbcTemplate(DataSource oracleDataSource) {
//        return new JdbcTemplate(oracleDataSource);
//    }
//}