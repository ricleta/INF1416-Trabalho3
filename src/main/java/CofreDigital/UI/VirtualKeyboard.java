/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.UI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VirtualKeyboard extends JFrame {
    private String senhaReal = null; 
    private final StringBuilder senhaDigitada = new StringBuilder();
    private int tentativas = 0;
    private long tempoBloqueio = 0;
    private final JButton[] botoes = new JButton[5];

    public VirtualKeyboard (String senhaReal) {
        this.senhaReal = senhaReal;

        setTitle("Digite sua Senha Pessoal: ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel painelTeclado = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 5; i++) {
            botoes[i] = new JButton();
            botoes[i].addActionListener(e -> {
                if (System.currentTimeMillis() < tempoBloqueio) {
                    JOptionPane.showMessageDialog(this, "Acesso bloqueado. Voltando para a primeira etapa, aguarde 2 minutos para tentar novamente.");
                    return;
                }

                String textoBotao = ((JButton) e.getSource()).getText();
                String[] numeros = textoBotao.split("/");
                String escolhido = (String) JOptionPane.showInputDialog(this, "Selecione um número:", "Seleção", JOptionPane.PLAIN_MESSAGE, null, numeros, numeros[0]);
                
                if (escolhido != null) {
                    senhaDigitada.append(escolhido);
                    if (senhaDigitada.length() == senhaReal.length()) {
                        verificarSenha();
                    } 
                    
                    else {
                        embaralharBotoes();
                    }
                }
            });

            painelTeclado.add(botoes[i]);
        }

        add(painelTeclado, BorderLayout.CENTER);
        embaralharBotoes();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verificarSenha() {
        if (senhaDigitada.toString().equals(senhaReal)) {
            JOptionPane.showMessageDialog(this, "Senha correta! Seguindo para a próxima etapa.");
            dispose(); // ou próxima etapa
        } 
        
        else {
            tentativas++;
            JOptionPane.showMessageDialog(this, "Senha incorreta. Tentativa " + tentativas + " de 3, tente novamente.");
            senhaDigitada.setLength(0);
            if (tentativas >= 3) {
                tempoBloqueio = System.currentTimeMillis() + 2 * 60 * 1000; // 2 minutos
                tentativas = 0;
                JOptionPane.showMessageDialog(this, "Acesso bloqueado por 2 minutos, voltando para a primeira etapa.");
            }
            embaralharBotoes();
        }
    }

    private void embaralharBotoes() {
        List<Integer> numeros = new ArrayList<>();

        for (int i = 0; i < 10; i++){ 
            numeros.add(i);
        }

        Collections.shuffle(numeros);

        for (int i = 0; i < 5; i++) {
            int n1 = numeros.get(i * 2);
            int n2 = numeros.get(i * 2 + 1);
            botoes[i].setText(n1 + "/" + n2);
        }
    }

    public String getSenhaDigitada() {
        return senhaDigitada.toString();
    }
}
