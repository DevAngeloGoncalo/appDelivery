package br.com.projetoDelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.R;

public class CatalogoActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCatalogo;
    private ImageView imageEmpresaCatalogo;
    private TextView textNomeEmpresaCatalogo;
    private Empresa empresaEscolhida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        inicializarComponentes();

        //identificar empresa escolhida
        identificarEmpresaEscolhida();
    }

    private void identificarEmpresaEscolhida(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresaEscolhida = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresaCatalogo.setText(empresaEscolhida.getNome());

            String url = empresaEscolhida.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCatalogo);
        }
    }

    private void inicializarComponentes(){
        recyclerProdutosCatalogo = findViewById(R.id.recyclerProdutosCatalogo);
        textNomeEmpresaCatalogo = findViewById(R.id.textNomeEmpresaCatalogo);
        imageEmpresaCatalogo = findViewById(R.id.imageEmpresaCatalogo);

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos Disponíveis");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}