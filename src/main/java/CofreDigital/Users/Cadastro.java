package CofreDigital.Users;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.crypto.SecretKey;

import CofreDigital.Code.KeyValidator;
import CofreDigital.Code.EncryptionUtil;
import CofreDigital.DB.DB;

public class Cadastro {
    private DB db;

    public Cadastro(DB db) {
        this.db = db;
    }

    public void cadastrarUsuario(String login, String senha, String nome, String caminhoCertificado, String caminhoChavePrivada, String fraseSecreta) {
        User user = new User(login, senha, fraseSecreta);

        // Verifica se o usuário já existe
        if (db.userExists(user)) {
            System.out.println("Usuário já existe.");
            return;
        }

        /*A frase secreta da chave privada deve ser testada e a chave privada deve ser verificada com a
validação da assinatura digital de um array aleatório de 8192 bytes com a chave pública que
consta no certificado digital fornecido. */

        try {
            boolean isValid = KeyValidator.validatePrivateKey(caminhoCertificado, caminhoChavePrivada, fraseSecreta);
            if (!isValid) {
                System.out.println("Chave privada ou frase secreta inválida. Tente novamente.");
                return;
            }

            //assinatura digital foi verificada com sucesso

            String kid = UUID.randomUUID().toString();

            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(caminhoChavePrivada));
            SecretKey encryptionKey = EncryptionUtil.generateKey();
            byte[] encryptedPrivateKey = EncryptionUtil.encrypt(privateKeyBytes, encryptionKey);

            String certificatePEM = new String(Files.readAllBytes(Paths.get(caminhoCertificado)));

            //TODO : (store user + kid in user table) + (store encrypted private key + PEM certificate in chaveiro table)
            /*db.addUser(user, encryptedPrivateKey, caminhoCertificado);
            db.addKeyChain(user, encryptedPrivateKey, certificatePEM);
            db.addKid(user, kid); 
            System.out.println("Usuário cadastrado com sucesso.");*/

        }

        catch (Exception e) {
            System.out.println("Erro ao validar a chave privada: " + e.getMessage());
            return;
        }

        // TODO: Get hash of the password and actual certificate
        byte[] chavePrivada = caminhoChavePrivada.getBytes();
    }
}
