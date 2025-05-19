/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import CofreDigital.Cofre;
import CofreDigital.Users.User;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * CofreDigital - Tela de Login com teclado virual para autenticacao da senha
 */
public class TelaLogin2 extends JFrame {
    private String currentUserEmail;
    private User user;
    private int tentativas = 0;

    private JPasswordField passwordField;
    private JPanel buttonPanel;
    private List<Integer> numberPool;
    
    private List<List<String>> pressedPairs = new ArrayList<>();

    private int maxPasswordLength = 10;
    private int minPasswordLength = 8;
    
    private JButton okButton;

    public TelaLogin2(User user) {
        this.user = user;
        this.currentUserEmail = user.getEmail();
        setTitle("Cofre Digital - Autenticação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        passwordField = new JPasswordField(maxPasswordLength);
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.add(new JLabel("Senha pessoal: "));
        passwordPanel.add(passwordField);
        add(passwordPanel, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        numberPool = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            numberPool.add(i);
        }
        createNumberButtons(); // Initial button creation
        add(buttonPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton = new JButton("OK");
        okButton.addActionListener(new OkButtonListener());
        okButton.setEnabled(false); // Initially disable the OK button
        passwordField.getDocument().addDocumentListener(new passwordFieldDocumentListener());
        controlPanel.add(okButton);

        JButton clearButton = new JButton("LIMPAR");
        clearButton.addActionListener(new ClearButtonListener());
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.SOUTH);
        pack();
    }

    private void createNumberButtons() {
        buttonPanel.removeAll(); // Clear existing buttons
        Collections.shuffle(numberPool); // Shuffle the numbers

        for (int i = 0; i < numberPool.size() / 2; i++) {
            String n1 = numberPool.get(i * 2).toString();
            String n2 = numberPool.get(i * 2 + 1).toString();
            JButton button = new JButton(n1 + " " + n2);
            button.addActionListener(new NumberButtonListener(n1, n2));
            buttonPanel.add(button);
        }
        buttonPanel.revalidate(); // Update the layout
        buttonPanel.repaint();
    }

    private void generateCombinations(List<List<String>> pairs, int index, String current, ArrayList<String> result) {
        if (index == pairs.size()) {
            if (current.length() >= minPasswordLength && current.length() <= maxPasswordLength) {
                result.add(current);
            }
            return;
        }
        List<String> pair = pairs.get(index);
        generateCombinations(pairs, index + 1, current + pair.get(0), result); // Choose num1
        generateCombinations(pairs, index + 1, current + pair.get(1), result); // Choose num2
    }

   private class NumberButtonListener implements ActionListener {
        private String num1, num2;
        public NumberButtonListener(String num1, String num2) {
            this.num1 = num1;
            this.num2 = num2;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String currentPassword = new String(passwordField.getPassword());
            if (currentPassword.length() < maxPasswordLength) {
                passwordField.setText(currentPassword + "*");
                pressedPairs.add(List.of(num1, num2));
            }
            createNumberButtons();
        }
    }

    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<String> passwordList = new ArrayList<>();
            generateCombinations(pressedPairs, 0, "", passwordList);
            String password = Cofre.isPasswordCorrect(currentUserEmail, passwordList);
            if (password != null) {
                user.setHashSenhaPessoal(password);
                Cofre.authenticateTOTP(user);
                tentativas = 0;
                dispose();
            } else {
                tentativas++;
                JOptionPane.showMessageDialog(TelaLogin2.this, "Senha incorreta. Tentativa " + tentativas + " de 3.");
                
                if (tentativas >= 3) {
                    tentativas = 0;
                }
            }
            passwordField.setText("");
            pressedPairs.clear();
            okButton.setEnabled(false);
            createNumberButtons();
        }    
    }

    private class passwordFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            String currentPassword = new String(passwordField.getPassword());
            if (currentPassword.length() >= minPasswordLength && currentPassword.length() <= maxPasswordLength) {
                okButton.setEnabled(true);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            // Handle password field changes if needed
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // Handle password field changes if needed
        }        
    }

   private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            passwordField.setText("");
            createNumberButtons();
        }
    }
}