
/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*import javax.sql.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;*/

import CofreDigital.DB.DB;
import CofreDigital.Users.UserRegistrationService;
import CofreDigital.UI.TelaPrincipal;
import CofreDigital.UI.TelaCadastro;
import CofreDigital.UI.TelaConfirmacao;
import CofreDigital.UI.TelaLogin1;
import CofreDigital.UI.TelaLogin2;

public class Cofre{
    private static DB db;

    public static void main(String[] args) throws Exception {   
        db = new DB();
        // TelaPrincipal tela = new TelaPrincipal("admin", "admins", "Admin", 1);
        // tela.setVisible(true);

        // TelaCadastro tela = new TelaCadastro("admin", "admins", "Admin", 1, new String[]{"Grupo1", "Grupo2"});
        // tela.setVisible(true);

        TelaLogin1 tela = new TelaLogin1();
        tela.setVisible(true);

        // TelaLogin2 tela = new TelaLogin2("admin@inf1416.puc-rio.br");
        // tela.setVisible(true);
    }

    public static void confirmaCadastro(String pathCertificado, String chavePrivada, String fraseSecreta, String grupo, String senha, String confirmacaoSenha) {
        if (!senha.equals(confirmacaoSenha)) {
            System.out.println("As senhas não coincidem.");
            return;
        }

        db = new DB();

        UserRegistrationService cadastro = new UserRegistrationService(db);

        Map<String, String> certificateData = cadastro.getCertificateData(pathCertificado);

        for (Map.Entry<String, String> entry : certificateData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        TelaConfirmacao tela = new TelaConfirmacao(certificateData, pathCertificado, chavePrivada, fraseSecreta, grupo, senha);
        tela.setVisible(true);
    }
    
    public static void cadastrarUsuario(String certificado, String chavePrivada, String fraseSecreta, String grupo, String senha) {
      db = new DB();

      UserRegistrationService cadastro = new UserRegistrationService(db);

      cadastro.cadastrarUsuario(certificado, chavePrivada, fraseSecreta, senha);
    }

    public static void checaEmailValido(String email) {
        db = new DB();
 
        if (db.userExists(email.trim())) {
            System.out.println("Email valido.");
        } else {
            System.out.println("Nao encontrado.");
        }
    }

    public static void authenticatePassword(String email) {
        db = new DB();

        TelaLogin2 tela = new TelaLogin2(email);
        tela.setVisible(true);
    }

    public static boolean isPasswordCorrect(String email, ArrayList<String> possiblePasswords) {
        db = new DB();
        String senha_db = db.getUserPasswordHash(email);
        UserRegistrationService cadastro = new UserRegistrationService(db);

        for (String password : possiblePasswords) {          
          if (cadastro.isPasswordCorrect(senha_db, password)) {
              return true;
          }
        }
        return false;
    }
}




