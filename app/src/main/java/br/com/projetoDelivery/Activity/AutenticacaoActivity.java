package br.com.projetoDelivery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.projetoDelivery.Helper.ConfigFireBase;
import br.com.projetoDelivery.Helper.usuarioFireBase;
import br.com.projetoDelivery.R;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        //Esconder tela
        //getSupportActionBar().hide();

        //Instanciar Autenticacao VERIFICAR SE PODE COLOCAR EM INICIALIZACOMPONENTES
        inicializaComponentes();
        //autenticacao = ConfigFireBase.getFirebaseAutenticacao();

        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{//usuario
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (verificarCampos(email, senha) == true){
                    //Verificando Switch
                    if(tipoAcesso.isChecked()){//Cadastro
                        cadastro(email, senha);
                    }else{//Login
                        login(email, senha);
                    }
                }
            }
        });
    }
    private boolean verificarCampos(String email, String senha){
        if (!email.isEmpty()){
            if (!senha.isEmpty()){
                return true;
            }else{
                Toast.makeText(AutenticacaoActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(AutenticacaoActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void login(String email, String senha){
        autenticacao.signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Logado realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    String tipoUsuario = task.getResult().getUser().getDisplayName();
                    abrirTelaPrincipal(tipoUsuario);
                }else
                {
                    String erroExcecaoLogin = "";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        erroExcecaoLogin = "Por favor, informe um e-mail cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecaoLogin = "Senha ou/e e-mail incorreto(s)";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecaoLogin = "Este conta já foi cadastrada";
                    } catch (Exception e) {
                        erroExcecaoLogin = " Não foi possível logar, entre em contato com o suporte"  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(AutenticacaoActivity.this,
                            "Erro:" + erroExcecaoLogin ,
                            Toast.LENGTH_LONG).show();

//                    Toast.makeText(AutenticacaoActivity.this,
//                            "Erro ao fazer login : " + task.getException() ,
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cadastro(String email, String senha)
    {
        autenticacao.createUserWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    String tipoUsuario = getTipoUsuario();
                    usuarioFireBase.atualizarTipoUsuario(tipoUsuario);
                    abrirTelaPrincipal(tipoUsuario);
                }
                else
                {
                    String erroExcecaoCadastro = "";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecaoCadastro = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecaoCadastro = "Por favor, digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecaoCadastro = "Este conta já foi cadastrada";
                    } catch (Exception e) {
                        erroExcecaoCadastro = "ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(AutenticacaoActivity.this,
                            "Erro: " + erroExcecaoCadastro ,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario){

        if (tipoUsuario.equals("E")){//empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
        }
    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if( usuarioAtual != null ){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editRegistroEmail);
        campoSenha = findViewById(R.id.editRegistroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        autenticacao = ConfigFireBase.getFirebaseAutenticacao();
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }
}