package CofreDigital.Users;

import java.awt.RenderingHints.Key;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import CofreDigital.DB.DB;
import CofreDigital.SecurityEncryption.EncryptionUtil;
import CofreDigital.SecurityEncryption.KeyValidator;

public class Cadastro {
    private DB db;
    private KeyValidator keyValidator;

    public Cadastro(DB db) {
        this.db = db;
        this.keyValidator = new KeyValidator();
    }

    public void cadastrarUsuario(String caminhoCertificado, String caminhoChavePrivada, String fraseSecreta, String senha) {        
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
            byte[] encryptedPrivateKey = null;
            try {

                privateKeyBytes = Files.readAllBytes(Paths.get(caminhoChavePrivada));
                SecretKey encryptionKey = EncryptionUtil.generateKey();
                encryptedPrivateKey = EncryptionUtil.encrypt(privateKeyBytes, encryptionKey);

                System.out.println("Chave privada criptografada: " + encryptedPrivateKey.toString());
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

            User user = new User(login, senha, fraseSecreta);

            // Verifica se o usuário já existe
            if (db.userExists(user)) {
                System.out.println("Usuário já existe.");
                return;
            }

            System.out.println("User email: " + user.getEmail());
            System.out.println("User senha: " + user.getSenhaPessoal());
            System.out.println("User frase secreta: " + user.getFraseSecreta());

            //store user + store encrypted private key and PEM certificate in chaveiro table
            db.addUser(user, encryptedPrivateKey, certificatePEM);

        }
}