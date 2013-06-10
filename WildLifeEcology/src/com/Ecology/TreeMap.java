package com.Ecology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;



public class TreeMap extends MapActivity {


	LinearLayout linearLayout;
	volatile MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	PersonOverLay itemizedOverlay;
	OverlayItem userOverlayitem;
	TreeOverlay treeOverlay;
	LocationManager locationManager;
	Location userLocation;
	LocationListener locationListener;
	volatile List<Address> addr;
	boolean locationDisplayed = false;
	boolean mMarkOnce = false; 
	TreeMapClient mTreeMapClient = new TreeMapClient();
	//Binomial Name -> GPS Record
	public static volatile HashMap<String,ArrayList<GPSRecord>> mCoordinatesMap = new HashMap<String,ArrayList<GPSRecord>>();
	//Common Name -> Binomial Name
	public static volatile HashMap<String,String> mCommonNameToBinomialNameMap = new HashMap<String,String>();
	volatile boolean mFileDownloaded = false;
	public static volatile String mSearchString = "";
	private static final int TWO_MINUTES = 2 * 60 * 1000;
	private static final String TAG = "MyActivity";
	private static final String FILENAME = "GPSCoordinates.txt";
	private static final long ONEWEEKTIME = 18316800;
	private static final String QUERYURL = "http://wecuf.zoka.cc/TreeMap.php?name=ALL";
	private static volatile ProgressDialog mProgressDialog;
	private static volatile boolean mSearchByCommonName = false;
	private static volatile boolean mSearchByBinomialName = false;
	
