package com.Ecology;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class PicasaClient extends Thread {
	
	//PicasawebService mService;
	private static final String TAG = "MyActivity";
	private static final String USER = "wis5521";
	private static final String USERNAME = "wis5521@gmail.com";
	private static final String PASSWD = "bruna2010";
	private static final String APPNAME = "WildLifeEcology";
	private volatile String mBinomialName = "null";
	private volatile String mRootPath = "";
	private static String ROOTURL = "http://picasaweb.google.com/data/feed/api/user/wis5521?kind=photo&tag=";
	private volatile static PicasaClient mPicasaClient = null;
	private TreeInfo mTreeInfo;
	
	private PicasaClient(String albumName,TreeInfo treeInfo) {
		mBinomialName = albumName;
		mTreeInfo = treeInfo;
	}
	
	synchronized public static PicasaClient getPicasaClient() {
		if(mPicasaClient == null) {
			mPicasaClient = new PicasaClient("null",null);
			mPicasaClient.start();
		}
		return mPicasaClient;
	}
	
	
	public void run() {
		HttpURLConnection connection = null;
		while(true) {
			if(mBinomialName.equals("null") || (mTreeInfo == null))
				continue;
			
			try {
				URL serverAddress = new URL(ROOTURL+mBinomialName);
				Log.d(TAG,"url = "+ROOTURL+mBinomialName);
				try {

					//Parse the XML to get the link to photos
					PicasaFeedHandler handler = new PicasaFeedHandler();
					SAXParserFactory factory = SAXParserFactory.newInstance();
					
					try {
						SAXParser parser;
						parser = factory.newSAXParser();
						parser.parse(serverAddress.toString(), handler);
						//parser.parse
						HashMap<String,PhotoDetails> photosToUrlMap = handler.getPhotoMap();
						Iterator iterator = photosToUrlMap.keySet().iterator();
						while (iterator.hasNext()) {
							String key = iterator.next().toString();  
							// value = photosToUrlMap.get(key).toString();  
							Log.d(TAG,"key = "+key); 
							getImage(photosToUrlMap.get(key));
							addToTreeInfoLists(key);
						}
						Log.d(TAG,"--- Download Complete ---");
						TreeInfo.mImagesDownloaded = true;
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
						e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(TAG, "Malformed URL");
				continue;
			}
			mBinomialName = "null";
			mTreeInfo = null;
		}
	}
	
	public void setBinomialName(String name) {
		mBinomialName = name;
	}
	
	public void setTreeInfo(TreeInfo treeInfo) {
		mTreeInfo = treeInfo;
	}
	public void setRootPath(String path) {
		mRootPath = path;
	}
	private void addToTreeInfoLists(String photoName) {
		if(photoName.contains("tree") || photoName.contains("habitat"))
			mTreeInfo.addToTreePhotoList(photoName);
		else if(photoName.contains("stem") || photoName.contains("trunk"))
			mTreeInfo.addToStemPhotoList(photoName);
		else if(photoName.contains("root"))
			mTreeInfo.addToRootPhotoList(photoName);
		else if(photoName.contains("leaves"))
			mTreeInfo.addToLeavesPhotoList(photoName);
		else if(photoName.contains("flower"))
			mTreeInfo.addToFlowersPhotoList(photoName);
		else if(photoName.contains("fruit")|| photoName.contains("seed"))
			mTreeInfo.addToFruitsPhotoList(photoName);
	}
	private boolean getImage(PhotoDetails photoDetail) {
		HttpURLConnection connection = null;
		URL serverAddress;
		try {
			serverAddress = new URL(photoDetail.getUrl());
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);
			connection.connect();
			//Log.d(TAG,"User dir = "+System.getProperty("user.dir"));
			//InputStreamReader ins = new InputStreamReader(connection.getInputStream());
			DataInputStream dis = new DataInputStream(connection.getInputStream());
			File rootDir = new File(mRootPath+"/images/"+mBinomialName);
			if(rootDir.mkdirs())
				Log.d(TAG,"Dir successfully created");
			FileOutputStream fos = new FileOutputStream(mRootPath+"/images/"+mBinomialName+"/"+photoDetail.getPhotoName());
			
			byte[] temp = new byte[photoDetail.getFileSize()];
			int readBytes = 0;
			while((readBytes = dis.read(temp,0,photoDetail.getFileSize())) > 0) {
				fos.write(temp,0,readBytes);
				fos.flush();
			}
			fos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	public static class XMLTags {
		public static final String ENTRY = "entry";
		public static final String PHOTO_TITLE = "title";
		public static final String PHOTO_CONTENT = "content";
		public static final String PHOTO_URL = "url";
		public static final String PHOTO_THUMBNAIL = "thumbnail";
		public static final String PHOTO_HEIGHT = "height";
		public static final String PHOTO_WIDTH = "width";
		public static final String PHOTO_SIZE = "size";
		public static final String GPHOTO_NAMESPACE = "http://schemas.google.com/photos/2007";
		public static final String MEDIA_NAMESPACE = "http://search.yahoo.com/mrss/";
		public static final String ENTRY_NAMESPACE = "http://www.w3.org/2005/Atom";
	}
	
	private class PhotoDetails {
		private String mPhotoName;
		private String mUrl;
		private int mFileSize;
		
		public PhotoDetails(String photoName, String url, int fileSize) {
			mPhotoName = photoName;
			mUrl = url;
			mFileSize = fileSize;
		}
		
		public String getPhotoName() {
			return mPhotoName.trim();
		}
		
		public String getUrl() {
			return mUrl.trim();
		}
		
		public int getFileSize() {
			return mFileSize;
		}
		
		public void setPhotoName(String photoName) {
			mPhotoName = photoName;
		}
		
		public void setUrl(String url) {
			mUrl = url;
		}
		
		public void setFileSize(int size) {
			mFileSize = size;
		}
	}
	
	private class PicasaFeedHandler extends DefaultHandler {
		private String mPhotoName;
		private String mUrl;
		private String mTempName;
		private int mFileSize;
		private HashMap<String,PhotoDetails> mAlbumMap = new HashMap<String,PhotoDetails>();
		
		public void StartDocument() throws SAXException {
			super.startDocument();
			//Log.d(TAG,"StartDocument");
			//mAlbumMap = new HashMap<String,String>();
		}
		
		public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			//Log.d(TAG,"startElement - " + localName);
			if(uri.equals(XMLTags.ENTRY_NAMESPACE)
					&& XMLTags.ENTRY.equals(localName)) {
				mPhotoName = "";
				mUrl = "";
				mTempName = "";
				mFileSize = 0;
			} else if(uri.equals(XMLTags.MEDIA_NAMESPACE)
					&& XMLTags.PHOTO_CONTENT.equals(localName)) {
				mUrl = attributes.getValue(XMLTags.PHOTO_URL); 
				//Log.d(TAG,"GOTTTTTTTTTTTT mUrl = "+ mUrl);
			} else if(uri.equals(XMLTags.MEDIA_NAMESPACE)
					&& XMLTags.PHOTO_TITLE.equals(localName)) {
				mPhotoName = "";
				mTempName = "";
			} else if(uri.equals(XMLTags.GPHOTO_NAMESPACE)
					&& XMLTags.PHOTO_SIZE.equals(localName)) {
				mFileSize = 0;
			}
		}
		
		public void endElement(String uri, String localName, String name) 
			throws SAXException {
			//Log.d(TAG,"uri - "+uri+"; endElement - "+localName + "; name = "+name);
			if(uri.equals(XMLTags.ENTRY_NAMESPACE)
					&& XMLTags.ENTRY.equals(localName)) {
				//Log.d(TAG,"mPhotoName = "+ mPhotoName+"; mUrl = "+mUrl);
				
				mAlbumMap.put(mPhotoName, new PhotoDetails(mPhotoName,mUrl,mFileSize));
			} else if(uri.equals(XMLTags.MEDIA_NAMESPACE)
					&& XMLTags.PHOTO_TITLE.equals(localName)) {
				mPhotoName = mTempName;
				Log.d(TAG,"GOTTTTTTTTTTTT mPhotoName = "+mPhotoName+"; mTempName = "+mTempName);
			} else if(uri.equals(XMLTags.GPHOTO_NAMESPACE)
					&& XMLTags.PHOTO_SIZE.equals(localName)) {
				mFileSize = Integer.parseInt(mTempName);
			}
		}
		
        public void characters(char[] ch, int start, int length)
                       throws SAXException {
                mTempName = new String(ch, start, length);
               //Log.d(TAG,"endElement");
        }
        
        public HashMap<String,PhotoDetails> getPhotoMap() {
        	return mAlbumMap;
        }
	}
}
