package android.helloworld;

import android.helloworld.R;
import android.helloworld.adapter.NotesCursorAdapter;
import android.helloworld.sync.QuickNotesSyncService;
import android.app.Activity;
import android.app.ListActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class MainActivity extends BaseActivity {

	private static final String TAG = "QuickNotesMainActivity";
	private Cursor mCursor;

	static {  
		System.loadLibrary("ndk1");  
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Criando a MainActivity");

		setContentView(R.layout.main);

		Button insertButton = (Button)findViewById(R.id.insert_button);
		insertButton.setOnClickListener(mInsertListener);

		// adicionando um 'Hint' ao Editbox.
		EditText editBox = (EditText)findViewById(R.id.edit_box);
		editBox.setHint("Nova nota...");

		mCursor = this.getContentResolver().
				query(QuickNotesProvider.Notes.CONTENT_URI, null, null, null, null);

		ListView list = (ListView)findViewById(R.id.notes_list);

		ListAdapter adapter = new NotesCursorAdapter(this, mCursor);

		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				String note = cursor.getString(cursor.getColumnIndex(QuickNotesProvider.Notes.TEXT));

				Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
				intent.putExtra(NoteDetailsActivity.NOTE, note);
				startActivity(intent);
			}
		});
	}

	/*
	 *  Definindo um OnClickListener para o botao "Inserir"
	 */
	private OnClickListener mInsertListener = new OnClickListener() {
		public void onClick(View v) {
			EditText editBox = (EditText)findViewById(R.id.edit_box);
			addNote(editBox.getText().toString());
			editBox.setText("");
		}
	};

	/*
	 * Metodo responsavel por inserir um registro no content provider
	 */
	protected void addNote(String text) {
		ContentValues values = new ContentValues();
		values.put(QuickNotesProvider.Notes.TEXT, text);
		values.put(QuickNotesProvider.Notes.SYNCED, 0);

		getContentResolver().insert(
				QuickNotesProvider.Notes.CONTENT_URI, values);

		ConnectivityManager cm =
				(ConnectivityManager)getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		boolean isConnected = (activeNetwork != null) && activeNetwork.isConnected();
		if (isConnected) {
			Intent i = new Intent(this, QuickNotesSyncService.class);
			startService(i); 
		}

		helloLog("Nota corretamente adicionada!");  
	}

	private native void helloLog(String logThis);

	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_remote:
			Intent intent = new Intent(MainActivity.this, RemoteNotesActivity.class);
			startActivity(intent);
			break;

		}
		return true;
	}
}