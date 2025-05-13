package CofreDigital.Users;

import java.awt.RenderingHints.Key;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        
        // try {
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
            }
            catch (Exception e) {
                System.out.println("Erro ao ler a chave privada: " + e.getMessage());
                return;
            }

            String certificatePEM = null;
            try {
                certificatePEM = new String(Files.readAllBytes(Paths.get(caminhoCertificado)));

                System.out.println("Certificado PEM: " + certificatePEM);
            }
            catch (Exception e) {
                System.out.println("Erro ao ler o certificado: " + e.getMessage());
                return;
            }
            // Pegar email do usuario usando certificado
            // String email = KeyValidator.getEmailFromCertificate(certificatePEM);

            // obter login do usuario usando o email no certificado
            String login = "login"; // TODO: Obter o login do usuário

            User user = new User(login, senha, fraseSecreta);

            // Verifica se o usuário já existe
            if (db.userExists(user)) {
                System.out.println("Usuário já existe.");
                return;
            }

            //store user + store encrypted private key and PEM certificate in chaveiro table
            db.addUser(user, encryptedPrivateKey, certificatePEM);

        }

        // catch (Exception e) {
        //     System.out.println("Erro ao validar a chave privada: " + e.getMessage());
        //     return;
        // }

        /*  TODO: Get hash of the password and actual certificate
        byte[] chavePrivada = caminhoChavePrivada.getBytes();*/
    // }
}