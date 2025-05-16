package CofreDigital;

import CofreDigital.SecurityEncryption.TOTP;

public class GenerateCurrentTOTP {
    private static final String BASE32_SECRET = "JBSWY3DPEHPK3PXP"; // Decodes to "Hello!\0"
    private static final long TIME_STEP = 30;

    public static void main(String[] args) throws Exception {
        TOTP totp = new TOTP(BASE32_SECRET, TIME_STEP);
        long timeInterval = System.currentTimeMillis() / 1000 / TIME_STEP;
        String code = totp.generateCode(timeInterval);

        System.out.println("Generated TOTP code: " + code);
    }
}
