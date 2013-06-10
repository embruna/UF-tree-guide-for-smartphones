package com.Ecology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainPage extends MainMenu {

	//tree data is stored in this file
	private static String mDownloadedVersionFileName = "DatabaseVersions_server.txt";
	private static String mStoredVersionFileName = "DatabaseVersions_phone.txt";
	private static String TAG = "MyActivity";
	private static long ONEWEEKTIME = 18316800;
	private static volatile boolean  mFileDownloaded = false;
	private static volatile ProgressDialog mProgressDialog;// = new ProgressDialog(getApplicationContext());
	private HashMap<String,Integer> mDatabaseVersionInServerMap 
		= new HashMap<String,Integer>();
	private HashMap<String,Integer> mDatabaseVersionInPhoneMap 
		= new HashMap<String,Integer>();
	private static HashMap<String,Boolean> mFilesToBeDownloadedMap 
		= new HashMap<String,Boolean>();
	
	private static volatile Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			//Log.d(TAG,"Inside handler! - Before Dismissing");
			if(mProgressDialog != null)
				mProgressDialog.dismiss();
			//Log.d(TAG,"Inside handler! - After Dismissing");
			//mProgressDialog = null;
			//mFileDownloaded = false;
		}
	};
	
	private final String mExerciseTabName = "Exercises";
	private final String mGlossaryTabName = "Glossary";
	private final String mQuizTabName = "Quiz";
	private final String mReferencesTabName = "References";
	private final String mTreeMapTabName = "GPSCoordinates";
	private final String mSearchTreeTabName = "TreeName";
	
	private String exercisesURL = "http://wecuf.zoka.cc/Exercises.php?name=ALL";
	private final static String EXERCISES_FILENAME = "exercises.txt";
	private String GlossaryURL = "http://wecuf.zoka.cc/Glossary.php?name=ALL";
	private final static String GLOSSARY_FILENAME = "glossary.txt";
	private String QuizURL = "http://wecuf.zoka.cc/Quiz.php?name=ALL";
	private final static String QUIZ_FILENAME = "quiz.txt";
	private String ReferencesURL = "http://wecuf.zoka.cc/References.php?name=ALL";
	private final static String REFERENCES_FILENAME = "reference.txt";
	private String TreeMapURL = "http://wecuf.zoka.cc/TreeMap.php?name=ALL";
	private static final String TREEMAP_FILENAME = "GPSCoordinates.txt";
	private String SearchTreeUrl = "http://wecuf.zoka.cc/SearchTreeZoka.php?name=ALL";
	private static String SEARCHTREE_FILENAME = "TreeData.txt";
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        Log.d(TAG,"MAIN PAGE ONCREATE");

        updateDatabaseVersion();
        //String searchTreeUrl = "http://wecuf.zoka.cc/SearchTreeZoka.php?name=ALL";
		//downloadFile(SEARCHTREE_FILENAME,searchTreeUrl);

    	/*File file = getApplicationContext().getFileStreamPath(mFileName);
    	if(!file.exists()) {
    		httpClient.setFileName(file.getAbsolutePath());
    		httpClient.setUrl("http://wecuf.zoka.cc/SearchTreeZoka.php?name=ALL");
    		httpClient.setActivity(this);
    		httpClient.start();
    		mFileDownloaded = false;
        	
            mProgressDialog = ProgressDialog.show(MainPage.this, "Wild Life Ecology", 
                    "Loading. Please wait...", true);
            new Thread(){
            	public void run() {
            		while(true) {
    	    			if(mFileDownloaded) {
    	    				mHandler.sendEmptyMessage(0);
    	    				Log.d(TAG,"DIALOG gonna be dismissed");
    	    				break;
    	    			}
            		}
            	}
            }.start();
            
    	}
    	else {
/*    		long timeCreated = file.lastModified();
    		long timeDifference = System.currentTimeMillis() - timeCreated;
    		Log.d(TAG, "timeCreated = "+timeCreated);
    		Log.d(TAG, "timeDifference = "+timeDifference);
    		Log.d(TAG, "System.currentTimeMillis() = "+System.currentTimeMillis());
    		if(timeDifference > ONEWEEKTIME || timeDifference < 0) {
    			httpClient.setFileName(file.getAbsolutePath());
        		httpClient.setUrl("http://wecuf.zoka.cc/SearchTreeZoka.php?name=ALL");
        		httpClient.start();
    		}*/
   /* 		mFileDownloaded = true;
    		Log.d(TAG, "File already present!");
    		Log.d(TAG, getApplicationContext().getFilesDir().toString());
    	}*/

        
    	
    	Button browseTreesButton = (Button) findViewById(R.id.Button_BrowseTrees);
    	browseTreesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent SearchTree = new Intent(MainPage.this,SearchTree.class);
				startActivity(SearchTree);
			}
    		
    	});
    	
    	Button TreeMapButton = (Button) findViewById(R.id.Button_TreeMaps);
    	TreeMapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent treeMap = new Intent(MainPage.this,TreeMap.class);
				startActivity(treeMap);
			}
    		
    	});
    	
    	Button GlossaryButton = (Button) findViewById(R.id.Button_Glossory);
    	GlossaryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent glossary = new Intent(MainPage.this,Glossary.class);
				startActivity(glossary);
			}
    	});
    	
    	Button QuizButton = (Button) findViewById(R.id.Button_Quiz);
    	QuizButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent quiz = new Intent(MainPage.this,Quiz.class);
				startActivity(quiz);
			}
    	});
    	
    	Button ReferencesButton = (Button) findViewById(R.id.Button_References);
    	ReferencesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent references = new Intent(MainPage.this,References.class);
				startActivity(references);
			}
    	});
    	
    	Button exercisesButton = (Button) findViewById(R.id.Button_Exercises);
    	exercisesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent exercises = new Intent(MainPage.this,Exercises.class);
				startActivity(exercises);
			}
    	});
    	
    	Button AboutButton = (Button) findViewById(R.id.Button_About);
    	AboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent about = new Intent(MainPage.this,About.class);
				startActivity(about);
			}
    	});
    }

	private void updateDatabaseVersion() {
        if(!isConnected()) {
        	showAlertDialog("No Network detected!");
        }
		if(mProgressDialog == null)
			mProgressDialog = ProgressDialog.show(MainPage.this, "UF Campus Tree Guide", 
					"Loading. Please wait...", true);
		
		File file = getApplicationContext().getFileStreamPath(mDownloadedVersionFileName);
		//if(file.exists()) {
		//	file.delete();
		//}
		 String databaseVersionUrl = "http://wecuf.zoka.cc/DatabaseVersion.php?name=ALL";
		downloadFile(mDownloadedVersionFileName,databaseVersionUrl);
		
		while(true) {
			if(mFileDownloaded) {
				break;
				}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mFileDownloaded = false;
		//update current version from the local file
		intializeDatabaseVersion(mStoredVersionFileName,mDatabaseVersionInPhoneMap);
		//download version from server
		intializeDatabaseVersion(mDownloadedVersionFileName,mDatabaseVersionInServerMap);
		//Log.d(TAG,"After intializing");
		//printMaps();
		Set<String> key1 = mDatabaseVersionInServerMap.keySet();
		Iterator<String> iter1 = key1.iterator();
		if(mDatabaseVersionInPhoneMap.isEmpty()) {
			while(iter1.hasNext()) {
				String tableName = iter1.next();
				int version = mDatabaseVersionInServerMap.get(tableName);
				mDatabaseVersionInPhoneMap.put(tableName, version);
				mFilesToBeDownloadedMap.put(tableName,true);
			}
		} else {
			while(iter1.hasNext()) {
				String tableName = iter1.next();
				int version = mDatabaseVersionInServerMap.get(tableName);
				//Log.d(TAG,"tableName = "+tableName+" :version  = "+version);
				int curVersion = mDatabaseVersionInPhoneMap.get(tableName);
				//Log.d(TAG,"tableName = "+tableName+" :curVersion  = "+curVersion);
				if(curVersion != version)
					mFilesToBeDownloadedMap.put(tableName,true);
				else
					mFilesToBeDownloadedMap.put(tableName,false);
			}
		}
		
		downloadNewFiles();

		
		copyDownloadedFile();
		//Log.d(TAG,"END");
		//printMaps();
		//removeDownloadedFile
		/*String downloadedFile = getApplicationContext().getFilesDir().toString()+
			"/"+mDownloadedVersionFileName;
		Log.d(TAG,"downloaded File Name = "+downloadedFile);
		File file = getApplicationContext().getFileStreamPath(mDownloadedVersionFileName);
		if(file.exists()) {
			file.delete();
		}*/
	}

	private void downloadNewFiles() {
		if(mProgressDialog == null)
			mProgressDialog = ProgressDialog.show(MainPage.this, "UF Campus Tree Guide", 
					"Loading. Please wait...", true);
       // mProgressDialog = ProgressDialog.show(MainPage.this, "UF Campus Tree Guide", 
       //         "Loading. Please wait...", true);
        
		Set<String> keys = mFilesToBeDownloadedMap.keySet();
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext()) {
			String tabName = iter.next();
			Log.d(TAG,"tabName = "+tabName);
			if(mFilesToBeDownloadedMap.get(tabName)) {
				if(tabName.equals(mExerciseTabName)) {
					downloadFile(EXERCISES_FILENAME, exercisesURL);	
				} else if(tabName.equals(mGlossaryTabName)) {
					downloadFile(GLOSSARY_FILENAME, GlossaryURL);
				} else if(tabName.equals(mTreeMapTabName)) {
					downloadFile(TREEMAP_FILENAME, TreeMapURL);
				} else if(tabName.equals(mQuizTabName)) {
					downloadFile(QUIZ_FILENAME, QuizURL);
				} else if(tabName.equals(mReferencesTabName)) {
					downloadFile(REFERENCES_FILENAME, ReferencesURL);
				} else if(tabName.equals(mSearchTreeTabName)) {
					downloadFile(SEARCHTREE_FILENAME, SearchTreeUrl);
				}
				mFilesToBeDownloadedMap.put(tabName,false);
			}
		}
		if(mProgressDialog != null)
			mProgressDialog.dismiss();
        /*new Thread(){
        	public void run() {
	    		while(true) {
	    			if(mFileDownloaded) {
	    				//mProgressDialog.dismiss();
	    				mHandler.sendEmptyMessage(0);
	    				Log.d(TAG,"DIALOG gonna be dismissed");
	    				break;
	    			}
	    		}
		}};*/
	}
	
	private void downloadFile(String fileName,String url) {
        if(!isConnected()) {
        	showAlertDialog("No Network detected!");
        }
    	File file = getApplicationContext().getFileStreamPath(fileName);
    	//if(!file.exists()) {
  	      //  mFileDownloaded = false;
    		Log.d(TAG,"....Downloading - "+fileName);
    		//Log.d(TAG,"....Showing Dialog  - "+fileName);
    		//if(mProgressDialog == null)
    			//mProgressDialog = ProgressDialog.show(MainPage.this, "UF Campus Tree Guide", 
    				//	"Loading. Please wait...", true);
			MyHttpClient httpClient = new MyHttpClient();
    		httpClient.setFileName(file.getAbsolutePath());
    		httpClient.setUrl(url);
    		httpClient.setActivity(this);
    		
    		httpClient.start();    
/*
           new Thread(){
            	public void run() {
		    		while(true) {
		    			if(mFileDownloaded) {
		    				//mProgressDialog.dismiss();
		    				mHandler.sendEmptyMessage(0);
		    				Log.d(TAG,"DIALOG gonna be dismissed");
		    				break;
		    			}
		    		}
    		}};*/
    		
/*
    		while(true) {
    			if(mFileDownloaded) {
    				break;
    				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}*/
    		Log.d(TAG,"!!!!!!!!!!I'm here!!!!");
    		//if(mProgressDialog != null ) {
    		//	mProgressDialog.dismiss();
    		//}
    		
	}
	private void intializeDatabaseVersion(String fileName,HashMap<String,Integer> map) {
		try {
			FileInputStream fis = openFileInput(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				parseDatabaseVersion(sb,map);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void parseDatabaseVersion(StringBuilder sb,HashMap<String,Integer> map) {
		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject resultsObj = jsonObject.getJSONObject("results");
			JSONArray dbArray = resultsObj.getJSONArray("databaseversion");
			for(int count = 0; count < dbArray.length(); count++) {
				String tableName = dbArray.getJSONObject(count).getString("TableName").trim();
				String version = dbArray.getJSONObject(count).getString("Version").trim();
				//Log.d(TAG,"version = "+version+": tabName = "+tableName);
				map.put(tableName, Integer.parseInt(version));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void copyDownloadedFile() {
		try {
			FileInputStream fis = openFileInput(mDownloadedVersionFileName);
			//FileOutputStream fos = openFileOutput(mStoredVersionFileName, 0);
			File file = getApplicationContext().getFileStreamPath(mStoredVersionFileName);
			FileOutputStream fos = new FileOutputStream (file,false);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0){
				fos.write(buf, 0, len);
				fos.flush();
			}
		    fis.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void showAlertDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                MainPage.this.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void setFileDownloaded(boolean status) {
		//mFileDownloaded = status;
		Log.d(TAG,"Dialog gonna be dismissed!");
		//mHandler.sendEmptyMessage(0);
		mFileDownloaded = true;
		//if(mProgressDialog != null)
		//	mProgressDialog.dismiss();
	}
	
    public static boolean IsFileUptoDate(String tableName) {
    	return mFilesToBeDownloadedMap.get(tableName);
    }
   
    public boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null)
        	return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        else
        	return false;
    }

    
    private void printMaps() {
    	Set<String> key1 = mDatabaseVersionInServerMap.keySet();
		Iterator<String> iter1 = key1.iterator();
		Log.d(TAG,"----Printing MAPS----");
		while(iter1.hasNext()) {
				String tableName = iter1.next();
				int version = mDatabaseVersionInServerMap.get(tableName);
				int curVersion = mDatabaseVersionInPhoneMap.get(tableName);
				
				boolean bl = mFilesToBeDownloadedMap.get(tableName);
				Log.d(TAG," version = "+version+" ;curVersion = "+curVersion+" ;bl = "+bl);
				//mFilesToBeDownloadedMap.put(tableName,true);
		}
    }
}