package CofreDigital.Users;

import CofreDigital.DB.DB;
import CofreDigital.SecurityEncryption.KeyValidator;
import CofreDigital.Cofre;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.HashMap;
import java.util.Map;

public class UserRegistrationService {
    private static final int SALT_COST = 8;
    private static final int SALT_LENGTH = 16;
    private DB db;
    private KeyValidator keyValidator;

    public UserRegistrationService(DB db) {
        this.db = db;
        this.keyValidator = new KeyValidator();
        Security.addProvider(new BouncyCastleProvider());
    }

    public boolean doPasswordsMatch(String senha, String confirmacaoSenha) {
        if (!senha.equals(confirmacaoSenha)) {
            System.out.println("As senhas não coincidem.");
            return false;
        }
        return true;
    }

    public Map<String, String> getCertificateData(String caminhoCertificado) {
        Map<String, String> certificateData = new HashMap<>();
        try {
            X509Certificate certificate = keyValidator.getCertificate(caminhoCertificado);
            String certVersion = certificate.getVersion() == 3 ? "V3" : "V1";
            String certSerialNumber = certificate.getSerialNumber().toString();
            String certExpirationDate = certificate.getNotAfter().toString();
            String certSignatureAlgotithm = certificate.getSigAlgName();
            String certIssuer = certificate.getIssuerX500Principal().getName();
            String certSubject = certificate.getSubjectX500Principal().getName();
            String certEmail = keyValidator.getLoginFromCertificate(certificate);

            certificateData.put("Version", certVersion);
            certificateData.put("Serial Number", certSerialNumber);
            certificateData.put("Expiration Date", certExpirationDate);
            certificateData.put("Signature Algorithm", certSignatureAlgotithm);
            certificateData.put("Issuer", certIssuer);
            certificateData.put("Subject", certSubject);
            certificateData.put("Email", certEmail);

        } catch (Exception e) {
            System.out.println("Erro ao obter os dados do certificado: " + e.getMessage());
        }
        return certificateData;
    }

    public void cadastrarUsuario(String caminhoCertificado, String caminhoChavePrivada, String fraseSecreta, String senha, String grupo) {        
        try {
            boolean isValid = keyValidator.validatePrivateKey(caminhoCertificado, caminhoChavePrivada, fraseSecreta);
            if (!isValid) {
                System.out.println("Chave privada ou frase secreta inválida. Tente novamente.");
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Erro ao validar a chave privada: " + e.getMessage());
            return; 
        }
            //assinatura digital foi verificada com sucesso

            byte[] privateKeyBytes = null;
            try {
                privateKeyBytes = Files.readAllBytes(Paths.get(caminhoChavePrivada));
            }
            catch (Exception e) {
                System.out.println("Erro ao ler a chave privada: " + e.getMessage());
                return;
            }

            String certificatePEM = null;
            X509Certificate certificate = null;
            try {
                certificatePEM = new String(Files.readAllBytes(Paths.get(caminhoCertificado)));
                certificate = keyValidator.getCertificate(caminhoCertificado);

                System.out.println("Certificado: " + certificate);
            }
            catch (Exception e) {
                System.out.println("Erro ao ler o certificado: " + e.getMessage());
                return;
            }


            // obter login do usuario usando o email no certificado
            String login = keyValidator.getLoginFromCertificate(certificate);
            System.out.println("Login: " + login);

            // Gerar um salt aleatório
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] salt = new byte[SALT_LENGTH]; // O tamanho do salt para bcrypt é 16 bytes
            random.nextBytes(salt);

            // Gerar o hash da senha usando o salt aleatório e o custo especificado
            String hashedPassword = OpenBSDBCrypt.generate(senha.toCharArray(), salt, SALT_COST);

            // Cria tokenKey para o TOTP
            String base32TokenKey = db.generateTokenKey();

            User user = new User(login, senha, hashedPassword, base32TokenKey, fraseSecreta, grupo);

            // Verifica se o usuário já existe
            if (db.userExists(user)) {
                System.out.println("Usuário já existe.");
                return;
            }

            System.out.println("User email: " + user.getEmail());
            System.out.println("User senha: " + user.getHashSenhaPessoal());
            System.out.println("User frase secreta: " + user.getFraseSecreta());

            //store user + store encrypted private key and PEM certificate in chaveiro table

            try {
                db.addUser(user, privateKeyBytes, certificatePEM);
            }
            catch (Exception e) {
                System.out.println("Erro ao cadastrar o usuário: " + e.getMessage());
                return;
            }

            // Se usuario foi cadastrado com sucesso, exibe tokenKey
            Cofre.showTokenKey(base32TokenKey, user.getEmail());
        }

    public boolean isPasswordCorrect(String db_password, String attempted_password) {
        // Verifica se a senha fornecida corresponde à senha armazenada no banco de dados
        if (OpenBSDBCrypt.checkPassword(db_password, attempted_password.toCharArray())) {
            return true;
        } else {
            return false;
        }
    }
}