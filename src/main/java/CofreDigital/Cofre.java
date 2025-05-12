
/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*import javax.sql.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;*/

import CofreDigital.DB.DB;
import CofreDigital.Users.Cadastro;
import CofreDigital.UI.TelaPrincipal;
import CofreDigital.UI.TelaCadastro;

public class Cofre{
    private static DB db;

    public static void main(String[] args) throws Exception {   
        
        //when running for the 1st time, register admin
    
        //if not then verify admin credentials
    
        //if everything is ok, start to register users
    
        //1st step
    
        //2nd step
    
        //3rd step
    
        //if everything is ok, show menu
        db = new DB();
        // TelaPrincipal tela = new TelaPrincipal("admin", "admins", "Admin", 1);
        // tela.setVisible(true);

        TelaCadastro tela = new TelaCadastro("admin", "admins", "Admin", 1, new String[]{"Grupo1", "Grupo2"});
        tela.setVisible(true);
    }

    public static void cadastrarUsuario(String certificado, String chavePrivada, String fraseSecreta, String grupo, String senha, String confirmacaoSenha) {
      // TODO Auto-generated method stub
      db = new DB();

      Cadastro cadastro = new Cadastro(db);

      cadastro.cadastrarUsuario(certificado, chavePrivada, fraseSecreta, grupo, senha, confirmacaoSenha);
    }
}

//one method for each authentication step?



