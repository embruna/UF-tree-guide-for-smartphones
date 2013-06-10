package com.Ecology;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainMenu extends Activity {
	private static String TAG = "MyActivity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mainmenu);
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
    		Intent SearchTree = new Intent(MainMenu.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(MainMenu.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Glossary:
    		Intent glossary = new Intent(MainMenu.this,Glossary.class);
			startActivity(glossary);
    		return true;
    	case R.id.Quiz:
    		Intent quiz = new Intent(MainMenu.this,Quiz.class);
			startActivity(quiz);
    		return true;
    	default:
    		return true;
    	}
    }
}