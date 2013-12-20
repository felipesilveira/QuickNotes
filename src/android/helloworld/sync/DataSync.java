package android.helloworld.sync;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.helloworld.QuickNotesProvider;
import android.net.Uri;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataSync {

	private static final String TAG = "DataSync";
	
    private Context mContext;

    public DataSync(Context context) {
    	mContext = context;
    }

	public void syncPendingNotes() {
		
		Log.v(TAG, "Iniciando");
    	// Retrieving unsynced tags
        String columns[] = new String[] { QuickNotesProvider.Notes.TEXT};
        Uri contentUri = QuickNotesProvider.Notes.CONTENT_URI;
        Cursor cur = mContext.getContentResolver().query(contentUri, columns,
        		QuickNotesProvider.Notes.SYNCED + "=0",
                null,
                null
        );
        if (cur.moveToFirst()) {
        	Log.v("DataSync", "Existem notas a serem enviadas.");
            String note = null;
            int id = 0;
            do {
                // Get the field values
                note = cur.getString(cur.getColumnIndex(QuickNotesProvider.Notes.TEXT));
                id = cur.getInt(cur.getColumnIndex(QuickNotesProvider.Notes.NOTE_ID));

                Log.v("DataSync", "Sincronizando " + note );
                if(sendPendingNote(note)) {
                	Log.v("DataSync", "Sincronizacao feita com sucesso!");
                	// Como a sincronizacao foi bem sucedida, vamos agora marcar a
                	// flag SYNCED com 1.
                	ContentValues values = new ContentValues();  
                    values.put(QuickNotesProvider.Notes.SYNCED, "1"); 
                	int result = mContext.getContentResolver().update(contentUri,
                			values,
                			QuickNotesProvider.Notes.NOTE_ID + "=" + id,
                			null);
                	Log.v(TAG, "Done. Result = "+result);

                }
 
            } while (cur.moveToNext());
        } else {
        	Log.v(TAG, "Sem novas notas");
        }
        cur.close();
    }

	public boolean sendPendingNote(String note) {

		String urlParameters = "nota="+note;
		String request = "http://www.felipesilveira.com.br/android-core/backend/send.php";
		URL url;
		try {
			url = new URL(request);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches (false);
	
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			connection.disconnect();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}
}
