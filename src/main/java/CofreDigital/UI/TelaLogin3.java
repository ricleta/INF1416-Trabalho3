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

/*
 * CofreDigital - Tela de Login para autenticacao do TOTP do usuario
 */
public class TelaLogin3 extends JFrame{
    private static final int TOTPCodeSize = 6;
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
                Cofre.validaTOTP(user, totpCode);
            } else {
                System.out.println("O código TOTP deve ter " + TOTPCodeSize + " dígitos.");
            }
        });

        btnLimpar.addActionListener(e -> {
            txtTOTPField.setText("");
        });

        pack();
    }
}
