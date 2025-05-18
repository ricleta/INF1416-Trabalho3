/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.Users;

public class User {
    private String email;
    private String hashSenhaPessoal;
    private String fraseSecreta;
    private String senhaPessoal;
    private int total_de_acessos;
    private String base32TokenKey;
    private byte[] encryptedtokenKey;

    public User(String email, String senhaPessoal, String hashSenhaPessoal, String base32TokenKey, String fraseSecreta) {
        this.email = email;
        this.hashSenhaPessoal = hashSenhaPessoal;
        this.senhaPessoal = senhaPessoal;
        this.fraseSecreta = fraseSecreta;
        this.base32TokenKey = base32TokenKey;
    }

    public User(String email, String senhaPessoal, String fraseSecreta) {
        this.email = email;
        this.hashSenhaPessoal = senhaPessoal;
        this.fraseSecreta = fraseSecreta;
    }

    public User(String email, String senhaPessoal, byte[] encryptedtokenKey) {
        this.email = email;
        this.hashSenhaPessoal = senhaPessoal;
        this.encryptedtokenKey = encryptedtokenKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}


