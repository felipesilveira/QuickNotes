package android.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoteDetailsActivity extends Activity {

	protected static final String NOTE = "note";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		
		TextView noteTextView = (TextView) findViewById(R.id.noteTextView);
		noteTextView.setText(getIntent().getStringExtra(NOTE));
		
		Button exitButton = (Button) findViewById(R.id.button1);
		exitButton.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
	        	  finish();
	          }
		});
	}

}
