package edu.olemiss.rcollier.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	int requestInput = 1;
	Button btneasy, btnmedium, btnhard, btnhighscores, btndone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btneasy = (Button) findViewById(R.id.easy);
		btnmedium = (Button) findViewById(R.id.medium);
		btnhard = (Button) findViewById(R.id.hard);
		btnhighscores = (Button) findViewById(R.id.highscores);
		//btndone = (Button) findViewById(R.id.done);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onClick(View v) {
		Button clicked = (Button) v;
		if (clicked.getId() == btnhighscores.getId()) {
			Intent i = new Intent("edu.olemiss.rcollier.mastermind.ShowScores");
			startActivity(i);
		}
//		else if (clicked.getId() == btndone.getId()) {
//			finish();
//		}
		else {
				Bundle data = new Bundle();
				data.putString("gameType", (String) clicked.getTag());
				
				Intent i = new Intent("edu.olemiss.rcollier.mastermind.GameActivity");
				i.putExtras(data);
				startActivity(i);
		}
	}
	
	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
}
