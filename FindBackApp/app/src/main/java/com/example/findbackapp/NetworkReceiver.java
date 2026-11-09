package com.example.findbackapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

    public interface NetworkStateListener {
        void onNetworkChanged(boolean isConnected);
    }

    private static NetworkStateListener listener;

    public static void setListener(NetworkStateListener l) {
        listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean isConnected = isNetworkAvailable(context);
            if (listener != null) {
                listener.onNetworkChanged(isConnected);
            }
            if (!isConnected) {
                Toast.makeText(context, "網路連線中斷，部分資料可能無法即時同步", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "網路連線已復原", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
