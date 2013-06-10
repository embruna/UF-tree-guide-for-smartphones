package com.Ecology;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TreeInfo extends MainMenu {
	private static String TAG = "MyActivity";
	private String mBinomialName = null;
	private String mHabitat = null;
	private String mCommonName = null;
	private String mFamilyName = null;
	private String mInteractionRoot = null;
	private String mInteractionStem = null;
	private String mInteractionLeaves = null;
	private String mInteractionFlowers = null;
	private String mInteractionFruits = null;
	private ArrayList<String> mTreePhotoList = new ArrayList<String>();
	private ArrayList<String> mRootPhotoList = new ArrayList<String>();
	private ArrayList<String> mStemPhotoList = new ArrayList<String>();
	private ArrayList<String> mLeavesPhotoList = new ArrayList<String>();
	private ArrayList<String> mFlowersPhotoList = new ArrayList<String>();
	private ArrayList<String> mFruitsPhotoList = new ArrayList<String>();
	
	private static ProgressDialog mProgressDialog;
	
	public static boolean mTreeInfoObtained = false;
	public static boolean mImagesDownloaded = false;
	
	private static volatile Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(mProgressDialog!=null)
				mProgressDialog.dismiss();
			mTreeInfoObtained = false;
			mImagesDownloaded = false;
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treeinfo);
        setTitle("Tree Information");

        mBinomialName = getIntent().getExtras().getString("BinomialName");
        Log.d(TAG,mBinomialName);
        TextView tv = (TextView)findViewById(R.id.TextView_BinomialName);
        tv.setText(mBinomialName);
        
        mCommonName = getIntent().getExtras().getString("CommonName");
        Log.d(TAG,mCommonName);
        TextView tv1 = (TextView)findViewById(R.id.TextView_CommonName);
        tv1.setText(mCommonName);
        
        final Button buttonTreeInfo = (Button)findViewById(R.id.Button_TreeInfo);
        buttonTreeInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay = "Family Name:"+mFamilyName+"\n"+
									"Habitat:"+mHabitat;
				
				String imagePath = "";
				if(!mTreePhotoList.isEmpty())
					imagePath = mTreePhotoList.get(0);

				displayDialog("Tree Info",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		        //          Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button buttonRoot = (Button)findViewById(R.id.Button_Root);
        buttonRoot.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay;
				if(mInteractionRoot == null || mInteractionRoot.equals("null"))
					toDisplay = "Not Observed";
				else
					toDisplay = mInteractionRoot;
				
				String imagePath = "";
				if(!mRootPhotoList.isEmpty())
					imagePath = mRootPhotoList.get(0);

				displayDialog("Root",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		         //         Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button buttonStem = (Button)findViewById(R.id.Button_Stem);
        buttonStem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay;
				if(mInteractionStem == null || mInteractionStem.equals("null"))
					toDisplay = "Not Observed";
				else
					toDisplay = mInteractionStem;
				
				String imagePath = "";
				if(!mStemPhotoList.isEmpty())
					imagePath = mStemPhotoList.get(0);

				displayDialog("Stem",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		        //          Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button buttonLeaves = (Button)findViewById(R.id.Button_Leaves);
        buttonLeaves.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay;
				if(mInteractionLeaves == null || mInteractionLeaves.equals("null"))
					toDisplay = "Not Observed";
				else
					toDisplay = mInteractionLeaves;
				
				String imagePath = "";
				if(!mLeavesPhotoList.isEmpty())
					imagePath = mLeavesPhotoList.get(0);

				displayDialog("Leaves",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		       //           Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button buttonFlowers = (Button)findViewById(R.id.Button_Flowers);
        buttonFlowers.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay;
				if(mInteractionFlowers == null || mInteractionFlowers.equals("null"))
					toDisplay = "Not Observed";
				else
					toDisplay = mInteractionFlowers;
				
				String imagePath = "";
				if(!mFlowersPhotoList.isEmpty())
					imagePath = mFlowersPhotoList.get(0);

				displayDialog("Flowers",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		       //           Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button buttonFruits = (Button)findViewById(R.id.Button_Fruits);
        buttonFruits.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String toDisplay;
				if(mInteractionFruits == null || mInteractionFruits.equals("null"))
					toDisplay = "Not Observed";
				else
					toDisplay = mInteractionFruits;
				String imagePath = "";
				if(!mFruitsPhotoList.isEmpty())
					imagePath = mFruitsPhotoList.get(0);
						
				displayDialog("Fruits",toDisplay,imagePath);
				//Toast.makeText(getApplicationContext(), toDisplay,
		        //          Toast.LENGTH_SHORT).show();
			}
		});
        //ProgressThread progressThread = new ProgressThread(this);
        //progressThread.start();
        
        //getInteractionInfos(mBinomialName);
        
        TreeInfoClient treeInfoClient = new TreeInfoClient(mBinomialName,
        		getApplicationContext().getFilesDir().toString(),this);
        treeInfoClient.start();
        
        mProgressDialog = ProgressDialog.show(TreeInfo.this, "Tree Info", 
                "Loading. Please wait...", true);
        new Thread(){
        	public void run() {
        		while(true) {
	    			if(mTreeInfoObtained && mImagesDownloaded) {
	    				//mProgressDialog.dismiss();
	    				mHandler.sendEmptyMessage(0);
	    				Log.d(TAG,"DIALOG gonna be dismissed");
	    				break;
	    			}
        		}
        	}
        }.start();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG,"----- ONPAUSE");
		deleteAlbumDir();
		//finish();
	}
	 private void displayDialog(String type,String contents,String photoName) {
		 Context context = this;//getApplicationContext();
		 Dialog dialog = new Dialog(context);
		 
		 dialog.setContentView(R.layout.dialogtreeinfo);
		 dialog.setTitle(type);
		 
		 TextView tv = (TextView)dialog.findViewById(R.id.TextView_Description);
		 Log.d(TAG,tv==null?"tv null":"tv not null");
		 tv.setText(contents);
		 Log.d(TAG,"2");
		 
		 ImageView iv = (ImageView)dialog.findViewById(R.id.ImageView_Pic);
		 if(photoName.equals(""))
			 iv.setImageResource(R.drawable.no_image);
		 else {			 
			 //String[] temp = mBinomialName.split("(");
			 String binomialName = "";
			 if(mBinomialName.indexOf('(') != -1) {
				 binomialName = mBinomialName.substring(0, mBinomialName.indexOf('(')-1).replaceAll(" ", "");
			 } else {
				 binomialName = mBinomialName.replaceAll(" ", "");
			 }
			 //binomialName = binomialName.split("(");
			 Log.d(TAG, "binomialName = "+binomialName+"; mBinomialName = "+mBinomialName);
			 String photoPath = getApplicationContext().getFilesDir().toString() + 
			 				"/images/" + binomialName +"/"+photoName;
			 Log.d(TAG,"photoPath = "+photoPath);
			 Bitmap bmap = BitmapFactory.decodeFile(photoPath);
			 if(bmap == null){
				 Log.d(TAG,"bmap is null");
				  return;
			 } //else
			 iv.setImageBitmap(bmap);
		 }
			 
		 
		 
		 //AlertDialog.Builder builder = new AlertDialog.Builder(context);
		 dialog.show();
	 }
	 
	 private void deleteAlbumDir() {
		 //String binomialName = mBinomialName.replaceAll(" ", "");
		 String binomialName = "";
		 if(mBinomialName.indexOf('(') != -1) {
			 binomialName = mBinomialName.substring(0, mBinomialName.indexOf('(')-1).replaceAll(" ", "");
		 } else {
			 binomialName = mBinomialName.replaceAll(" ", "");
		 }
		 Log.d(TAG,"deleteAlbumDir - binomialName = "+binomialName);
		 String AlbumDir = getApplicationContext().getFilesDir().toString() + 
			"/images/" + binomialName +"/";
		 File album = new File(AlbumDir);
		 String[] files = album.list();
		 if(files != null) {
			 for(int count = 0; count < files.length; count++) {
				 new File(AlbumDir+files[count]).delete();
			 }
			 album.delete();
		 }
	 }
	 
	 public void setTreeInformations(String commonName, String familyName, String habitat,
			 String interactionRoot, String interactionStem, String interactionLeaves,
			 String interactionFlowers,String interactionFruits) {
		    Log.d(TAG,"setTreeInformations DONE");
		 	mCommonName = commonName;
			mFamilyName = familyName;
			mHabitat = habitat;
			mInteractionRoot = interactionRoot;
			mInteractionStem = interactionStem;
			mInteractionLeaves = interactionLeaves;
			mInteractionFlowers = interactionFlowers;
			mInteractionFruits = interactionFruits;
	 }
	 
	 public void addToTreePhotoList(String treePhoto) {
		 mTreePhotoList.add(treePhoto);
	 }
	 
	 public void addToRootPhotoList(String rootPhoto) {
		 mRootPhotoList.add(rootPhoto);
	 }
	 
	 public void addToStemPhotoList(String stemPhoto) {
		 mStemPhotoList.add(stemPhoto);
	 }
	 
	 public void addToLeavesPhotoList(String leavesPhoto) {
		 mLeavesPhotoList.add(leavesPhoto);
	 }
	 
	 public void addToFlowersPhotoList(String flowerPhoto) {
		 mFlowersPhotoList.add(flowerPhoto);
	 }
	 
	 public void addToFruitsPhotoList(String fruitPhoto) {
		 mFruitsPhotoList.add(fruitPhoto);
	 }
	 
}