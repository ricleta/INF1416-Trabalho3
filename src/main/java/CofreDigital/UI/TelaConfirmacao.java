/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import java.util.Date;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Container;
import java.awt.FlowLayout;
import java.security.cert.X509Certificate;

import CofreDigital.Cofre;

public class TelaConfirmacao extends JFrame {
    /**
     * Constructs a confirmation screen for user registration.
     * This screen displays the certificate data provided and offers options to finalize or cancel the registration.
     *
     * @param certificateData A map containing the certificate details to be displayed for confirmation.
     *                        The keys represent the field names (e.g., "Subject", "Issuer") and
     *                        the values are the corresponding certificate information.
     * @param parthCertificado The file path to the user's digital certificate. This will be used
     *                         if the user confirms the registration.
     * @param chavePrivada The file path to the user's private key. This will be used
     *                     if the user confirms the registration.
     * @param fraseSecreta The secret phrase or password required to access the private key.
     * @param grupo The group to which the user will be assigned upon registration.
     * @param senha The password chosen by the user for their new account.
     */
    public TelaConfirmacao(Map<String, String> certificateData, String parthCertificado, String chavePrivada, String fraseSecreta, String grupo, String senha) {
        setTitle("Confirmação de Cadastro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        StringBuilder sb = new StringBuilder("<html>");
        for (Map.Entry<String, String> entry : certificateData.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("<br>");
        }
        sb.append("</html>");

        JLabel label = new JLabel(sb.toString());
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(label);

        // Painel de Botões
        JPanel painelBotoesCadastro = new JPanel(new FlowLayout());
        painelBotoesCadastro.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnVoltar = new JButton("Voltar");

        btnCadastrar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cadastrando o usuário...");
            Cofre.cadastrarUsuario(parthCertificado, chavePrivada, fraseSecreta, grupo, senha);
            Date data = new Date();
            String dataString = String.valueOf(data);
            Cofre.addLog(dataString, fraseSecreta + "@inf1416.puc-rio.br", "6008");
            dispose();
        });


        btnVoltar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Voltando ao menu principal...");
            Date data = new Date();
            String dataString = String.valueOf(data);
            Cofre.addLog(dataString, fraseSecreta + "@inf1416.puc-rio.br", "6009");
            dispose();
        });

        painelBotoesCadastro.add(btnCadastrar);
        painelBotoesCadastro.add(btnVoltar);

        contentPane.add(painelBotoesCadastro);

        pack();
    }
}
