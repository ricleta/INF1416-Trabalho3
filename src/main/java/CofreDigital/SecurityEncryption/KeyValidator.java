package CofreDigital.SecurityEncryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.io.FileInputStream;

public class KeyValidator {
    private static final String PROVIDER = "BC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final int RANDOM_ARRAY_SIZE = 8192;

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

    private PrivateKey getPrivateKey(String privateKeyPath) {
        try (PEMParser pemParser = new PEMParser(new FileReader(privateKeyPath))) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(PROVIDER);
            Object parsedObject = pemParser.readObject();
            if (!(parsedObject instanceof org.bouncycastle.asn1.pkcs.PrivateKeyInfo)) {
                throw new IllegalArgumentException("The provided private key file does not contain a valid PrivateKeyInfo object.");
            }
            return converter.getPrivateKey((org.bouncycastle.asn1.pkcs.PrivateKeyInfo) parsedObject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key: " + e.getMessage(), e);
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
        PrivateKey privateKey = getPrivateKey("./".replace("/", System.getProperty("file.separator")) + Paths.get(privateKeyPath).toString());

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