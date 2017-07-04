package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
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

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.PerfilDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.service.VerificarNovasMensagensService;
import br.edu.ifspsaocarlos.sdm.mychat.util.ContatoUtil;
import br.edu.ifspsaocarlos.sdm.mychat.view.adapter.ContatoAdapter;
import br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS;

public class ListaContatosActivity extends ListActivity {
    private List<Contato> listaContatos = new ArrayList<>();
    private ContatoAdapter contatoAdapter;
    private Contato perfil;
    private PerfilDAO perfilDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos);

        perfilDao = new PerfilDAO(this);

        perfil = perfilDao.buscaPerfil();
        setTitle(getString(R.string.contatos_de) + " " + perfil.getNome());

        carregarContatos();

        //Menu de contexto
        registerForContextMenu(getListView());

        //Inicia serviço de busca de mensagens
        iniciarServicoBuscaMensagens();
    }

    private void iniciarServicoBuscaMensagens() {
        Intent intentService = new Intent(getApplicationContext(), VerificarNovasMensagensService.class);
        startService(intentService);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contatos, menu);

        // Obtém a referência para a action de busca:
        SearchView searchView;
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_pesquisar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_editar:
                editarPerfil();
                break;
            case R.id.menu_remover:
                removerContatosSelecionados();
                break;
            case R.id.menu_sair:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregarContatos() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> responseListener = recuperarContatosResponseListener(listaContatos);
        Response.ErrorListener errorListener = criarErrorResponseListener();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, ContatoWS.WS_CONTATO, null, responseListener, errorListener);
        requestQueue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> recuperarContatosResponseListener(final List<Contato> listaContatos) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray contatos = response.getJSONArray(ContatoWS.CONTATOS);
                    for (int i = 0; i < contatos.length(); i++) {
                        Contato contato = ContatoUtil.converterParaContato(contatos.getJSONObject(i));
                        listaContatos.add(contato);
                    }
                    ContatoUtil.ordenarPorNome(listaContatos);
                    configurarListAdapter();
                    contatoAdapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao tentar recuperar os contatos");
                }
            }
        };
    }

    private void editarPerfil() {
        Intent intent = new Intent(ListaContatosActivity.this, EditarPerfilActivity.class);
        intent.putExtra("perfil", perfil);
        startActivity(intent);
    }

    private void removerContatosSelecionados() {
        AlertDialog.Builder dialogRemoverContatos = new AlertDialog.Builder(this);
        dialogRemoverContatos.setTitle(getString(R.string.remover_contatos));
        dialogRemoverContatos.setMessage(getString(R.string.deseja_remover_contatos));
        dialogRemoverContatos.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Remove os contatos selecionados
                for (Contato contato : listaContatos) {
                    if (contato.isSelecionado()) {
                        removerContato(contato);
                    }
                }
            }
        });

        dialogRemoverContatos.setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogRemoverContatos.show();
    }

    private void removerContato(Contato contato) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Response.Listener<JSONObject> responseListener = removerContatoResponseListener(contato);
        Response.ErrorListener errorListener = criarErrorResponseListener();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, ContatoWS.WS_CONTATO + "/" + contato.getId(), null, responseListener, errorListener);
        requestQueue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> removerContatoResponseListener(final Contato contato) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        contato.setId(response.getInt(ContatoWS.ID));
                        listaContatos.remove(contato);
                        contatoAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException ex) {
                    Log.e(getString(R.string.app_name), "Erro ao excluir o contato");
                }
            }
        };
    }

    private Response.ErrorListener criarErrorResponseListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaContatosActivity.this, getString(R.string.erro_executar_operacao), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Contato destinatario = (Contato) getListAdapter().getItem(position);
        iniciarConversa(destinatario);
    }

    private void iniciarConversa(Contato destinatario) {
        Log.i(getString(R.string.app_name), "Iniciar chat com " + destinatario.getNome());
        Intent intent = new Intent(ListaContatosActivity.this, ChatActivity.class);
        intent.putExtra("perfil", perfil);
        intent.putExtra("destinatario", destinatario);
        startActivity(intent);
    }

    /**
     * Configura adapter de acordo com a ação
     * da activity, que pode ser de busca (carrega apenas os nomes procurados)
     * ou carrega todos os contatos
     */
    private void configurarListAdapter() {
        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
            //abriu essa activiy para buscar um contato por nome
            String nomeBuscado = getIntent().getStringExtra(SearchManager.QUERY);
            List<Contato> listaContatosEncontrados = null;
            for (Contato c : listaContatos) {
                boolean contemParteDoNome = c.getNome() != null && c.getNome().toUpperCase().contains(nomeBuscado.toUpperCase());
                boolean contemParteDoApelido = c.getApelido() != null && c.getApelido().toUpperCase().contains(nomeBuscado.toUpperCase());
                if (contemParteDoApelido || contemParteDoNome) {
                    if (listaContatosEncontrados == null) {
                        listaContatosEncontrados = new ArrayList();
                    }
                    listaContatosEncontrados.add(c);
                }
            }
            contatoAdapter = new ContatoAdapter(this, R.layout.contato_layout, listaContatosEncontrados);
        } else {
            contatoAdapter = new ContatoAdapter(this, R.layout.contato_layout, listaContatos);
        }

        setListAdapter(contatoAdapter);
    }
}
