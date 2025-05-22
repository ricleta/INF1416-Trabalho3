/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import CofreDigital.Cofre;
import CofreDigital.Users.User;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

/*
 * CofreDigital - Tela de Login para autenticacao do TOTP do usuario
 */
public class TelaLogin3 extends JFrame{
    private static final int TOTPCodeSize = 6;
    private int tentativas = 0;
    
    public TelaLogin3(User user) {
        setTitle("Tela de Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        JPanel totpPanel = new JPanel(new FlowLayout());
        totpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTOTP = new JLabel("TOTP: ");
        JTextField txtTOTPField = new JTextField(TOTPCodeSize);
        
        totpPanel.add(lblTOTP);
        totpPanel.add(txtTOTPField);

        contentPane.add(totpPanel);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnOk = new JButton("Ok");
        JButton btnLimpar = new JButton("Limpar");

        buttonsPanel.add(btnOk);
        buttonsPanel.add(btnLimpar);

        contentPane.add(buttonsPanel);

        btnOk.addActionListener(e -> {
            String totpCode = txtTOTPField.getText();
            if (totpCode.length() == TOTPCodeSize) {
                // Call the method to validate the TOTP code
                if (Cofre.validaTOTP(user, totpCode))
                {
                    Cofre.updateAccessCount(user);
                    Cofre.showMenuPrincipal(user);
                    dispose(); // Fecha a tela de login
                }
                else {
                    tentativas++;
                    if (tentativas >= 3) {
                        JOptionPane.showMessageDialog(this, "Número máximo de tentativas atingido.");
                        Cofre.blockUser(user.getEmail());
                    } else {
                        JOptionPane.showMessageDialog(this, "Código TOTP inválido. Tentativa " + tentativas + " de 3.");
                        txtTOTPField.setText(""); // Limpa o campo de entrada
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "O código TOTP deve ter " + TOTPCodeSize + " dígitos.");
            }
        });

        btnLimpar.addActionListener(e -> {
            txtTOTPField.setText("");
        });

        pack();
    }
}
