package br.edu.ifspsaocarlos.sdm.mychat.ws;

/**
 * Created by Andrey Brugnera on 25/06/2017.
 */

public abstract class MensagemWS extends WebServiceUtil {

    public static final String WS_MENSAGEM = SERVICE_URL + "/mensagem";
    public static final String WS_ADICIONAR_MENSAGEM = WS_MENSAGEM + "/add";

    public static final String ID = "id";
    public static final String ORIGEM_ID = "origem_id";
    public static final String DESTINO_ID = "destino_id";
    public static final String ASSUNTO = "assunto";
    public static final String CORPO = "corpo";
    public static final String ORIGEM = "origem";
    public static final String DESTINO = "destino";

}
