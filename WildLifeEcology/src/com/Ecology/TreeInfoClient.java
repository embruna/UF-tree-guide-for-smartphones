package com.Ecology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

public class TreeInfoClient extends Thread{
	private String mBinomialName;
	private String mRootDir;
	private TreeInfo mTreeInfo;
	private String mHabitat = null;
	private String mCommonName = null;
	private String mFamilyName = null;
	private String mInteractionRoot = null;
	private String mInteractionStem = null;
	private String mInteractionLeaves = null;
	private String mInteractionFlowers = null;
	private String mInteractionFruits = null;
	private static final String TAG = "MyActivity";
	
	public TreeInfoClient(String binomialName,String rootDir, TreeInfo treeInfo) {
		mBinomialName = binomialName;
		mRootDir = rootDir;
		mTreeInfo = treeInfo;
	}
	
	public void run() {
		getInteractionInfos(mBinomialName);
		mTreeInfo.setTreeInformations(mCommonName, mFamilyName, mHabitat, mInteractionRoot, 
				mInteractionStem, mInteractionLeaves, mInteractionFlowers, mInteractionFruits);
		mTreeInfo.mTreeInfoObtained = true;
	}
	
	 private void getInteractionInfos(String name) {
	        HttpURLConnection connection = null;
	        URL serverAddress = null;
	        //tv.setText("http://wecuf.zoka.cc/SearchTreeZoka.php?family=Cupressaceae");
	        try {
				//serverAddress = new URL("http://localhost/wecuf/SearchTree.php?family=Cupressaceae");
				//serverAddress = new URL("http://wecuf.zoka.cc/TreeInfoZoka.php?name="+name);
	        	//name.replace(" ", "%20");
	 
	        	String[] nameArray = name.split(" ");
	        	String temp = name.replaceAll(" ", "%20");
	        	Log.d(TAG,"temp = "+temp);
	        	//String url = "http://wecuf.zoka.cc/TreeInfoZoka.php?name="+nameArray[0]+"%20"+nameArray[1];
	        	String url = "http://wecuf.zoka.cc/TreeInfoZoka.php?name="+temp;
	        	Log.d(TAG,"Path = "+mRootDir);
	        	
	        	PicasaClient.getPicasaClient().setRootPath(mRootDir);
	        	PicasaClient.getPicasaClient().setBinomialName(nameArray[0]+nameArray[1]);
	        	PicasaClient.getPicasaClient().setTreeInfo(mTreeInfo);
				serverAddress = new URL(url);
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
					parseJSON(sb);

					Log.d(TAG, "After Parsing");
					//Log.d(TAG,treeNameArray.get(0));
					//tv.setText(sb.toString());
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	 }
	 
	 private void parseJSON(StringBuilder sb) {
		 try {
				JSONObject mJobject = new JSONObject(sb.toString());
				Log.d(TAG, sb.toString());
				JSONObject resultsObj = mJobject.getJSONObject("results");
				JSONArray treeArray = resultsObj.getJSONArray("trees");
				Log.d(TAG, ((Integer)treeArray.length()).toString());
				for(int count = 0; count < treeArray.length();count++) {
					/*treeSB.append(treeArray.getJSONObject(count).getString("ScientificName")+" ");
					treeSB.append(treeArray.getJSONObject(count).getString("CommonName")+" ");
					treeSB.append(treeArray.getJSONObject(count).getString("FamilyName")+ " ");
					treeSB.append("\n");*/
					String ScientificName = treeArray.getJSONObject(count).getString("ScientificName");
					mCommonName = treeArray.getJSONObject(count).getString("CommonName");
					mFamilyName = treeArray.getJSONObject(count).getString("FamilyName");
					mHabitat = treeArray.getJSONObject(count).getString("Habitat");
					mInteractionRoot = treeArray.getJSONObject(count).getString("Root");
					mInteractionStem = treeArray.getJSONObject(count).getString("Stem");
					mInteractionLeaves = treeArray.getJSONObject(count).getString("Leaves");
					mInteractionFlowers = treeArray.getJSONObject(count).getString("Flower");
					mInteractionFruits = treeArray.getJSONObject(count).getString("Fruit");
				/*	
					Log.d(TAG,mCommonName);
					Log.d(TAG,mFamilyName);
					Log.d(TAG,mHabitat);
					Log.d(TAG,mInteractionRoot);
					Log.d(TAG,mInteractionStem);
					Log.d(TAG,mInteractionLeaves);
					Log.d(TAG,mInteractionFlowers);
					Log.d(TAG,mInteractionFruits);*/
				}
				Log.d(TAG,"-- TreeInfo Obtained --");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
}
