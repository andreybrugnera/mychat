package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;
import br.edu.ifspsaocarlos.sdm.mychat.util.MensagemUtil;
import br.edu.ifspsaocarlos.sdm.mychat.view.adapter.MensagemAdapter;
import br.edu.ifspsaocarlos.sdm.mychat.ws.MensagemWS;

/**
 * 1 - Serão carregadas as mensagens enviadas/recebidas
 * no banco de dados SQLite.
 * 2 - Serão requisitadas as novas mensagens do WS
 * com id maior que o maior id armazenado no banco de dados.
 * 3 - Novas mensagens serão armazenadas no banco interno.
 * 4 - Uma thread será iniciada para verificar novas mensagens
 * a cada TEMPO_ATUALIZACAO em milisegundos
 */
public class ChatActivity extends Activity {
    private Contato perfil;
    private Contato destinatario;
    private List<Mensagem> listaMensagens;
    private MensagemAdapter mensagemAdapter;
    private EditText etMensagem;
    private Integer idUltimaMensagem = -1;
    private MensagemDAO mensagemDao;
    private RecyclerView recyclerView;

    private static final int TEMPO_ATUALIZACAO = 2000;
    private AtualizaMensagensThread threadAtualizacao;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.recyclerView = (RecyclerView) findViewById(R.id.list_mensagens);
        this.etMensagem = (EditText) findViewById(R.id.et_mensagem);

        this.perfil = (Contato) getIntent().getSerializableExtra("perfil");
        this.destinatario = (Contato) getIntent().getSerializableExtra("destinatario");

        setTitle(getString(R.string.chat_com) + " " + destinatario.getNome());

        listaMensagens = new ArrayList<>();
        this.mensagemAdapter = new MensagemAdapter(this, listaMensagens, perfil);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(mensagemAdapter);

        //Carrega as mensagens armazenadas no banco SQLite interno
        carregarMensagensGravadas();
        MensagemUtil.ordenarPorId(listaMensagens);
        mensagemAdapter.notifyDataSetChanged();
        moverParaUltimaMensagem();
        //Carrega novas mensagens do WS
        //carregarMensagensWS();

        iniciarThreadAtualizacao();
    }

    private void iniciarThreadAtualizacao() {
        timer = new Timer();
        threadAtualizacao = new AtualizaMensagensThread();
        timer.scheduleAtFixedRate(threadAtualizacao, 0, TEMPO_ATUALIZACAO);
    }

    /**
     * Carrega todas as listaMensagens gravadas
     * no banco de dados SQLite recebidas e enviadas
     */
    private void carregarMensagensGravadas() {
        mensagemDao = new MensagemDAO(this);

        if (listaMensagens == null) {
            this.listaMensagens = new ArrayList<>();
        }

        List<Mensagem> liistaMensagensEnviadas = mensagemDao.buscarMensagens(perfil, destinatario);
        List<Mensagem> liistaMensagensRecebidas = mensagemDao.buscarMensagens(destinatario, perfil);

        this.listaMensagens.addAll(liistaMensagensEnviadas);
        this.listaMensagens.addAll(liistaMensagensRecebidas);
    }

    private void carregarMensagensWS() {
        //Carrega novas mensagens do WS
        Log.i(getString(R.string.app_name), "Buscando mensagens enviadas");
        carregarMensagensWS(perfil, destinatario);
        Log.i(getString(R.string.app_name), "Buscando mensagens recebidas");
        carregarMensagensWS(destinatario, perfil);
        atualizarIdUltimaMensagem();
    }

    private void carregarMensagensWS(Contato contato1, Contato contato2) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Response.Listener<JSONObject> responseListener = lerMensagensResponseListener();
        Response.ErrorListener errorListener = criarErrorResponseListener();
        String URL = MensagemWS.WS_MENSAGEM + "/" + idUltimaMensagem + "/" + contato1.getId() + "/" + contato2.getId();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, null, responseListener, errorListener);

        requestQueue.add(jsonRequest);
    }

    private void atualizarIdUltimaMensagem() {
        //O último id é sempre o maior, já que a lista é ordenada crescente por id
        if (!listaMensagens.isEmpty()) {
            this.idUltimaMensagem = listaMensagens.get(listaMensagens.size() - 1).getId();
        }
        Log.i(getString(R.string.app_name), "Maior id de mensagem recebida: " + idUltimaMensagem);
    }

    public void enviarMensagem(View v) {
        String textoMensagem = String.valueOf(etMensagem.getText());
        if (textoMensagem == null || textoMensagem.length() == 0) {
            return;
        }
        Mensagem mensagem = new Mensagem();
        mensagem.setDestino(destinatario);
        mensagem.setOrigem(perfil);
        mensagem.setCorpo(textoMensagem);
        JSONObject mensagemJSON;
        try {
            mensagemJSON = MensagemUtil.converterParaJSON(mensagem);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Response.Listener<JSONObject> responseListener = criarMensagemResponseListener(mensagem);
            Response.ErrorListener errorListener = criarErrorResponseListener();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, MensagemWS.WS_MENSAGEM, mensagemJSON, responseListener, errorListener);
            requestQueue.add(jsonRequest);
        } catch (JSONException ex) {
            Log.e(getString(R.string.app_name), "Erro ao converter a mensagem em JSON");
        }
    }

    private Response.Listener<JSONObject> lerMensagensResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        boolean novasMensagensEncontradas = false;
                        List<Mensagem> listaMensagensNovas = new ArrayList<>();

                        JSONArray mensagens = response.getJSONArray(MensagemWS.MENSAGENS);
                        for (int i = 0; i < mensagens.length(); i++) {
                            JSONObject jsonObject = mensagens.getJSONObject(i);
                            Mensagem mensagem = MensagemUtil.converterParaMensagem(jsonObject);
                            if (mensagem.getId() > idUltimaMensagem) {
                                listaMensagensNovas.add(mensagem);
                                mensagemDao.salvarMensagem(mensagem);
                                novasMensagensEncontradas = true;
                            }
                        }
                        //Atualiza lista
                        if (novasMensagensEncontradas) {
                            MensagemUtil.ordenarPorId(listaMensagensNovas);
                            listaMensagens.addAll(listaMensagensNovas);
                            mensagemAdapter.notifyDataSetChanged();
                            moverParaUltimaMensagem();
                            atualizarIdUltimaMensagem();
                        }
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao receber as mensagens");
                }
            }
        };
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
                        if (!listaMensagens.contains(mensagem)) {
                            listaMensagens.add(mensagem);
                            //Insere mensagem no banco
                            mensagemDao.salvarMensagem(mensagem);
                            //Atualiza lista
                            mensagemAdapter.notifyDataSetChanged();
                            moverParaUltimaMensagem();
                        }
                        etMensagem.setText("");
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

    /**
     * Move para último elemento
     * da lista de mensagens
     */
    private void moverParaUltimaMensagem() {
        recyclerView.smoothScrollToPosition(mensagemAdapter.getItemCount() - 1);
    }

    private class AtualizaMensagensThread extends TimerTask {
        @Override
        public void run() {
            carregarMensagensWS();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }
}
