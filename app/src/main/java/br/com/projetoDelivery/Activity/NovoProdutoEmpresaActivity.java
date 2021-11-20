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
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Adapter.AdapterEmpresa;
import br.com.projetoDelivery.Adapter.AdapterProduto;
import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Model.Empresa;
import br.com.projetoDelivery.Model.Produto;
import br.com.projetoDelivery.Model.Usuario;
import br.com.projetoDelivery.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private CircleImageView imageProdutoCatalogo;
    private String idUsuarioLogado, idProduto;
    private List<Produto> produtos = new ArrayList<>();
    private Produto produto;
    private String urlImagemEscolhida = "";

    private static final int SELECIONAR_GALERIA = 200;

    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configurações iniciais
        inicializarComponentes();

        //Para acessar uma imagem
        imageProdutoCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECIONAR_GALERIA);
                }
            }
        });

        if (EmpresaActivity.Flag != -1)
        {
            quantidadeProdutos();
        }


    }
    private void quantidadeProdutos(){
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    produtos.add(dataSnapshot.getValue(Produto.class));
                }
                materializarDadosProduto();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });
    }
    public void materializarDadosProduto(){


        int position = EmpresaActivity.Flag;
        Produto produtoSelecionado = produtos.get(position);
        idProduto = produtoSelecionado.getIdProduto();
        //DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado).child(idProduto);
        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado).child(idProduto);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    produto = snapshot.getValue(Produto.class);

                    editProdutoNome.setText(produto.getNome());
                    editProdutoDescricao.setText(produto.getDescricao());

                    String preco = String.valueOf(produto.getPreco());
                    editProdutoPreco.setText(preco);

                    urlImagemEscolhida = produto.getUrlImagem();

                    if(urlImagemEscolhida != ""){

                        Picasso.get().load(urlImagemEscolhida).into(imageProdutoCatalogo);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void validarDadosProduto(View view) {
        String nome = editProdutoNome.getText().toString();
        String preco = editProdutoPreco.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!preco.isEmpty()) {
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.setUrlImagem(urlImagemEscolhida);

                    produto.salvar();
                    exibirMensagem("Produto salvo com sucesso!");

                    finish();


                } else {
                    exibirMensagem("Digite um preço para o produto");
                }

            } else {
                exibirMensagem("Digite uma descrição para o produto");
            }

        } else {
            exibirMensagem("Digite um nome para o produto");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            Bitmap imagem = null;
            try {

                Uri localImagem = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);

                if(imagem!= null){

                    imageProdutoCatalogo.setImageBitmap(imagem);

                    //Salvamento, considerar colocar no botao salvar AG20211111
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado).child("produtos").child(produto.getIdProduto() +".jpeg");

                    //Upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImg);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    urlImagemEscolhida = url.toString();

                                    Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {

        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
        imageProdutoCatalogo = findViewById(R.id.imageProduto);

        storageReference = ConfigFireBase.getFirebaseStorage();
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();
        produto = new Produto(idUsuarioLogado);


        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}