/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.Users;

import CofreDigital.DB.DB;
import CofreDigital.SecurityEncryption.KeyValidator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
public class Authenticate {
    private DB db;


    public Authenticate(DB db) {
        this.db = db;
    }

    public boolean loginAuth(String email) {
        
        //pegar usuario do banco de dados
        String emailUser = db.getUserEmail(email);
        if (emailUser == null) {
            System.out.println("Email inválido ou não cadastrado.");
            return false;
        }

        //comparar email com o email do usuario
        return emailUser.equals(email);
    }

    //Pensar : precisa validar a frase secreta?
    public boolean senhaPessoalAuth(String senha) {

        int erros = 0;

        while (erros < 3){
            if(senha.length() >= 8 && senha.length() <= 10) {
                String senhaUser = db.getUserSenhaPessoal(senha);
                if (senhaUser == null) {
                    System.out.println("Senha nao encontrada.");
                    erros++;
                }

                else if(senhaUser.equals(senha)) {
                    System.out.println("Senha válida.");
                    return true;
                }

                else {
                    System.out.println("Senha inválida, tente novamente.");
                    erros++;
                }
            }
        
            else {
                System.out.println("As senhas pessoais são sempre formadas por oito, nove ou 10 números, tente novamente.");
                erros++;
            }

        }

        System.out.println("Numero maximo de tentativas atingido, tente novamente em 2 minutos.Voltando para a primeira etapa.");
        //TODO : voltar para a primeira etapa
        //TODO : bloquear o acesso por 2 minutos
        //Talvez os TODOs acima nao sejam implementados nessa funcao
        //Da pra controlar esse fluxo usando o boolean de retorno dessa funaco!!
        return false;   
    }

    public boolean fraseSecretaAuth(String fraseSecreta) {

        //validar a frase secreta
        String fraseSecretaUser = db.getUserFraseSecreta(fraseSecreta);
        if (fraseSecretaUser == null) {
            return false;
        }

        return fraseSecretaUser.equals(fraseSecreta);
    }

    public boolean tokenAuth(String token) {

        String tokenUser = db.getUserToken(token);
        if (tokenUser == null) {
            System.out.println("Token inválido ou não cadastrado.");
            return false;
        }

        //comparar token com o token do usuario
        return tokenUser.equals(token);
    }


    //talvez mudar o tipo de retorno?
    public boolean ListFiles(String fraseSecreta) {

        //validar a frase secreta
        boolean fraseValida = fraseSecretaAuth(fraseSecreta);

        if(!fraseValida) {
            System.out.println("Frase secreta inválida");
            return false;
        }

        //1. verificar a integridade e autenticidade do arquivo de índice; 

        /*O envelope digital do arquivo de índice é
        armazenado no arquivo index.env (protege a semente SHA1PRNG que gera a chave secreta
        AES)*/
        String indexenvPath = "Files\\index.env";
        File fileEnv = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(indexenvPath).toString());
        byte[] semente = null;
        SecretKey aesKey;
        try (FileInputStream fis = new FileInputStream(fileEnv)) {
            byte[] indexenv = new byte[(int) fileEnv.length()];
            fis.read(indexenv);

            //decriptando o arquivo de envelope digital
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(fraseSecreta.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            semente = cipher.doFinal(indexenv);

            //gerando a chave AES
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            prng.setSeed(semente);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, prng); 
            aesKey = keyGen.generateKey();
        }

        catch (Exception e) {
            System.out.println("Falha ao decriptar o arquivo de envelope digital: " + e.getMessage());
            return false; 
        }

