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
}