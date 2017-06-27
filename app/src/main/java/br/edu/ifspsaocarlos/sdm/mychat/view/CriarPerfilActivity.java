package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class CriarPerfilActivity extends Activity {
    private EditText editNome;
    private EditText editApelido;
    private PerfilDAO perfilDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_perfil);
        setTitle(getString(R.string.novo_contato));

        editNome = (EditText) findViewById(R.id.nome);
        editApelido = (EditText) findViewById(R.id.apelido);

        perfilDao = new PerfilDAO(this);
    }

    public void criarPerfil(View v) {
        Contato usuario = new Contato();
        usuario.setNome(String.valueOf(editNome.getText()));
        usuario.setApelido(String.valueOf(editApelido.getText()));

        JSONObject contatoJSON;
        try {
            contatoJSON = ContatoUtil.converterParaJSON(usuario);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Response.Listener<JSONObject> responseListener = criarPerfilResponseListener(usuario);
            Response.ErrorListener errorListener = criarErrorResponseListener();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, ContatoWS.WS_CONTATO, contatoJSON, responseListener, errorListener);
            requestQueue.add(jsonRequest);
        } catch (JSONException ex) {
            Log.e(getString(R.string.app_name), "Erro ao converter o usuário em JSON");
        }
    }

    private Response.Listener<JSONObject> criarPerfilResponseListener(final Contato contato) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        contato.setId(response.getInt(ContatoWS.ID));
                        contato.setNome(response.getString(ContatoWS.NOME));
                        contato.setApelido(response.getString(ContatoWS.APELIDO));
                        criarPerfil(contato);
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao tentar criar o perfil");
                }
            }
        };
    }

    private Response.ErrorListener criarErrorResponseListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CriarPerfilActivity.this, getString(R.string.erro_executar_operacao), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void criarPerfil(Contato contato) {
        perfilDao.criarPerfil(contato);
        Toast.makeText(CriarPerfilActivity.this, getString(R.string.perfil_criado), Toast.LENGTH_LONG).show();
        abrirListaDeContatos();
    }

    private void abrirListaDeContatos() {
        Intent intent = new Intent(CriarPerfilActivity.this, ListaContatosActivity.class);
        startActivity(intent);
    }
}
