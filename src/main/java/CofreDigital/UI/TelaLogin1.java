/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Container;
import java.awt.FlowLayout;

import CofreDigital.Cofre;
import CofreDigital.Users.User;

/*
 * CofreDigital - Tela de Login para autenticacao do email do usuario
 */
public class TelaLogin1 extends JFrame {
    // Constructor
    public TelaLogin1() {
        setTitle("Tela de Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel loginPanel = new JPanel(new FlowLayout()); 
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Login name: ");
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JTextField loginField = new JTextField(20);

        loginPanel.add(label);
        loginPanel.add(loginField);

        contentPane.add(loginPanel);

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnLogin = new JButton("Login");
        JButton btnLimpar = new JButton("Limpar");

        btnLogin.setEnabled(false); // Initially disable the login button

        loginField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            public void validateInput() {
                String text = loginField.getText();
                // Regex for basic email validation:
                // Ensures the format is something@something.something
                // This is a common pattern, not strictly RFC 5322 but widely used and generally effective.
                String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                if (text != null && text.matches(emailRegex)) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String loginName = loginField.getText();

                User user = Cofre.checaEmailValido(loginName);
                if (user != null)
                {
                    Cofre.authenticatePassword(user);
                    dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Email não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            loginField.setText("");
            }
        });

        painelBotoes.add(btnLogin);
        painelBotoes.add(btnLimpar);

        contentPane.add(painelBotoes);
        
        pack();
        setLocationRelativeTo(null); // Centraliza a janela
    }
    
}
