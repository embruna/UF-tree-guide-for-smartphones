package com.Ecology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchTree extends ListActivity {
	private static String TAG = "MyActivity";
	private static String mFileName = "TreeData.txt";
	//HashMap<String,String> mScientificToCommonNameMap = new HashMap<String,String>();
	//HashMap<String,String> mCommonToScientificNameMap = new HashMap<String,String>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Browse Trees");
        //setContentView(R.layout.searchtree);
        
        //To do
        //Contact the URL to get the list of Binomial names first time the app is started.
        //Create SQLite database
        //ArrayList<String> treeArray = getTreeNames("ALL");
        FileInputStream fis;
        ArrayList<HashMap<String,String>> treeArray = new ArrayList<HashMap<String,String>>();
		try {
			fis = openFileInput(mFileName);
			InputStreamReader ins = new InputStreamReader(fis);
	        BufferedReader br = new BufferedReader(ins);
	        StringBuilder sb = new StringBuilder();
			String str = null;
			try {
				while((str = br.readLine()) != null) {
					sb.append(str);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        treeArray = insertToLocalDatabase(sb);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(!treeArray.isEmpty()) {
        //setListAdapter(new ArrayAdapter<String>(this,R.layout.searchtree_list_item , treeArray.toArray(new String[treeArray.size()])));
        setListAdapter(new SimpleAdapter(this,treeArray,R.layout.searchtree_temp,
        		new String[]{"ScientificName","CommonName"}, new int[] {R.id.TextView_ScientificName,R.id.TextView_CommonName}));
        //String[] countries = getResources().getStringArray(R.array.countries_array);
        //setListAdapter(new ArrayAdapter<String>(this,R.layout.searchtree_list_item , countries));
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
    	//onSearchRequested();
        Log.d(TAG, "B4 Intent");
        Intent intent = getIntent();
        Log.d(TAG, "After Intent");
      //  handleIntent(intent);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
              /* When clicked, show a toast with the TextView text
              Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                  Toast.LENGTH_SHORT).show();*/
				 Intent TreeInfo = new Intent(SearchTree.this,TreeInfo.class);
				 Bundle bundle = new Bundle();
				 HashMap<String,String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				 bundle.putString("BinomialName",map.get("ScientificName"));
				 bundle.putString("CommonName",map.get("CommonName"));
				 TreeInfo.putExtras(bundle);
				 startActivity(TreeInfo);
            }
        });
        }
    }
   /* 
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	String query = intent.getStringExtra(SearchManager.QUERY);
        	Log.d(TAG, "Search Text = "+query);
        }
    }
*/
    public void onResume() {
    	super.onResume();
    	getListView().clearTextFilter();
    }
    private ArrayList<HashMap<String,String>> insertToLocalDatabase(StringBuilder sb) {
    	//StringBuilder treeSB = new StringBuilder();
    	ArrayList<HashMap<String,String>> treeNameList = new ArrayList<HashMap<String,String>>();
		try {
			JSONObject mJobject = new JSONObject(sb.toString());
			Log.d(TAG, "1");
			JSONObject resultsObj = mJobject.getJSONObject("results");
			Log.d(TAG, "2");
			JSONArray treeArray = resultsObj.getJSONArray("trees");
			Log.d(TAG, "3");
			for(int count = 0; count < treeArray.length();count++) {
				//treeNameList.add(treeArray.getJSONObject(count).getString("ScientificName"));
				//Log.d(TAG,treeNameList.get(count));
				String scientificName = treeArray.getJSONObject(count).getString("ScientificName");
				String commonName = treeArray.getJSONObject(count).getString("CommonName");
				//mScientificToCommonNameMap.put(scientificName, commonName);
				//mCommonToScientificNameMap.put(commonName, scientificName);
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("ScientificName", scientificName);
				map.put("CommonName", commonName);
				treeNameList.add(map);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return treeNameList;
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
    		Intent SearchTree = new Intent(SearchTree.this,SearchTree.class);
			startActivity(SearchTree);
    		return true;
    	case R.id.Map:
    		Intent map = new Intent(SearchTree.this,TreeMap.class);
			startActivity(map);
    		return true;
    	case R.id.Glossary:
    		Intent glossary = new Intent(SearchTree.this,Glossary.class);
			startActivity(glossary);
    		return true;
    	case R.id.Search:
    		//Intent quiz = new Intent(SearchTree.this,Quiz.class);
			//startActivity(quiz);
    		InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		inputMgr.toggleSoftInput(0, 0);
    		return true;
    	default:
    		return true;
    	}
    }
    
}
