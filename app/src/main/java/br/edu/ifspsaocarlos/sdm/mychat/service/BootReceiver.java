package br.edu.ifspsaocarlos.sdm.mychat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent iniciarServiceIntent = new Intent(context, VerificarNovasMensagensService.class);
        context.startService(iniciarServiceIntent);
    }
}
