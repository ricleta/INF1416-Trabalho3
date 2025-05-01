package CofreDigital;

import java.util.Date;

public class TOTPTest {

    // Test secret in base32 format (should decode properly)
    private static final String BASE32_SECRET = "JBSWY3DPEHPK3PXP"; // Decodes to "Hello!\0"
    private static final long TIME_STEP = 30;

    public static void main(String[] args) {
        testConstructorValidSecret();
        testGenerateCode();
        testValidateCode();
        testInvalidCodeValidation();
    }

    private static void testConstructorValidSecret() {
        try {
            TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
            if (totp.getKeyByteArray() != null) {
                System.out.println("testConstructorValidSecret PASSED");
            } else {
                System.out.println("testConstructorValidSecret FAILED: Key is null");
            }
        } catch (Exception e) {
            System.out.println("testConstructorValidSecret FAILED: " + e.getMessage());
        }
    }

    private static void testGenerateCode() {
        try {
            TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
            long timeInterval = new Date().getTime() / 1000 / TIME_STEP;
            String code = totp.generateCode(timeInterval);

            if (code != null && code.length() == 6) {
                System.out.println("testGenerateCode PASSED");
            } else {
                System.out.println("testGenerateCode FAILED: Invalid code format");
            }
        } catch (Exception e) {
            System.out.println("testGenerateCode FAILED: " + e.getMessage());
        }
    }

    private static void testValidateCode() {
        try {
            TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
            long timeInterval = new Date().getTime() / 1000 / TIME_STEP;
            String code = totp.generateCode(timeInterval);
    
            boolean valid = totp.validateCode(code);
    
            if (valid) {
                System.out.println("testValidateCode PASSED");
            } else {
                System.out.println("testValidateCode FAILED: Valid code not accepted");
            }
    
            // Tolerance for previous/next window (±1 interval)
            boolean validTolerance = totp.validateCode(totp.generateCode(timeInterval - 1)) || totp.validateCode(totp.generateCode(timeInterval + 1));
            if (validTolerance) {
                System.out.println("testValidateCode TOLERANCE PASSED");
            } else {
                System.out.println("testValidateCode TOLERANCE FAILED");
            }
        } catch (Exception e) {
            System.out.println("testValidateCode FAILED: " + e.getMessage());
        }
    }
    

    private static void testInvalidCodeValidation() {
        try {
            TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
    
            // Test invalid codes
            boolean valid1 = totp.validateCode("123456"); // Random invalid code
            boolean valid2 = totp.validateCode(""); // Empty string
            boolean valid3 = totp.validateCode(null); // Null input
            boolean valid4 = totp.validateCode("12345"); // Invalid length
            boolean valid5 = totp.validateCode("1234567"); // Invalid length
    
            if (!valid1 && !valid2 && !valid3 && !valid4 && !valid5) {
                System.out.println("testInvalidCodeValidation PASSED");
            } else {
                System.out.println("testInvalidCodeValidation FAILED: Invalid code accepted");
            }
        } catch (Exception e) {
            System.out.println("testInvalidCodeValidation FAILED: " + e.getMessage());
        }
    }
    
}
