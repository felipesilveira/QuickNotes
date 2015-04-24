package android.helloworld.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SyncerBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
    	ConnectivityManager cm =
    	        (ConnectivityManager)context.getSystemService(
    	        		Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	
    	boolean isConnected = (activeNetwork != null) && activeNetwork.isConnected();
        if (isConnected) {
        	Intent i = new Intent(context, QuickNotesSyncService.class);
        	context.startService(i);
        }
    }
}
