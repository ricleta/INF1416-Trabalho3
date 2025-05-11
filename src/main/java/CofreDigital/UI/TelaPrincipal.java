package CofreDigital.UI;

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
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Layout
        setLayout(new BorderLayout());

        // Cabeçalho
        JLabel labelCabecalho = new JLabel("<html>Login: " + loginNameAtual + "<br>" +
                "Grupo: " + nomeGrupoAtual + "<br>" +
                "Nome: " + nomeUsuarioAtual + "<br>" +
                "Acesso: " + total_de_acessos_usuario + "</html>");
        labelCabecalho.setHorizontalAlignment(SwingConstants.CENTER);
        labelCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(labelCabecalho, BorderLayout.NORTH);

        // Painel com botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton btnAcessar = new JButton("Acessar Cofre");
        JButton btnQRCode = new JButton("Gerar QR Code");
        JButton btnSair = new JButton("Sair");

        // Ações dos botões
        btnAcessar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Acessando Cofre..."));
        btnQRCode.addActionListener(e -> JOptionPane.showMessageDialog(this, "Gerando QR Code..."));
        btnSair.addActionListener(e -> System.exit(0));

        painelBotoes.add(btnAcessar);
        painelBotoes.add(btnQRCode);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.CENTER);
    }

    public void exibirTela() {
        setVisible(true);
    }
}
