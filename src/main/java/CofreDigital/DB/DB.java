/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.DB;

import CofreDigital.SecurityEncryption.Base32;
import CofreDigital.Users.User;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.IOException;

import java.security.SecureRandom;

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
            "tokenKey BLOB NOT NULL, " +
            "grupo TEXT NOT NULL, " +
            "total_acessos INT NOT NULL," + 
            "FOREIGN KEY (KID) REFERENCES Chaveiro(KID) " +
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

    public boolean isAdminRegistered() {
        try (Connection con = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT COUNT(*) FROM Usuarios WHERE email = 'admin@inf1416.puc-rio.br'";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query)) {
                return rs.getInt(1) > 0;
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        catch (SQLException e) {
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
        // System.out.println("Adicionando chaveiro e usuario ao banco de dados...");
        int kid = addKeyChain(chavePrivada, certificadoDigital);

        if (kid == -1) {
            System.out.println("Erro ao adicionar chaveiro.");
            return;
        }

        //gerar token_key do TOTP e criptografar com chave AES-256 gera com SHA1-PRNG da senha pessoal
        byte[] encryptedtokenKey = generateEncryptedTokenKey(user.getSenhaPessoal(), user.getBase32TokenKey());

        String queryInsertUser = "INSERT INTO Usuarios (email, senhaPessoal, KID, tokenKey, grupo, total_acessos) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(queryInsertUser)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getHashSenhaPessoal());
            pstmt.setInt(3, kid);
            pstmt.setBytes(4, encryptedtokenKey);
            pstmt.setString(5, user.getGrupo());
            pstmt.setInt(6, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in addUser: " + e.getMessage());
        }
    }

    public void updateAccessCount(User user) {
        user.setTotal_de_acessos(user.getTotal_de_acessos() + 1);

        String queryUpdateAccessCount = "UPDATE Usuarios SET total_acessos = total_acessos + 1 WHERE email = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(queryUpdateAccessCount)) {
            pstmt.setString(1, user.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in updateAccessCount: " + e.getMessage());
        }
    }

    public void addLog(String dataHora, int uid, String mid) {
        String queryInsertLog = "INSERT INTO Registros (dataHora, UID, MID) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(queryInsertLog)) {
            pstmt.setString(1, dataHora);
            pstmt.setInt(2, uid);
            pstmt.setString(3, mid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in addLog: " + e.getMessage());
        }
    }

    public String getMessage(String mid) {
        String query = "SELECT conteudo FROM Mensagens WHERE MID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, mid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("conteudo");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    public int addKeyChain(byte[] chavePrivada, String certificadoDigital) {
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
        String query = "SELECT COUNT(*) FROM Usuarios WHERE email = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, user.getEmail());
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    public User getUser(String email) {
        String query = "SELECT * FROM Usuarios WHERE email = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // For debugging, access after rs.next()
                byte[] tokenKeyBytes = rs.getBytes("tokenKey"); // Read bytes once
                
                return new User(rs.getString("email"), rs.getString("senhaPessoal"), tokenKeyBytes, rs.getString("grupo"), rs.getInt("total_acessos"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    public String getUserPasswordHash(String email) {
        String query = "SELECT senhaPessoal FROM Usuarios WHERE email = ?";
        try (Connection con = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("senhaPessoal");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        return null; // Return null if the user is not found
    }

    public String generateTokenKey()
    {
        final int TOKEN_KEY_SIZE = 20; // 20 bytes for TOTP key

        try {             
            // Generate a random 20-byte key for TOTP
            byte[] tokenKey = new byte[TOKEN_KEY_SIZE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(tokenKey);
                
            // Codifica a tokenKey em base32
            Base32 base32 = new Base32(Base32.Alphabet.BASE32, true, false);    
            String base32TokenKey = base32.toString(tokenKey);
            System.out.println("Base32 Token Key: " + base32TokenKey);
            
            return base32TokenKey;
        } catch (Exception e) {
            System.err.println("Error generating token key: " + e.getMessage());
        }
        return null;
    }

    private byte[] generateEncryptedTokenKey(String senhaPessoal, String base32TokenKey) {
        final int AES_KEY_SIZE = 256;
        final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
        final String AES_ALGORITHM = "AES";
        final String PRNG_ALGORITHM = "SHA1PRNG";
        
        try {
            Base32 base32 = new Base32(Base32.Alphabet.BASE32, true, false);  
            byte[] tokenKey = base32.fromString(base32TokenKey);

            // Gera chave aes usando senha pessoal e SHA1-PRNG
            SecureRandom secureRandom = SecureRandom.getInstance(PRNG_ALGORITHM);
            secureRandom.setSeed(senhaPessoal.getBytes(StandardCharsets.UTF_8));
    
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, secureRandom); // AES-256
            SecretKey aesKey = keyGen.generateKey();

            // Encripta a tokenKey usando AES/ECB/PKCS5Padding
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] encryptedTokenKey = cipher.doFinal(tokenKey);
    
            // Print the encrypted token key
            System.out.println("Encrypted Token Key: " + base32.toString(encryptedTokenKey));

            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedTokenKey = cipher.doFinal(encryptedTokenKey);
            System.out.println("Decrypted Token Key: " + base32.toString(decryptedTokenKey));

            return encryptedTokenKey;
        } catch (Exception e) {
            System.err.println("Error generating token key: " + e.getMessage());
        }

        return null;
    }

    public String decryptTokenKey(byte[] encryptedTokenKey, String senhaPessoal) {
        final int AES_KEY_SIZE = 256;
        final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
        final String AES_ALGORITHM = "AES";
        final String PRNG_ALGORITHM = "SHA1PRNG";

        try {
            System.out.println("Senha pessoal: " + senhaPessoal);
            // senhaPessoal = "12345678";
            // Gera chave aes usando senha pessoal e SHA1-PRNG
            SecureRandom secureRandom = SecureRandom.getInstance(PRNG_ALGORITHM);
            secureRandom.setSeed(senhaPessoal.getBytes(StandardCharsets.UTF_8));

            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, secureRandom); // AES-256
            SecretKey aesKey = keyGen.generateKey();

            // Decripta a tokenKey usando AES/ECB/PKCS5Padding
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedTokenKey = cipher.doFinal(encryptedTokenKey);

            Base32 base32 = new Base32(Base32.Alphabet.BASE32, true, false);
            System.out.println("Decrypted Token Key: " + base32.toString(decryptedTokenKey));
            return base32.toString(decryptedTokenKey);
        } catch (Exception e) {
            System.err.println("Error decrypting token key: " + e.getMessage());
        }
        return null;
    }

    public int getNumeroUsuariosCadastrados() {
        String query = "SELECT COUNT(*) FROM Usuarios";
        try (Connection con = DriverManager.getConnection(DB_URL);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }

    public String[] getGrupos() {
        String query = "SELECT nome FROM Grupos";
        try (Connection con = DriverManager.getConnection(DB_URL);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            String[] grupos = new String[GROUP_NAMES.length];
            int i = 0;
            while (rs.next()) {
                grupos[i++] = rs.getString("nome");
            }
            return grupos;
        } catch (SQLException e) {
            System.err.println("Error getting groups: " + e.getMessage());
        }
        return null;
    }
}