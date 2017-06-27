package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.mychat.R;

public class MainActivity extends Activity {
    private TextView tvEntrar;
    private TextView tvCriarPerfil;
    private TextView tvSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEntrar = (TextView) findViewById(R.id.tv_entrar);
        tvCriarPerfil = (TextView) findViewById(R.id.tv_criar_perfil);
        tvSair = (TextView) findViewById(R.id.tv_sair);
    }
}
