package br.com.projetoDelivery.Model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

import br.com.projetoDelivery.Helper.ConfigFireBase;

public class Pedido {
    private String idUsuario;
    private String idEmpresa;
    private String idPedido;

    private String nome;
    private String CEP;
    private String UF;
    private String Cidade;
    private String Bairro;
    private String Logradouro;
    private int NumeroEndereco;
    private String Complemento;
    private String NumeroContato;

    private String endereco;
    private List<ItemPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento = 0;
    private String observacao;

    public Pedido() {
    }

    public Pedido(String idUsu, String idEmp ) {
        setIdUsuario(idUsu);
        setIdEmpresa(idEmp);

        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(idEmp).child(idUsu);

        setIdPedido(pedidoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(getIdEmpresa()).child(getIdUsuario());

        pedidoRef.setValue(this);
    }

    public void excluir(){
        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(getIdEmpresa()).child(getIdUsuario());

        pedidoRef.removeValue();
    }

    public void confirmar(){
        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedido());

        pedidoRef.setValue(this);
    }

    public void atualizarStatus(){
        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfigFireBase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdEmpresa()).child(getIdPedido());

        pedidoRef.updateChildren(status);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
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

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public String getLogradouro() {
        return Logradouro;
    }

    public void setLogradouro(String logradouro) {
        Logradouro = logradouro;
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
}
