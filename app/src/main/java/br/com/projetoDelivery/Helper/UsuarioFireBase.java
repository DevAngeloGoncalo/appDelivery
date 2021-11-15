package br.com.projetoDelivery.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFireBase {
    public static String getIdUsuario(){
        FirebaseAuth autenticacao = ConfigFireBase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static FirebaseUser getUserAtual(){
        FirebaseAuth usuario =  ConfigFireBase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipoUsuario(String tipo){
        try{
            FirebaseUser usuario = getUserAtual();
            //Verificar salvamento via firebase depois
            UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder().setDisplayName(tipo).build();
            usuario.updateProfile(perfil);
            return true;
        }catch (Exception e){
            //Caso de erros
            e.printStackTrace();
            return false;
        }

    }
}
