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
    private TextView textNomeEmpresaCatalogo;

    private Empresa empresaEscolhida;
    private String idEmpresa;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;

    private AlertDialog dialog; //app
    private String idUsuarioLogado;
    private Usuario usuario;

    private Pedido pedidoRecuperado;

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
                pedidoRecuperado.setEndereco(usuario.getEndereco());
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
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();

        dialog.show();

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
//        DatabaseReference pedidoRef = firebaseRef.child("pedidos_usuario").child(idEmpresa).child(idUsuarioLogado);
//        pedidoRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                qtdItensCarrinho = 0;
//                totalCarrinho = 0.0;
//                itensCarrinho = new ArrayList<>();
//                if(snapshot.getValue() !=null){
//                    pedidoRecuperado = snapshot.getValue(Pedido.class);
//                    itensCarrinho = pedidoRecuperado.getItens();
//                    for(ItemPedido itemPedido : itensCarrinho){
//                        int qtde = itemPedido.getQuantidade();
//                        Double preco = itemPedido.getPreco();
//
//                        totalCarrinho += (qtde*preco);
//                        qtdItensCarrinho += qtde;
//                    }
//                }
//
//                DecimalFormat df = new DecimalFormat("0.00");
//                textCarrinhoQtd.setText("qtd: "+ qtdItensCarrinho);
//                textCarrinhoTotal.setText("R$ "+df.format(totalCarrinho));
//
                dialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
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
            textNomeEmpresaCatalogo.setText(empresaEscolhida.getNome());

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

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos Disponíveis");
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
//                deslogarUsuario();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}