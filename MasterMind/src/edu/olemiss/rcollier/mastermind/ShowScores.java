package edu.olemiss.rcollier.mastermind;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowScores extends Activity {

	DBAdapter db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scoreboard);
		
		db = new DBAdapter(this);
	    db.open(); 
		
		Button btn_done = (Button) findViewById(R.id.btn_scoreboard_done);
		btn_done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Button btn_reset = (Button) findViewById(R.id.btn_scoreboard_reset);
		btn_reset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				db.resetAllScores();
				finish();
				startActivity(getIntent());
			}
		});
		
		TextView tv = (TextView) findViewById(R.id.scores);
		String s = "";
	    for (int i=0; i<3; i++) {
	    	     Cursor c = db.getAllScores(i);
	    	     switch (i) {
	    	     case 0: s += "Easy\n";
	    	             break;
	    	     case 1: s += "\nMedium\n";
	    	             break;
	    	     case 2: s+= "\nHard\n";
	    	             break;
	    	     }
	    	     if (c.getCount() == 0) {
	    	    	    s += "     No scores\n";
	    	     }
	    	     else {
	    	    	    for (int pos=0; pos<c.getCount(); pos++) {
    	    	    	        c.moveToPosition(pos);
	    	    	    	    s += "     " + c.getInt(2) + "  " + c.getString(1) + "\n";

	    	    	    }
	    	     }
	    }
	    tv.setText(s);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}

}