        /*a assinatura digital do arquivo de índice é armazenada no arquivo index.asd
        (representação binária da assinatura digital) */
        String indexasdPath = "Files\\index.asd";
        File fileAsd = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(indexasdPath).toString());
        byte[] signatureBytes = null;

        try (FileInputStream fis = new FileInputStream(fileAsd)) {
            signatureBytes = new byte[(int) fileAsd.length()];
            fis.read(signatureBytes);
        } 
        
        catch (IOException e) {
            System.out.println("Falha ao ler o arquivo de assinatura digital: " + e.getMessage());
            return false; 
        }

        /*deve-se
        decriptar o arquivo de índice da pasta fornecida (cifra AES, modo ECB e enchimento PKCS5),
        chamado index.enc */
        String indexencPath = "Files\\index.enc";
        File file = new File("./".replace("/", System.getProperty("file.separator")) + Paths.get(indexencPath).toString());
        byte[] indexencDecripted = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] indexenc = new byte[(int) file.length()];
            fis.read(indexenc);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            indexencDecripted = cipher.doFinal(indexenc);
        }

        catch(Exception e) {
            System.out.println("Falha ao decriptar o arquivo de índice: " + e.getMessage());
            return false; 
        }
        
        //chave publica do usuario administrador 
        KeyValidator keyValidator = new KeyValidator();
        PublicKey adminPublicKey = keyValidator.getPublicKeyFromCertificate("Keys\\admin-x509.crt");

        //validando a assinatura digital
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(adminPublicKey);
            signature.update(indexencDecripted);
            boolean isValid = signature.verify(signatureBytes);

            if (!isValid) {
                System.out.println("Assinatura digital inválida.");
                return false;
            }
        } 
        
        catch (Exception e) {
            System.out.println("Erro ao validar a assinatura digital: " + e.getMessage());
            return false;
        }


        /*3. e listar o
        conteúdo do arquivo de índice apresentando APENAS os atributos dos arquivos (nome código,
        nome, dono e grupo) do usuário ou do grupo do usuário*/

        /*Formato : O arquivo de índice decriptado possui zero ou mais linhas
        formatadas da seguinte forma:
        NOME_CODIGO_DO_ARQUIVO<SP>NOME_SECRETO_DO_ARQUIVO<SP>DONO_ARQUIVO
        <SP><GRUPO_ARQUIVO><EOL>
        Onde:
        NOME_CODIGO_DO_ARQUIVO: caracteres alfanuméricos (nome código do arquivo).
        NOME_SECRETO_DO_ARQUIVO: caracteres alfanuméricos (nome original do arquivo).
        DONO_ARQUIVO: caracteres alfanuméricos (atributo do arquivo).
        GRUPO_ARQUIVO: caracteres alfanuméricos (atributo do arquivo).
        <SP> = caractere espaço em branco.
        <EOL> = caractere nova linha (\n). */

        try{
            String indexencDecriptedString = new String(indexencDecripted, "UTF-8");
            String[] lines = indexencDecriptedString.split("\n");

            // Exibir os atributos do arquivo
            System.out.printf("%-20s %-20s %-15s %-10s\n", "NOME_CODIGO_DO_ARQUIVO", "NOME_SECRETO_DO_ARQUIVO", "DONO_ARQUIVO", "GRUPO_ARQUIVO");
            System.out.println("----------------------------------------------------------------------------");

            for (String line : lines) {
                String[] attributes = line.split(" ");
                if (attributes.length >= 4) {
                    String nomeCodigo = attributes[0];
                    String nomeSecreto = attributes[1];
                    String donoArquivo = attributes[2];
                    String grupoArquivo = attributes[3];

                    // Exibir os atributos do arquivo
                    System.out.println("Nome Código: " + nomeCodigo);
                    System.out.println("Nome Secreto: " + nomeSecreto);
                    System.out.println("Dono Arquivo: " + donoArquivo);
                    System.out.println("Grupo Arquivo: " + grupoArquivo);
                    System.out.println("-----------------------------");
                }
            }

            return true;
        }


        catch(Exception e) {
            System.out.println("Erro ao listar o conteudo do arquivo de indice: " + e.getMessage());
            return false;
        }

    }   
     
}


