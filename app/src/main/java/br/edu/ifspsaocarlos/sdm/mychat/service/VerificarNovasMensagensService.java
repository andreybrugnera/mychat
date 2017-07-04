package br.edu.ifspsaocarlos.sdm.mychat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
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
import br.edu.ifspsaocarlos.sdm.mychat.dao.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.mychat.dao.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;
import br.edu.ifspsaocarlos.sdm.mychat.util.ContatoUtil;
import br.edu.ifspsaocarlos.sdm.mychat.util.MensagemUtil;
import br.edu.ifspsaocarlos.sdm.mychat.view.ChatActivity;
import br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS;
import br.edu.ifspsaocarlos.sdm.mychat.ws.MensagemWS;

/**
 * Created by Andrey Brugnera on 02/07/2017.
 * Busca novas mensagens do WS, se houver alguma nova registra no
 * banco de dados e exibe notificação de chegada.
 * Ao clicar na notificação, a tela de chat é aberta.
 */
public class VerificarNovasMensagensService extends Service {
    private List<Contato> listaContatos;
    private Contato perfil;
    private ContatoDAO contatoDao;
    private MensagemDAO mensagemDao;

    private Response.Listener<JSONObject> contatosResponseListener;
    private Response.ErrorListener errorResponseListener;

    private Timer timer;
    private VerificarMensagensThread verificarMensagensThread;
    private int TEMPO_ATUALIZACAO = 10000;

    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(getString(R.string.app_name), "Iniciando serviço de verificação de mensagens");

        contatoDao = new ContatoDAO(this);
        mensagemDao = new MensagemDAO(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        carregarPerfil();

        //Busca contatos e as novas mensagens recebidas
        timer = new Timer();
        verificarMensagensThread = new VerificarMensagensThread();
        timer.scheduleAtFixedRate(verificarMensagensThread, TEMPO_ATUALIZACAO, TEMPO_ATUALIZACAO);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();
    }

    private void carregarPerfil() {
        perfil = contatoDao.buscaPerfil();
    }

    private void atualizarListaContatosEBuscarNovasMensagens() {
        Log.i(getString(R.string.app_name), "Carregando lista de contatos");
        if (listaContatos == null) {
            listaContatos = new ArrayList<>();
        }
        listaContatos.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> responseListener = recuperarContatosResponseListener(listaContatos);
        Response.ErrorListener errorListener = criarErrorResponseListener();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, ContatoWS.WS_CONTATO, null, responseListener, errorListener);
        requestQueue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> recuperarContatosResponseListener(final List<Contato> listaContatos) {
        if (contatosResponseListener == null) {
            contatosResponseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray contatos = response.getJSONArray(ContatoWS.CONTATOS);
                        for (int i = 0; i < contatos.length(); i++) {
                            JSONObject contatoJson = contatos.getJSONObject(i);
                            Contato contato = ContatoUtil.converterParaContato(contatoJson);
                            if (!contato.equals(perfil) && ContatoUtil.isContatoDoApp(contatoJson)) {
                                listaContatos.add(contato);
                            }
                        }
                        buscarNovasMensagens();
                    } catch (JSONException ex) {
                        Log.e(getString(R.string.app_name), "Erro ao recuperar a lista de contatos");
                    }
                }
            };
        }
        return contatosResponseListener;
    }

    private Response.ErrorListener criarErrorResponseListener() {
        if (errorResponseListener == null) {
            errorResponseListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(VerificarNovasMensagensService.this, getString(R.string.erro_executar_operacao), Toast.LENGTH_LONG).show();
                }
            };
        }
        return errorResponseListener;
    }

    /**
     * Busca novas mensagens
     * de todos os contatos
     */
    private void buscarNovasMensagens() {
        Log.i(getString(R.string.app_name), "Buscando novas mensagens");
        for (Contato contato : listaContatos) {
            List<Mensagem> mensagensRecebidas = mensagemDao.buscarMensagens(contato, perfil);
            if (!mensagensRecebidas.isEmpty()) {
                Mensagem ultimaMensage = mensagensRecebidas.get(mensagensRecebidas.size() - 1);
                carregarMensagensWS(contato, perfil, ultimaMensage.getId());
            }
        }
    }

    private void carregarMensagensWS(Contato contato1, Contato contato2, int idUltimaMensagem) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> responseListener = lerMensagensResponseListener(idUltimaMensagem);
        Response.ErrorListener errorListener = criarErrorResponseListener();
        String URL = MensagemWS.WS_MENSAGEM + "/" + idUltimaMensagem + "/" + contato1.getId() + "/" + contato2.getId();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, null, responseListener, errorListener);
        requestQueue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> lerMensagensResponseListener(final int idUltimaMensagem) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        JSONArray mensagens = response.getJSONArray(MensagemWS.MENSAGENS);
                        for (int i = 0; i < mensagens.length(); i++) {
                            JSONObject jsonObject = mensagens.getJSONObject(i);
                            Mensagem mensagem = MensagemUtil.converterParaMensagem(jsonObject);
                            if (mensagem.getId() > idUltimaMensagem) {
                                mensagemDao.salvarMensagem(mensagem);
                                criarNotificacao(mensagem);
                            }
                        }
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao receber as mensagens");
                }
            }
        };
    }

    /**
     * Exibe notificação de nova mensagem
     *
     * @param mensagem
     */
    private void criarNotificacao(Mensagem mensagem) {
        Log.i(getString(R.string.app_name), "Criando notificação de nova mensagem " + mensagem);

        //Cria notificação
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.nova_mensagem_de) + " " + mensagem.getOrigem().getNome());
        notificationBuilder.setContentText(mensagem.getCorpo());
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(Boolean.TRUE);

        //Intent para abrir tela do chat quando clicar na notificação
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("perfil", perfil);
        chatIntent.putExtra("destinatario", mensagem.getOrigem());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatActivity.class);

        stackBuilder.addNextIntent(chatIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(mensagem.getId(), notificationBuilder.build());
    }

    /**
     * Thread para buscar as mensagens
     */
    private class VerificarMensagensThread extends TimerTask {

        @Override
        public void run() {
            atualizarListaContatosEBuscarNovasMensagens();
        }
    }
}
