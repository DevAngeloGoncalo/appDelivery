package br.com.projetoDelivery.Activity;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import br.com.projetoDelivery.R;

public class ConfigEmpresaActivity extends AppCompatActivity {
    //pensar na possibilidade de tirar uma foto para usuario
    private static final int SELECIONAR_GALERIA = 200;
    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaEntregaTempo, editEmpresaEntregaTaxa;
    private ImageView imagePerfilEmpresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        inicializarComponentes();


        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPadrao);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Para acessar uma imagem
        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECIONAR_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECIONAR_GALERIA:
                        Uri localImagem = data.getData(); break;
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagem); break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void inicializarComponentes(){
        editEmpresaEntregaTaxa = findViewById(R.id.editEmpresaEntregaTaxa);
        editEmpresaEntregaTempo = findViewById(R.id.editEmpresaEntregaTempo);
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
    }
}