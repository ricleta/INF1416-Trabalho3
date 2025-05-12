package CofreDigital.UI;

import CofreDigital.UI.TelaCadastro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {
    private String loginNameAtual;
    private String nomeGrupoAtual;
    private String nomeUsuarioAtual;
    private int total_de_acessos_usuario;

    public TelaPrincipal(String loginNameAtual, String nomeGrupoAtual, String nomeUsuarioAtual, int total_de_acessos_usuario) {
        this.loginNameAtual = loginNameAtual;
        this.nomeGrupoAtual = nomeGrupoAtual;
        this.nomeUsuarioAtual = nomeUsuarioAtual;
        this.total_de_acessos_usuario = total_de_acessos_usuario;

        configurarTela();
    }

    private void configurarTela() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Layout
        setLayout(new BorderLayout());

        // Cabeçalho
        JLabel labelCabecalho = new JLabel("<html>Login: " + loginNameAtual + "<br>" +
            "Grupo: " + nomeGrupoAtual + "<br>" +
            "Nome: " + nomeUsuarioAtual + "</html>");
        labelCabecalho.setHorizontalAlignment(SwingConstants.CENTER);
        labelCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(labelCabecalho, BorderLayout.NORTH);

        JLabel labelCorpo1 = new JLabel("<html>Total de acessos do usuario: " + total_de_acessos_usuario + "</html>");
        labelCorpo1.setHorizontalAlignment(SwingConstants.CENTER);
        labelCorpo1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(labelCorpo1, BorderLayout.CENTER);

        // Painel com botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton btnAcessar = new JButton("Cadastrar um novo usuario");
        JButton btnQRCode = new JButton("Consultar pasta de arquivos secretos do usuario");
        JButton btnSair = new JButton("Sair do Sistema");

        // Ações dos botões
        btnAcessar.addActionListener(e -> cadastrarUsuario());
        btnQRCode.addActionListener(e -> JOptionPane.showMessageDialog(this, "Gerando QR Code..."));
        btnSair.addActionListener(e -> System.exit(0));

        painelBotoes.add(btnAcessar);
        painelBotoes.add(btnQRCode);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);
        
        pack();
    }

    private void cadastrarUsuario() {
        String [] grupos = {"admins", "usuarios"};
        TelaCadastro telaCadastro = new TelaCadastro(loginNameAtual, nomeGrupoAtual, nomeUsuarioAtual, total_de_acessos_usuario, grupos);
        telaCadastro.setVisible(true);
    }
}
