package br.edu.ifspsaocarlos.sdm.mychat.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrey Brugnera on 26/06/2017.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mychat.db";
    public static final String PERFIL_TABLE = "perfil";
    public static final String MENSAGENS_TABLE = "mensagens";
    public static final int DATABASE_VERSION = 1;

    public static final String MSG_ID = "id";
    public static final String MSG_ID_ORIGEM = "id_origem";
    public static final String MSG_ID_DESTINO = "id_destino";
    public static final String MSG_ASSUNTO = "assunto";
    public static final String MSG_CORPO = "corpo";

    public static final String PERFIL_ID = "id";
    public static final String PERFIL_NOME = "nome_completo";
    public static final String PERFIL_APELIDO = "apelido";

    private static final String CREATE_TABLE_PERFIL = "CREATE TABLE " + PERFIL_TABLE + " (" +
            PERFIL_ID + " INTEGER PRIMARY KEY, " +
            PERFIL_NOME + " TEXT NOT NULL, " +
            PERFIL_APELIDO + " TEXT NOT NULL) ";

    private static final String CREATE_TABLE_MENSAGEM = "CREATE TABLE " + MENSAGENS_TABLE + " (" +
            MSG_ID + " INTEGER PRIMARY KEY, " +
            MSG_ID_ORIGEM + " INTEGER NOT NULL, " +
            MSG_ID_DESTINO + " INTEGER NOT NULL, " +
            MSG_CORPO + " TEXT, " +
            MSG_ASSUNTO + " TEXT) ";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_PERFIL);
        database.execSQL(CREATE_TABLE_MENSAGEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        //...
    }
}
