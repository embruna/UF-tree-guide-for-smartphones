package com.Ecology;

import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import android.widget.Toast;


import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class PersonOverLay extends ItemizedOverlay{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	TreeMap map;
	
	public PersonOverLay(Drawable defaultMarker, TreeMap map) {
		super(boundCenterBottom(defaultMarker));
		this.map = map;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {    
		mOverlays.add(overlay);    
		populate();
	}
	
	public void removeOverlay(OverlayItem overlayItem) {
		if(mOverlays.contains(overlayItem)) {
			mOverlays.remove(overlayItem);
		}
	}
	
	@Override
	protected boolean onTap(int index) {/*
		String text = "I'm at Lat: "+map.getUserLocation().getLatitude()+
						"; Long: "+ map.getUserLocation().getLongitude();
		Toast toast = Toast.makeText(map.getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();*/
		return true;
	}
}

