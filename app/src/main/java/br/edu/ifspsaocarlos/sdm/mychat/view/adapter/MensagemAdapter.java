package br.edu.ifspsaocarlos.sdm.mychat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;

/**
 * Created by Andrey Brugnera on 28/06/2017.
 */
public class MensagemAdapter extends ArrayAdapter<Mensagem> {
    private List<Mensagem> listaMensagens;
    private Context context;

    public MensagemAdapter(Context context, int resource, List<Mensagem> objects) {
        super(context, resource, objects);
        this.listaMensagens = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaMensagens != null ? listaMensagens.size() : 0;
    }

    @Override
    public Mensagem getItem(int position) {
        return listaMensagens != null ? listaMensagens.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return listaMensagens != null ? listaMensagens.get(position).getId() : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Mensagem mensagem = listaMensagens.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mensagem_layout, null);

            TextView tvRemetente = (TextView) convertView.findViewById(R.id.tv_remetente);
            TextView tvMensagem = (TextView) convertView.findViewById(R.id.tv_mensagem);

            viewHolder = new ViewHolder(tvRemetente, tvMensagem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getTvRemetente().setText(mensagem.getOrigem().getNome());
        viewHolder.getTvMensagem().setText(mensagem.getCorpo());
        return convertView;
    }

    private class ViewHolder {
        private TextView tvRemetente;
        private TextView tvMensagem;

        public ViewHolder(TextView tvRemetente, TextView tvMensagem) {
            this.tvRemetente = tvRemetente;
            this.tvMensagem = tvMensagem;
        }

        public TextView getTvRemetente() {
            return tvRemetente;
        }

        public TextView getTvMensagem() {
            return tvMensagem;
        }
    }
}
