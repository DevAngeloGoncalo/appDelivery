package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Listener.RecyclerItemClickListener;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.R;

public class EmpresaActivity extends AppCompatActivity{

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private int backButtonCount = 0;
    public static int Flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        //Configurações iniciais
        inicializarComponentes();

        //Materializar
        materializarProdutos();

        //eventos de toque
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutos,
                        new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {
                Flag = position;
                startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Produto produtoSelecionado = produtos.get(position);
                //Pedir  de exclusão AG20211115
                produtoSelecionado.excluir();
                Toast.makeText(EmpresaActivity.this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

    }

    //Sair do App
    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Pressione novamente para sair do app", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private void materializarProdutos(){
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);
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

    //Criar menus na tela
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Vai inicializar com os menus na tela

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair: deslogarUsuario(); break;
            case R.id.menuConfig: abrirConfig(); break;
            case R.id.menuNovoProduto: abrirNovoProduto();break;
            case R.id.menuListaPedido: abrirListaPedidos();break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            //Pedir  CONFIRMAÇÃO AG20211115
            autenticacao.signOut();
            finish();

            TelaInicial();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void TelaInicial(){
        startActivity(new Intent(EmpresaActivity.this, AutenticacaoActivity.class));
    }

    private void inicializarComponentes(){
        autenticacao = ConfigFireBase.getFirebaseAutenticacao();
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();
        recyclerProdutos = findViewById(R.id.recyclerProdutos);

        //Configura RecyclerView
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    //Configurações Toolbar
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle(empresa.getNomeFantasia());
                    setSupportActionBar(toolbar);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void abrirConfig(){
        startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
    private void abrirListaPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }
}