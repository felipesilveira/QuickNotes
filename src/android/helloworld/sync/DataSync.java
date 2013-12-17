package android.helloworld.sync;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.helloworld.QuickNotesProvider;
import android.net.Uri;
import android.util.Log;

public class DataSync {

	private static final String TAG = "DataSync";
	
    private static String URL = "http://177.71.249.232/WebService/SaveService.asmx ";
	
    private Context mContext;

	private static boolean sSyncing;
    
    public DataSync(Context context) {
    	mContext = context;
    }

	public void syncPendingNotes() {
		
		if(sSyncing) return;
		
		sSyncing = true;
		
		Log.v(TAG, "Starting sync process");
    	// Retrieving unsynced tags
        String columns[] = new String[] { QuickNotesProvider.Notes.TEXT};
        Uri contentUri = QuickNotesProvider.Notes.CONTENT_URI;
        Cursor cur = mContext.getContentResolver().query(contentUri, columns, // Which columns to return
        		QuickNotesProvider.Notes.SYNCED + "=0",
                null, // WHERE clause selection arguments (none)
                null // Order-by clause (ascending by name)
        );
        if (cur.moveToFirst()) {
        	Log.v("DataSync", "There are unsynced tags");
            String note = null;
            int id = 0;
            do {
                // Get the field values
                note = cur.getString(cur.getColumnIndex(QuickNotesProvider.Notes.TEXT));
                id = cur.getInt(cur.getColumnIndex(QuickNotesProvider.Notes.NOTE_ID));

                Log.v("DataSync", "Sincronizando " + note );
                if(sendPendingNote(note)) {
                	Log.v("DataSync", "Sync feito com sucesso!");
                	// marking as synced
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
        	Log.v(TAG, "No new records");
        }
        cur.close();
        sSyncing = false;
    }

	public boolean sendPendingNote(String note) {

		return false;
	}
}
