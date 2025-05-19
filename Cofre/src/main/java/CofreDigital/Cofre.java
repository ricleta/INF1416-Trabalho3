/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

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
import CofreDigital.UI.TelaConsulta;

import CofreDigital.SecurityEncryption.Base32;
import CofreDigital.SecurityEncryption.KeyValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


public class Cofre{
    private static DB db;

    public static void main(String[] args) throws Exception {        
        db = new DB();
        
        addLogToDB("1001"); // Sistema iniciado

        if (db.isAdminRegistered()) {
            // System.out.println("Admin já cadastrado.");
            addLogToDB("admin@inf1416.puc-rio.br", "1006");
            showLoginScreen();
        } 
        
        else {
            // System.out.println("Admin não cadastrado.");
            
            addLogToDB( "admin@inf1416.puc-rio.br", "1005");
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
        
        addLogToDB( email, "2001");

        if(db.getUser(email) == null) {
          
          addLogToDB(email , "2005");
        }
        return db.getUser(email);
    }

    public static void authenticatePassword(User user) { 
        addLogToDB(user.getEmail(), "2002");
       
        addLogToDB(user.getEmail(), "3001");

        TelaLogin2 tela = new TelaLogin2(user);
        tela.setVisible(true);
    }

    public static void authenticateTOTP(User user) {
        
        addLogToDB( user.getEmail(), "3002");

        
        addLogToDB( user.getEmail(), "4001");
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
            
            addLogToDB( user.getEmail(), "4003");
            return true;
        } else {
            // System.out.println("Código TOTP inválido.");
            return false;
        }
    }

    public static void showMenuPrincipal(User user) {
      addLogToDB( user.getEmail(), "1003");
        
        addLogToDB( user.getEmail(), "5001");
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

        addLogToDB( user.getEmail(), "1004");
        TelaSaida tela = new TelaSaida(user);
        tela.setVisible(true);
    }

    public static void showTelaCadastro(User usuario) {
      if (usuario == null) {
        System.out.println("Error: User is null in showTelaCadastro");
        return; // Or show an error dialog
      }

      addLogToDB( usuario.getEmail(), "6001");
      
      TelaCadastro telaCadastro = new TelaCadastro(usuario, getGrupos());
      telaCadastro.setVisible(true);
    }

    public static void showTelaConsulta(User usuario) {
      if (usuario == null) {
        System.out.println("Error: User is null in showTelaConsulta");
        return; // Or show an error dialog
      }

      addLogToDB( usuario.getEmail(), "7001");
      
      TelaConsulta tela = new TelaConsulta(usuario);
      tela.setVisible(true);
    }

    public static String isPasswordCorrect(String email, ArrayList<String> possiblePasswords) {
        String senha_db = db.getUserPasswordHash(email);
        UserRegistrationService cadastro = new UserRegistrationService(db);

        for (String password : possiblePasswords) {          
          if (cadastro.isPasswordCorrect(senha_db, password)) {
            
            addLogToDB( email, "3003");
              return password;
          }
        }

        
        addLogToDB( email, "6003");
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

    public static List<String[]> listFiles(User usuario, String adminPassphrase) {
        db.updateTotalConsultas(usuario);
        KeyValidator keyValidator = new KeyValidator();

        List<String[]> files = keyValidator.listFiles(usuario.getEmail(), usuario.getFraseSecreta(), adminPassphrase);
        return files;
    }

    public static byte[] getDBAdminPrivateKey()
    {
      return db.getAdminPrivateKey();
    }

    public static String getDBAdminCert()
    {
      return db.getAdminCert();
    }

    public static void abrirArquivoSecreto(String nomeArquivo, String fileOwner, String loginNameAtual, String fraseSecretaUsuario, String extensao) {
        KeyValidator keyValidator = new KeyValidator();
        keyValidator.abrirArquivoSecreto(nomeArquivo, fileOwner, loginNameAtual, fraseSecretaUsuario, extensao);
    }

    public static byte [] getUserPrivateKey(String email) {
        byte [] privatekey = db.getUserPrivateKey(email);

        return privatekey;
    }

    public static void addLogToDB(String message_id) {
      LocalDate date = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
      LocalTime time = LocalTime.now(ZoneId.of("America/Sao_Paulo"));
      String dateTime = date + " " + time;

      System.out.println("Data e hora: " + dateTime + " - Mensagem: " + message_id);

      db.addLog(dateTime, message_id);
    }

    public static void addLogToDB(String email, String message_id) {
      LocalDate date = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
      LocalTime time = LocalTime.now(ZoneId.of("America/Sao_Paulo"));
      String dateTime = date + " " + time;

      System.out.println("Data e hora: " + dateTime + " - Mensagem: " + message_id + " - Email: " + email);

      db.addLog(dateTime, email, message_id);
    }
}
