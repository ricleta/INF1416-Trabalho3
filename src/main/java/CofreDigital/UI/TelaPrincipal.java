package CofreDigital.UI;

import CofreDigital.UI.TelaCadastro;
import CofreDigital.Cofre;
import CofreDigital.Users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {
    private String loginNameAtual;
    private String nomeGrupoAtual;
    private String nomeUsuarioAtual;
    private int total_de_acessos_usuario;
    private User usuario;

    public TelaPrincipal(User usuario) {
        this.loginNameAtual = usuario.getEmail();
        this.nomeGrupoAtual = usuario.getGrupo();
        this.nomeUsuarioAtual = usuario.getNome();
        this.total_de_acessos_usuario = usuario.getTotalAcessos();
        this.usuario = usuario;

        System.out.println("Grupo: " + nomeGrupoAtual);
        if (nomeGrupoAtual.equals("administrador"))
        {
            configurarTelaAdmin();
        }
        else if (nomeGrupoAtual.equals("usuario"))
        {
            configurarTelaUser();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Grupo inválido.");
            dispose(); // Fecha a tela atual
        }
    }

    private void configurarTelaAdmin() {
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

        JButton btnCadastrar = new JButton("Cadastrar um novo usuario");
        JButton btnConsultFileArchives = new JButton("Consultar pasta de arquivos secretos do usuario");
        JButton btnSair = new JButton("Sair do Sistema");

        // Ações dos botões
        btnCadastrar.addActionListener(e -> {
            Cofre.showTelaCadastro(usuario);
        });
        
        // Ações dos botões
        btnConsultFileArchives.addActionListener(e -> {
            consultaArquivos(usuario);
            dispose(); // Fecha a tela atual
        });
        
        btnSair.addActionListener(e -> {
            Cofre.showExitScreen(usuario);
            dispose(); // Fecha a tela atual
        });

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnConsultFileArchives);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);
        
        pack();
    }

    private void configurarTelaUser() {
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

        JButton btnConsultFileArchives = new JButton("Consultar pasta de arquivos secretos do usuario");
        JButton btnSair = new JButton("Sair do Sistema");

        // Ações dos botões
        btnConsultFileArchives.addActionListener(e -> {
            consultaArquivos(usuario);
            dispose(); // Fecha a tela atual
        });
        
        btnSair.addActionListener(e -> {
            Cofre.showExitScreen(usuario);
            dispose(); // Fecha a tela atual
        });

        painelBotoes.add(btnConsultFileArchives);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);
        
        pack();
    }

    private void consultaArquivos(User user) {
        // Implementar a lógica para consultar arquivos secretos do usuário
        Cofre.showTelaConsulta(user);
    }
}
