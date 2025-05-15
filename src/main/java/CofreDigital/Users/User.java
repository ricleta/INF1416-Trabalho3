/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.Users;

/*import com.eatthepath:otp-java;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;*/

public class User {
    private String email;
    private String senhaPessoal;
    private String fraseSecreta;
    private int total_de_acessos;

    //from Google authenticator
    private String token;

    public User(String email, String senhaPessoal, String token, String fraseSecreta) {
        this.email = email;
        this.senhaPessoal = senhaPessoal;
        this.fraseSecreta = fraseSecreta;
        this.token = token;
    }

    public User(String email, String senhaPessoal, String fraseSecreta) {
        this.email = email;
        this.senhaPessoal = senhaPessoal;
        this.fraseSecreta = fraseSecreta;
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
    
    public boolean authlogin(){
        return false;
    }

    public boolean authsenhaPessoal(){return false;}

    public boolean authtoken(){return false;}

    public boolean validaFraseSecreta() {return false;}
}


