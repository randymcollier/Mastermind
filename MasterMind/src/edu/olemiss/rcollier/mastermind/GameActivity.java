package edu.olemiss.rcollier.mastermind;

import java.util.Random;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.olemiss.rcollier.mastermind.Dialog.InputNameDialogListener;

public class GameActivity extends FragmentActivity 
implements InputNameDialogListener {

	String gameType, name, solution[];
	String colorNames[] = {"Lime Green", "Blue", "Aqua", "Red", "Pink", "Yellow", "Dark Green", "Orange"};
	int colors[] = {0xff00ff00, 0xff0000ff, 0xff00ccff, 0xffff0000, 0xffff00cc,  
			0xffffff00, 0xFF006600, 0xFFF07000};
	int pegs = 0, numColors = 0, guesses = 0, nextPeg = colors.length, totalPegs = 0, score = 0,
			colorsSelected = 0, checkedWins = 0, gameLevel = 0, numHints = 0, totalTime = 0;
	long startTime = 0, endTime = 0;
	TextView gameTitle, gameScore;
	LinearLayout colorsLayout, guessesLayout, hintsLayout;
	Boolean gameOver = false, started = false;
	DBAdapter db;
	LinearLayout pegsLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		gameTitle = (TextView) findViewById(R.id.levelTitle);
		gameScore = (TextView) findViewById(R.id.tvScore);
		Bundle data = getIntent().getExtras();
		if (data != null) {
			gameType = data.getString("gameType");
			if (gameType.equals("Easy")) {
				gameTitle.setText("Easy");
				gameLevel = 0;
				pegs = 4;
				numColors = 5;
				guesses = 10;
				totalPegs = pegs * guesses + colors.length;
				solution = new String [pegs];
				Random r = new Random();
				for (int i = 0; i < pegs; i++) {
					solution[i] = colorNames[r.nextInt(numColors)];
				}
			}
			else if (gameType.equals("Medium")) {
				gameTitle.setText("Medium");
				gameLevel = 1;
				pegs = 4;
				numColors = 7;
				guesses = 10;
				totalPegs = pegs * guesses + colors.length;
				solution = new String [pegs];
				Random r = new Random();
				for (int i = 0; i < pegs; i++) {
					solution[i] = colorNames[r.nextInt(numColors)];				
				}
			}
			else if (gameType.equals("Hard")) {
				gameTitle.setText("Hard");
				gameLevel = 2;
				pegs = 5;
				numColors = 8;
				guesses = 12;
				totalPegs = pegs * guesses + colors.length;
				solution = new String [pegs];
				Random r = new Random();
				for (int i = 0; i < pegs; i++) {
					solution[i] = colorNames[r.nextInt(numColors)];
				}
			}
			else {
				showToast("Error processing game.");
			}
		}
		else {
			showToast("Data is null!");
		}
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
    		    LayoutParams.WRAP_CONTENT, 
    		    LayoutParams.WRAP_CONTENT
    	    );
        lp.setMargins(5, 5, 5, 5);
        colorsLayout = (LinearLayout) findViewById(R.id.colorsLayout);
        guessesLayout = (LinearLayout) findViewById(R.id.guessesLayout);
        hintsLayout = (LinearLayout) findViewById(R.id.hintsLayout);
        
        int pegId = colors.length, tvId = 1000;
        for (int i = 0; i < guesses; i++) {
	        pegsLayout = new LinearLayout(this);
	        pegsLayout.setOrientation(LinearLayout.HORIZONTAL);
	        pegsLayout.setBackgroundResource(R.drawable.border);
	        guessesLayout.addView(pegsLayout);
        	
        	for (int count = 0; count < pegs; count++) {
	 			ImageView v = new ImageView(this);
	 			ShapeDrawable circle = drawOval(Color.GRAY, 45, 45);
	 			v.setImageDrawable(circle);	
	 			v.setId(pegId);
	 			v.setOnClickListener(circleClick);
	 			pegsLayout.addView(v, lp);
	 			pegId++;
	 		}
        	TextView tv1 = new TextView(this);
        	TextView tv2 = new TextView(this);
        	tv1.setText("0");
        	tv1.setId(tvId);
        	tv1.setTextColor(0xff00ff00);
        	tv2.setText("0");
        	tv2.setId(tvId + 1);
        	tv2.setTextColor(0xff00ff00);
        	pegsLayout.addView(tv1, lp);
        	pegsLayout.addView(tv2, lp);
        	tvId += 2;
        }
        
        for (int count = 0; count < numColors; count++) {
     			ImageView v = new ImageView(this);
     			ShapeDrawable circle = drawOval(colors[count], 45, 45);
     			v.setImageDrawable(circle);	
     			v.setId(count);
     			v.setTag(colorNames[count]);
     			v.setOnClickListener(circleClick);
     			colorsLayout.addView(v, lp);
     		}
     		
     		
     		Button btn1 = (Button) findViewById(R.id.button1);
     		btn1.setOnClickListener(new OnClickListener() {
     		     public void onClick(View v) {
     		    	 finish();
     		     }
     		});
     		
     		Button btn2 = (Button) findViewById(R.id.button2);
     		btn2.setOnClickListener(new OnClickListener() {
     			public void onClick(View v) {
     				if (gameOver)
     					showToast("Game over!");
     				else if (numHints < ((pegs / 2) + gameLevel)){
	     				if(!started)
	     					startTime = System.currentTimeMillis();
     					Random r = new Random();
	     				int ranInt = r.nextInt(numColors);
	     				int numColorsSame = 0;
	     				for (int i = 0; i < pegs; i++) {
	     					if (solution[i].equals(colorNames[ranInt])) {
	     						numColorsSame++;
	     					}
	     				}
	     				showToast("There are " + numColorsSame + " " + colorNames[ranInt] + " pegs.");
	     				numHints++;
     				}
     				else
     					showToast("Sorry! No more hints.");
     			}
     		});
	}

	public ShapeDrawable drawOval(int c, int h, int w) {
		ShapeDrawable oval = new ShapeDrawable(new OvalShape());
		oval.setIntrinsicHeight(h);
		oval.setIntrinsicWidth(w);
		oval.getPaint().setAlpha(0);
		oval.getPaint().setColor(c);
	    return oval;	
	}
	
	public OnClickListener circleClick = new OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			if (gameOver) {
				showToast("Game over!");
			}
			else if (id < colors.length) {
				if (!started) {
					startTime = System.currentTimeMillis();
				}
				ImageView pegView = (ImageView) findViewById(nextPeg);
				ShapeDrawable circle = drawOval(colors[id], 45, 45);
				pegView.setImageDrawable(circle);
				pegView.setTag(colorNames[id]);
				started = true;
				nextPeg++;
				colorsSelected++;
				if ((colorsSelected % pegs) == 0) {
					checkWin();
				}
				if ((nextPeg == totalPegs) && (!gameOver)) {
					endTime = System.currentTimeMillis();
					gameOver = true;
					showToast("Sorry! Game over!");
					String answer = "";
     				for (int i = 0; i < pegs; i++) {
     					answer += solution[i];
     					if (i != (pegs - 1)) {
     						answer += ", ";
     					}
     				}
     				showToast("Solution was: " + answer);					
				}
			}
			else if (id >= colors.length) {
				if (id == (nextPeg - 1) && ((colorsSelected % pegs) != 0)) {	
					ImageView pegView = (ImageView) findViewById(id);
					ShapeDrawable circle = drawOval(Color.GRAY, 45, 45);
					pegView.setImageDrawable(circle);
					pegView.setTag("");
					nextPeg--;
					colorsSelected--;
				}
				else
					showToast("Cannot undo that color.");
			}
		}
	};
	
	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
	public void checkWin() {
		String[] selectedColors = new String [pegs];
		String[] newSolution = new String [pegs];
		for (int i = 0; i < pegs; i++) {
			newSolution[i] = solution[i];
		}
		int bothCorrect = 0, colorCorrect = 0;
		for (int i = 1; i <= pegs; i++) {
			ImageView v = (ImageView) findViewById(nextPeg - i);
			selectedColors[pegs - i] = (String) v.getTag();
		}
		for (int i = 0; i < pegs; i++) {
			if (newSolution[i].equals(selectedColors[i])) {
				bothCorrect++;
				newSolution[i] = "Used";
				selectedColors[i] = "Right";
			}
		}
		for (int i = 0; i < pegs; i++) {
			for (int j = 0; j < pegs; j++) {
				if (newSolution[j].equals(selectedColors[i])) {
					colorCorrect++;
					newSolution[j] = "Used";
					selectedColors[i] = "Right";
				}
			}
		}
		TextView tvBothCorrect = (TextView) findViewById(1000 + checkedWins);
		tvBothCorrect.setText(bothCorrect + "");
		TextView tvColorCorrect = (TextView) findViewById(1000 + checkedWins + 1);
		tvColorCorrect.setText(colorCorrect + "");
		checkedWins += 2;
		if (bothCorrect == pegs) {
			endTime = System.currentTimeMillis();
			gameOver = true;
			showToast("Congratulations! You won!");
			totalTime = (int) (endTime - startTime) / 1000;
			score = max((1100 * (gameLevel + 1)) - (checkedWins * 5) - (numHints * 5) - (totalTime * 2), 50);
			gameScore.setText("Score: " + score);
			db = new DBAdapter(this);
			db.open();
			if (db.isHighscore(gameLevel, score)) {
				showInputNameDialog();
			}
		}
	}
	
	private void showInputNameDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Dialog inputNameDialog = new Dialog();
        inputNameDialog.setCancelable(false);
        inputNameDialog.setDialogTitle("New Highscore!");
        inputNameDialog.show(fragmentManager, "input dialog");
    }

    @Override
    public void onFinishInputDialog(String inputText) {
        name = inputText;
        db.check_score(gameLevel, name, score);
    }
    
    public int max(int a, int b) {
    	if (a < b) return b;
    	else return a;
    }

}
