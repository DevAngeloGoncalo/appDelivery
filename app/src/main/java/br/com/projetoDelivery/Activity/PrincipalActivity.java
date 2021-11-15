package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.R;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializarComponentes();
    }

    //Criar menus na tela
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Vai inicializar com os menus na tela
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        MenuItem item =  menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        //SearchView searchView1 = (SearchView) item.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair : deslogarUsuario(); break;
            case R.id.menuConfig : abrirConfig(); break;
//            case R.id.menuNovoProduto : abrirPesquisa(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            //Pedir Confirmacao AG20211115
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfig(){
        startActivity(new Intent(PrincipalActivity.this, ConfigUsuarioActivity.class));
    }
    private void inicializarComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        toolbar = findViewById(R.id.toolbar);

        autenticacao = ConfigFireBase.getFirebaseAutenticacao();

        //Configurações Toolbar
        toolbar.setTitle("projetoDelivery");
        setSupportActionBar(toolbar);

    }

}