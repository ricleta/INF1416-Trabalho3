
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
import CofreDigital.UI.TelaSaida;

import CofreDigital.SecurityEncryption.Base32;

public class Cofre{
    private static DB db;
    private static TelaPrincipal telaPrincipal;
    private static TelaCadastro telaCadastro;
    private static TelaLogin1 telaLogin1;
    private static TelaLogin2 telaLogin2;
    private static TelaLogin3 telaLogin3;
    private static TelaQRCode telaQRCode;
    private static TelaConfirmacao telaConfirmacao;
    private static TelaSaida telaSaida;

    public static void main(String[] args) throws Exception {   
        db = new DB();
        // TelaPrincipal tela = new TelaPrincipal("admin", "admins", "Admin", 1);
        // tela.setVisible(true);

        if (db.isAdminRegistered()) {
            // System.out.println("Admin já cadastrado.");
            showLoginScreen();
        } else {
            // System.out.println("Admin não cadastrado.");
            TelaCadastro tela = new TelaCadastro("admin", "admins", "Admin", 1, getGrupos());
            tela.setVisible(true);
        }
        // TelaCadastro tela = new TelaCadastro("admin", "admins", "Admin", 1, getGrupos());
        // tela.setVisible(true);

        // TelaLogin1 tela1 = new TelaLogin1();
        // tela1.setVisible(true);

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

      cadastro.cadastrarUsuario(certificado, chavePrivada, fraseSecreta, senha, grupo);
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

    public static boolean validaTOTP(User user, String totpCode) {
        if (user.getEncryptedtokenKey() == null) {
            System.out.println("Usuário não encontrado.");
            return false;
        }
        final long TIME_STEP = 30;

        Base32 base32 = new Base32(Base32.Alphabet.BASE32, true, false);   
        System.out.println("Chave TOTP: " + base32.toString(user.getEncryptedtokenKey()));
        String base32TokenKey = null;
        System.out.println("Senha pessoal: " + user.getHashSenhaPessoal());
        try {
          base32TokenKey = db.decryptTokenKey(user.getEncryptedtokenKey(), user.getHashSenhaPessoal());
        }
        catch (Exception e) {
          System.out.println("Erro ao descriptografar a chave TOTP: " + e.getMessage());
          return false;
        }

        String code = null;
        try {
          TOTP totp = new TOTP(base32TokenKey, TIME_STEP);
          long timeInterval = System.currentTimeMillis() / 1000 / TIME_STEP;
          code = totp.generateCode(timeInterval);
        }
        catch (Exception e) {
          System.out.println("Erro ao gerar o código TOTP: " + e.getMessage());
          return false;
        }

        if (code.equals(totpCode)) {
            // System.out.println("Código TOTP válido.");
            return true;
        } else {
            // System.out.println("Código TOTP inválido.");
            return false;
        }
    }

    public static void showMenuPrincipal(User user) {
        db.updateAccessCount(user);
        TelaPrincipal tela = new TelaPrincipal(user);
        tela.setVisible(true);
    }

    public static void showLoginScreen() {
        TelaLogin1 tela = new TelaLogin1();
        tela.setVisible(true);
    }

    public static void showExitScreen(User user) {
      if (user == null) {
        System.out.println("Error: User is null in showTelaCadastro");
        return; // Or show an error dialog
    }
        TelaSaida tela = new TelaSaida(user);
        tela.setVisible(true);
    }

    public static void showTelaCadastro(User usuario) {
      if (usuario == null) {
        System.out.println("Error: User is null in showTelaCadastro");
        return; // Or show an error dialog
    }
        TelaCadastro telaCadastro = new TelaCadastro(usuario, getGrupos());
        telaCadastro.setVisible(true);
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

    public static String[] getGrupos() {
        return db.getGrupos();
    }
}




