package android.helloworld;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	private static ArrayList<Activity> activities = new ArrayList<Activity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activities.add(this);	
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// terminar todas activities ativas
		for(Activity a : activities) {
			a.finish();
		}
	}
}
