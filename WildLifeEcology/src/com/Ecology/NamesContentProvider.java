package com.Ecology;

import java.util.ArrayList;
import java.util.Vector;

import com.Ecology.TreeMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class NamesContentProvider extends ContentProvider {
	public static String AUTHORITY = "com.Ecology.NamesContentProvider";
	public final static Uri CONTENT_URI = 
		Uri.parse("content://com.Ecology.NamesContentProvider/names");
	public final static String NAMES_COLUMN = SearchManager.SUGGEST_COLUMN_TEXT_1;
    private static final int SEARCH_WORDS = 0;
    private static final int GET_WORD = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();
	public static String TAG = "MyActivity";
	
    private MatrixCursor mCursor;

	TreeMap mActivity;
	Vector<String> mNames = new Vector<String>();
	
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "names", SEARCH_WORDS);
        matcher.addURI(AUTHORITY, "names/#", GET_WORD);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//throw new UnsupportedOperationException();
		if(!mNames.contains((String)values.get(NAMES_COLUMN)))
			mNames.add((String)values.get(NAMES_COLUMN));
		return null;
	}

	@Override
	public boolean onCreate() {		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG,"NamesContentProvider::query");
		//Log.d("TAG","query = "+uri.getEncodedQuery());
		 // Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_WORDS:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_WORD:
                return getWord(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }	
}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	public void setActivity(TreeMap activity){
		mActivity = activity;
	}
	
	private Cursor refreshShortcut(Uri uri) {
		Log.d("TAG","refreshShortcut");
		return null;
	}

	private Cursor search(String string) {
		Log.d("TAG","search");
		return null;
	}
	
	private Cursor getWord(Uri uri) {
		Log.d(TAG,"getWord");
		int index = Integer.parseInt(uri.getLastPathSegment().trim());
		mCursor.moveToFirst();
		Log.d(TAG,"index = "+index);
        // Log.d("TAG","index = "+wIndex);
        //Log.d(TAG,"No. of rows = "+mCursor.getCount());
        mCursor.moveToPosition(index);
        //Log.d(TAG,"Curr Position = "+mCursor.getPosition());
        int wIndex = mCursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
        //Log.d("TAG","windex = "+wIndex);
		//Log.d(TAG,"selected suggestion = "+mCursor.getString(wIndex));
		MatrixCursor cursor = new MatrixCursor(new String[] {
                BaseColumns._ID, // must include this column
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                });
		cursor.addRow(new Object[]{wIndex,mCursor.getString(wIndex),wIndex});		
		return cursor;
	}

	private Cursor getSuggestions(String string) {
		Log.d(TAG,"getSuggestions = "+string);
		/*if(selection != null) {
			Log.d("TAG","Selected String = "+selection);
			return null;
		}*/
		//MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
		mCursor = new MatrixCursor(new String[] {
		                                BaseColumns._ID, // must include this column
		                                SearchManager.SUGGEST_COLUMN_TEXT_1,
		                                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
		                                });
		int index = 0;
		Log.d(TAG,"selectionArgs = "+string);
		for(int j = 0; j<mNames.size();++j){
			if(mNames.get(j).toLowerCase().contains(string.toLowerCase())){
				mCursor.addRow(new Object[]{index,mNames.get(j),index});
				++index;
			}
		}
		Log.d(TAG,"Row Count = "+mCursor.getCount());
		return mCursor;
	}
}
