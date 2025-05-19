package CofreDigital.UI;

import CofreDigital.Cofre;
import CofreDigital.Users.User;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.awt.*;

public class TelaCadastro extends JFrame {
    private String loginNameAtual;
    private String nomeGrupoAtual;
    private String nomeUsuarioAtual;
    private int total_de_usuarios;
    private String[] grupos;

    private User usuario;

    public TelaCadastro(String loginNameAtual, String nomeGrupoAtual, String nomeUsuarioAtual, int total_de_usuarios, String[] grupos) {
        this.loginNameAtual = loginNameAtual;
        this.nomeGrupoAtual = nomeGrupoAtual;
        this.nomeUsuarioAtual = nomeUsuarioAtual;
        this.total_de_usuarios = total_de_usuarios;
        this.grupos = grupos;

        configurarTela();
    }

    public TelaCadastro(User usuario, String[] grupos) {
        this.loginNameAtual = usuario.getEmail();
        this.nomeGrupoAtual = usuario.getGrupo();
        this.nomeUsuarioAtual = usuario.getNome();
        this.total_de_usuarios = usuario.getTotalAcessos();
        this.grupos = grupos;
        this.usuario = usuario;

        configurarTela();
    }

   private void configurarTela() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Set BoxLayout for the main content pane (vertical layout)
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Cabeçalho
        JLabel labelCabecalho = new JLabel("<html>Login: " + loginNameAtual + "<br>" +
            "Grupo: " + nomeGrupoAtual + "<br>" +
            "Nome: " + nomeUsuarioAtual + "</html>");
        labelCabecalho.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(labelCabecalho);

        JLabel labelCorpo1 = new JLabel("<html>Total de usuários: " + total_de_usuarios + "</html>");
        labelCorpo1.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelCorpo1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(labelCorpo1);

        // Painel de Cadastro (GridLayout for the form)
        JPanel painelCadastro = new JPanel(new GridLayout(6, 2, 5, 5));
        painelCadastro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblCertificado = new JLabel("Caminho do Certificado:");
        JTextField txtCertificado = new JTextField(20);

        JLabel lblChavePrivada = new JLabel("Caminho da Chave Privada:");
        JTextField txtChavePrivada = new JTextField(20);

        JLabel lblFraseSecreta = new JLabel("Frase Secreta:");
        JPasswordField txtFraseSecreta = new JPasswordField(20);

        JLabel lblGrupo = new JLabel("Grupo:");
        JComboBox<String> comboGrupo = new JComboBox<>(grupos);

        JLabel lblSenha = new JLabel("Senha Pessoal:");
        JPasswordField txtSenha = new JPasswordField(10);

        JLabel lblConfirmacaoSenha = new JLabel("Confirmação Senha:");
        JPasswordField txtConfirmacaoSenha = new JPasswordField(10);

        painelCadastro.add(lblCertificado);
        painelCadastro.add(txtCertificado);
        painelCadastro.add(lblChavePrivada);
        painelCadastro.add(txtChavePrivada);
        painelCadastro.add(lblFraseSecreta);
        painelCadastro.add(txtFraseSecreta);
        painelCadastro.add(lblGrupo);
        painelCadastro.add(comboGrupo);
        painelCadastro.add(lblSenha);
        painelCadastro.add(txtSenha);
        painelCadastro.add(lblConfirmacaoSenha);
        painelCadastro.add(txtConfirmacaoSenha);

        contentPane.add(painelCadastro);

        // Painel de Botões
        JPanel painelBotoesCadastro = new JPanel(new FlowLayout());
        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setEnabled(false); // Initially disabled
        JButton btnVoltar = new JButton("Voltar");
        if (usuario == null){
            btnVoltar.setEnabled(false); // Disable the button if no user is provided (it's the first user)
        }

        // Add DocumentListener to password fields to enable/disable the button based on length
        DocumentListener passwordLengthListener = createPasswordLengthListener(txtSenha, txtConfirmacaoSenha, btnCadastrar, 8);
        txtSenha.getDocument().addDocumentListener(passwordLengthListener);
        txtConfirmacaoSenha.getDocument().addDocumentListener(passwordLengthListener);
        
        btnCadastrar.addActionListener(e -> {
                Cofre.confirmaCadastro(
                    txtCertificado.getText(),
                    txtChavePrivada.getText(),
                    new String(txtFraseSecreta.getPassword()),
                    (String) comboGrupo.getSelectedItem(),
                    new String(txtSenha.getPassword()),
                    new String(txtConfirmacaoSenha.getPassword())
                );

                dispose();
            }
        );

        btnVoltar.addActionListener(e -> {
            Cofre.showMenuPrincipal(usuario);
            dispose();
        });

        painelBotoesCadastro.add(btnCadastrar);
        
        painelBotoesCadastro.add(btnVoltar);

        contentPane.add(painelBotoesCadastro);

        pack();
    }

    private static DocumentListener createPasswordLengthListener(JPasswordField passwordField,JPasswordField confirmPasswordField, JButton button, int minLength) {
        return new DocumentListener() {
            private void updateButtonState() {
                button.setEnabled(passwordField.getPassword().length >= minLength);
                button.setEnabled(confirmPasswordField.getPassword().length >= minLength);
            }

            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }
        };
    }

}
