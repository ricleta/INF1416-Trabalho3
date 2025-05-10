/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import java.sql.*;

public class DBconnect {
    public static void main(String[] args) {

        try{
            
            //aqui tem q usar o nome do driver
            Class.forName("org.sqlite.JDBC");

            //establish connection
            //os nomes provavelmente estão errados
            Connection con = DriverManager.getConnection("jdbc:sqlite:cofre.db");

            if (con != null){
                System.out.println("Connected");  
            }                       
            else{
                System.out.println("Not Connected");
                
            }
            
            Statement stmt = con.createStatement();

            createTable(con, stmt);

            con.close(); 
        }

        catch (Exception e) {
            // Handle any errors that may have occurred
            System.err.println("Error: " + e.getMessage());
        }
    }


    public static void createTable(Connection con, Statement stmt) {
        try {
            
            Class.forName("org.sqlite.JDBC");

            //cria a tabela users
            String queryCreateUser = "CREATE TABLE IF NOT EXISTS users (" +
                " email TEXT NOT NULL," +
                " senhaPessoal TEXT NOT NULL," +
                " token TEXT NOT NULL)";
            stmt.executeUpdate(queryCreateUser);

            //cria a tabela admin - talvez nao precise
            String queryCreateAdmin = "CREATE TABLE IF NOT EXISTS admin (" +
                "email TEXT NOT NULL," +
                "senhaPessoal TEXT NOT NULL," +
                "token TEXT NOT NULL," +
                "fraseSecreta TEXT NOT NULL)";
            stmt.executeUpdate(queryCreateAdmin);

            stmt.close();
            con.close();
        } 
        
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void insertUser(Connection con, User u) {
        try {
            String queryInsertUser = "INSERT INTO users (email, senhaPessoal, token) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(queryInsertUser);
            pstmt.setString(1, u.getEmail());
            pstmt.setString(2, u.getSenhaPessoal());
            pstmt.setString(3, u.getToken());
            pstmt.executeUpdate();
            pstmt.close();
        } 
        
        catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void insertAdmin(Connection con, Admin a) {
        try {
            String queryInsertAdmin = "INSERT INTO admin (email, senhaPessoal, token, fraseSecreta) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(queryInsertAdmin);
            pstmt.setString(1, a.getEmail());
            pstmt.setString(2, a.getSenhaPessoal());
            pstmt.setString(3, a.getToken());
            pstmt.setString(4, a.getFraseSecreta());
            pstmt.executeUpdate();
            pstmt.close();
        } 
        
        catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}