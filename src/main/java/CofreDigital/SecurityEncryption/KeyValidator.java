package CofreDigital.SecurityEncryption;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class KeyValidator {
    private static final String PROVIDER = "BC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final int RANDOM_ARRAY_SIZE = 8192;
    private static final String CRIPT_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES_ALGORITHM = "AES";
    private static final String DIGEST_ALGORITHM = "SHA-256";

    public KeyValidator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Certificate getCertificate(String certPath)
    {
        try (FileInputStream fis = new FileInputStream(certPath)) {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            return certFactory.generateCertificate(fis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load certificate: " + e.getMessage(), e);
        }
    }

    public static PrivateKey getPrivateKey(String privateKeyPath, String passphrase) {
        try {
            
            // 1. Lê os dados criptografados da chave privada 
            byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(privateKeyPath));

            // 2. Deriva uma chave AES de 256 bits a partir da frase secreta usando SHA-256
            MessageDigest sha256 = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] keyBytes = sha256.digest(passphrase.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec aesKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);

            // 3. Decripta com AES/ECB/PKCS5Padding 
            Cipher cipher = Cipher.getInstance(CRIPT_ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            // problema ta aqui!!!
            byte[] decryptedBytes = cipher.doFinal(encryptedPrivateKey);

            // 4. Interpreta os bytes PKCS#8 com Bouncy Castle
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(decryptedBytes);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            return converter.getPrivateKey(privateKeyInfo);

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