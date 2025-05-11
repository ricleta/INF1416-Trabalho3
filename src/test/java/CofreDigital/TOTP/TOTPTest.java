package CofreDigital.TOTP;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class TOTPTest {

    private static final String BASE32_SECRET = "JBSWY3DPEHPK3PXP"; // Decodes to "Hello!\0"
    private static final long TIME_STEP = 30;

    @Test
    void testConstructorValidSecret() throws Exception {
        TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
        assertNotNull(totp.getKeyByteArray(), "Key should not be null");
    }

    @Test
    void testGenerateCode() throws Exception {
        TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
        long timeInterval = new Date().getTime() / 1000 / TIME_STEP;
        String code = totp.generateCode(timeInterval);

        assertNotNull(code, "Code should not be null");
        assertEquals(6, code.length(), "Code should be 6 digits");
    }

    @Test
    void testValidateCode() throws Exception {
        TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
        long timeInterval = new Date().getTime() / 1000 / TIME_STEP;
        String code = totp.generateCode(timeInterval);

        assertTrue(totp.validateCode(code), "Valid code should be accepted");

        // Tolerance for previous/next window (±1 interval)
        boolean validTolerance = totp.validateCode(totp.generateCode(timeInterval - 1)) ||
                                totp.validateCode(totp.generateCode(timeInterval + 1));
        assertTrue(validTolerance, "Code should be valid within tolerance window");
    }

    @Test
    void testInvalidCodeValidation() throws Exception {
        TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);

        assertFalse(totp.validateCode("123456"), "Random invalid code should not be accepted");
        assertFalse(totp.validateCode(""), "Empty string should not be accepted");
        assertFalse(totp.validateCode(null), "Null input should not be accepted");
        assertFalse(totp.validateCode("12345"), "Invalid length code should not be accepted");
        assertFalse(totp.validateCode("1234567"), "Invalid length code should not be accepted");
    }
}
