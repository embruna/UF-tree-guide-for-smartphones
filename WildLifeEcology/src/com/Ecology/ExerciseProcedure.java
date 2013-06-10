package com.Ecology;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ExerciseProcedure extends Activity{
	String mExerciseName;
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.exercise_procedure);
	     mExerciseName = getIntent().getExtras().getString("ExerciseName");
	     TextView heading = (TextView) findViewById(R.id.TextView_Heading);
	     heading.setText(mExerciseName+" : Procedure");
	     ArrayList<String> procedureList = Exercises.mExerciseMap.get(mExerciseName);
	     if(procedureList != null) {
	    	 for(int index = 0; index < procedureList.size(); ++index) {
	    		 TableLayout tLayout = (TableLayout) findViewById(R.id.TableLayout01);
	    		 TableRow tRow = new TableRow(ExerciseProcedure.this);
	    		// TextView tView1 = new TextView(ExerciseProcedure.this);
	    		 //tView1.setText(String.valueOf(index+1));
	    		 //tView1.setTextColor(0);
	    		 TextView tView2 = new TextView(ExerciseProcedure.this);
	    		 tView2.setText((index+1) + ". " + procedureList.get(index));
	    		 tView2.append("\n");
	    		 tView2.setTextColor(heading.getTextColors());	    
	    		 tView2.setMaxWidth(400);
	    		 tView2.setInputType(0x00020001);
	    		// tRow.addView(tView1);
	    		 tRow.addView(tView2);
	    		 tLayout.addView(tRow);
	    	 }
	     }

	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.SearchTrees:
    		Intent SearchTree = new Intent(ExerciseProcedure.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(ExerciseProcedure.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Glossary:
    		Intent glossary = new Intent(ExerciseProcedure.this,Glossary.class);
			startActivity(glossary);
    		return true;
    	case R.id.Quiz:
    		Intent quiz = new Intent(ExerciseProcedure.this,Quiz.class);
			startActivity(quiz);
    		return true;
    	default:
    		return true;
    	}
    }
}
