package br.com.projetoDelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.auth.FirebaseAuth;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.R;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        autenticacao = ConfigFireBase.getFirebaseAutenticacao();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPadrao);
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);
    }


    //Criar menus na tela
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }
}