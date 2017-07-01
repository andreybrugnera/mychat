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
     * Busca todas as mensagens
     *
     * @param remetente
     * @param destinatario
     * @return lista com todas as mensagens
     * ordenadas por id
     */
    public List<Mensagem> buscarMensagens(Contato remetente, Contato destinatario) {
        database = dbHelper.getReadableDatabase();
        Cursor cursor;
        List<Mensagem> listaMensagens = new ArrayList<>();

        String[] cols = new String[]{SQLiteHelper.MSG_ID, SQLiteHelper.MSG_ID_ORIGEM,
                SQLiteHelper.MSG_ID_DESTINO, SQLiteHelper.MSG_CORPO,
                SQLiteHelper.MSG_ASSUNTO};

        String where = SQLiteHelper.MSG_ID_DESTINO + " = ? AND " + SQLiteHelper.MSG_ID_ORIGEM + " = ? ";
        String[] argWhere = new String[]{String.valueOf(destinatario.getId()), String.valueOf(remetente.getId())};

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
                mensagem.setDestino(destinatario);

                listaMensagens.add(mensagem);
                cursor.moveToNext();
            }
            cursor.close();
        }
        database.close();
        return listaMensagens;
    }

    /**
     * Persiste mensagem
     *
     * @param mensagem
     */
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
