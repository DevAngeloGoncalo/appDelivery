package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.R;

public class CatalogoActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCatalogo;
    private ImageView imageEmpresaCatalogo;
    private TextView textNomeEmpresaCatalogo;

    private Empresa empresaEscolhida;
    private String idEmpresa;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        inicializarComponentes();

        //identificar empresa escolhida
        identificarEmpresaEscolhida();

        //MaterializarProdutos
        materializarProdutos();
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
}