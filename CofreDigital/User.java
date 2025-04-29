/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

public class User {
    private String email;
    private String senhaPessoal;
    
    //from Google authenticator
    private String token;

    public User(String email,String senhaPessoal,String token){
        this.email = email;
        this.senhaPessoal = senhaPessoal;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaPessoal() {
        return senhaPessoal;
    }

    public void setSenhaPessoal(String senhaPessoal) {
        this.senhaPessoal = senhaPessoal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


