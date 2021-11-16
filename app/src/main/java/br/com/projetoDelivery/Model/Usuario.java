package br.com.projetoDelivery.Model;

import com.google.firebase.database.DatabaseReference;

import br.com.projetoDelivery.Helper.ConfigFireBase;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String endereco;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getIdUsuario());

        usuarioRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
