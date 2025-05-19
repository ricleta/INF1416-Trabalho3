package LogRead;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LogReader extends JFrame {

    private JTable logTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private final Map<String, String> mensagens = new HashMap<>();

    public LogReader(String dbPath) {
        setTitle("Log Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();

        connectToDatabase(dbPath);
        loadMensagens();
        loadLogs();
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new String[]{"Data/Hora", "Mensagem"}, 0);
        logTable = new JTable(tableModel);
        logTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void connectToDatabase(String dbPath) {
        String url = "jdbc:sqlite:" + dbPath;
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void loadMensagens() {
        String query = "SELECT MID, conteudo FROM Mensagens";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mensagens.put(rs.getString("MID"), rs.getString("conteudo"));
            }

        } catch (SQLException e) {
            showError("Failed to load messages: " + e.getMessage());
        }
    }

    private void loadLogs() {
        String query = "SELECT R.dataHora, R.MID, U.email " +
                       "FROM Registros R " +
                       "LEFT JOIN Usuarios U ON R.UID = U.UID " +
                       "ORDER BY R.dataHora";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dataHora = rs.getString("dataHora");
                String mid = rs.getString("MID");
                String login = rs.getString("email");
                String conteudo = mensagens.get(mid);

                if (conteudo != null) {
                    if (login != null) {
                        conteudo = conteudo.replace("{login_name}", login);
                    }
                    tableModel.addRow(new Object[]{dataHora, conteudo});
                }
            }

        } catch (SQLException e) {
            showError("Failed to load logs: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            JOptionPane.showMessageDialog(null, "Usage: java -jar LogRead.jar <path_to_db_file>");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            LogReader viewer = new LogReader(args[0]);
            viewer.setVisible(true);
        });
    }
}
