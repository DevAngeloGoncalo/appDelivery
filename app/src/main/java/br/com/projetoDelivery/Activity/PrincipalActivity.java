package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterEmpresa;
import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.R;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private Toolbar toolbar;
    private RecyclerView recyclerEmpresas;
    private List<Empresa> empresas = new ArrayList<>();

    private DatabaseReference fireBaseRef;
    private AdapterEmpresa adapterEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializarComponentes();

        materializarEmpresas();

        configurarSearchView();
    }

    private void configurarSearchView(){
        //Configurar SearchView
        searchView.setHint("Pesquisar Estabelecimentos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });
    }

    private void pesquisarEmpresas(String pesquisa){
        DatabaseReference empresasRef = fireBaseRef.child("empresas");

        //estudar colocar um nome_filtro com tolower para pesquisar
        Query query = empresasRef.orderByChild("nome").startAt(pesquisa).endAt(pesquisa + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    empresas.add(dataSnapshot.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void materializarEmpresas(){
        DatabaseReference empresaRef = fireBaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    empresas.add(dataSnapshot.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
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
        recyclerEmpresas = findViewById(R.id.recyclerEmpresas);
        toolbar = findViewById(R.id.toolbar);

        autenticacao = ConfigFireBase.getFirebaseAutenticacao();
        fireBaseRef = ConfigFireBase.getFirebase();

        //Configurações Toolbar
        toolbar.setTitle("projetoDelivery");
        setSupportActionBar(toolbar);

        //Configura RecyclerView
        recyclerEmpresas.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresas.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresas.setAdapter(adapterEmpresa);

    }

}