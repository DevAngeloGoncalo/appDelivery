package br.com.projetoDelivery.Activity;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.com.projetoDelivery.Model.Empresa;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.R;

public class ConfigEmpresaActivity extends AppCompatActivity {

    private static final int SELECIONAR_GALERIA = 200;

    //Firebase
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private String idUsuarioLogado;
    private String urlImagemEscolhida = "";

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

        materializarDadosEmpresa();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                Uri localImagem = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagem);

                if (imagem != null){

                    imagePerfilEmpresa.setImageBitmap(imagem);

                    //Salvamento, considerar colocar no botao salvar AG20211111
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado + "jpeg");

                    //Upload
                    UploadTask uploadTask= imagemRef.putBytes(dadosImg);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return imagemRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                urlImagemEscolhida = downloadUrl.toString();
                                Toast.makeText(ConfigEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ConfigEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ConfigEmpresaActivity.this, "ERRO AO FAZER UPLOAD DA IMAGEM",Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            //urlImagemEscolhida  = taskSnapshot.getStorage().getDownloadUrl().toString();
//
//
//                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    Uri url = task.getResult();
//
//                                }
//                            });
//
//                            Toast.makeText(ConfigEmpresaActivity.this, "SUCESSO AO FAZER UPLOAD DA IMAGEM",Toast.LENGTH_SHORT).show();
//                        }
//                    });

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

        storageReference = ConfigFireBase.getFirebaseStorage();
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();

        Picasso.get().setLoggingEnabled(true);
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    //public para ser acessado do botao
    public void validarDadosEmpresa(View view) {
        String nome = editEmpresaNome.getText().toString();
        String taxa = editEmpresaEntregaTaxa.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaEntregaTempo.getText().toString();

        if (!nome.isEmpty()) {
            if (!taxa.isEmpty()) {
                if (!categoria.isEmpty()) {
                    if (!tempo.isEmpty()) {

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setPrecoEntrega( Double.parseDouble(taxa));
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemEscolhida);
                        empresa.salvar();
                        finish();
                    }else{
                        exibirMensagem("Digite um tempo de entrega");
                    }
                }else{
                    exibirMensagem("Digite uma categoria");
                }
            }else{
                exibirMensagem("Digite uma taxa de entrega");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }

    }

    private void materializarDadosEmpresa(){

        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaEntregaTaxa.setText(empresa.getPrecoEntrega().toString());
                    editEmpresaEntregaTempo.setText(empresa.getTempo());
                    editEmpresaNome.setText(empresa.getNome());

                    urlImagemEscolhida = empresa.getUrlImagem();

                    if (urlImagemEscolhida != ""){
                        //Glide.with(getApplication()).load(urlImagemEscolhida).into(imagePerfilEmpresa);
                        Picasso.get().load(urlImagemEscolhida).into(imagePerfilEmpresa);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}