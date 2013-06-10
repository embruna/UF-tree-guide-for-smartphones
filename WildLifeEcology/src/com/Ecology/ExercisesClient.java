package com.Ecology;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

public class ExercisesClient extends Thread {
	private static String TAG = "MyActivity";
	private String mFileName;
	private String mUrl;
	private Exercises mActivity;
	
	public ExercisesClient() {
		this.mFileName = null;
		this.mUrl = null;
		mActivity = null;
	}
	
	
	public void run() {
		getTreeNames();
	}
	
	public void setFileName(String fileName) {
		this.mFileName = fileName;
	}
	
	public void setUrl(String url) {
		this.mUrl = url;
	}
	
	public void setActivity(Exercises activity) {
		mActivity = activity;
	}
	
    private void getTreeNames() {
        HttpURLConnection connection = null;
        URL serverAddress = null;
        ArrayList<String> treeNameArray = null;
        //tv.setText("http://wecuf.zoka.cc/SearchTreeZoka.php?family=Cupressaceae");
        try {
			//serverAddress = new URL("http://localhost/wecuf/SearchTree.php?family=Cupressaceae");
			serverAddress = new URL(mUrl);
			try {
				connection = (HttpURLConnection) serverAddress.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setReadTimeout(10000);
				connection.connect();
				
				InputStreamReader ins = new InputStreamReader(connection.getInputStream());
				BufferedReader br = new BufferedReader(ins);
				StringBuilder sb = new StringBuilder();
				String str = null;
				while((str = br.readLine()) != null) {
					sb.append(str);
				}
				sb.append('\n');
				FileOutputStream fs = new FileOutputStream(mFileName,false);
				fs.write(sb.toString().getBytes());
				fs.close();
				mActivity.setExercisesFileDownloaded(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
}
