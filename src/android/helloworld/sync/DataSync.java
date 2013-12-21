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
import java.net.URLEncoder;
import java.util.Scanner;

public class DataSync {

	private static final String TAG = "DataSync";

	private Context mContext;

	public DataSync(Context context) {
		mContext = context;
	}

	public void syncPendingNotes() {

		Log.v(TAG, "Iniciando");
		// Procurando no banco de dados as entradas com SYNCED = 0,
		// o que significa que tal dado ainda nao foi enviado ao 
		// servidor remoto.
		String columns[] = new String[] { 
				QuickNotesProvider.Notes.TEXT, 
				QuickNotesProvider.Notes.NOTE_ID};
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
				// Obtendo o valor do campo text
				note = cur.getString(cur.getColumnIndex(QuickNotesProvider.Notes.TEXT));
				id = cur.getInt(cur.getColumnIndex(QuickNotesProvider.Notes.NOTE_ID));

				Log.v("DataSync", "Sincronizando " + note );
				if(sendPendingNote(note)) {
					Log.v("DataSync", "Sincronizacao feita com sucesso!");
					// Como a sincronizacao foi bem sucedida, vamos agora marcar a
					// flag SYNCED com 1.
					ContentValues values = new ContentValues();  
					values.put(QuickNotesProvider.Notes.SYNCED, "1"); 
					mContext.getContentResolver().update(contentUri,
							values,
							QuickNotesProvider.Notes.NOTE_ID + "=" + id,
							null);
				}

			} while (cur.moveToNext());
		} else {
			Log.v(TAG, "Sem novas notas");
		}
		cur.close();
	}

	public boolean sendPendingNote(String note) {
		try {
			String urlParameters = "nota=" + URLEncoder.encode(note,"UTF-8");
			String request = "http://tests.felipesilveira.com.br/android-core/insert.php";
			URL url;

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

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			String response = "";
			Scanner inStream = new Scanner(connection.getInputStream());

			while (inStream.hasNextLine()) {
			    response += (inStream.nextLine());
			}
			
			// Removendo possiveis quebras de linha
			response.replaceAll("\n", "");
	        Log.v("DataSync", "Resposta do server=("+response+")");
	        
	        inStream.close();
			connection.disconnect();
			
			// Quando a nota eh corretamente salva no servidor,
			// este responde com 1.
			return response.equals("1");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return false;
	}
}
