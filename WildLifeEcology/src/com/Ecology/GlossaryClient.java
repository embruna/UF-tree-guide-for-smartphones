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

public class GlossaryClient extends Thread {
	private static String TAG = "MyActivity";
	private String mFileName;// = "TreeData.txt";
	private String mUrl;
	private Glossary mActivity;
	
	public GlossaryClient() {
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
	
	public void setActivity(Glossary activity) {
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
				mActivity.setGlossaryFileDownloaded(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//return treeNameArray;
    }
    /*
    private ArrayList<String> writeToFile(StringBuilder sb) {
    	//StringBuilder treeSB = new StringBuilder();
    	ArrayList<String> treeNameList = new ArrayList<String>();
		try {
			JSONObject mJobject = new JSONObject(sb.toString());
			Log.d(TAG, "1");
			JSONObject resultsObj = mJobject.getJSONObject("results");
			Log.d(TAG, "2");
			JSONArray treeArray = resultsObj.getJSONArray("trees");
			Log.d(TAG, "3");
			for(int count = 0; count < treeArray.length();count++) {
				/*treeSB.append(treeArray.getJSONObject(count).getString("ScientificName")+" ");
				treeSB.append(treeArray.getJSONObject(count).getString("CommonName")+" ");
				treeSB.append(treeArray.getJSONObject(count).getString("FamilyName")+ " ");
				treeSB.append("\n");*/
	/*			treeNameList.add(treeArray.getJSONObject(count).getString("ScientificName"));
				Log.d(TAG,treeNameList.get(count));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return treeNameList;
    }*/
}
