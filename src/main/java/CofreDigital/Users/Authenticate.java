/*
  Lívia Lutz dos Santos, 2211055
  Ricardo Bastos Leta Vieira, 2110526
*/

package CofreDigital.Users;

import CofreDigital.DB.DB;

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
        if (emailUser.equals(email)) {
            System.out.println("Email válido.");
            return true;
        } 
        
        else {
            System.out.println("Email inválido.");
            return false;
        }
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

    public boolean tokenAuth(String token) {

        String tokenUser = db.getUserToken(token);
        if (tokenUser == null) {
            System.out.println("Token inválido ou não cadastrado.");
            return false;
        }

        //comparar token com o token do usuario
        if (tokenUser.equals(token)) {
            System.out.println("Token válido.");
            return true;
        } 
        
        else {
            System.out.println("Token inválido.");
            return false;
        }
    }
}
        


