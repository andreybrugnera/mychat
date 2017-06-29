package br.edu.ifspsaocarlos.sdm.mychat.util;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;
import br.edu.ifspsaocarlos.sdm.mychat.ws.MensagemWS;

/**
 * Created by Andrey Brugnera on 28/06/2017.
 */
public class MensagemUtil {

    /**
     * Converte mensagem em objeto JSON
     *
     * @param mensagem
     * @return objeto JSON
     * @throws JSONException
     */
    public static JSONObject converterParaJSON(final Mensagem mensagem) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(MensagemWS.ID, mensagem.getId());
        jsonObject.put(MensagemWS.CORPO, mensagem.getCorpo());
        jsonObject.put(MensagemWS.ASSUNTO, mensagem.getAssunto());
        jsonObject.put(MensagemWS.ORIGEM_ID, mensagem.getIdOrigem());
        jsonObject.put(MensagemWS.DESTINO_ID, mensagem.getIdDestino());

        return jsonObject;
    }

    /**
     * Converte json em um objeto do tipo mensagem
     *
     * @param json
     * @return objeto do tipo mensagem
     * @throws JSONException
     */
    public static Mensagem converterParaMensagem(final JSONObject json) throws JSONException {
        Mensagem mensagem = new Mensagem();
        mensagem.setId(json.getInt(MensagemWS.ID));
        mensagem.setCorpo(json.getString(MensagemWS.CORPO));
        mensagem.setAssunto(json.getString(MensagemWS.ASSUNTO));
        //Converte os contatos de origem e destino
        JSONObject jsonOrigem = json.getJSONObject(MensagemWS.ORIGEM);
        JSONObject jsonDestino = json.getJSONObject(MensagemWS.DESTINO);
        Contato origem = ContatoUtil.converterParaContato(jsonOrigem);
        Contato destino = ContatoUtil.converterParaContato(jsonDestino);

        mensagem.setOrigem(origem);
        mensagem.setDestino(destino);
        return mensagem;
    }
}
