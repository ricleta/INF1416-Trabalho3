package CofreDigital.Users;

import CofreDigital.DB.DB;

public class Cadastro {
    private DB db;

    public Cadastro(DB db) {
        this.db = db;
    }

    public void cadastrarUsuario(String login, String senha, String nome, String caminhoCertificado, String caminhoChavePrivada, String fraseSecreta) {
        User user = new User(login, senha, fraseSecreta);

        // Verifica se o usuário já existe
        if (db.userExists(user)) {
            System.out.println("Usuário já existe.");
            return;
        }

        // TODO: Get hash of the password and actual certificate
        byte[] chavePrivada = caminhoChavePrivada.getBytes();

        db.addUser(user, chavePrivada, caminhoCertificado);
    }
}
