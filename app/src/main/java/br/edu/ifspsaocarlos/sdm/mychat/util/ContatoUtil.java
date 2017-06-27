package br.edu.ifspsaocarlos.sdm.mychat.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS;

/**
 * Created by Andrey Brugnera on 20/06/2017.
 */
public class ContatoUtil {

    private ContatoUtil() {
    }

    /**
     * Ordena lista de contatos pelo nome
     *
     * @param listaContatos
     */
    public static void ordenarPorNome(List<Contato> listaContatos) {
        Comparator<Contato> comparador = new Comparator<Contato>() {
            @Override
            public int compare(Contato o1, Contato o2) {
                return o1.getNome().compareTo(o2.getNome());
            }
        };
        Collections.sort(listaContatos, comparador);
    }

    /**
     * Converte contato em objeto JSON
     *
     * @param contato
     * @return objeto JSON
     * @throws JSONException
     */
    public static JSONObject converterParaJSON(final Contato contato) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS.ID, contato.getId());
        jsonObject.put(br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS.NOME, contato.getNome());
        jsonObject.put(br.edu.ifspsaocarlos.sdm.mychat.ws.ContatoWS.APELIDO, contato.getApelido());
        return jsonObject;
    }

    /**
     * Converte json em um objeto do tipo contato
     *
     * @param json
     * @return objeto do tipo contato
     * @throws JSONException
     */
    public static Contato converterParaContato(final JSONObject json) throws JSONException {
        Contato contato = new Contato();
        contato.setId(json.getInt(ContatoWS.ID));
        contato.setNome(json.getString(ContatoWS.NOME));
        contato.setApelido(json.getString(ContatoWS.APELIDO));
        return contato;
    }

    /**
     * Salva o perfil do usuário padrão do sistema
     *
     * @param context
     * @param contato
     */
    public static void criarPerfil(Context context, Contato contato) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.perfil_usuario), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(context.getString(R.string.id_perfil), contato.getId());
        editor.putString(context.getString(R.string.nome_perfil), contato.getNome());
        editor.putString(context.getString(R.string.apelido_perfil), contato.getApelido());

        editor.commit();
    }
}