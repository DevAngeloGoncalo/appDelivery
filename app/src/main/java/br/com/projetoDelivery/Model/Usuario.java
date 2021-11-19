package br.com.projetoDelivery.Model;

import com.google.firebase.database.DatabaseReference;

import br.com.projetoDelivery.Helper.ConfigFireBase;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String CEP;
    private String UF;
    private String Cidade;
    private String Bairro;
    private String Logradouro;
    private int NumeroEndereco;
    private String Complemento;
    private String NumeroContato;
    private boolean autenticado;

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

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public int getNumeroEndereco() {
        return NumeroEndereco;
    }

    public void setNumeroEndereco(int numeroEndereco) {
        NumeroEndereco = numeroEndereco;
    }

    public String getComplemento() {
        return Complemento;
    }

    public void setComplemento(String complemento) {
        Complemento = complemento;
    }

    public String getNumeroContato() {
        return NumeroContato;
    }

    public void setNumeroContato(String numeroContato) {
        NumeroContato = numeroContato;
    }

    public String getLogradouro() {
        return Logradouro;
    }

    public void setLogradouro(String logradouro) {
        Logradouro = logradouro;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}
