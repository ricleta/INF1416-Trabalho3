
/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import java.util.ArrayList;
import java.util.Map;

import CofreDigital.DB.DB;
import CofreDigital.SecurityEncryption.TOTP;
import CofreDigital.Users.UserRegistrationService;
import CofreDigital.Users.User;
import CofreDigital.UI.TelaPrincipal;
import CofreDigital.UI.TelaCadastro;
import CofreDigital.UI.TelaConfirmacao;
import CofreDigital.UI.TelaLogin1;
import CofreDigital.UI.TelaLogin2;
import CofreDigital.UI.TelaLogin3;
import CofreDigital.UI.TelaQRCode;

public class Cofre{
    private static DB db;

    public static void main(String[] args) throws Exception {   
        db = new DB();
        // TelaPrincipal tela = new TelaPrincipal("admin", "admins", "Admin", 1);
        // tela.setVisible(true);

        TelaCadastro tela = new TelaCadastro("admin", "admins", "Admin", 1, new String[]{"Grupo1", "Grupo2"});
        tela.setVisible(true);

        TelaLogin1 tela1 = new TelaLogin1();
        tela1.setVisible(true);

        // TelaLogin2 tela = new TelaLogin2("admin@inf1416.puc-rio.br");
        // tela.setVisible(true);
    }

    public static void confirmaCadastro(String pathCertificado, String chavePrivada, String fraseSecreta, String grupo, String senha, String confirmacaoSenha) {
        if (!senha.equals(confirmacaoSenha)) {
            System.out.println("As senhas não coincidem.");
            return;
        }

        for (int i = 0; i < senha.length() - 1; i++) {
          if (Character.isDigit(senha.charAt(i)) && senha.charAt(i) == senha.charAt(i+1)) {
            System.out.println("A senha não pode conter uma sequência de números iguais.");
            return;
          }
        }


        UserRegistrationService cadastro = new UserRegistrationService(db);

        Map<String, String> certificateData = cadastro.getCertificateData(pathCertificado);

        for (Map.Entry<String, String> entry : certificateData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        TelaConfirmacao tela = new TelaConfirmacao(certificateData, pathCertificado, chavePrivada, fraseSecreta, grupo, senha);
        tela.setVisible(true);
    }
    
    public static void cadastrarUsuario(String certificado, String chavePrivada, String fraseSecreta, String grupo, String senha){ 

      UserRegistrationService cadastro = new UserRegistrationService(db);

      cadastro.cadastrarUsuario(certificado, chavePrivada, fraseSecreta, senha);
    }

    public static User checaEmailValido(String email) {
        return db.getUser(email);
    }

    public static void authenticatePassword(User user) {
        if (user == null) {
            System.out.println("Usuário não encontrado.");
        }
        TelaLogin2 tela = new TelaLogin2(user);
        tela.setVisible(true);
    }

    public static void authenticateTOTP(User user) {
        TelaLogin3 tela = new TelaLogin3(user);
        tela.setVisible(true);
    }

    public static void validaTOTP(User user, String totpCode) {
        if (user.getEncryptedtokenKey() == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        final long TIME_STEP = 30;

        String base32TokenKey = null;
        System.out.println("Senha pessoal: " + user.getSenhaPessoal());
        try {
          // System.out.println("Encrypted token key: " + user.getEncryptedtokenKey().toString());
          base32TokenKey = db.decryptTokenKey(user.getEncryptedtokenKey(), user.getSenhaPessoal());
        }
        catch (Exception e) {
          System.out.println("Erro ao descriptografar a chave TOTP: " + e.getMessage());
          return;
        }

        String code = null;
        try {
          TOTP totp = new TOTP(base32TokenKey, TIME_STEP);
          long timeInterval = System.currentTimeMillis() / 1000 / TIME_STEP;
          code = totp.generateCode(timeInterval);
        }
        catch (Exception e) {
          System.out.println("Erro ao gerar o código TOTP: " + e.getMessage());
          return;
        }

        if (code.equals(totpCode)) {
            System.out.println("Código TOTP válido.");
            // TelaPrincipal tela = new TelaPrincipal(user);
            // tela.setVisible(true);
        } else {
            System.out.println("Código TOTP inválido.");
        }
    }

    public static String isPasswordCorrect(String email, ArrayList<String> possiblePasswords) {
        String senha_db = db.getUserPasswordHash(email);
        UserRegistrationService cadastro = new UserRegistrationService(db);

        for (String password : possiblePasswords) {          
          if (cadastro.isPasswordCorrect(senha_db, password)) {
              return password;
          }
        }
        return null;
    }

    public static void showTokenKey(String base32TokenKey, String email)
    {
        TelaQRCode tela = new TelaQRCode(base32TokenKey, email);
        tela.setVisible(true);
    }
}




