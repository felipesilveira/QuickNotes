package android.helloworld;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.helloworld.sync.DataSync;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*
 * Activity responsável por mostrar as notas remotas
 */
public class RemoteNotesActivity extends Activity {
    private static final String TAG = "RemoteNotesActivity";

    private ListAdapter adapter;
    private List<String> notesList;
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.remote_notes);

        list = (ListView)findViewById(R.id.notes_remote_list);

        notesList = DataSync.fetchNotes();
        adapter = new NoteListAdapter(this, notesList);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                final String note = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(RemoteNotesActivity.this, NoteDetailsActivity.class);
                intent.putExtra(NoteDetailsActivity.NOTE, note);
                startActivity(intent);
            }
        });

		Button exitButton = (Button) findViewById(R.id.button1);
		exitButton.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
	        	  finish();
	          }
		});
    }

    private class NoteListAdapter extends ArrayAdapter<String> {

        private List<String> notes;

        public NoteListAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            notes = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.remote_list_item, null);

            ((TextView) v.findViewById(R.id.text)).setText(notes.get(position));
            return v;
        }
    }

}
