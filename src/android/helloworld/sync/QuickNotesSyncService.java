package android.helloworld.sync;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class QuickNotesSyncService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		new SyncDataTask().execute();

		return START_NOT_STICKY;
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class SyncDataTask extends AsyncTask<Void, Integer, Void> {
		protected Void doInBackground(Void... p) {
			DataSync d = new DataSync(QuickNotesSyncService.this);
			d.syncPendingNotes();
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}
	}

}
