package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.UsuarioFireBase;
import br.com.projetoDelivery.Model.Usuario;
import br.com.projetoDelivery.R;

public class ConfigUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioComplemento,
            editCEP, editEstado, editCidade, editUsuarioBairro, editUsuarioLogradouro,
            editNumeroEndereco, editNumeroContato;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_usuario);

        //Configurações iniciais
        inicializarComponentes();

        //Recuperar dados do usuário
        materializarDadosUsuario();
    }

    private void materializarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() !=null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editCEP.setText(usuario.getCEP());
                    editEstado.setText(usuario.getUF());
                    editCidade.setText(usuario.getCidade());
                    editUsuarioBairro.setText(usuario.getBairro());
                    editUsuarioLogradouro.setText(usuario.getLogradouro());

                    String numeroEndereco = String.valueOf(usuario.getNumeroEndereco());

                    editNumeroEndereco.setText(numeroEndereco);
                    editNumeroContato.setText(usuario.getNumeroContato());
                    editUsuarioComplemento.setText(usuario.getComplemento());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(){
        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editCEP = findViewById(R.id.editCEP);
        editEstado = findViewById(R.id.editEstado);
        editCidade = findViewById(R.id.editCidade);
        editUsuarioBairro = findViewById(R.id.editUsuarioBairro);
        editUsuarioLogradouro = findViewById(R.id.editUsuarioLogradouro);
        editNumeroEndereco = findViewById(R.id.editNumeroEndereco);
        editUsuarioComplemento = findViewById(R.id.editUsuarioComplemento);
        editNumeroContato = findViewById(R.id.editNumeroContato);


        firebaseRef = ConfigFireBase.getFirebase();
        idUsuario = UsuarioFireBase.getIdUsuario();
    }
    public void validarDadosUsuario(View view){
        String nome = editUsuarioNome.getText().toString();
        String cep = editCEP.getText().toString();
        String estado = editEstado.getText().toString();
        String cidade = editCidade.getText().toString();
        String bairro = editUsuarioBairro.getText().toString();
        String logradouro = editUsuarioLogradouro.getText().toString();
        String numeroEndereco = editNumeroEndereco.getText().toString();
        String complemento = editUsuarioComplemento.getText().toString();
        String numeroContato = editNumeroContato.getText().toString();


        //Pensar em uma forma melhor
        if(!nome.isEmpty()){
            if(!cep.isEmpty()){
                if(!estado.isEmpty()){
                    if(!cidade.isEmpty()){
                        if(!bairro.isEmpty()){
                            if(!logradouro.isEmpty()){
                                if(!numeroEndereco.isEmpty()){
                                    if(!complemento.isEmpty()){
                                        if(!numeroContato.isEmpty()){
                                            Usuario usuario = new Usuario();
                                            usuario.setNumeroContato(numeroContato);
                                            usuario.setIdUsuario(idUsuario);
                                            usuario.setNome(nome);
                                            usuario.setCEP(cep);
                                            usuario.setUF(estado);
                                            usuario.setCidade(cidade);
                                            usuario.setBairro(bairro);
                                            usuario.setLogradouro(logradouro);
                                            usuario.setNumeroEndereco(Integer.parseInt(numeroEndereco));
                                            usuario.setComplemento(complemento);
                                            usuario.setAutenticado(true);
                                            usuario.salvar();

                                            exibirMensagem("Dados atualizado com sucesso!");
                                            finish();

                                            abrirTelaPrincipal();
                                        }else{
                                            exibirMensagem("Digite seu Númere de Celular!");
                                        }
                                    }else{
                                        exibirMensagem("Digite seu Complemento!");
                                    }
                                }else{
                                    exibirMensagem("Digite seu Número");
                                }
                            }else{
                                exibirMensagem("Digite seu logradouro!");
                            }
                        }else{
                            exibirMensagem("Digite seu Bairro!");
                        }
                    }else{
                        exibirMensagem("Digite sua Cidade!");
                    }
                }else{
                    exibirMensagem("Digite seu Estado!");
                }
            }else{
                exibirMensagem("Digite seu CEP!");
            }
        }else{
            exibirMensagem("Digite seu nome!");
        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void abrirTelaPrincipal(){
        startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
    }
}