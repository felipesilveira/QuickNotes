package android.helloworld;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.helloworld.sync.QuickNotesSyncService;
import android.helloworld.sync.SyncerBroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private static final String TAG = "QuickNotesMainActivity";
	private Cursor mCursor;

	/** Invocado quando a Activity é criada */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Criando a MainActivity");

		setContentView(R.layout.main);

		Intent i = new Intent(this, WelcomeActivity.class);
		startActivity(i);

		Button insertButton = (Button)findViewById(R.id.insert_button);
		insertButton.setOnClickListener(mInsertListener);

		// adicionando um 'Hint' ao Editbox.
		EditText editBox = (EditText)findViewById(R.id.edit_box);
		editBox.setHint("Nova nota...");

		mCursor = this.getContentResolver().
				query(QuickNotesProvider.Notes.CONTENT_URI, null, null, null, null);

		ListAdapter adapter = new SimpleCursorAdapter(
				// O primeiro parametro é o context.
				this, 
				// O segundo, o layout de cada item. 
				R.layout.list_item,
				// O terceiro parametro eh o cursor que contem os dados
				// a serem mostrados
				mCursor,
				// o quarto parametro eh um array com as colunas do cursor
				// que serao mostradas
				new String[] {QuickNotesProvider.Notes.TEXT},
				// o quinto parametro é um array (com o mesmo tamanho
				// do anterior) com os elementos que receberao
				// os dados.
				new int[] {R.id.text});

		setListAdapter(adapter);

	}

	/*
	 *  Definindo um OnClickListener para o botão "Inserir"
	 */
	private OnClickListener mInsertListener = new OnClickListener() {
		public void onClick(View v) {
			EditText editBox = (EditText)findViewById(R.id.edit_box);
			addNote(editBox.getText().toString());
			editBox.setText("");

		}
	};

	/*
	 * Método responsável por inserir um registro no content provider
	 */
	protected void addNote(String text) {
		AddNoteTask task = new AddNoteTask();
		// É necessário passar como parâmetro um array de strings,
		// que será o parâmetro recebido pelo método doInBackground.
		// Neste caso, será passado um array com apenas um item: A nova nota
		task.execute(new String[] { text } );
	}

	private class AddNoteTask extends AsyncTask<String, Void, Uri> {
		@Override
		protected Uri doInBackground(String... string) {
			ContentValues values = new ContentValues();
			values.put(QuickNotesProvider.Notes.TEXT, string[0]);

			// Gravando a flag SYNCED com o valor 0, significando que 
			// a nota ainda não foi enviada ao servidor
			values.put(QuickNotesProvider.Notes.SYNCED, "0");

			Uri inserted = getContentResolver().insert(
					QuickNotesProvider.Notes.CONTENT_URI, values);

			ConnectivityManager cm =
					(ConnectivityManager)getSystemService(
							Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

			boolean isConnected = (activeNetwork != null) && activeNetwork.isConnected();

			// Iremos executar o serviço de sincronização apenas se houver conectividade com rede.
			// Caso contrário, iremos registrar o SyncerBroadcastReceiver para ser executado
			// assim que houver uma mudança no estado de conectividade.
			if (isConnected) {
				Intent i = new Intent(MainActivity.this, QuickNotesSyncService.class);
				startService(i); 
			} else {
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

				SyncerBroadcastReceiver receiver = new SyncerBroadcastReceiver();
				registerReceiver(receiver, filter);
			}

			return inserted;
		}

		@Override
		protected void onPostExecute(Uri result) {
			// Após a inserção da nota, vamos mostrar um Toast ao usuário
			Toast toast = Toast.makeText(MainActivity.this, 
					"Nota inserida com sucesso!", 
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
