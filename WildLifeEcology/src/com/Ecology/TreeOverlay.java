package com.Ecology;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class TreeOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private TreeMap mActivity;
	private static final String TAG = "MyActivity";
	ArrayList<String> mScientificName = new ArrayList<String>();
	ArrayList<String> mCommonName = new ArrayList<String>();
	
	public TreeOverlay(Drawable defaultMarker,Context context,TreeMap activity) {
		super(boundCenterBottom(defaultMarker));
		this.mContext = context;
		this.mActivity = activity;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay,String scientificName,String commonName) {
		mScientificName.add(scientificName);
		mCommonName.add(commonName);
		mOverlays.add(overlay);    
		//Log.d(TAG,"mOverLay size = "+mOverlays.size());
		//populate();
	}
	
	public void populateNow() {
		populate();
	}
	
	public void removeOverlay() {
	 //mOverlays.remove(mOverlays.size() - 1);
	 mOverlays.clear();
	}
	@Override
	protected boolean onTap(int index) {
		/*
		String text = "Scientific Name : "+mScientificName.get(index)+"\n Common Name : "+mCommonName.get(index);
		Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		toast.show();*/
		Log.d(TAG,"index = "+index);
		OverlayItem item = mOverlays.get(index);
		/*AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);//.getApplicationContext());
		//Dialog dialog = new Dialog(mContext);
		Log.d(TAG,"Title = "+item.getTitle());
		Log.d(TAG,"Snippet = "+item.getSnippet());
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		*/
		
		Dialog dialog = new Dialog(mActivity);
		 
		 dialog.setContentView(R.layout.dialog_map_tree_info);
		 dialog.setTitle(item.getTitle());
		 
		 TextView tv = (TextView)dialog.findViewById(R.id.TextView_Description);
		 //Log.d(TAG,tv==null?"tv null":"tv not null");
		 tv.setText(item.getSnippet());
		 String[] str = item.getSnippet().split("\n");
		 String[] str1 = str[0].split(":");
		 final String scientificName = str1[1].trim();
		 String[] str2 = str[1].split(":");
		 final String commonName = str2[1].trim();
		 Log.d(TAG, "scientificName = "+ scientificName);
		 Log.d(TAG, "commonName = "+ commonName);
		 Button bt = (Button)dialog.findViewById(R.id.Button_TreeInfo);
		 bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent TreeInfo = new Intent(mActivity,TreeInfo.class);
				 Bundle bundle = new Bundle();
				 bundle.putString("BinomialName",scientificName);
				 bundle.putString("CommonName",commonName);
				 TreeInfo.putExtras(bundle);
				mActivity.startActivity(TreeInfo);
			}
			 
		 });
		 //Log.d(TAG,"2");
		 dialog.show();
		return true;
	}
}
