package br.com.projetoDelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import br.com.projetoDelivery.Model.Usuario;
import br.com.projetoDelivery.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Esconder tela
        //getSupportActionBar().hide();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                TelaAutenticacao();
            }
        }, 3000);
    }

    private void TelaAutenticacao(){
        Intent i = new Intent(SplashActivity.this, AutenticacaoActivity.class);
        //Intent i = new Intent(SplashActivity.this, PrincipalActivity.class);
        startActivity(i);
        finish();
    }
}