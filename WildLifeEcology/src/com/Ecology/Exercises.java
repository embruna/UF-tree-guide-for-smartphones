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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Exercises extends ListActivity {
	private final static String EXERCISES_FILENAME = "exercises.txt";
	private ArrayList<HashMap<String,String>> mExercisesList = new ArrayList<HashMap<String,String>>();
	//ExerciseName -> Procedure
	static volatile HashMap<String,ArrayList<String>> mExerciseMap = new HashMap<String,ArrayList<String>>();
	private volatile boolean mExercisesFileDownloaded = false;
	private final static String TAG = "MyActivity";
	private static volatile ProgressDialog mProgressDialog;
	private final String URL = "http://wecuf.zoka.cc/Exercises.php?name=ALL";
	
	private static volatile Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.id.)
		downloadExercisesFile();
		intializeExercises();
		
		if(!mExercisesList.isEmpty()) {
			Log.d(TAG,"mExercisesList is not empty");
			/*setListAdapter(new SimpleAdapter(this,mExercisesList,R.layout.references,
					new String[]{"SlNo", "ExerciseName"},
					new int[] {R.id.TextView_SlNo,R.id.TextView_Citation}));
			*/
			setListAdapter(new SimpleAdapter(this,mExercisesList,R.layout.exercises,
					new String[]{"SlNo", "ExerciseName"},
					new int[] {R.id.TextView_SlNo,R.id.TextView_Exercise}));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1,
						int position, long id) {
					 Intent exerciseProcedure = new Intent(Exercises.this,ExerciseProcedure.class);
					 Bundle bundle = new Bundle();
					 @SuppressWarnings("unchecked")
					HashMap<String,String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
					 bundle.putString("ExerciseName",map.get("ExerciseName"));	
					 exerciseProcedure.putExtras(bundle);
					 startActivity(exerciseProcedure);
					 //finish();
				}
			});
		} else {
			Log.d(TAG,"mExercisesList is empty");
		}
		
		
	}
	
	public void onResume() {
		super.onResume();
		getListView().clearTextFilter();
	}
	
	private boolean downloadExercisesFile(){
		ExercisesClient exercisesClient = new ExercisesClient();
		File file = getApplicationContext().getFileStreamPath(EXERCISES_FILENAME);
    	if(!file.exists()) {
    		exercisesClient.setFileName(file.getAbsolutePath());
    		exercisesClient.setUrl(URL);
    		exercisesClient.setActivity(this);
    		exercisesClient.start();
    		
            mProgressDialog = ProgressDialog.show(Exercises.this, "Exercises", 
                    "Loading. Please wait...", true);
    		while(true) {
    			if(mExercisesFileDownloaded) {
    				//mProgressDialog.dismiss();
    				mHandler.sendEmptyMessage(0);
    				Log.d(TAG,"DIALOG gonna be dismissed");
    				break;
    			}
    		}
    	}
    	else
    		mExercisesFileDownloaded = true;
    	return true;
	}
	private void intializeExercises() {
		Log.d(TAG,"intializeExercises");
		try {
			FileInputStream fis = openFileInput(EXERCISES_FILENAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				parseExercises(sb);
				Log.d(TAG,"After parseExercises");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showAlertDialog("Exercises file not found!");
		}
	}
	
	public void parseExercises(StringBuilder sb) {
		mExerciseMap.clear();
		try {
			Log.d(TAG,"Inside parseExercises");
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject resultsObj = jsonObject.getJSONObject("results");
			JSONArray exercisesArray = resultsObj.getJSONArray("exercises");
			int slno = 0;
			for(int count = 0; count < exercisesArray.length(); count++) {
				String exerciseName = exercisesArray.getJSONObject(count).getString("ExerciseName").trim();
				String procedure = exercisesArray.getJSONObject(count).getString("Procedure").trim();

				if(mExercisesList == null) {
					mExercisesList = new ArrayList<HashMap<String,String>>();
				}
				ArrayList<String> procedureList = mExerciseMap.get(exerciseName);
				if(procedureList == null) {
					procedureList = new ArrayList<String>();
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("SlNo",String.valueOf(++slno));
					map.put("ExerciseName", exerciseName);
					//map.put("Procedure", procedure);
					Log.d(TAG,exerciseName+":"+procedure);
					mExercisesList.add(map);
				}
				procedureList.add(procedure);
				Log.d(TAG,"count = "+count);
				mExerciseMap.put(exerciseName, procedureList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void showAlertDialog(String message) {
		
	}

	public void setExercisesFileDownloaded(boolean status) {
		mExercisesFileDownloaded = status;
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
    		Intent SearchTree = new Intent(Exercises.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(Exercises.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Glossary:
    		Intent glossary = new Intent(Exercises.this,Glossary.class);
			startActivity(glossary);
    		return true;
    	case R.id.Quiz:
    		Intent quiz = new Intent(Exercises.this,Quiz.class);
			startActivity(quiz);
    		return true;
    	default:
    		return true;
    	}
    }
}