	private volatile static Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
		}
	};
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);
		 setContentView(com.Ecology.R.layout.treemap);
		 //Log.d(TAG,"onCreate 2" ); 
		 Intent intent = getIntent();  
		 if (Intent.ACTION_SEARCH.equals(intent.getAction())) {  
		     mSearchString = intent.getStringExtra(SearchManager.QUERY);  
		     Log.d(TAG," ---- query = "+mSearchString);
		     findCommonNameOrBinomialName();
		     
		     if(!mSearchByBinomialName && !mSearchByCommonName) {
		    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    		//builder.setMessage("Search Result - Empty")
		    		builder.setMessage(mSearchString + " is not found!")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		                //markAllTrees();
		    		                dialog.dismiss();
		    		                finish();
		    		           }
		    		       });
		    		AlertDialog alert = builder.create();
		    		alert.show();
		     }
		    // doMySearch(query);  
         } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        	    // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
        	    Uri data = intent.getData();
        	    showResult(data);
    	} else {
			 if(intent != null && intent.getExtras() != null)
				 mSearchString = intent.getExtras().getString("BinomialName");
			   Log.d(TAG,"inside else mSearchString = "+mSearchString);
    	}

		mapView = (MapView) findViewById(com.Ecology.R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		//Get Coordinates File
    	File file = getApplicationContext().getFileStreamPath(FILENAME);
    	if(!file.exists()) {
    		mTreeMapClient.setFileName(file.getAbsolutePath());
    		mTreeMapClient.setUrl(QUERYURL);
    		mTreeMapClient.setActivity(this);
    		mTreeMapClient.start();
    		
            mProgressDialog = ProgressDialog.show(TreeMap.this, "Tree Map", 
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
    		long timeCreated = file.lastModified();
    		long timeDifference = System.currentTimeMillis() - timeCreated;
    		Log.d(TAG, "timeCreated = "+timeCreated);
    		Log.d(TAG, "timeDifference = "+timeDifference);
    		Log.d(TAG, "System.currentTimeMillis() = "+System.currentTimeMillis());
    		if(timeDifference > ONEWEEKTIME || timeDifference < 0) {
    			mTreeMapClient.setFileName(file.getAbsolutePath());
    			mTreeMapClient.setUrl(QUERYURL);
        		mTreeMapClient.setActivity(this);
    			mTreeMapClient.start();
    		}
    		Log.d(TAG, "File already present!");
    		Log.d(TAG, getApplicationContext().getFilesDir().toString());
    	}
    	
		Log.d(TAG,"getOverlays" );
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(com.Ecology.R.drawable.pin);
		
		itemizedOverlay = new PersonOverLay(drawable,this);
		
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		registerListener();
	}
	
	private void showResult(Uri uri) {
		// TODO Auto-generated method stub
	    if(uri != null){
    		/*Log.d(TAG,"1 Uri = "+uri.getEncodedFragment());
    		Log.d(TAG,"2 Uri = "+uri.getFragment());
    		Log.d(TAG,"3 Uri = "+uri.getQuery());
    		Log.d(TAG,"4 Uri = "+uri.getFragment());
    		Log.d(TAG,"5 Uri = "+uri.toString());
    		Log.d(TAG,"6 Uri = "+uri.getScheme());*/
    		//String[] columns = { BaseColumns._ID,SearchManager.SUGGEST_COLUMN_TEXT_1};
    		Log.d(TAG,"5 Uri = "+uri.toString());
    		 Cursor cursor = managedQuery(uri, null, null, null, null);
    		 if (cursor == null) {
    	            finish();
    	        } else {
    	        	Log.d("TAG","Cursor is not Null");
    	            cursor.moveToFirst();
    	            int wIndex = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
    	            Log.d(TAG,"TreeMap wIndex = "+wIndex);
    	            Log.d(TAG,"TreeMap selected suggestion = "+cursor.getString(wIndex));
    	            mSearchString = cursor.getString(wIndex);
    	            findCommonNameOrBinomialName();
    			    Log.d(TAG," ---- showResult = "+mSearchString);
    			    markSelectedTrees();
    	        }
    	}		
	}
	
	private void findCommonNameOrBinomialName() {
		Set<String> set = mCommonNameToBinomialNameMap.keySet();
		Iterator<String> iter = set.iterator();
		while(iter.hasNext()) {
			String commonName = iter.next();
			String binomialName = mCommonNameToBinomialNameMap.get(commonName);
			if(mSearchString.toLowerCase().equals(commonName.toLowerCase())) {
				mSearchByCommonName = true;
				mSearchByBinomialName = false;
				break;
			} else if(mSearchString.toLowerCase().equals(binomialName.toLowerCase())) {
				mSearchByBinomialName = true;
				mSearchByCommonName = false;
				break;
			} else {
				mSearchByCommonName = false;
				mSearchByBinomialName = false;
			}
		}
	}
	
	private void registerListener() {
		locationListener  = new LocationListener() {    
			public void onLocationChanged(Location newLocation) {  
				Log.d(TAG,"LocationListener.onLocationChanged()");
				if(isBetterLocation(newLocation)) {
					userLocation = newLocation;
					displayNewLocation();
				}
			}    
			public void onStatusChanged(String provider, int status, Bundle extras) {}    
			public void onProviderEnabled(String provider) {}    
			public void onProviderDisabled(String provider) {}
		};
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TWO_MINUTES, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 0, locationListener);
		userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(isBetterLocation(locationNetwork)) {
			displayNewLocation();
		}
		

		Log.d(TAG,"registerListener()");
	}
	
	public void removeUpdates() {
		locationManager.removeUpdates(locationListener);
	}
	protected boolean isBetterLocation(Location location) {
		if (userLocation == null) {        
		// A new location is always better than no location        
		return true;    
		}    
		// Check whether the new location fix is newer or older    
		long timeDelta = location.getTime() - userLocation.getTime();    
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;    
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;    
		boolean isNewer = timeDelta > 0;    
		// If it's been more than two minutes since the current location, use the new location    
		// because the user has likely moved    
		if (isSignificantlyNewer) {        
		return true;    
		// If the new location is more than two minutes older, it must be worse    
		} else if (isSignificantlyOlder) {       
		 return false;    
		}   
		 // Check whether the new location fix is more or less accurate    
		int accuracyDelta = (int) (location.getAccuracy() - userLocation.getAccuracy());    
		boolean isLessAccurate = accuracyDelta > 0;    
		boolean isMoreAccurate = accuracyDelta < 0;    
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;    
		// Check if the old and new location are from the same provider    
		boolean isFromSameProvider = isSameProvider(location.getProvider(),userLocation.getProvider());    
		// Determine location quality using a combination of timeliness and accuracy    
		if (isMoreAccurate) {        
		return true;    
		} else if (isNewer && !isLessAccurate) {        
		return true;    
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) 
		{        return true;    
		}    return false;}
		

		private boolean isSameProvider(String provider1, String provider2) {    
		if (provider1 == null) {      
		return provider2 == null;    }    
		return provider1.equals(provider2);
		}
		
		private void displayNewLocation() {
			Log.d(TAG,"displayNewLocation");
			/*if(locationDisplayed) {
				removeUpdates();
				return;
			}*/
			if(userLocation != null) {
				GeoPoint point = new GeoPoint((int)(userLocation.getLatitude() * 1E6),(int) (userLocation.getLongitude()*1E6));
				//OverlayItem overlayitem  = new OverlayItem(point, "", "");
				if(userOverlayitem != null)
					itemizedOverlay.removeOverlay(userOverlayitem);
				userOverlayitem = new OverlayItem(point, "", "");
				itemizedOverlay.addOverlay(userOverlayitem);
				mapOverlays.add(itemizedOverlay);
				locationDisplayed = true;
				if(!mMarkOnce) {
					mapView.getController().animateTo(point);
					mapView.getController().setCenter(point);
					mapView.getController().setZoom(15);
				}
				mMarkOnce = true;				
				markTrees();
				
				//markAllTrees();
				
				//ReverseGeocodeLookupTask rlt= new ReverseGeocodeLookupTask();
				//rlt.applicationContext = this;
				//rlt.execute();
			}
		}

	    private void markTrees() {
	    	Log.d(TAG,"markTrees: mSearchString = "+mSearchString);
	    	if(mSearchString.equals("")) {
	    		markAllTrees();
	    	}
	    	else 
	    	if(mSearchByBinomialName || mSearchByCommonName) {
	    		markSelectedTrees();
	    	}
	    	else {
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage("Search Result - Empty")
	    		       .setCancelable(false)
	    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		                //markAllTrees();
	    		                dialog.dismiss();
	    		           }
	    		       });
	    		AlertDialog alert = builder.create();
	    		alert.show();
	    	}
	    		
		}

		private void markSelectedTrees() {
			Log.d(TAG,"-------markSelectedTrees-------");
			if(mCoordinatesMap.size() == 0) {
				extractCoordinates();
			}
			String binomialName = mSearchString.trim();
			if(mSearchByCommonName) {
				Log.d(TAG,"markSelectedTrees - mSearchString = "+mSearchString);
				binomialName = mCommonNameToBinomialNameMap.get(mSearchString.trim());
				Log.d(TAG,"markSelectedTrees - binomialName = "+binomialName);
				
				Set<String> keyset = mCommonNameToBinomialNameMap.keySet();
				Iterator<String> iter = keyset.iterator();
				while(iter.hasNext()){
					String str = iter.next();
					Log.d(TAG,"key = "+str+"; val = "+mCommonNameToBinomialNameMap.get(str));
				}
			}
			mapView = (MapView) findViewById(R.id.mapview);
			mapOverlays = mapView.getOverlays();
			
			Drawable drawable = this.getResources().getDrawable(com.Ecology.R.drawable.tree_marker);
			if(treeOverlay == null)
				treeOverlay = new TreeOverlay(drawable,getApplicationContext(),this);
			else
				treeOverlay.removeOverlay();
			Log.d(TAG,"markSelectedTrees - binomialName = "+binomialName);
			ArrayList<GPSRecord> recordList = mCoordinatesMap.get(binomialName);
			for(int index = 0;index < recordList.size();index++) {
				GPSRecord record = recordList.get(index);
				GeoPoint point = new GeoPoint(record.getLatitude(),
								record.getLongitude());
				Log.d(TAG,"ScientificName = "+record.getScientificName());
				Log.d(TAG,"CommonName = "+record.getCommonName());
				Log.d(TAG,"latitude = "+record.getLatitude());
				Log.d(TAG,"longitude = "+record.getLongitude());
				OverlayItem item = new OverlayItem(point,"Tree Info",
						"Binomial Name: "+record.getScientificName()+"\nCommon Name: "+record.getCommonName());
				treeOverlay.addOverlay(item,record.getScientificName(),record.getCommonName());
				//mapOverlays.add(treeOverlay);
			}
			mapOverlays.add(treeOverlay);
			treeOverlay.populateNow();
			//mSearchByCommonName = false;
			//mSearchByBinomialName = false;
			//mSearchString = "";
		}

		public class ReverseGeocodeLookupTask extends AsyncTask <Void, Void, String>
	    {
	        private ProgressDialog dialog;
	        protected Context applicationContext;
	       
	        @Override
	        protected void onPreExecute()
	        {
	            this.dialog = ProgressDialog.show(applicationContext, "Please wait...",
	                    "Marking Trees", true);
	        }
	       
	        @Override
	        protected String doInBackground(Void... params)
	        {
	        	Log.d(TAG,"doInBackGround");
	        	try {
	        		markAllTrees();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return "Trees marked!";
	        }
	       
	        protected void onPostExecute(String result)
	        {
	            //markHospitals();
	            this.dialog.cancel();
	        }
	    }
		
	    public void markAllTrees() {
	    	Log.d(TAG,"markAllTrees");
			//extractCoordinates();
	    	if(mCoordinatesMap.size() == 0) {
				extractCoordinates();
			}
	    	mapOverlays = mapView.getOverlays();
			Drawable drawable = this.getResources().getDrawable(com.Ecology.R.drawable.tree_marker);
			treeOverlay = new TreeOverlay(drawable,getApplicationContext(),this);
			
			for(Map.Entry<String, ArrayList<GPSRecord>> entry : mCoordinatesMap.entrySet()) {
				ArrayList<GPSRecord> list = entry.getValue();
				for(int index = 0;index < list.size();index++) {
					GPSRecord record = list.get(index);
					GeoPoint point = new GeoPoint(record.getLatitude(),
									record.getLongitude());
					//Log.d(TAG,"ScientificName = "+record.getScientificName());
					//Log.d(TAG,"CommonName = "+record.getCommonName());
					//Log.d(TAG,"latitude = "+record.getLatitude());
					//Log.d(TAG,"longitude = "+record.getLongitude());
					OverlayItem item = new OverlayItem(point,"Tree Info",
							"Binomial Name: "+record.getScientificName()+"\nCommon Name: "+record.getCommonName());
					treeOverlay.addOverlay(item,record.getScientificName(),record.getCommonName());
					//mapOverlays.add(treeOverlay);
				}
				//treeOverlay.populateNow();
			}
			mapOverlays.add(treeOverlay);
			treeOverlay.populateNow();
	    }
	    
		private boolean extractCoordinates() {
			Log.d(TAG,"extractCoordinates");
			try {
				FileInputStream fis = openFileInput(FILENAME);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuilder sb = new StringBuilder();
				String line;
				try {
					while((line = br.readLine()) != null) {
						sb.append(line);
						Log.d(TAG,line);
					}
				} catch (IOException e) {
					Log.d(TAG,"IOException");
					return false;
				}
				boolean status = parseFile(sb);
				
				return status;
			} catch (FileNotFoundException e) {
				Log.d(TAG,"FileNotFoundException");
				return false;
			}
		}		
		
		private boolean parseFile(StringBuilder sb) {
			Log.d(TAG,"parseFile");
			try {
				JSONObject mJobject = new JSONObject(sb.toString());
				JSONObject resultsObj = mJobject.getJSONObject("results");
				JSONArray treeArray = resultsObj.getJSONArray("trees");
				Log.d(TAG,"Length = "+treeArray.length());
				ContentValues values = new ContentValues();
				for(int count = 0; count < treeArray.length();count++) {
					String scientificName = treeArray.getJSONObject(count).getString("ScientificName").trim();
					String commonName = treeArray.getJSONObject(count).getString("CommonName").trim();
					String latitude = treeArray.getJSONObject(count).getString("Latitude").trim();
					String longitude = treeArray.getJSONObject(count).getString("Longitude").trim();
					//Log.d(TAG,"scientificName = "+scientificName);
					//Log.d(TAG,"commonName = "+commonName);
					//Log.d(TAG,"longitude = "+longitude);
					//Log.d(TAG,"scientificName = "+latitude);
					// To update the content provider
					values.put(NamesContentProvider.NAMES_COLUMN, scientificName);
					getContentResolver().insert(NamesContentProvider.CONTENT_URI, values);
					values.clear();
					values.put(NamesContentProvider.NAMES_COLUMN, commonName);
					getContentResolver().insert(NamesContentProvider.CONTENT_URI, values);
					values.clear();
					ArrayList<GPSRecord> list = mCoordinatesMap.get(scientificName);
					if(list != null) {
						list.add(new GPSRecord(scientificName,commonName,latitude,longitude));
						mCoordinatesMap.put(scientificName, list);
					} else {
						ArrayList<GPSRecord> listNew = new ArrayList<GPSRecord>();
						listNew.add(new GPSRecord(scientificName,commonName,latitude,longitude));
						mCoordinatesMap.put(scientificName, listNew);
					}					
					mCommonNameToBinomialNameMap.put(commonName,scientificName);
				}
			} catch (JSONException e) {
				Log.d(TAG,"JSONException");
				return false;
			}
			return true;
		}
	    
	    public Location getUserLocation() {
	    	return userLocation;
	    }
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.map_menu, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch(item.getItemId()) {
	    	case R.id.SearchTrees:
	    		Intent SearchTree = new Intent(TreeMap.this,SearchTree.class);
				startActivity(SearchTree);
	    		return true;
	    	case R.id.Map:
	    		//Intent map = new Intent(TreeMap.this,TreeMap.class);
				//startActivity(map);
	    		onSearchRequested();
	    		return true;
	    	case R.id.Glossary:
	    		Intent glossary = new Intent(TreeMap.this,Glossary.class);
				startActivity(glossary);
	    		return true;
	    	case R.id.Quiz:
	    		Intent quiz = new Intent(TreeMap.this,Quiz.class);
				startActivity(quiz);
	    		return true;
/*	    	case R.id.BinomialName:
	    		Log.d(TAG,"Search By Binomial Name");
	    		onSearchRequested();  
	    		mSearchByBinomialName = true;
	    		return true;
	    	case R.id.CommonName:
	    		Log.d(TAG,"Seach By CommonName");
	    		onSearchRequested();  
	    		mSearchByCommonName = true;
	    		return true;
*/	    	default:
	    		return true;
	    	}
	    }
	    
	    private class GPSRecord {
	    	String mScientificName;
	    	String mCommonName;
	    	int mLatitude;
	    	int mLongitude;
	    	
	    	public GPSRecord(String scientificName, String commonName, String latitude, String longitude) {
	    		mScientificName = scientificName;
	    		mCommonName = commonName;
	    		//Log.d(TAG,"Degrees = "+Location.convert(-82.336111,0));
	    		//Log.d(TAG,"Minutes = "+Location.convert(-82.336111,1));
	    		//Log.d(TAG,"Seconds = "+Location.convert(-82.336111,2));
	    		//Log.d(TAG,"convert -ve = "+Location.convert("-82:22.092"));
	    		//Log.d(TAG,"convert +ve = "+Location.convert("29:38.073"));
	    		mLatitude = (int)(Location.convert(latitude.trim())*1E6);
	    		mLongitude = (int)(Location.convert(longitude.trim())*1E6);
	    	}
	    	
	    	public String getScientificName() {
	    		return mScientificName;
	    	}
	    	
	    	public String getCommonName() {
	    		return mCommonName;
	    	}
	    	
	    	public int getLatitude() {
	    		return mLatitude;
	    	}
	    	
	    	public int getLongitude() {
	    		return mLongitude;
	    	}
	    }
	    
		public void setFileDownloaded(boolean status) {
			mFileDownloaded = status;
		}
}