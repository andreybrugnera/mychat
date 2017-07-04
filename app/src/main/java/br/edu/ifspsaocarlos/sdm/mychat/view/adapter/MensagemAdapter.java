package br.edu.ifspsaocarlos.sdm.mychat.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;

/**
 * Created by Andrey Brugnera on 28/06/2017.
 */
public class MensagemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Mensagem> listaMensagens;
    private Contato perfil;

    private static final int TIPO_MESSAGEM_ENVIADA = 1;
    private static final int TIPO_MESSAGEM_RECEBIDA = 2;

    public MensagemAdapter(Context context, List<Mensagem> listaMensagens, Contato perfil) {
        this.context = context;
        this.listaMensagens = listaMensagens;
        this.perfil = perfil;
    }

    @Override
    public int getItemCount() {
        return listaMensagens != null ? listaMensagens.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = listaMensagens.get(position);
        if (mensagem.getOrigem().equals(perfil)) {
            return TIPO_MESSAGEM_ENVIADA;
        } else {
            return TIPO_MESSAGEM_RECEBIDA;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == TIPO_MESSAGEM_ENVIADA) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mensagem_enviada_layout, parent, false);
        } else if (viewType == TIPO_MESSAGEM_RECEBIDA) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mensagem_recebida_layout, parent, false);
        }
        return new MensagemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Mensagem messagem = listaMensagens.get(position);
        ((MensagemHolder) holder).bind(messagem);
    }

    private class MensagemHolder extends RecyclerView.ViewHolder {
        TextView tvRemetente;
        TextView tvMensagem;

        MensagemHolder(View itemView) {
            super(itemView);
            tvRemetente = (TextView) itemView.findViewById(R.id.tv_remetente);
            tvMensagem = (TextView) itemView.findViewById(R.id.tv_mensagem);
        }

        void bind(Mensagem messagem) {
            tvRemetente.setText(messagem.getOrigem().getNome());
            tvMensagem.setText(messagem.getCorpo());
        }
    }
}