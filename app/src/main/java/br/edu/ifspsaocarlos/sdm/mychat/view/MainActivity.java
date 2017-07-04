package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;

public class MainActivity extends Activity {
    private Button btEntrar;
    private Button btCriarPerfil;
    private Contato perfil;

    private ContatoDAO contatoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Desabilita toolbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        contatoDao = new ContatoDAO(this);

        btEntrar = (Button) findViewById(R.id.bt_entrar);
        btCriarPerfil = (Button) findViewById(R.id.bt_criar_perfil);

        atualizarStatusDosComandos();
    }

    private void atualizarStatusDosComandos() {
        btEntrar.setVisibility(perfilCriado() ? View.VISIBLE : View.GONE);
        btCriarPerfil.setVisibility(perfilCriado() ? View.GONE : View.VISIBLE);
    }

    private boolean perfilCriado() {
        this.perfil = contatoDao.buscaPerfil();
        return perfil != null;
    }

    public void entrar(View v) {
        Intent intent = new Intent(MainActivity.this, ListaContatosActivity.class);
        startActivity(intent);
    }

    public void criarPerfil(View v) {
        Intent intent = new Intent(MainActivity.this, CriarPerfilActivity.class);
        startActivity(intent);
    }

    public void sair(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarStatusDosComandos();
    }
}
