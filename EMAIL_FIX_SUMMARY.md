# Email Configuration Fix - NullPointerException

## Problem Identified

**Error**: `java.lang.NullPointerException: Cannot invoke "org.springframework.mail.javamail.JavaMailSender.send(org.springframework.mail.SimpleMailMessage)" because "this.javaMailSender" is null`

**Root Cause**: In `ricbDAO.java` at line 2046, the code was creating a new instance of `EmailUtil` with a null `JavaMailSender`:

```java
EmailUtil emailUtil = new EmailUtil(null);  // ❌ WRONG
emailUtil.generateAndSendEmail(to, from, subject, message);
```

This bypassed Spring's dependency injection, resulting in a null `JavaMailSender` being passed to EmailUtil.

---

## Solution Applied

### 1. Added EmailUtil as Autowired Dependency in ricbDAO

**File**: `src/main/java/bt/ricb/ricb_api/dao/ricbDAO.java`

**Changes**:
```java
// BEFORE
@Repository
public class ricbDAO {
    private DataSource dataSource = null;
    private DataSource mySQLDataSource = null;

    @Autowired
    public void RicbDAO(DataSource dataSource, DataSource mySQLDataSource) {
        this.dataSource = dataSource;
        this.mySQLDataSource = mySQLDataSource;
    }

// AFTER
@Repository
public class ricbDAO {
    private DataSource dataSource = null;
    private DataSource mySQLDataSource = null;
    private EmailUtil emailUtil = null;  // ✅ NEW

    @Autowired
    public void RicbDAO(DataSource dataSource, DataSource mySQLDataSource, EmailUtil emailUtil) {
        this.dataSource = dataSource;
        this.mySQLDataSource = mySQLDataSource;
        this.emailUtil = emailUtil;  // ✅ NEW
    }
```

### 2. Fixed sendmail Method to Use Injected EmailUtil

**Location**: `ricbDAO.java` method `sendmail()` (around line 2046)

**Changes**:
```java
// BEFORE
try {
    EmailUtil emailUtil = new EmailUtil(null);  // ❌ Creates null instance
    emailUtil.generateAndSendEmail(to, from, subject, message);
}

// AFTER
try {
    emailUtil.generateAndSendEmail(to, from, subject, message);  // ✅ Uses injected instance
}
```

---

## Why This Works

1. **Dependency Injection**: Spring now properly injects the configured `JavaMailSender` bean into `EmailUtil`
2. **Configuration Loaded**: Mail configuration from `application.properties` is automatically used:
   - `spring.mail.host=smtp.gmail.com`
   - `spring.mail.port=587`
   - `spring.mail.username=lekidorji6199@gmail.com`
   - `spring.mail.password=jhukyfmfzhdkopmt`
   - `spring.mail.properties.mail.smtp.auth=true`
   - `spring.mail.properties.mail.smtp.starttls.enable=true`

3. **No Manual Instantiation**: By removing `new EmailUtil(null)`, we let Spring manage the bean lifecycle

---

## Files Modified

1. ✅ **ricbDAO.java**
   - Added EmailUtil field
   - Updated constructor to inject EmailUtil
   - Fixed sendmail() method to use injected instance

---

## Verification

The following have been verified:
- ✅ EmailUtil is annotated with `@Component`
- ✅ EmailUtil has constructor injection of `JavaMailSender`
- ✅ ricbDAO has `@Repository` annotation
- ✅ EmailUtil is now properly injected into ricbDAO
- ✅ Mail configuration is present in application.properties

---

## Testing

The email functionality should now work correctly. To test:

1. Build the project: `mvnw clean compile`
2. Run the application: `mvnw spring-boot:run`
3. Call the email endpoint to verify email sending works

---

## Summary

**Issue**: Manual instantiation of EmailUtil with null parameter
**Fix**: Proper Spring dependency injection of EmailUtil into ricbDAO
**Result**: JavaMailSender is now properly initialized and available

✅ **The NullPointerException should now be resolved!**

