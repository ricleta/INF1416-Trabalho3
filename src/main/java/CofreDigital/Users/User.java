/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.Users;

public class User {
    private String email;
    private String nome;

    private String hashSenhaPessoal;
    private String senhaPessoal;
    
    private String fraseSecreta;
    
    private int total_de_acessos;
    private int total_consultas;
    
    private String base32TokenKey;
    private byte[] encryptedtokenKey;

    private String grupo;

    public User(String email, String senhaPessoal, String hashSenhaPessoal, String base32TokenKey, String fraseSecreta, String grupo) {
        setEmail(email);
        setHashSenhaPessoal(hashSenhaPessoal);
        this.senhaPessoal = senhaPessoal;
        setFraseSecreta(fraseSecreta);
        setBase32TokenKey(base32TokenKey);
        this.grupo = grupo;
    }

    public User(String email, String senhaPessoal, String fraseSecreta) {
        setEmail(email);
        setHashSenhaPessoal(senhaPessoal);
        setFraseSecreta(fraseSecreta);
    }

    public User(String email, String senhaPessoal, byte[] encryptedtokenKey, String grupo, int total_de_acessos) {
        setEmail(email);
        setHashSenhaPessoal(senhaPessoal);
        this.encryptedtokenKey = encryptedtokenKey;
        this.grupo = grupo;
        setTotal_de_acessos(total_de_acessos);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setNome();
    }

    public String getHashSenhaPessoal() {
        return hashSenhaPessoal;
    }

    public void setHashSenhaPessoal(String senhaPessoal) {
        this.hashSenhaPessoal = senhaPessoal;
    }

    public String getBase32TokenKey() {
        return base32TokenKey;
    }
    
    public void setBase32TokenKey(String base32TokenKey) {
        this.base32TokenKey = base32TokenKey;
    }

    public String getFraseSecreta() {
        return fraseSecreta;
    }

    public void setFraseSecreta(String fraseSecreta) {
        this.fraseSecreta = fraseSecreta;
    }

    public int getTotal_de_acessos() {
        return total_de_acessos;
    }

    public void setTotal_de_acessos(int total_de_acessos) {
        this.total_de_acessos = total_de_acessos;
    }
    
    public byte[] getEncryptedtokenKey() {
        return encryptedtokenKey;
    }

    public String getSenhaPessoal()
    {
        return senhaPessoal;
    }

    public String getGrupo()
    {
        return grupo;
    }

    private void setNome()
    {
        // Extrai o nome do email (parte antes do '@')
        this.nome = email.substring(0, email.indexOf('@'));
    }

    public String getNome()
    {
        return nome;
    }
}


