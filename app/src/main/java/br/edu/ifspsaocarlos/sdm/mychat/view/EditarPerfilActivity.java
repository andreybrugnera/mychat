package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.PerfilDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.util.ContatoUtil;
import br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS;

public class EditarPerfilActivity extends Activity {
    private TextView tvTitulo;
    private EditText editNome;
    private EditText editApelido;
    private PerfilDAO perfilDao;
    private Contato perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_perfil);
        setTitle(R.string.editar);

        perfil = (Contato) getIntent().getSerializableExtra("perfil");

        tvTitulo = (TextView) findViewById(R.id.tv_titulo);
        tvTitulo.setText(R.string.editar);
        editNome = (EditText) findViewById(R.id.et_nome);
        editNome.setText(perfil.getNome());
        editApelido = (EditText) findViewById(R.id.et_apelido);
        editApelido.setText(perfil.getApelido());

        perfilDao = new PerfilDAO(this);
    }

    public void salvarPerfil(View v) {
        perfil.setNome(String.valueOf(editNome.getText()));
        perfil.setApelido(String.valueOf(editApelido.getText()));

        JSONObject contatoJSON;
        try {
            contatoJSON = ContatoUtil.converterParaJSON(perfil);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Response.Listener<JSONObject> responseListener = atualizarPerfilResponseListener(perfil);
            Response.ErrorListener errorListener = criarErrorResponseListener();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, ContatoWS.WS_CONTATO, contatoJSON, responseListener, errorListener);
            requestQueue.add(jsonRequest);
        } catch (JSONException ex) {
            Log.e(getString(R.string.app_name), "Erro ao converter o usuário em JSON");
        }
    }

    private Response.Listener<JSONObject> atualizarPerfilResponseListener(final Contato perfil) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        perfil.setId(response.getInt(ContatoWS.ID));
                        perfil.setNome(response.getString(ContatoWS.NOME));
                        perfil.setApelido(response.getString(ContatoWS.APELIDO));
                        atualizarPerfil(perfil);
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao tentar editar o perfil");
                }
            }
        };
    }

    private Response.ErrorListener criarErrorResponseListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarPerfilActivity.this, getString(R.string.erro_executar_operacao), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void atualizarPerfil(Contato perfil) {
        perfilDao.atualizarPerfil(perfil);
        Toast.makeText(EditarPerfilActivity.this, getString(R.string.perfil_atualizado), Toast.LENGTH_LONG).show();
        finish();
    }
}
