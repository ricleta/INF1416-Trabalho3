/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.DB;

import CofreDigital.Users.User;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.IOException;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:cofre.db";
    private static final String CSV_FILE_PATH = "/CofreDigital/DB/mensagens.csv";
    private static final String [] GROUP_NAMES = {"admins", "usuarios"};
    private static final String [] GROUP_IDS = {"1", "2"}; // IDs dos grupos, 1 para admins e 2 para usuarios


    public DB() {

        try (Connection con = DriverManager.getConnection(DB_URL))
        {
            if (con != null){
                System.out.println("Connected");  
            }                       
            else{
                System.out.println("Not Connected");
                
            }
            
            setupTables(con);
        }
        catch (Exception e) {
            // Handle any errors that may have occurred
            System.err.println("Error: " + e.getMessage());
        }
    }

    // TODO
    // Verifica se campos das tabelas estão corretos, especialmente os tipos
    private void setupTables(Connection con) {
        /*
            Usamos TEXT para MID
            Enquanto usamos INTEGER PRIMARY KEY para o UID, KID, GID e RID 
            porque SQLite os incrementa automaticamente por default. 
        */
        createUserTable(con);
        createKeyChainTable(con);
        createGroupsTable(con);
        createMessageTable(con);
        createLogTable(con);

        // se a tabela de mensagens estiver vazia, preenche-la com mensagens do .csv
        if (isMessagesTableEmpty(con)) {
            System.out.println("Tabela de mensagens vazia, preenchendo com dados do CSV...");
            fillMessagesTable(con);
        }

        // se a tabela de grupos estiver vazia, preenche-la com grupos default
        if (isGroupsTableEmpty(con)) {
            System.out.println("Tabela de grupos vazia, preenchendo com grupos default...");
            fillGroupsTable(con);
        }
    }

    private void createUserTable(Connection con) {
        String queryCreateUser =
            "CREATE TABLE IF NOT EXISTS Usuarios (" +
            "UID INTEGER PRIMARY KEY, " +
            "email TEXT NOT NULL, " +
            "senhaPessoal TEXT NOT NULL, " +
            "KID INTEGER NOT NULL, " +
            "token TEXT NOT NULL, " +
            "FOREIGN KEY (KID) REFERENCES Chaveiro(KID)" +
            ");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(queryCreateUser);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createKeyChainTable(Connection con) {
        // PEM é texto em base64, então usamos TEXT
        // chavePrivada é um BLOB porque está em binário
        String queryCreateKeyChain =
            "CREATE TABLE IF NOT EXISTS Chaveiro (" +
            "KID INTEGER PRIMARY KEY, " +
            "certificadoDigital TEXT NOT NULL, " +
            "chavePrivada BLOB NOT NULL " +
            ");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(queryCreateKeyChain);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createGroupsTable(Connection con) {
        String queryCreateGroups =
            "CREATE TABLE IF NOT EXISTS Grupos (" +
            "GID INTEGER PRIMARY KEY, " +
            "nome TEXT" +
            ");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(queryCreateGroups);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createMessageTable(Connection con) {
        String queryCreateMessages =
            "CREATE TABLE IF NOT EXISTS Mensagens (" +
            "MID TEXT PRIMARY KEY, " +
            "conteudo TEXT NOT NULL" +
            ");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(queryCreateMessages);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void createLogTable(Connection con) {
        String queryCreateLogs =
            "CREATE TABLE IF NOT EXISTS Registros (" +
            "RID INTEGER PRIMARY KEY, " +
            "dataHora TEXT NOT NULL, " +
            "UID INTEGER, " +
            "MID TEXT, " +
            "FOREIGN KEY (UID) REFERENCES Usuarios(UID), " +
            "FOREIGN KEY (MID) REFERENCES Mensagens(MID)" +
            ");";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(queryCreateLogs);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private boolean isMessagesTableEmpty(Connection con) {
        String query = "SELECT EXISTS (SELECT 1 FROM Mensagens LIMIT 1)";

        try (Statement stmt = con.createStatement()) {
            // Get the count of rows in the Mensagens table
            ResultSet rs = stmt.executeQuery(query);

            // Retorna falso se pelo menos uma linha existe
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    private boolean isGroupsTableEmpty(Connection con) {
        String query = "SELECT EXISTS (SELECT 1 FROM Grupos LIMIT 1)";

        try (Statement stmt = con.createStatement()) {
            // Get the count of rows in the Grupos table
            ResultSet rs = stmt.executeQuery(query);

            // Retorna falso se pelo menos uma linha existe
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    private void fillMessagesTable(Connection con) {
        String queryInsertMessage = "INSERT INTO Mensagens (MID, conteudo) VALUES (?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(queryInsertMessage);
                InputStream inputStream = getClass().getResourceAsStream(CSV_FILE_PATH);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            // Le mensagens do arquivo CSV e insere na tabela Mensagens
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", 2);
                String mid = values[0].strip();
                String conteudo = values[1].strip();
                conteudo = conteudo.replaceAll("\"", ""); // Remove aspas

                // Substitui caracteres especiais para tornar mais facil exibir mensagens depois
                // evitando confusao com tags XML ou HTML
                conteudo = conteudo.replaceAll("<", "{"); // Substitui < por {
                conteudo = conteudo.replaceAll(">", "}"); // Substitui > por }
                
                pstmt.setString(1, mid);
                pstmt.setString(2, conteudo);
                pstmt.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void fillGroupsTable(Connection con) {
        String queryInsertGroup = "INSERT INTO Grupos (GID, nome) VALUES (?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(queryInsertGroup)) {
            for (int i = 0; i < GROUP_NAMES.length; i++) {
                pstmt.setString(1, GROUP_IDS[i]);
                pstmt.setString(2, GROUP_NAMES[i]);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void addUser(User user, byte[] chavePrivada, String certificadoDigital) {
        System.out.println("Adicionando chaveiro e usuario ao banco de dados...");
        int kid = addKeyChain(user, chavePrivada, certificadoDigital);

        if (kid == -1) {
            System.out.println("Erro ao adicionar chaveiro.");
            return;
        }

        // TODO: Remove this and have the token generated correctly
        user.setToken("token");

        String queryInsertUser = "INSERT INTO Usuarios (email, senhaPessoal, KID, token) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(queryInsertUser)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getSenhaPessoal());
            pstmt.setInt(3, kid);
            pstmt.setString(4, user.getToken());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in addUser: " + e.getMessage());
        }
    }

    public int addKeyChain(User user, byte[] chavePrivada, String certificadoDigital) {
        String queryInsertKeyChain = "INSERT INTO Chaveiro (certificadoDigital, chavePrivada) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(queryInsertKeyChain)) {
            pstmt.setString(1, certificadoDigital);
            pstmt.setBytes(2, chavePrivada);
            pstmt.executeUpdate();

            // Get the last inserted KID
            String queryGetLastKid = "SELECT last_insert_rowid()";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(queryGetLastKid)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in addKeyChain: " + e.getMessage());
        }

        return -1; // Return -1 if there was an error
    }

    public boolean userExists(User user) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE email = ? AND senhaPessoal = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getSenhaPessoal());
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
}