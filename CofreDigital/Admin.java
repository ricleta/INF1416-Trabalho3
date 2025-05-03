/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

public class Admin extends User {
    private String fraseSecreta;

    public Admin(String email, String senhaPessoal, String token, String fraseSecreta) {
        super(email, senhaPessoal, token);
        this.fraseSecreta = fraseSecreta;
    }

    public String getFraseSecreta() {
        return fraseSecreta;
    }

    public void setFraseSecreta(String fraseSecreta) {
        this.fraseSecreta = fraseSecreta;
    }

    public void setemail(String email) {
        super.setEmail(email);
    }

    public String getemail() {
       return super.getEmail();
    }

    public void setSenhaPessoal(String senhaPessoal) {
        super.setSenhaPessoal(senhaPessoal);
    }

    public String getSenhaPessoal() {
        return super.getSenhaPessoal();
    }

    public void setToken(String token) {
        super.setToken(token);
    }

    public String getToken() {
        return super.getToken();
    }
}