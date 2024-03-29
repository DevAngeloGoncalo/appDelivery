package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Listener.RecyclerItemClickListener;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.ItemPedido;
import br.com.projetoDelivery.Model.Pedido;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.Model.Usuario;
import br.com.projetoDelivery.R;
import dmax.dialog.SpotsDialog;

public class CatalogoActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCatalogo;
    private ImageView imageEmpresaCatalogo;
    private TextView textNomeEmpresaCatalogo, textCarrinhoQuantidade, textCarrinhoTotal;

    private Empresa empresaEscolhida;
    private String idEmpresa;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;

    private AlertDialog alertDialog; //app
    private String idUsuarioLogado;
    private Usuario usuario;

    private Pedido pedidoRecuperado;

    private int qtdItensCarrinho;
    private Double totalCarrinho;

    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        inicializarComponentes();

        //identificar empresa escolhida
        identificarEmpresaEscolhida();

        //EventoClique
        EventosCliqueProdutos();

        //MaterializarProdutos
        materializarProdutos();

        //Materializar dados do usuario
        materializarDadosUsuario();
    }

    private void EventosCliqueProdutos(){
        recyclerProdutosCatalogo.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutosCatalogo, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                quantidadeItem(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    private void quantidadeItem(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        //Considerar criar editText via XML AG20211115
        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQuantidade.getText().toString();
                Produto produtoEscolhido = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoEscolhido.getIdProduto());
                itemPedido.setNomeProduto(produtoEscolhido.getNome());
                itemPedido.setPreco(produtoEscolhido.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));

                //Estudar Colocar FOR para percorrer e verificar se ja existe do mesmo, caso exista, somar
                //nao deixar colocar 0 ou numero negativo AG20211115
                itensCarrinho.add(itemPedido);

                if(pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setCEP(usuario.getCEP());
                pedidoRecuperado.setCidade(usuario.getCidade());
                pedidoRecuperado.setBairro(usuario.getBairro());
                pedidoRecuperado.setEndereco(usuario.getLogradouro());
                pedidoRecuperado.setNumeroEndereco(usuario.getNumeroEndereco());
                pedidoRecuperado.setComplemento(usuario.getComplemento());
                pedidoRecuperado.setNumeroContato(usuario.getNumeroContato());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void materializarDadosUsuario(){
        alertDialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();

        alertDialog.show();

        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(idUsuarioLogado);
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    usuario = snapshot.getValue(Usuario.class);
                }

                //Materializa o pedido para não perder
                materializarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void materializarPedido(){
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(idEmpresa).child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(dataSnapshot.getValue() !=null){
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();
                    for(ItemPedido itemPedido : itensCarrinho){
                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde*preco);
                        qtdItensCarrinho += qtde;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");
                textCarrinhoQuantidade.setText("qtd: "+ qtdItensCarrinho);
                textCarrinhoTotal.setText("R$ "+df.format(totalCarrinho));

                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void materializarProdutos(){
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idEmpresa);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    produtos.add(dataSnapshot.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void identificarEmpresaEscolhida(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresaEscolhida = (Empresa) bundle.getSerializable("empresa");

            //idUsuario nesse caso é o id da empresa
            idEmpresa = empresaEscolhida.getIdUsuario();
            textNomeEmpresaCatalogo.setText(empresaEscolhida.getNomeFantasia());

            String url = empresaEscolhida.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCatalogo);
        }
    }

    private void inicializarComponentes(){
        recyclerProdutosCatalogo = findViewById(R.id.recyclerProdutosCatalogo);
        textNomeEmpresaCatalogo = findViewById(R.id.textNomeEmpresaCatalogo);
        imageEmpresaCatalogo = findViewById(R.id.imageEmpresaCatalogo);
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();

        textCarrinhoQuantidade = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura RecyclerView
        recyclerProdutosCatalogo.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCatalogo.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCatalogo.setAdapter(adapterProduto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalogo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPedido:
                confirmarPedido();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarPedido(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione forma de pagamento");


        //vai guardar a posicao
        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Máquina cartão"
        };

        //Botões de seleção
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = which;

            }
        });

        EditText editObservacao = new EditText(this);
        editObservacao.setHint("Observações:'Troco para 50'");
        builder.setView(editObservacao);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                pedidoRecuperado.setObservacao(observacao);
                pedidoRecuperado.setStatus("confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.excluir();
                pedidoRecuperado = null;
                Toast.makeText(CatalogoActivity.this, "Pedido enviado para a loja.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}