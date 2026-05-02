package bt.ricb.ricb_api.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    /**
     * Basic OpenAPI information shown in Swagger UI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RICB API Documentation")
                        .version("1.0.0")
                        .description("Comprehensive API documentation for RICB Backend")
                        .contact(new Contact().name("RICB Support Team").email("support@ricb.bt"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    // --- All /api/** group (general) ---
    @Bean
    public GroupedOpenApi apiBaseGroup() {
        return GroupedOpenApi.builder()
                .group("API")
                .displayName("All APIs Endpoints")
                .pathsToMatch("/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("API"));
                    return operation;
                })
                .build();
    }


    @Bean
    public GroupedOpenApi ClaimApiGroup() {
        return GroupedOpenApi.builder()
                .group("Claim")
                .displayName("Claim Endpoints")
                .pathsToMatch("/claims/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Claim"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi PolicyApiGroup() {
        return GroupedOpenApi.builder()
                .group("Policy")
                .displayName("PolicyRICB Endpoints")
                .pathsToMatch("/policies/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Policy"));
                    return operation;
                })
                .build();
    }
    // --- Per-controller groups ---

    @Bean
    public GroupedOpenApi apiControllerGroup() {
        return GroupedOpenApi.builder()
                .group("API Controller")
                .displayName("Generic API Controller Endpoints")
                .pathsToMatch("/api/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("API Controller"));
                    return operation;
                })
                .build();
    }
    @Bean
    public GroupedOpenApi businessControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Business")
                .displayName("Business Endpoints")
                .pathsToMatch("/ricb-business/**")
                .addOperationCustomizer((
                        operation,handlerMethod) -> { operation.setTags(Collections.singletonList("Business"));
                    return operation; })
                .build();
    }

    @Bean
    public GroupedOpenApi ccdbApiGroup() {
        return GroupedOpenApi.builder()
                .group("CCDB")
                .displayName("CCDB Endpoints")
                .pathsToMatch("/ccdb/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("CCDB"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi creditControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Credit")
                .displayName("Credit Endpoints")
                .pathsToMatch("/credit/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Credit"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi customerControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Customer")
                .displayName("Customer Endpoints")
                .pathsToMatch("/api/customer/**")
                .pathsToExclude("/api/customer/search")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Customer"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi customerSearchControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Customer Search")
                .displayName("Customer Search Endpoints")
                .pathsToMatch("/api/customer/search/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Customer Search"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi dtiControllerGroup() {
        return GroupedOpenApi.builder()
                .group("DTI")
                .displayName("DTI Endpoints")
                .pathsToMatch("/api/dti/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("DTI"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi insuranceApiGroup() {
        return GroupedOpenApi.builder()
                .group("Insurance")
                .displayName("Insurance Endpoints")
                .pathsToMatch("/insurance/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Insurance"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi lifeInsuranceApiGroup() {
        return GroupedOpenApi.builder()
                .group("Life Insurance")
                .displayName("Life Insurance Endpoints")
                .pathsToMatch("/life/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Life Insurance"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi masterApiGroup() {
        return GroupedOpenApi.builder()
                .group("Master")
                .displayName("Master Data Endpoints")
                .pathsToMatch("/master/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Master"));
                    return operation;
                })
                .build();
    }



    @Bean
    public GroupedOpenApi mtpNewPolicyGroup() {
        return GroupedOpenApi.builder()
                .group("MTP New Policy")
                .displayName("MTP New Policy Endpoints")
                .pathsToMatch("/api/mtp-policies/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("MTP New Policy"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi nyekorControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Nyekor")
                .displayName("Nyekor Endpoints")
                .pathsToMatch("/api/nyekor/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Nyekor"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApiGroup() {
        return GroupedOpenApi.builder()
                .group("Payment")
                .displayName("Payment Endpoints")
                .pathsToMatch(
                        "/paymentrequest",
                        "/accountenq",
                        "/finalPaymentRequest",
                        "/insertPaymentTrans"
                )
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Payment"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi paymentTransactionControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Payment Transactions")
                .displayName("Payment Transaction Endpoints")
                .pathsToMatch("/api/payments/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Payment Transactions"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi policyApiGroup() {
        return GroupedOpenApi.builder()
                .group("Policy API")
                .displayName("Policy Endpoints")
                .pathsToMatch("/insertNyekorDtiPolicy",
                        "/getDtiNyekkorDetailsAgainstCidNo",
                        "/getPolciciesAgainstCid",
                        "/getUserDetailsAgainstTpn")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Policy"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi reportControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Reporting")
                .displayName("Report Endpoints")
                .pathsToMatch("/report/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Reporting"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi rhiControllerGroup() {
        return GroupedOpenApi.builder()
                .group("RHI")
                .displayName("RHI Endpoints")
                .pathsToMatch("/api/RHI/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("RHI"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi rliNewPolicyControllerGroup() {
        return GroupedOpenApi.builder()
                .group("RLI New Policy")
                .displayName("RLI New Policy Endpoints")
                .pathsToMatch(
                        "/api/rli-policies",
                        "/api/{serialNo}",
                        "/api/transaction/{transactionId}"
                )
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("RLI New Policy"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi shareInfoControllerGroup() {
        return GroupedOpenApi.builder()
                .group("Share Info")
                .displayName("Share Information Endpoints")
                .pathsToMatch(
                        "/sendEmail",
                        "/sendSms"
                )
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Share Info"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi transactionApiGroup() {
        return GroupedOpenApi.builder()
                .group("Transaction API")
                .displayName("Transaction Endpoints")
                .pathsToMatch("/ricb-transaction/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Transaction"));
                    return operation;
                })
                .build();
    }

}
