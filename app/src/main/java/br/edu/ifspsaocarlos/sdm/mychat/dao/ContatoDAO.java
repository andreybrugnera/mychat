package br.edu.ifspsaocarlos.sdm.mychat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.edu.ifspsaocarlos.sdm.mychat.model.Contato;

/**
 * Created by Andrey Brugnera on 26/06/2017.
 */
public class ContatoDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public ContatoDAO(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    public Contato buscaPerfil() {
        database = dbHelper.getReadableDatabase();
        Cursor cursor;
        Contato perfil = null;

        String[] cols = new String[]{SQLiteHelper.PERFIL_ID, SQLiteHelper.PERFIL_NOME, SQLiteHelper.PERFIL_APELIDO};
        String where = null;
        String[] argWhere = null;

        cursor = database.query(SQLiteHelper.PERFIL_TABLE, cols, where, argWhere,
                null, null, SQLiteHelper.PERFIL_ID);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                perfil = new Contato();
                perfil.setId(cursor.getInt(0));
                perfil.setNome(cursor.getString(1));
                perfil.setApelido(cursor.getString(2));
                break;
            }
            cursor.close();
        }
        database.close();
        return perfil;
    }

    public void atualizarPerfil(Contato c) {
        database = dbHelper.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(SQLiteHelper.PERFIL_ID, c.getId());
        updateValues.put(SQLiteHelper.PERFIL_NOME, c.getNome());
        updateValues.put(SQLiteHelper.PERFIL_APELIDO, c.getApelido());
        database.update(SQLiteHelper.PERFIL_TABLE, updateValues, SQLiteHelper.PERFIL_ID + "="
                + c.getId(), null);
        database.close();
    }

    public void criarPerfil(Contato c) {
        Contato perfilExistente = buscaPerfil();
        if (perfilExistente == null) {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.PERFIL_ID, c.getId());
            values.put(SQLiteHelper.PERFIL_NOME, c.getNome());
            values.put(SQLiteHelper.PERFIL_APELIDO, c.getApelido());
            database.insert(SQLiteHelper.PERFIL_TABLE, null, values);
            database.close();
        } else {
            perfilExistente = new Contato(c);
            atualizarPerfil(perfilExistente);
        }
    }
}
