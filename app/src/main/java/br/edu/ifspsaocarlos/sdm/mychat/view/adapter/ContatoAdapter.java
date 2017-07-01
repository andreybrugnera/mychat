package br.edu.ifspsaocarlos.sdm.mychat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.R;
import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;

/**
 * Created by Andrey Brugnera on 20/06/2017.
 */
public class ContatoAdapter extends ArrayAdapter<Contato> {
    private List<Contato> listaContatos;
    private Context context;

    public ContatoAdapter(Context context, int resource, List<Contato> objects) {
        super(context, resource, objects);
        this.listaContatos = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listaContatos != null ? listaContatos.size() : 0;
    }

    @Override
    public Contato getItem(int position) {
        return listaContatos != null ? listaContatos.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return listaContatos != null ? listaContatos.get(position).getId() : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contato_layout, null);

            CheckBox chkSelecionado = (CheckBox) convertView.findViewById(R.id.cb_selecionado);
            TextView txtNome = (TextView) convertView.findViewById(R.id.et_nome);
            TextView txtApelido = (TextView) convertView.findViewById(R.id.et_apelido);

            viewHolder = new ViewHolder(txtNome, txtApelido, chkSelecionado);
            convertView.setTag(viewHolder);

            viewHolder.getComboBoxSelecionado().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox chkBox = (CheckBox) v;
                    Contato contato = (Contato) chkBox.getTag();
                    contato.setSelecionado(chkBox.isChecked());
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contato contato = listaContatos.get(position);
        viewHolder.getNome().setText(contato.getNome());
        viewHolder.getApelido().setText(contato.getApelido());
        viewHolder.getComboBoxSelecionado().setChecked(contato.isSelecionado());
        viewHolder.getComboBoxSelecionado().setTag(contato);

        return convertView;
    }

    private class ViewHolder {
        private TextView nome;
        private TextView apelido;
        private CheckBox comboBoxSelecionado;

        public ViewHolder(TextView nome, TextView apelido, CheckBox comboBoxSelecionado) {
            this.nome = nome;
            this.apelido = apelido;
            this.comboBoxSelecionado = comboBoxSelecionado;
        }

        public TextView getNome() {
            return nome;
        }

        public TextView getApelido() {
            return apelido;
        }

        public CheckBox getComboBoxSelecionado() {
            return comboBoxSelecionado;
        }
    }
}
