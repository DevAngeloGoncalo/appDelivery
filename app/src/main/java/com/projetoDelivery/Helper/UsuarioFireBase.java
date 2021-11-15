package com.projetoDelivery.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFireBase {

    public static String getIdUsuario(){
        FirebaseAuth autenticacao = ConfigFireBase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfigFireBase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarTipoUsuario(String tipo){
        try{
            FirebaseUser user = getUsuarioAtual();
            //Verificar salvamento via firebase depois
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(tipo).build();
            user.updateProfile(profile);
            return true;

        }catch (Exception e){
            //Caso de erros
            e.printStackTrace();
            return false;
        }
    }
}
