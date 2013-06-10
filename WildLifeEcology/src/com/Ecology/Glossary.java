package com.Ecology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Glossary extends ListActivity {
	private final static String GLOSSARY_FILENAME = "glossary.txt";
	private ArrayList<HashMap<String,String>> mGlossaryList = new ArrayList<HashMap<String,String>>();
	private volatile boolean mGlossaryFileDownloaded = false;
	private final static String TAG = "MyActivity";
	private static volatile ProgressDialog mProgressDialog;
	private final String URL = "http://wecuf.zoka.cc/Glossary.php?name=ALL";
	
	private static volatile Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.id.)
		downloadGlossaryFile();
		intializeGlossary();
		
		if(!mGlossaryList.isEmpty()) {
			setListAdapter(new SimpleAdapter(this,mGlossaryList,R.layout.glossary,
					new String[]{"SlNo", "Term","Definition"},
					new int[] {R.id.TextView_SlNo,R.id.TextView_Term,R.id.TextView_Definition}));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
		}

	}
	
	public void onResume() {
		super.onResume();
		getListView().clearTextFilter();
	}
	
	private boolean downloadGlossaryFile(){
		GlossaryClient glossaryClient = new GlossaryClient();
		File file = getApplicationContext().getFileStreamPath(GLOSSARY_FILENAME);
    	if(!file.exists()) {
    		glossaryClient.setFileName(file.getAbsolutePath());
    		glossaryClient.setUrl(URL);
    		glossaryClient.setActivity(this);
    		glossaryClient.start();
    		
            mProgressDialog = ProgressDialog.show(Glossary.this, "Glossary", 
                    "Loading. Please wait...", true);
    		while(true) {
    			if(mGlossaryFileDownloaded) {
    				//mProgressDialog.dismiss();
    				mHandler.sendEmptyMessage(0);
    				Log.d(TAG,"DIALOG gonna be dismissed");
    				break;
    			}
    		}
    	}
    	else
    		mGlossaryFileDownloaded = true;
    	return true;
	}
	private void intializeGlossary() {
		try {
			FileInputStream fis = openFileInput(GLOSSARY_FILENAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				parseGlossary(sb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showAlertDialog("Glossary file not found!");
		}
	}
	
	public void parseGlossary(StringBuilder sb) {
		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject resultsObj = jsonObject.getJSONObject("results");
			JSONArray glossaryArray = resultsObj.getJSONArray("glossary");
			for(int count = 0; count < glossaryArray.length(); count++) {
				String term = glossaryArray.getJSONObject(count).getString("Term");
				String definition = glossaryArray.getJSONObject(count).getString("Definition");
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("SlNo", String.valueOf(count+1));
				map.put("Term", term);
				map.put("Definition", definition);
				if(mGlossaryList == null) {
					mGlossaryList = new ArrayList<HashMap<String,String>>();
				}
				mGlossaryList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void showAlertDialog(String message) {
		
	}

	public void setGlossaryFileDownloaded(boolean status) {
		mGlossaryFileDownloaded = status;
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.glossary_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.SearchTrees:
    		Intent SearchTree = new Intent(Glossary.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(Glossary.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Search:
    		InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		inputMgr.toggleSoftInput(0, 0);
    		return true;
    	case R.id.Quiz:
    		Intent quiz = new Intent(Glossary.this,Quiz.class);
			startActivity(quiz);
    		return true;
    	default:
    		return true;
    	}
    }
}
