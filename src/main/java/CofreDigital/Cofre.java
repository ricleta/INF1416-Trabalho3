
/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import java.util.Scanner;

/*import javax.sql.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;*/

import CofreDigital.DB.DB;
import CofreDigital.UI.TelaPrincipal;

public class Cofre{
    public static void main(String[] args) throws Exception {   
        
        //when running for the 1st time, register admin
    
        //if not then verify admin credentials
    
        //if everything is ok, start to register users
    
        //1st step
    
        //2nd step
    
        //3rd step
    
        //if everything is ok, show menu
        
        DB db = new DB();

        while (true) {
          TelaPrincipal tela = new TelaPrincipal("admin", "admins", "Admin", 1);
          tela.setVisible(true);
        }
    }
}

//one method for each authentication step?



