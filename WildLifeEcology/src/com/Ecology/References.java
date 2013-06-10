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

public class References extends ListActivity {
	private final static String REFERENCES_FILENAME = "reference.txt";
	private ArrayList<HashMap<String,String>> mReferencesList = new ArrayList<HashMap<String,String>>();
	private volatile boolean mReferencesFileDownloaded = false;
	private final static String TAG = "MyActivity";
	private static volatile ProgressDialog mProgressDialog;
	private final String URL = "http://wecuf.zoka.cc/References.php?name=ALL";
	
	private static volatile Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.id.)
		downloadReferencesFile();
		intializeReferences();


		if(!mReferencesList.isEmpty()) {
			setListAdapter(new SimpleAdapter(this,mReferencesList,R.layout.references,
					new String[]{"SlNo", "Citation"},
					new int[] {R.id.TextView_SlNo,R.id.TextView_Citation}));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
		}
		//InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//inputMgr.toggleSoftInput(0, 0);
	}
	
	public void onResume() {
		super.onResume();
		getListView().clearTextFilter();
	}
	
	private boolean downloadReferencesFile(){
		ReferencesClient referencesClient = new ReferencesClient();
		File file = getApplicationContext().getFileStreamPath(REFERENCES_FILENAME);
    	if(!file.exists()) {
    		referencesClient.setFileName(file.getAbsolutePath());
    		referencesClient.setUrl(URL);
    		referencesClient.setActivity(this);
    		referencesClient.start();
    		
            mProgressDialog = ProgressDialog.show(References.this, "References", 
                    "Loading. Please wait...", true);
    		while(true) {
    			if(mReferencesFileDownloaded) {
    				//mProgressDialog.dismiss();
    				mHandler.sendEmptyMessage(0);
    				Log.d(TAG,"DIALOG gonna be dismissed");
    				break;
    			}
    		}
    	}
    	else
    		mReferencesFileDownloaded = true;
    	return true;
	}
	private void intializeReferences() {
		try {
			FileInputStream fis = openFileInput(REFERENCES_FILENAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				parseReferences(sb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showAlertDialog("References file not found!");
		}
	}
	
	public void parseReferences(StringBuilder sb) {
		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject resultsObj = jsonObject.getJSONObject("results");
			JSONArray referencesArray = resultsObj.getJSONArray("references");
			for(int count = 0; count < referencesArray.length(); count++) {
				String slNo = referencesArray.getJSONObject(count).getString("SlNo").trim();
				String citation = referencesArray.getJSONObject(count).getString("Citation").trim();
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("SlNo", slNo);
				map.put("Citation", citation);
				//Log.d(TAG,slNo+":"+citation);
				if(mReferencesList == null) {
					mReferencesList = new ArrayList<HashMap<String,String>>();
				}
				mReferencesList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void showAlertDialog(String message) {
		
	}

	public void setReferencesFileDownloaded(boolean status) {
		mReferencesFileDownloaded = status;
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reference_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.SearchTrees:
    		Intent SearchTree = new Intent(References.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(References.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Glossary:
    		Intent glossary = new Intent(References.this,Glossary.class);
			startActivity(glossary);
    		return true;
    	case R.id.Search:
    		//Intent quiz = new Intent(References.this,Quiz.class);
			//startActivity(quiz);
    		InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		inputMgr.toggleSoftInput(0, 0);
    		return true;
    	default:
    		return true;
    	}
    }
}

