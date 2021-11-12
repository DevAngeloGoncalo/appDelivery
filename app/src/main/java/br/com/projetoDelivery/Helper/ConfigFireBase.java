package br.com.projetoDelivery.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFireBase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenciaStorage;

    private static FirebaseDatabase db;


    //Retorna a referencia do database
    public static DatabaseReference getFirebase(){
        if( referenciaFirebase == null ){

            db = FirebaseDatabase.getInstance();
            referenciaFirebase = db.getReference("Empresa");
            referenciaFirebase.child("pato");
//            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
//            referenciaFirebase.setValue("25");
        }
        return referenciaFirebase;
    }

    //Retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if( referenciaAutenticacao == null ){
            referenciaAutenticacao = FirebaseAuth.getInstance() ;
        }
        return referenciaAutenticacao;
    }

    //Retorna instancia do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if( referenciaStorage == null ){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}
