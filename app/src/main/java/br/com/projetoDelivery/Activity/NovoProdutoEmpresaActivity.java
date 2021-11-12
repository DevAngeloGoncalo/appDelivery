package br.com.projetoDelivery.Activity;

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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.R;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private ImageView imageProduto;
    private String idUsuarioLogado;
    //COLOCAR OPÇÃO DE TIRAR FOTO
    private static final int SELECIONAR_GALERIA = 200;

    private StorageReference storageReference;
    private String urlImagemEscolhida = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializarComponentes();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPadrao);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Para acessar uma imagem
        imageProduto.setOnClickListener(new View.OnClickListener() {
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

                Uri localImagem = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagem);

                if (imagem != null){

                    imageProduto.setImageBitmap(imagem);

                    //Salvamento, considerar colocar no botao salvar AG20211111
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    //pegar nome produto
                    Produto produto = new Produto();
                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child("produtos").child(idUsuarioLogado + "jpeg");

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
                                Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    //public para ser acessado do botao
    public void validarDadosProduto(View view) {
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!preco.isEmpty()) {
                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.setUrlImagem(urlImagemEscolhida);
                    produto.salvar();
                    finish();
                    exibirMensagem("Produto salvo com sucesso!");
                }else{
                    exibirMensagem("Digite o preço do produto");
                }
            }else{
                exibirMensagem("Digite uma descrição");
            }

        }else{
            exibirMensagem("Digite um nome do produto");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    private void inicializarComponentes(){
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);

        imageProduto = findViewById(R.id.imageProduto);

        storageReference = ConfigFireBase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();

    }
}