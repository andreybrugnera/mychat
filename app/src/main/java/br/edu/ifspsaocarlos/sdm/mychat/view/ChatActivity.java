package br.edu.ifspsaocarlos.sdm.mychat.view;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.dao.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;

public class ChatActivity extends Activity {
    private Contato perfil;
    private Contato destinatario;
    private List<Mensagem> mensagens;

    private MensagemDAO mensagemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        perfil = (Contato) getIntent().getSerializableExtra("perfil");
        destinatario = (Contato) getIntent().getSerializableExtra("destinatario");

        setTitle(getString(R.string.chat_com) + " " + destinatario.getNome());

        mensagens = carregarMensagensGravadas();
    }

    /**
     * Carrega todas as mensagens gravadas
     * no banco de dados SQLite recebidas e enviadas
     */
    private List<Mensagem> carregarMensagensGravadas() {
        mensagemDao = new MensagemDAO(this);
        return mensagemDao.buscarMensagensEnviadasERecebidas(perfil, destinatario);
    }
}
