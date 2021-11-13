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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Listener.RecyclerItemClickListener;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.R;

public class EmpresaActivity extends AppCompatActivity {

    private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private String idUsuarioLogado;
    private String urlImagemEscolhida = "";
    private ImageView imageProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        inicializarComponentes();

        autenticacao = ConfigFireBase.getFirebaseAutenticacao();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPadrao);
        toolbar.setTitle("Empresa");
        setSupportActionBar(toolbar);

        //Configuração RecyclerView
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        //Materializar
        materializarProdutos();

        //eventos de toque
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }

            @Override
            public void onLongItemClick(View view, int position) {
                Produto prdutoEscolhido = produtos.get(position);
                prdutoEscolhido.excluir();
                Toast.makeText(EmpresaActivity.this, "Produto Removido com sucesso", Toast.LENGTH_LONG);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    private void materializarProdutos(){
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
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
            case R.id.menuSair : deslogarUsuario(); break;
            case R.id.menuConfig : abrirConfig(); break;
            case R.id.menuNovoProduto : abrirNovoProduto(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfig(){
        startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

    private void inicializarComponentes(){
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();
    }
}