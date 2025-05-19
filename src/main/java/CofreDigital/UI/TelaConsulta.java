package CofreDigital.UI;

import CofreDigital.Cofre;
import CofreDigital.Users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class TelaConsulta extends JFrame {
    private String loginNameAtual;
    private String nomeGrupoAtual;
    private String nomeUsuarioAtual;
    private int total_de_consultas_usuario;
    private List<String[]> arquivos;
    private User user;

    public TelaConsulta(User user) {
        this.loginNameAtual = user.getEmail();
        this.nomeGrupoAtual = user.getGrupo();
        this.nomeUsuarioAtual = user.getNome();
        this.total_de_consultas_usuario = user.getTotalConsultas();
        this.user = user;

        configurarTela();
    }

    private void configurarTela() {
        setTitle("Consulta de Pasta de Arquivos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal com BoxLayout vertical
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // margem geral

        // Cabeçalho
        JLabel labelCabecalho = new JLabel("<html>Login: " + loginNameAtual + "<br>" +
                "Grupo: " + nomeGrupoAtual + "<br>" +
                "Nome: " + nomeUsuarioAtual + "</html>");
        labelCabecalho.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Corpo 1
        JLabel labelCorpo1 = new JLabel("<html>Total de consultas do usuario: " + total_de_consultas_usuario + "</html>");
        labelCorpo1.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Corpo 2        

        JPanel formularios = new JPanel();
        formularios.setLayout(new BoxLayout(formularios, BoxLayout.Y_AXIS));

        JLabel lblPassphraseUser = new JLabel("Frase Secreta:");
        JPasswordField textPassphraseUser = new JPasswordField(20);

        JLabel lblFolderPath = new JLabel("Caminho da pasta:");
        JTextField textFolderPath = new JTextField(20);

        formularios.add(lblPassphraseUser);
        formularios.add(textPassphraseUser);
        formularios.add(lblFolderPath);
        formularios.add(textFolderPath);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton btnListar = new JButton("Listar arquivos secretos do usuario");
        JButton btnSair = new JButton("Menu Principal");
        painelBotoes.add(btnListar);
        painelBotoes.add(btnSair);
        painelBotoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adiciona componentes e espaçamentos
        painelPrincipal.add(labelCabecalho);
        painelPrincipal.add(Box.createVerticalStrut(20));
        painelPrincipal.add(labelCorpo1);
        painelPrincipal.add(Box.createVerticalStrut(20));
        painelPrincipal.add(formularios);
        painelPrincipal.add(Box.createVerticalStrut(20));
        painelPrincipal.add(painelBotoes);

        setContentPane(painelPrincipal);

        btnListar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação para listar arquivos secretos do usuario
                JOptionPane.showMessageDialog(TelaConsulta.this, "Listando arquivos secretos do usuario...");
    
                String adminPassphrase = JOptionPane.showInputDialog(null, "Digite a frase secreta do admin:");

                arquivos = Cofre.listFiles(user, adminPassphrase);

                //montar a tabela com os arquivos para seleção
                DefaultListModel<String> model = new DefaultListModel<>();
                for (String[] arquivo : arquivos) {
                    if (arquivo[2].equals(user.getEmail()) || arquivo[3].equals(user.getGrupo())) {
                        model.addElement(arquivo[0] + " | " + arquivo[1] + " | " + arquivo[2] + " | " + arquivo[3]);
                    }
                }

                JList<String> listaArquivos = new JList<>(model);
                JButton btnAbrir = new JButton("Abrir");

                btnAbrir.addActionListener(evt -> {
                    String selectedListValue = listaArquivos.getSelectedValue();
                    if (selectedListValue != null) {
                        // Extract the filename (arquivo[0]) from the JList item string.
                        // The list item format is "filename | extension | owner | group".
                        String selectedFileName = selectedListValue.split(" \\| ")[0].trim();

                        int foundIndex = -1;
                        // Find the index of this filename in the 'arquivos' list.
                        for (int i = 0; i < arquivos.size(); i++) {
                            if (arquivos.get(i)[0].equals(selectedFileName)) {
                                foundIndex = i;
                                break;
                            }
                        }

                        if (foundIndex >= 0) {
                            String[] arquivoSelecionado = arquivos.get(foundIndex);
                            
                            // arquivoSelecionado[1] is the file extension.
                            String extensao = arquivoSelecionado[1]; 
                            
                            System.out.println("frase secreta: " + new String(textPassphraseUser.getPassword()));
                            Cofre.abrirArquivoSecreto(
                                    arquivoSelecionado[0], // filename
                                    arquivoSelecionado[2], // owner
                                    loginNameAtual,        // current user login
                                    new String(textPassphraseUser.getPassword()), // user's passphrase
                                    extensao               // file extension
                            );
                        } else {
                            // This case should ideally not occur if the selected item originated from 'arquivos'.
                            JOptionPane.showMessageDialog(TelaConsulta.this, "Erro: Arquivo selecionado não encontrado nos dados originais.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(TelaConsulta.this, "Selecione um arquivo.");
                    }
                });

                add(new JScrollPane(listaArquivos), "Center");
                add(btnAbrir, "South");

                pack();
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação para voltar ao menu principal
                JOptionPane.showMessageDialog(TelaConsulta.this, "Voltando ao menu principal...");
                TelaConsulta.this.dispose(); // Fecha a tela atual
                Cofre.showMenuPrincipal(user);
            }
        });

        pack();
    }
}