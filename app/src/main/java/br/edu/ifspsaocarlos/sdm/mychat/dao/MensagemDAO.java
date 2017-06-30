package br.edu.ifspsaocarlos.sdm.mychat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;
import br.edu.ifspsaocarlos.sdm.mychat.model.Mensagem;

/**
 * Created by Andrey Brugnera on 27/06/2017.
 */
public class MensagemDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public MensagemDAO(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    /**
     * Busca todas as mensagens recebidas
     *
     * @param perfil
     * @param remetente
     * @return lista com todas as mensagens recebidas
     * ordenadas por id
     */
    public List<Mensagem> buscarMensagensRecebidas(Contato perfil, Contato remetente) {
        database = dbHelper.getReadableDatabase();
        Cursor cursor;
        List<Mensagem> listaMensagens = new ArrayList<>();

        String[] cols = new String[]{SQLiteHelper.MSG_ID, SQLiteHelper.MSG_ID_ORIGEM,
                SQLiteHelper.MSG_ID_DESTINO, SQLiteHelper.MSG_CORPO,
                SQLiteHelper.MSG_ASSUNTO};

        String where = SQLiteHelper.MSG_ID_DESTINO + " = ? AND " + SQLiteHelper.MSG_ID_ORIGEM + " = ? ";
        String[] argWhere = new String[]{String.valueOf(perfil.getId()), String.valueOf(remetente.getId())};

        cursor = database.query(SQLiteHelper.MENSAGENS_TABLE, cols, where, argWhere,
                null, null, SQLiteHelper.MSG_ID);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Mensagem mensagem = new Mensagem();
                mensagem.setId(cursor.getInt(0));
                mensagem.setIdOrigem(cursor.getInt(1));
                mensagem.setIdDestino(cursor.getInt(2));
                mensagem.setCorpo(cursor.getString(3));
                mensagem.setAssunto(cursor.getString(4));
                mensagem.setOrigem(remetente);
                mensagem.setDestino(perfil);

                listaMensagens.add(mensagem);
                cursor.moveToNext();
            }
            cursor.close();
        }
        database.close();
        return listaMensagens;
    }

    /**
     * Busca todas as mensagens enviadas
     *
     * @param perfil
     * @param destinatario
     * @return lista com todas as mensagens enviadas
     * para o destinatário ordenadas por id
     */
    public List<Mensagem> buscarMensagensEnviadas(Contato perfil, Contato destinatario) {
        List<Mensagem> listaMensagens = buscarMensagensRecebidas(destinatario, perfil);
        for (Mensagem mensagem : listaMensagens) {
            mensagem.setOrigem(perfil);
            mensagem.setDestino(destinatario);
        }
        return listaMensagens;
    }

    public void salvarMensagem(Mensagem mensagem) {
        database = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.MSG_ID, mensagem.getId());
        values.put(SQLiteHelper.MSG_ID_ORIGEM, mensagem.getIdOrigem());
        values.put(SQLiteHelper.MSG_ID_DESTINO, mensagem.getIdDestino());
        values.put(SQLiteHelper.MSG_CORPO, mensagem.getCorpo());
        values.put(SQLiteHelper.MSG_ASSUNTO, mensagem.getAssunto());
        database.insert(SQLiteHelper.MENSAGENS_TABLE, null, values);
        database.close();
    }
}
