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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigEmpresaActivity extends AppCompatActivity {

    private EditText editRazaoSocial, editEmpresaNomeFantasia, editEmpresaCNPJ, editTelefone,
                     editEmpresaCEP, editEmpresaEstado, editEmpresaCidade, editEmpresaBairro,
                     editEmpresaLogradouro, editNumeroEndereco, editUsuarioComplemento, editEmpresaEspecialidade;

    private CircleImageView imagePerfilEmpresa;
    private static final int SELECIONAR_GALERIA = 200;

    //Firebase
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private String idUsuarioLogado;
    private String urlImagemEscolhida = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        //Configurações iniciais
        inicializarComponentes();

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECIONAR_GALERIA);
                }
            }
        });

        //Recuperar dados da empresa
        materializarDadosEmpresa();
    }

    private void materializarDadosEmpresa(){
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Empresa empresa = snapshot.getValue(Empresa.class);

                    String numeroEndereco = String.valueOf(empresa.getNumeroEndereco());

                    editRazaoSocial.setText(empresa.getRazaoSocial());
                    editEmpresaNomeFantasia.setText(empresa.getNomeFantasia());
                    editEmpresaCNPJ.setText(empresa.getCnpj());
                    editTelefone.setText(empresa.getTelefone());
                    editEmpresaCEP.setText(empresa.getCep());
                    editEmpresaEstado.setText(empresa.getEstado());
                    editTelefone.setText(empresa.getTelefone());
                    editEmpresaCidade.setText(empresa.getCidade());
                    editEmpresaBairro.setText(empresa.getBairro());
                    editEmpresaLogradouro.setText(empresa.getLogradouro());
                    editNumeroEndereco.setText(numeroEndereco);
                    editUsuarioComplemento.setText(empresa.getComplemento());
                    editEmpresaEspecialidade.setText(empresa.getEspecialidade());

                    urlImagemEscolhida = empresa.getUrlImagem();

                    //Configurações Toolbar
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle("Configurações");
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    if(urlImagemEscolhida != ""){
                        Picasso.get().load(urlImagemEscolhida).into(imagePerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //public para ser acessado do botao
    public void validarDadosEmpresa(View view){
        String razaoSocial = editRazaoSocial.getText().toString();
        String nomeFantasia = editEmpresaNomeFantasia.getText().toString();
        String cnpj = editEmpresaCNPJ.getText().toString();
        String telefone = editTelefone.getText().toString();
        String cep = editEmpresaCEP.getText().toString();
        String estado = editEmpresaEstado.getText().toString();
        String cidade = editEmpresaCidade.getText().toString();
        String bairro = editEmpresaBairro.getText().toString();
        String logradouro = editEmpresaLogradouro.getText().toString();
        String numeroEndereco = editNumeroEndereco.getText().toString();
        String complemento = editUsuarioComplemento.getText().toString();
        String especialidade = editEmpresaEspecialidade.getText().toString();


        if(!razaoSocial.isEmpty()){
            if(!nomeFantasia.isEmpty()){
                if(!cnpj.isEmpty()){
                    if(!telefone.isEmpty()){
                        if(!cep.isEmpty()){
                            if(!estado.isEmpty()){
                                if(!cidade.isEmpty()){
                                    if(!bairro.isEmpty()){
                                        if(!logradouro.isEmpty()){
                                            if(!numeroEndereco.isEmpty()){
                                                if(!especialidade.isEmpty()){
                                                    if(!complemento.isEmpty()){
                                                        if(!urlImagemEscolhida.isEmpty()){
                                                            Empresa empresa = new Empresa();
                                                            empresa.setIdUsuario(idUsuarioLogado);
                                                            empresa.setRazaoSocial(razaoSocial);
                                                            empresa.setNomeFantasia(nomeFantasia);
                                                            empresa.setCnpj(cnpj);
                                                            empresa.setTelefone(telefone);
                                                            empresa.setCep(cep);
                                                            empresa.setEstado(estado);
                                                            empresa.setCidade(cidade);
                                                            empresa.setBairro(bairro);
                                                            empresa.setLogradouro(logradouro);
                                                            empresa.setNumeroEndereco(Integer.parseInt(numeroEndereco));
                                                            empresa.setEspecialidade(especialidade);
                                                            empresa.setComplemento(complemento);
                                                            empresa.setUrlImagem(urlImagemEscolhida);

                                                            empresa.salvar();
                                                            finish();

                                                            abrirTelaPrincipalEmpresa();
                                                        }else{
                                                            exibirMensagem("Escolha uma imagem");
                                                        }
                                                    }else{
                                                        exibirMensagem("Digite o complemento");
                                                    }
                                                }else{
                                                    exibirMensagem("Digite a especialidade");
                                                }
                                            }else{
                                                exibirMensagem("Digite o numero do endereço");
                                            }
                                        }else{
                                            exibirMensagem("Digite o logradouro");
                                        }
                                    }else{
                                        exibirMensagem("Digite o bairro");
                                    }
                                }else{
                                    exibirMensagem("Digite a Cidade");
                                }
                            }else{
                                exibirMensagem("Digite o Estado");
                            }
                        }else{
                            exibirMensagem("Digite o CEP");
                        }
                    }else{
                        exibirMensagem("Digite O Telefone");
                    }
                }else{
                    exibirMensagem("Digite o CNPJ");
                }
            }else{
                exibirMensagem("Digite o nome fantasia");
            }
        }else{
            exibirMensagem("Digite a razão social");
        }
    }

    private void abrirTelaPrincipalEmpresa(){
        startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {

                Uri localImagem = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);

                if(imagem!= null){
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    //Salvamento, considerar colocar no botao salvar AG20211111
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado).child("perfil").child(idUsuarioLogado + ".jpeg");
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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {

        editRazaoSocial = findViewById(R.id.editRazaoSocial);
        editEmpresaNomeFantasia = findViewById(R.id.editEmpresaNomeFantasia);
        editEmpresaCNPJ = findViewById(R.id.editEmpresaCNPJ);
        editTelefone = findViewById(R.id.editTelefone);
        editEmpresaCEP = findViewById(R.id.editEmpresaCEP);
        editEmpresaEstado = findViewById(R.id.editEmpresaEstado);
        editEmpresaCidade = findViewById(R.id.editEmpresaCidade);
        editEmpresaBairro = findViewById(R.id.editEmpresaBairro);
        editEmpresaLogradouro = findViewById(R.id.editEmpresaLogradouro);
        editNumeroEndereco = findViewById(R.id.editNumeroEndereco);
        editUsuarioComplemento = findViewById(R.id.editUsuarioComplemento);
        editEmpresaEspecialidade = findViewById(R.id.editEmpresaEspecialidade);

        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);

        storageReference = ConfigFireBase.getFirebaseStorage();
        firebaseRef = ConfigFireBase.getFirebase();
        idUsuarioLogado = UsuarioFireBase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}