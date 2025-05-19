/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.SecurityEncryption;

import CofreDigital.Cofre;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi.SHA1;
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
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

public class KeyValidator {
    private final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private final String CERTIFICATE_TYPE = "X.509";
    private final int RANDOM_ARRAY_SIZE = 8192;
    private final int AES_KEY_SIZE = 256;
    private final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private final String AES_ALGORITHM = "AES";
    private final String KEY_ALGORITHM = "RSA";
    private final String PRNG_ALGORITHM = "SHA1PRNG";
    private final String INDEX_ENV_PATH = "Files\\index.env";
    private final String INDEX_ASD_PATH = "Files\\index.asd";
    private final String INDEX_ENC_PATH = "Files\\index.enc";

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

    public PublicKey getPublicKeyFromCertificate(String certPath) {
        try {
            Certificate cert = getCertificate(certPath);
            if (cert instanceof X509Certificate) {
                X509Certificate x509Cert = (X509Certificate) cert;
                return x509Cert.getPublicKey();
            }
            return null; // Not found
        } 
    
        catch (Exception e) {
            throw new RuntimeException("Failed to extract public key from certificate: " + e.getMessage(), e);
        }

    }

    private PrivateKey getAdminPrivateKey(String passphrase)
    {
        byte [] dbPrivatekey = Cofre.getDBAdminPrivateKey();
        if (dbPrivatekey == null) {
            System.out.println("!!!!!!!!!!!!!! Chave privada do admin não encontrada.");
            return null;
        }

        try {
            // 1. Gera uma chave secreta AES para descriptografar a chave privada
            SecureRandom secureRandom = SecureRandom.getInstance(PRNG_ALGORITHM);
            secureRandom.setSeed(passphrase.getBytes(StandardCharsets.UTF_8));

            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, secureRandom); // AES-256
            SecretKey aesKey = keyGen.generateKey();
            
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            
            // 2. Descriptografa a chave privada
            byte[] decryptedPrivateKey = cipher.doFinal(dbPrivatekey);

            String privateKeyBase64String = new String(decryptedPrivateKey);
            privateKeyBase64String = privateKeyBase64String.replace("-----BEGIN PRIVATE KEY-----\n","");
            privateKeyBase64String = privateKeyBase64String.replace("-----END PRIVATE KEY-----\n","");

            byte[] privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyBase64String);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } 
        catch (Exception e) {
            System.out.println("## (getAdminPrivateKey) ## Falha ao carregar ou decriptar a chave privada: " + e.getMessage());
        }

        return null;
    }

    private PrivateKey getUserPrivateKey(String login, String passphrase)
    {
        byte [] dbPrivatekey = Cofre.getUserPrivateKey(login);

        // passphrase = "admin";

        try {
            // 1. Gera uma chave secreta AES para descriptografar a chave privada
            SecureRandom secureRandom = SecureRandom.getInstance(PRNG_ALGORITHM);
            secureRandom.setSeed(passphrase.getBytes(StandardCharsets.UTF_8));

            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, secureRandom); // AES-256
            SecretKey aesKey = keyGen.generateKey();
            
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            
            // 2. Descriptografa a chave privada
            byte[] decryptedPrivateKey = cipher.doFinal(dbPrivatekey);

            String privateKeyBase64String = new String(decryptedPrivateKey);
            privateKeyBase64String = privateKeyBase64String.replace("-----BEGIN PRIVATE KEY-----\n","");
            privateKeyBase64String = privateKeyBase64String.replace("-----END PRIVATE KEY-----\n","");

            byte[] privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyBase64String);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } 
        catch (Exception e) {
            System.out.println("### (getUserPrivateKey) ### Falha ao carregar ou decriptar a chave privada: " + e.getMessage());
        }

        return null;
    }

    private PublicKey getAdminPublicKey()
    {
        String certificatePEM = Cofre.getDBAdminCert();

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            Certificate certificate = certFactory.generateCertificate(new ByteArrayInputStream(certificatePEM.getBytes(StandardCharsets.UTF_8)));
            return certificate.getPublicKey();
        } 
        catch (Exception e) {
            System.out.println("Falha ao carregar o certificado: " + e.getMessage());
        }

        return null;
    }

    public List<String[]> listFiles(String login, String fraseSecretaUsuarioAtual, String fraseSecretaAdmin) {        
        PrivateKey adminPrivateKey = getAdminPrivateKey(fraseSecretaAdmin);

        if (adminPrivateKey == null) {
            System.out.println("####### Falha ao carregar a chave privada do admin.");
            return null;
        }
        System.out.println("Chave privada do admin: " + adminPrivateKey.toString());

        File fileEnv = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(INDEX_ENV_PATH).toString());
        byte[] semente = null;
        SecretKey aesKey = null;
        try (FileInputStream fis = new FileInputStream(fileEnv)) {
            byte[] indexenv = new byte[(int) fileEnv.length()];
            fis.read(indexenv);

            //decriptando o arquivo de envelope digital
            // decriptando o arquivo de envelope digital com RSA
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, adminPrivateKey);
            semente = cipher.doFinal(indexenv);

            //gerando a chave AES
            SecureRandom prng = SecureRandom.getInstance(PRNG_ALGORITHM);
            prng.setSeed(semente);

            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, prng); 
            aesKey = keyGen.generateKey();
        }
        catch (Exception e) {
            System.out.println("Falha ao decriptar o arquivo de envelope digital: " + e.getMessage()); 
        }

        /*a assinatura digital do arquivo de índice é armazenada no arquivo index.asd
        (representação binária da assinatura digital) */
        File fileAsd = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(INDEX_ASD_PATH).toString());
        byte[] signatureBytes = null;

        try (FileInputStream fis = new FileInputStream(fileAsd)) {
            signatureBytes = new byte[(int) fileAsd.length()];
            fis.read(signatureBytes);
        } 
        catch (IOException e) {
            System.out.println("Falha ao ler o arquivo de assinatura digital: " + e.getMessage()); 
        }

        /*deve-se
        decriptar o arquivo de índice da pasta fornecida (cifra AES, modo ECB e enchimento PKCS5),
        chamado index.enc */
        File file = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(INDEX_ENC_PATH).toString());
        byte[] indexencDecripted = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] indexenc = new byte[(int) file.length()];
            fis.read(indexenc);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            indexencDecripted = cipher.doFinal(indexenc);
        }
        catch(Exception e) {
            System.out.println("Falha ao decriptar o arquivo de índice: " + e.getMessage()); 
        }
        
        //chave publica do usuario administrador 
        PublicKey adminPublicKey = getAdminPublicKey();

        //validando a assinatura digital
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(adminPublicKey);
            signature.update(indexencDecripted);
            boolean isValid = signature.verify(signatureBytes);

            if (!isValid) {
                System.out.println("Assinatura digital inválida.");
            }
        } 
        
        catch (Exception e) {
            System.out.println("Erro ao validar a assinatura digital: " + e.getMessage());
        }


        /*3. e listar o
        conteúdo do arquivo de índice apresentando APENAS os atributos dos arquivos (nome código,
        nome, dono e grupo) do usuário ou do grupo do usuário*/

        /*Formato : O arquivo de índice decriptado possui zero ou mais linhas
        formatadas da seguinte forma:
        NOME_CODIGO_DO_ARQUIVO<SP>NOME_SECRETO_DO_ARQUIVO<SP>DONO_ARQUIVO
        <SP><GRUPO_ARQUIVO><EOL>
        Onde:
        NOME_CODIGO_DO_ARQUIVO: caracteres alfanuméricos (nome código do arquivo).
        NOME_SECRETO_DO_ARQUIVO: caracteres alfanuméricos (nome original do arquivo).
        DONO_ARQUIVO: caracteres alfanuméricos (atributo do arquivo).
        GRUPO_ARQUIVO: caracteres alfanuméricos (atributo do arquivo).
        <SP> = caractere espaço em branco.
        <EOL> = caractere nova linha (\n). */

        List<String[]> arquivos = new ArrayList<>();
        try{
            String indexencDecriptedString = new String(indexencDecripted, "UTF-8");
            String[] lines = indexencDecriptedString.split("\n");

            // Exibir os atributos do arquivo
            System.out.printf("%-20s %-20s %-15s %-10s\n", "NOME_CODIGO_DO_ARQUIVO", "NOME_SECRETO_DO_ARQUIVO", "DONO_ARQUIVO", "GRUPO_ARQUIVO");
            System.out.println("----------------------------------------------------------------------------");

            for (String line : lines) {
                String[] attributes = line.split(" ");
                if (attributes.length >= 4) {
                    arquivos.add(attributes);
                    String nomeCodigo = attributes[0];
                    String nomeSecreto = attributes[1];
                    String donoArquivo = attributes[2];
                    String grupoArquivo = attributes[3];

                    // Exibir os atributos do arquivo
                    System.out.println("Nome Código: " + nomeCodigo);
                    System.out.println("Nome Secreto: " + nomeSecreto);
                    System.out.println("Dono Arquivo: " + donoArquivo);
                    System.out.println("Grupo Arquivo: " + grupoArquivo);
                    System.out.println("-----------------------------");
                }
            }

            return arquivos;
        }


        catch(Exception e) {
            System.out.println("Erro ao listar o conteudo do arquivo de indice: " + e.getMessage());
            return null;
        }

    }
    
    public boolean abrirArquivoSecreto(String nomeSecreto, String donoArquivo, String login, String fraseSecretaUsuario, String ext) { 
        if(!donoArquivo.equals(login)) {
            System.out.println("Usuario não tem permissão de acesso ao arquivo");
            return false;
        }

        //1. verificar a integridade e autenticidade do arquivo secreto;

        /*O envelope digital do arquivo secreto é
        armazenado no arquivo <NOME_CODIGO_DO_ARQUIVO>.env (protege a semente SHA1PRNG que gera a chave secreta
        AES)*/
        
        PrivateKey userPrivateKey = getUserPrivateKey(login, fraseSecretaUsuario);

        if (userPrivateKey == null) {
            System.out.println("123 ####### Falha ao carregar a chave privada do usuario.");
            return false;
        }

        String caminhoArquivoEnv = "Files\\" + nomeSecreto + ".env";
        File fileEnv = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(caminhoArquivoEnv).toString());
        byte[] semente = null;
        SecretKey aesKey = null;
        try (FileInputStream fis = new FileInputStream(fileEnv)) {
            byte[] arquivoEnv = new byte[(int) fileEnv.length()];
            fis.read(arquivoEnv);

            //decriptando o arquivo de envelope digital
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, userPrivateKey);
            semente = cipher.doFinal(arquivoEnv);

            //gerando a chave AES
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            prng.setSeed(semente);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, prng); 
            aesKey = keyGen.generateKey();
        }
        catch (Exception e) {
            System.out.println("(abrir Arquivo) ### Falha ao decriptar o arquivo de envelope digital: " + e.getMessage()); 
            return false;
        }

        /*a assinatura digital do arquivo secreto é armazenada no arquivo <NOME_CODIGO_DO_ARQUIVO>.asd
        (representação binária da assinatura digital) */
        String caminhoArquivoAsd = "Files\\" + nomeSecreto + ".asd";
        File fileAsd = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(caminhoArquivoAsd).toString());
        byte[] signatureBytes = null;
        try (FileInputStream fis = new FileInputStream(fileAsd)) {
            signatureBytes = new byte[(int) fileAsd.length()];
            fis.read(signatureBytes);
        } 
        
        catch (IOException e) {
            System.out.println("Falha ao ler o arquivo de assinatura digital: " + e.getMessage());
            return false; 
        }

        //decriptar o arquivo secreto
        String caminhoArquivo = "Files\\" + nomeSecreto + ".enc";
        File file = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(caminhoArquivo).toString());
        byte[] arquivoEncDecripted = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] arquivoEnc = new byte[(int) file.length()];
            fis.read(arquivoEnc);

            //decriptando o arquivo secreto
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey); // use a chave AES gerada da semente!
            arquivoEncDecripted = cipher.doFinal(arquivoEnc);
        }

        catch(Exception e) {
            System.out.println("Falha ao decriptar o arquivo secreto: " + e.getMessage()); 
            return false;
        }

        
        //gravando o arquivo decriptado em um novo arquivo com o nome secreto
        String caminhoArquivoDecriptado = "Files\\" + ext;
        File fileDecriptado = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(caminhoArquivoDecriptado).toString());
        try (FileOutputStream fos = new FileOutputStream(fileDecriptado, false)) {
            // String conteudo = new String(arquivoEncDecripted, "UTF-8");
            fos.write(arquivoEncDecripted);
            System.out.println("Arquivo decriptado (texto) com sucesso: " + fileDecriptado.getAbsolutePath());
            return true;
        } 
        catch (IOException e) {
            System.out.println("Falha ao gravar o arquivo decriptado: " + e.getMessage());
            return false;
        }
    }
}