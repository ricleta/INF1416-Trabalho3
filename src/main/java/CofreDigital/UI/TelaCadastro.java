package CofreDigital.UI;

import java.util.Scanner;

public class TelaCadastro {
    // Atributos da tela principal
    private String loginNameAtual;
    private String nomeGrupoAtual;
    private String nomeUsuarioAtual;
    private int total_de_acessos_usuario;

    // Construtor da tela principal
    public TelaCadastro(String loginNameAtual, String nomeGrupoAtual, String nomeUsuarioAtual, int total_de_acessos_usuario) {
        this.loginNameAtual = loginNameAtual;
        this.nomeGrupoAtual = nomeGrupoAtual;
        this.nomeUsuarioAtual = nomeUsuarioAtual;
        this.total_de_acessos_usuario = total_de_acessos_usuario;
    }

    // Método para exibir a tela principal
    public void exibirTela(Scanner keyboard) {
        String Cabecalho = "Login: " + loginNameAtual + "\n" + 
            "Grupo: " + nomeGrupoAtual + "\n" + 
            "Nome: " + nomeUsuarioAtual;
        System.out.println(Cabecalho);

        String corpo1 = "Acesso: " + total_de_acessos_usuario;
        System.out.println(corpo1);

        String corpo2 = "Menu Principal\n" +
            "1. Acessar Cofre\n" +
            "2. Gerar QR Code\n" +
            "3. Sair";

        System.out.println(corpo2);

        String linha = keyboard.nextLine().trim();
        System.out.printf("Your option was %s. ", linha);

        switch (linha) {
            case "1":
                System.out.println("Acessando Cofre...");
                // Aqui você pode adicionar a lógica para acessar o cofre
                break;
            case "2":
                System.out.println("Gerando QR Code...");
                // Aqui você pode adicionar a lógica para gerar o QR Code
                break;
            case "3":
                System.out.println("Saindo...");
                // Aqui você pode adicionar a lógica para sair do aplicativo
                keyboard.close();
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
    }
}