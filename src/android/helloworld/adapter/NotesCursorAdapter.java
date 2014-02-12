package android.helloworld.adapter;

import android.content.Context;
import android.database.Cursor;
import android.helloworld.QuickNotesProvider;
import android.helloworld.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
 
public class NotesCursorAdapter extends CursorAdapter {
 
    public NotesCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.list_item, parent, false);
 
        return retView;
    }
 
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textViewNote = (TextView) view.findViewById(R.id.text);
        textViewNote.setText(cursor.getString(cursor.getColumnIndex(QuickNotesProvider.Notes.TEXT)));
 
    }

}