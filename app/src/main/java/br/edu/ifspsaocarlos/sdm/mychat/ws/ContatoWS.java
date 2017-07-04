package br.edu.ifspsaocarlos.sdm.mychat.ws;


/**
 * Created by Andrey Brugnera on 18/06/2017.
 */
public abstract class ContatoWS extends WebServiceUtil {

    public static final String WS_CONTATO = SERVICE_URL + "/contato";
    public static final String CONTATOS = "contatos";
    public static final String ID = "id";
    public static final String NOME = "nome_completo";
    public static final String APELIDO = "apelido";

    //Usado para identificar contatos deste aplicativo no WS
    public static final String APP_CONTATO_KEY = " [MY_CHAT]";
}