/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.stream.IntStream;


public class TOTP {
    private byte [] key = null;
    private long timeStepInSeconds = 30;

    // Construtor da classe. Recebe a chave secreta em BASE32 e o intervalo
    // de tempo a ser adotado (default = 30 segundos). Deve decodificar a
    // chave secreta e armazenar em key. Em caso de erro, gera Exception.
    public TOTP(String base32EncodedSecret, long timeStepInSeconds) throws Exception {
        
        //guarda a chave secreta em base32
        Base32 base32 = new Base32(Base32.Alphabet.BASE32, true, false);

        //decodifica e armazena em key
        this.key = base32.fromString(base32EncodedSecret);
        this.timeStepInSeconds = timeStepInSeconds;

        // se gerar um erro, gera uma exception
        if (this.key == null) {
            throw new Exception("Erro ao decodificar a chave secreta.");
        }
    }
    
    // Recebe o HASH HMAC-SHA1 e determina o código TOTP de 6 algarismos
    // decimais, prefixado com zeros quando necessário.
    private String getTOTPCodeFromHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0x0F;

        long truncatedHash = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);

        int totpCode = (int) (truncatedHash %  Math.pow(10, 6));

        //prefixa o codigo de 6 algarismos com zeros quando necessario
        return String.format("%0" + 6 + "d", totpCode);
    }

    // Recebe o contador e a chave secreta para produzir o hash HMAC-SHA1.
    private byte[] HMAC_SHA1(byte[] counter, byte[] keyByteArray) {
        
        try {
            Mac hmac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyByteArray, "HmacSHA1");
            hmac.init(secretKeySpec);
            return hmac.doFinal(counter);
        } 
        
        catch (Exception e) {
            throw new RuntimeException("Erro ao gerar HMAC-SHA1: " + e.getMessage());
        }
    }

    // Recebe o intervalo de tempo e executa o algoritmo TOTP para produzir
    // o código TOTP. Usa os métodos auxiliares getTOTPCodeFromHash e HMAC_SHA1.
    private String TOTPCode(long timeInterval) {

        byte[] counter = new byte[8];

        //big endian!!!

        //extrai os bits menos significativos do timeInterval e armazena em counter
        for (int i = 7; i >= 0 ; i--) {
            counter[i] = (byte) (timeInterval & 0xFF);
            timeInterval >>= 8;
        }

        try{
            byte[] hash = HMAC_SHA1(counter, key);
            return getTOTPCodeFromHash(hash);
        } 
        
        catch (Exception e) {
            throw new RuntimeException("Erro ao gerar codigo TOTP: " + e.getMessage());
        }
        
    }

    // Método que é utilizado para solicitar a geração do código TOTP.
    public String generateCode(long timeInterval) {
        String totpCode = null;

        try {
            totpCode = TOTPCode(timeInterval);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return totpCode;
    }

    // Método que é utilizado para validar um código TOTP (inputTOTP).
    // Deve considerar um atraso ou adiantamento de 30 segundos no
    // relógio da máquina que gerou o código TOTP.
    public boolean validateCode(String inputTOTP) {
        long timeInterval = new Date().getTime() / 1000 / timeStepInSeconds;

        boolean matches = IntStream.of(-1,0,1).anyMatch(i -> generateCode(timeInterval + i).equals(inputTOTP));

        return matches;
    }

    public long getTimeStepInSeconds() {
        return timeStepInSeconds;
    }
    public void setTimeStepInSeconds(long timeStepInSeconds) {
        this.timeStepInSeconds = timeStepInSeconds;
    }
    public byte[] getKeyByteArray() {
        return key;
    }
    public void setKey(byte[] key) {
        this.key = key;
    }

}
