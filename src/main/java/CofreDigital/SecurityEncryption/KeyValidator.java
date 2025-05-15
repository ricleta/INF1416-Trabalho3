package CofreDigital.SecurityEncryption;

import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;

public class KeyValidator {
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final int RANDOM_ARRAY_SIZE = 8192;
    private static final int AES_KEY_SIZE = 256;
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES_ALGORITHM = "AES";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String PRNG_ALGORITHM = "SHA1PRNG";

    public KeyValidator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public X509Certificate getCertificate(String certPath)
    {
        try (FileInputStream fis = new FileInputStream(certPath)) {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            return (X509Certificate) certFactory.generateCertificate(fis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load certificate: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the login (email address) from the subject distinguished name (DN) of an X.509 certificate.
     *
     * @param certificate The X.509 certificate from which to extract the login.
     * @return The email address found in the certificate's subject DN, or null if not found.
     */
    public String getLoginFromCertificate(X509Certificate certificate) {
        // Get the full Subject Distinguished Name (DN) string from the certificate.
        // Example: "CN=Test User, OU=Test Org, O=Test Inc, EMAILADDRESS=test@example.com, C=US"
        String subjectDN = certificate.getSubjectDN().getName();

        // Split the DN string into its individual components (Relative Distinguished Names or RDNs).
        // The components are typically separated by commas.
        String[] parts = subjectDN.split(",");

        System.out.println("Subject DN: " + subjectDN);
        // Iterate over each component of the DN.
        for (String part : parts) {
            // Trim leading/trailing whitespace from the component.
            // Check if the component starts with "EMAILADDRESS=".
            if (part.trim().startsWith("EMAILADDRESS=")) {
                System.out.println("Found EMAILADDRESS: " + part.trim());
                // If it's the email address component, extract the actual email address.
                // The substring("EMAILADDRESS=".length()) is used to remove "EMAILADDRESS=" prefix.
                return part.trim().substring("EMAILADDRESS=".length());
            }
        }
        // If no "EMAILADDRESS=" component is found after checking all parts, return null.
        return null;
    }


     public PrivateKey getPrivateKey(String privateKeyPath, String passphrase) {
        try {
            // 1. Lê os dados criptografados da chave privada 
            byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(privateKeyPath));
            
            // 2. Gera uma chave secreta AES para descriptografar a chave privada
            SecureRandom secureRandom = SecureRandom.getInstance(PRNG_ALGORITHM);
            secureRandom.setSeed(passphrase.getBytes(StandardCharsets.UTF_8));

            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, secureRandom); // AES-256
            SecretKey aesKey = keyGen.generateKey();
            
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            
            // 3. Descriptografa a chave privada
            byte[] decryptedPrivateKey = cipher.doFinal(encryptedPrivateKey);

            String privateKeyBase64String = new String(decryptedPrivateKey);
            privateKeyBase64String = privateKeyBase64String.replace("-----BEGIN PRIVATE KEY-----\n","");
            privateKeyBase64String = privateKeyBase64String.replace("-----END PRIVATE KEY-----\n","");

            byte[] privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyBase64String);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } 
        catch (Exception e) {
            throw new RuntimeException("Failed to load or decrypt private key: " + e.getMessage(), e);
        }
    }

    public boolean validatePrivateKey(String certPath, String privateKeyPath, String passphrase) throws Exception {
        // Step 1: Generate a random 8192-byte array
        byte[] randomArray = new byte[RANDOM_ARRAY_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomArray);

        // Step 2: Load the certificate and extract the public key
        Certificate certificate = getCertificate("./".replace("/", System.getProperty("file.separator")) + Paths.get(certPath).toString());
        PublicKey publicKey = certificate.getPublicKey();

        // Step 3: Load the private key
        PrivateKey privateKey = getPrivateKey("./".replace("/", System.getProperty("file.separator")) + Paths.get(privateKeyPath).toString(), passphrase);

        // Step 4: Sign the random array with the private key
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(randomArray);
        byte[] digitalSignature = signature.sign();

        // Step 5: Verify the signature with the public key
        signature.initVerify(publicKey);
        signature.update(randomArray);
        return signature.verify(digitalSignature);
    }
}