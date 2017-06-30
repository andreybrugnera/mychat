package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;
import br.edu.ifspsaocarlos.sdm.mychat.util.MensagemUtil;
import br.edu.ifspsaocarlos.sdm.mychat.view.adapter.MensagemAdapter;
import br.edu.ifspsaocarlos.sdm.mychat.ws.MensagemWS;

public class ChatActivity extends Activity {
    private Contato perfil;
    private Contato destinatario;
    private List<Mensagem> listaMensagens;
    private ListView listView;
    private MensagemAdapter mensagemAdapter;
    private EditText etMensagem;
    private Integer idUltimaMensagem = 0;

    private MensagemDAO mensagemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.listView = (ListView) findViewById(R.id.list_mensagens);
        this.etMensagem = (EditText) findViewById(R.id.et_mensagem);

        this.perfil = (Contato) getIntent().getSerializableExtra("perfil");
        this.destinatario = (Contato) getIntent().getSerializableExtra("destinatario");

        setTitle(getString(R.string.chat_com) + " " + destinatario.getNome());

        carregarMensagensGravadas();

        mensagemAdapter = new MensagemAdapter(this, R.layout.mensagem_recebida_layout, listaMensagens, perfil);
        listView.setAdapter(mensagemAdapter);
    }

    /**
     * Carrega todas as listaMensagens gravadas
     * no banco de dados SQLite recebidas e enviadas
     */
    private void carregarMensagensGravadas() {
        mensagemDao = new MensagemDAO(this);

        this.listaMensagens = new ArrayList<>();

        Log.i("APP", "Reading enviadas");
        List<Mensagem> liistaMensagensEnviadas = mensagemDao.buscarMensagensEnviadas(perfil, destinatario);
        Log.i("APP", "Reading recebidas");
        List<Mensagem> liistaMensagensRecebidas = mensagemDao.buscarMensagensRecebidas(perfil, destinatario);

        this.listaMensagens.addAll(liistaMensagensEnviadas);
        this.listaMensagens.addAll(liistaMensagensRecebidas);

        Log.i("APP", "Sorting");
        MensagemUtil.ordenarPorId(this.listaMensagens);

        atualizarIdUltimaMensagem();
    }

    private void atualizarIdUltimaMensagem() {
        //O último id é sempre o maior, já que a lista é ordenada crescente por id
        if (!listaMensagens.isEmpty()) {
            this.idUltimaMensagem = listaMensagens.get(listaMensagens.size() - 1).getId();
        }
    }

    public void enviarMensagem(View v) {
        Mensagem mensagem = new Mensagem();
        mensagem.setDestino(destinatario);
        mensagem.setOrigem(perfil);
        mensagem.setCorpo(String.valueOf(etMensagem.getText()));
        JSONObject mensagemJSON;
        try {
            mensagemJSON = MensagemUtil.converterParaJSON(mensagem);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Response.Listener<JSONObject> responseListener = criarMensagemResponseListener(mensagem);
            Response.ErrorListener errorListener = criarErrorResponseListener();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, MensagemWS.WS_ADICIONAR_MENSAGEM, mensagemJSON, responseListener, errorListener);
            requestQueue.add(jsonRequest);
        } catch (JSONException ex) {
            Log.e(getString(R.string.app_name), "Erro ao converter a mensagem em JSON");
        }
    }

    private Response.Listener<JSONObject> criarMensagemResponseListener(final Mensagem mensagem) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        mensagem.setId(response.getInt(MensagemWS.ID));
                        mensagem.setAssunto(response.getString(MensagemWS.ASSUNTO));
                        mensagem.setAssunto(response.getString(MensagemWS.CORPO));
                        mensagem.setIdOrigem(response.getInt(MensagemWS.ORIGEM_ID));
                        mensagem.setIdDestino(response.getInt(MensagemWS.DESTINO_ID));
                        listaMensagens.add(mensagem);
                        //Insere mensagem no banco
                        mensagemDao.salvarMensagem(mensagem);
                        //Atualiza lista
                        mensagemAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao tentar enviar a mensagem");
                }
            }
        };
    }

    private Response.ErrorListener criarErrorResponseListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatActivity.this, getString(R.string.erro_executar_operacao), Toast.LENGTH_LONG).show();
            }
        };
    }
}
