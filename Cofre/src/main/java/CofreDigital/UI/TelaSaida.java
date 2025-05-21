/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import CofreDigital.Users.User;
import CofreDigital.Cofre;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class TelaSaida extends JFrame {
    private String loginNameUsuario;
    private String GrupoUsuario;
    private String NomeUsuario;
    private int total_de_acessos_usuario;
    private User usuario;

    public TelaSaida(User usuario) {
        this.loginNameUsuario = usuario.getEmail();
        this.GrupoUsuario = usuario.getGrupo();
        this.NomeUsuario = usuario.getNome();
        this.total_de_acessos_usuario = usuario.getTotalAcessos();
        this.usuario = usuario;

        configuarTela();
    }

    private void configuarTela() {
        Cofre.addLogToDB(usuario.getEmail(), "8001"); 
        setTitle("Tela de Saida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel labelCabecalho = new JLabel("<html>Login: " + loginNameUsuario + "<br>" +
                "Grupo: " + GrupoUsuario + "<br>" +
                "Nome: " + NomeUsuario + "</html>");
        labelCabecalho.setAlignmentX(CENTER_ALIGNMENT);
        painel.add(labelCabecalho);

        JLabel labelCorpo = new JLabel("<html>Total de acessos do usuario: " + total_de_acessos_usuario + "</html>");
        labelCorpo.setAlignmentX(CENTER_ALIGNMENT);
        painel.add(labelCorpo);

        JLabel labelCorpo2 = new JLabel("<html>Saída do sistema: <br>Como deseja sair :</html>");
        labelCorpo2.setAlignmentX(CENTER_ALIGNMENT);
        painel.add(labelCorpo2);

        JPanel painelBotoes = new JPanel();
        JButton btnEncerrarSessao = new JButton("Encerrar Sessão");
        JButton btnEncerrarSistema = new JButton("Encerrar Sistema");
        JButton btnVoltarMenuPrincipal = new JButton("Voltar para o Menu Principal");

        // Ações dos botões
        btnEncerrarSessao.addActionListener(e -> {
            Cofre.addLogToDB(usuario.getEmail(), "8002"); 
           
            // Lógica para encerrar a sessão
            System.out.println("Sessão encerrada.");
            
            Cofre.showLoginScreen();
            dispose();
        });

        btnEncerrarSistema.addActionListener(e -> {
            // Lógica para encerrar o sistema
            Cofre.addLogToDB(usuario.getEmail(), "8003"); 
            Cofre.addLogToDB(usuario.getEmail(), "1002"); 
            System.out.println("Sistema encerrado.");
            
            Cofre.encerrarSistema();
            dispose();
        });

        btnVoltarMenuPrincipal.addActionListener(e -> {
            // Lógica para voltar ao menu principal
            Cofre.addLogToDB(usuario.getEmail(), "8004"); 
            Cofre.showMenuPrincipal(usuario);
            dispose();
        });


        painelBotoes.add(btnEncerrarSessao);
        painelBotoes.add(btnEncerrarSistema);
        painelBotoes.add(btnVoltarMenuPrincipal);

        painel.add(painelBotoes);

        setContentPane(painel);
        pack();
    }
}