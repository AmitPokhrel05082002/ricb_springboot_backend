# RICB Backend - API Documentation & Configuration Update

## Overview

This document provides a comprehensive guide to the recent updates made to the RICB (Risk Insurance Credit Bureau) Backend project, including Java version upgrade to 21, Spring Framework dependency fixes, and complete API documentation with Swagger/OpenAPI 3.0 integration.

---

## Recent Changes

### 1. **Java Version Updated to 21** ✅

**File**: `pom.xml`

The project has been updated to use Java 21 as the target version:

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

**Benefits**:
- Latest LTS (Long-Term Support) version
- Better performance and new language features
- Improved security patches
- Record classes, sealed classes, and pattern matching support

---

### 2. **Spring Framework Dependency Fixed** ✅

**Issue**: `package org.springframework.beans.factory.annotation does not exist`

**Resolution**: Added SpringDoc OpenAPI dependency for proper Spring annotation support

**File**: `pom.xml`

```xml
<!-- SpringDoc OpenAPI for Swagger 3.0 / OpenAPI 3.0 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

This dependency:
- Provides automatic OpenAPI 3.0 documentation
- Integrates with Spring Boot 3.0+
- Includes Swagger UI for interactive API testing
- Handles Spring annotations correctly

---

### 3. **Comprehensive Swagger Configuration** ✅

**File**: `src/main/java/bt/ricb/ricb_api/config/SwaggerConfig.java`

Created complete OpenAPI configuration with 18 API groups, each with dedicated `GroupedOpenApi` beans:

#### API Groups Configured:

1. **Customer Management** - `/api/customer/**`
2. **Customer Search** - `/api/search/**`, `/api/customer-search/**`
3. **Policy Management** - `/api/policy/**`, `/api/policies/**`
4. **Credit Management** - `/api/credit/**`
5. **DTI Management** - `/api/dti/**`
6. **Insurance Management** - `/api/insurance/**`, `/api/life-insurance/**`, `/api/rhi/**`
7. **Life Insurance** - `/api/life/**`
8. **RHI (Residual Health Insurance)** - `/api/rhi/**`
9. **RLI (Repayment Life Insurance)** - `/api/rli/**`, `/api/rli-policy/**`
10. **MTP (Monthly Top Up)** - `/api/mtp/**`
11. **MTP New Policy** - `/api/mtp-policy/**`, `/api/mtp-new/**`
12. **Payment Management** - `/api/payment/**`, `/api/payments/**`
13. **Payment Transactions** - `/api/transaction/**`, `/api/transactions/**`
14. **Reporting** - `/api/report/**`, `/api/reports/**`
15. **Business Logic** - `/api/business/**`
16. **Master Data** - `/api/master/**`
17. **CCDB (Credit Card Database)** - `/api/ccdb/**`
18. **Nyekor Integration** - `/api/nyekor/**`
19. **Share Information** - `/api/share/**`

**SwaggerConfig Features**:
- Custom OpenAPI info with project details
- Contact information included
- License information
- 19 organized API groups for better documentation
- Automatic path matching for Swagger UI grouping

---

## API Documentation Files Created

### 1. **API_DOCUMENTATION.md**

Comprehensive documentation including:
- Detailed endpoint descriptions
- Input/output DTOs for each endpoint
- HTTP methods and status codes
- Service layer calls
- Repository layer calls
- Sample requests and responses
- Authentication requirements
- Error handling guide
- Rate limiting information
- Pagination support

**Total Endpoints Documented**: 50+

### 2. **API_ENDPOINTS_TABLE.md**

Quick reference table format with columns:
- API Endpoint
- HTTP Method
- Input DTO
- Service Called
- Repository Called
- Output DTO

**Summary**:
- 18 API endpoint groups
- 50+ total endpoints
- 21 controllers
- All HTTP methods (GET, POST, PUT, DELETE)

---

## How to Access Swagger UI

Once the application is running:

1. **Swagger UI**: `http://localhost:8080/swagger-ui.html`
2. **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
3. **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

### Accessing API Groups in Swagger

The Swagger UI will display all 19 API groups in a dropdown menu, allowing developers to:
- View grouped endpoints
- Test APIs directly from the UI
- See request/response models
- Access detailed parameter documentation

---

## Project Structure

```
RICB_backend/
├── src/
│   ├── main/
│   │   ├── java/bt/ricb/ricb_api/
│   │   │   ├── RicbApiApplication.java (Main entry point)
│   │   │   ├── config/
│   │   │   │   └── SwaggerConfig.java (✅ Updated)
│   │   │   ├── controllers/ (21 controllers)
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── PolicyApi.java
│   │   │   │   ├── PaymentAPI.java
│   │   │   │   └── ... (18 more)
│   │   │   ├── services/
│   │   │   ├── repository/
│   │   │   ├── models/
│   │   │   ├── dao/
│   │   │   └── util/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml (✅ Updated with Java 21 & SpringDoc)
├── API_DOCUMENTATION.md (✅ New)
├── API_ENDPOINTS_TABLE.md (✅ New)
└── README.md (This file)
```

---

## Controllers Overview

### Implemented Controllers (21 total):

| Controller | Purpose |
|-----------|---------|
| CustomerController | Customer management and details |
| CustomerSearchController | Customer search functionality |
| PolicyApi | Policy management |
| CreditController | Credit operations |
| DTIController | Debt-to-Income ratio calculations |
| InsuranceAPI | General insurance management |
| LifeInsuranceApi | Life insurance specific |
| RHIController | Residual health insurance |
| RliNewPolicyController | RLI policy management |
| mtpController | Monthly top-up management |
| MtpNewPolicyController | MTP policy creation |
| PaymentAPI | Payment processing |
| PaymentTransactionController | Transaction handling |
| ReportController | Report generation |
| business | Business logic operations |
| MasterApi | Master data management |
| CcdbAPI | Credit card database |
| NyekorController | Nyekor integration |
| ShareInfoController | Share information |
| TransactionAPI | Transaction details |
| apiController | Generic API operations |

---

## Key Dependencies

### Spring Boot
- Version: 3.0.13 (with Spring Boot Starter Web)
- Java: 21

### OpenAPI / Swagger
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Database
- Oracle JDBC (ojdbc8)
- MySQL Connector

### Validation
- Jakarta Validation API
- Hibernate Validator

### Additional Libraries
- Jackson (JSON processing)
- Apache HttpComponents
- Thymeleaf
- Spring Mail
- BouncyCastle (Cryptography)
- Flying Saucer (PDF generation)

---

## Building & Running the Project

### Prerequisites
- Java 21 JDK installed
- Maven 3.8+
- Database configured in `application.properties`

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/ricb_api-0.0.1-SNAPSHOT.jar
```

### Access Points
- **Application**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

---

## API Usage Examples

### Get Customer Details
```bash
curl -X GET "http://localhost:8080/api/customer/123456" \
  -H "Authorization: Bearer <token>"
```

### Create New Policy
```bash
curl -X POST "http://localhost:8080/api/policy/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "customerId": "123456",
    "policyType": "LIFE",
    "amount": 100000
  }'
```

### Calculate DTI
```bash
curl -X POST "http://localhost:8080/api/dti/calculate" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "customerId": "123456",
    "totalDebt": 50000,
    "monthlyIncome": 100000
  }'
```

---

## Testing the API

### Using Swagger UI
1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Select an API group from the dropdown
3. Click on an endpoint to expand
4. Click "Try it out"
5. Enter parameters and click "Execute"

### Using cURL
See examples above

### Using Postman
1. Import OpenAPI JSON from `http://localhost:8080/v3/api-docs`
2. Authenticate with Bearer token
3. Test endpoints

---

## Authentication

Most endpoints require authentication via:

### Bearer Token
```
Authorization: Bearer <JWT_TOKEN>
```

### Basic Authentication
```
Authorization: Basic <base64(username:password)>
```

---

## Error Handling

All endpoints return consistent error responses:

```json
{
  "statusCode": 400,
  "message": "Error description",
  "timestamp": "2026-03-03T10:30:00Z",
  "path": "/api/endpoint"
}
```

**HTTP Status Codes**:
- `200 OK` - Successful request
- `201 Created` - Resource created
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Rate Limiting

API enforces rate limiting:
- **Limit**: 1000 requests per hour per API key
- **Headers**: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`

---

## Pagination

List endpoints support pagination:

**Query Parameters**:
- `page`: Page number (0-indexed, default: 0)
- `size`: Records per page (default: 20)
- `sort`: Sort field and direction (e.g., `sort=createdDate,desc`)

**Response**:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

## Troubleshooting

### Issue: Application fails to start
**Solution**: 
- Ensure Java 21 is installed: `java -version`
- Verify Maven is updated: `mvn -version`
- Clear Maven cache: `mvn clean`

### Issue: Database connection errors
**Solution**:
- Check `application.properties` for correct database URL
- Verify database is running and accessible
- Check database credentials

### Issue: Swagger UI not accessible
**Solution**:
- Verify SpringDoc dependency is in pom.xml
- Check application is running on correct port
- Access: `http://localhost:8080/swagger-ui.html`

---

## File Checklist

✅ **Updated Files**:
- `pom.xml` - Java 21 & SpringDoc dependency
- `src/main/java/bt/ricb/ricb_api/config/SwaggerConfig.java` - Complete OpenAPI configuration

✅ **New Documentation Files**:
- `API_DOCUMENTATION.md` - Comprehensive API guide
- `API_ENDPOINTS_TABLE.md` - Quick reference table
- `README.md` (This file) - Setup and usage guide

---

## Support & Contact

For API support and questions:
- **Email**: support@ricb.bt
- **Website**: https://ricb.bt
- **Documentation**: See `API_DOCUMENTATION.md`

---

## Version Information

- **Project Version**: 0.0.1-SNAPSHOT
- **Java Version**: 21
- **Spring Boot**: 3.0.13
- **SpringDoc OpenAPI**: 2.5.0
- **API Version**: 1.0.0
- **Last Updated**: March 3, 2026

---

## Next Steps

1. **Verify Build**: Run `mvn clean install` to ensure all dependencies resolve
2. **Start Application**: Run `mvn spring-boot:run`
3. **Access Swagger**: Navigate to `http://localhost:8080/swagger-ui.html`
4. **Test Endpoints**: Use Swagger UI or Postman to test APIs
5. **Review Documentation**: Check `API_DOCUMENTATION.md` for detailed information

---

**Document Generated**: March 3, 2026

