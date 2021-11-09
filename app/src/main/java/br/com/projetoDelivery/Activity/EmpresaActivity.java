package br.com.projetoDelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toolbar;

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
        //setSupportActionBar(toolbar);
    }
}