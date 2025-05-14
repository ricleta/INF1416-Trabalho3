package CofreDigital.SecurityEncryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;



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

    private PrivateKey getPrivateKey(String privateKeyPath, String passphrase) {
        try {
            // 1. Lê os dados criptografados da chave privada
            byte[] encryptedBytes = Files.readAllBytes(Paths.get(privateKeyPath));
            System.out.println("Encrypted bytes length: " + encryptedBytes.length);

          
            // 3. Deriva a chave AES (256 bits) da frase secreta usando SHA1PRNG
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(passphrase.getBytes(StandardCharsets.UTF_8));
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, sr);
            SecretKey aesKey = keyGen.generateKey();

            // 4. Cria o Cipher AES/ECB/PKCS5Padding e decripta os dados
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes); 
            System.out.println("Decrypted bytes length: " + decryptedBytes.length);

            // 5. Constrói a chave privada a partir do conteúdo PKCS#8 decodificado
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
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