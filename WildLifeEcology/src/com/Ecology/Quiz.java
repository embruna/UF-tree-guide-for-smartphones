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
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class Quiz extends MainMenu implements 
RadioGroup.OnCheckedChangeListener, OnClickListener {
	private final static String QUIZ_FILENAME = "quiz.txt";
	private static HashMap<String,HashMap<String,ArrayList<String>>> mQuizMap 
					= new HashMap<String,HashMap<String,ArrayList<String>>>();
	private volatile boolean mQuizFileDownloaded = false;
	private final static String TAG = "MyActivity";
	private final String URL = "http://wecuf.zoka.cc/Quiz.php?name=ALL";
	String mTopicSelected = "";
	private static volatile int mScore = 0; 
	private static volatile boolean mQuestionAnswered = false;
	private static CyclicBarrier mBarrier = new CyclicBarrier(1);
	
	RadioGroup mRadioGroup;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		downloadQuizFile();
		initializeQuiz();
		displayTopics();
	}
	
	private void displayTopics() {
		Set<String> topicsSet = mQuizMap.keySet();
		Iterator<String> iter = topicsSet.iterator();
		mRadioGroup = (RadioGroup)findViewById(R.id.RadioGroup_Topics);
		//mRadioGroup.remove
		int id = 0;	
		while(iter.hasNext()) {
			String topic = iter.next();
			RadioButton newRadioButton = new RadioButton(this);
	        newRadioButton.setText(topic);
	        newRadioButton.setTextColor(Color.rgb(0, 33, 165));
	        newRadioButton.setId(id++);
	        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
	                RadioGroup.LayoutParams.WRAP_CONTENT,
	                RadioGroup.LayoutParams.WRAP_CONTENT);
	        mRadioGroup.addView(newRadioButton, 0, layoutParams);
		}
		mRadioGroup.setOnCheckedChangeListener(this);
		updateScore();
	}
	
	private boolean downloadQuizFile(){
		QuizClient quizClient = new QuizClient();
		File file = getApplicationContext().getFileStreamPath(QUIZ_FILENAME);
    	if(!file.exists()) {
    		quizClient.setFileName(file.getAbsolutePath());
    		quizClient.setUrl(URL);
    		quizClient.setActivity(this);
    		quizClient.start();
    		
            ProgressDialog progressDialog = ProgressDialog.show(Quiz.this, "Quiz", 
                    "Loading. Please wait...", true);
    		while(true) {
    			if(mQuizFileDownloaded) {
    				progressDialog.dismiss();
    				//mHandler.sendEmptyMessage(0);
    				Log.d(TAG,"DIALOG gonna be dismissed");
    				break;
    			}
    		}
    	}
    	else
    		mQuizFileDownloaded = true;
    	return true;
	}
	
	private void initializeQuiz() {
		try {
			FileInputStream fis = openFileInput(QUIZ_FILENAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				parseQuiz(sb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showAlertDialog("Quiz file not found!");
		}
	}
	
	private void parseQuiz(StringBuilder sb) {
		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject resultsObj = jsonObject.getJSONObject("results");
			JSONArray quizArray = resultsObj.getJSONArray("quiz");
			for(int count = 0; count < quizArray.length(); count++) {
				String question = quizArray.getJSONObject(count).getString("Question");
				String topic = quizArray.getJSONObject(count).getString("Topic");
				String choice1 = quizArray.getJSONObject(count).getString("Choice1");
				String choice2 = quizArray.getJSONObject(count).getString("Choice2");
				String choice3 = quizArray.getJSONObject(count).getString("Choice3");
				String choice4 = quizArray.getJSONObject(count).getString("Choice4");
				String choice5 = quizArray.getJSONObject(count).getString("Choice5");
				String answer = quizArray.getJSONObject(count).getString("Answer");
				String[] arr = answer.split("[a-zA-Z]");
				answer  = arr[arr.length - 1];
				ArrayList<String> choices = new ArrayList<String>();
				choices.add(choice1);
				choices.add(choice2);
				choices.add(choice3);
				choices.add(choice4);
				choices.add(choice5);
				choices.add(answer);
				HashMap<String,ArrayList<String>> topicMap = mQuizMap.get(topic);
				if(topicMap == null) {
					topicMap = new HashMap<String,ArrayList<String>>();					
				} 
				topicMap.put(question, choices);
				mQuizMap.put(topic, topicMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showAlertDialog(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setQuizFileDownloaded(boolean status) {
		mQuizFileDownloaded = status;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG,"OnCheckedChanged Quiz");
		RadioButton rb = (RadioButton)group.findViewById(checkedId);
		if(rb != null) {
			mTopicSelected = (String) rb.getText();
			Log.d(TAG,"Quiz topic = "+rb.getText());
			displayQuestions();
		}
	}

	private void displayQuestions() {
		HashMap<String,ArrayList<String>> questions = mQuizMap.get(mTopicSelected);
		Set<String> keys = questions.keySet();
		Iterator<String> iter = keys.iterator();
		Log.d(TAG,"Before iterating questions");
		Intent intent = new Intent(Quiz.this,QuizQuestions.class);
		Bundle bundle = new Bundle();
		bundle.putString("Topic", mTopicSelected);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		RadioGroup rg = (RadioGroup)findViewById(R.id.RadioGroup_Topics);
		rg.clearCheck();
	}
	
    public void onResume() {
    	super.onResume();
    	RadioGroup rg = (RadioGroup)findViewById(R.id.RadioGroup_Topics);
    	if(rg != null) {
    		rg.removeAllViews();
    		displayTopics();
    	} else
    		Log.d(TAG,"rg is null");
    }
    
    public static HashMap<String, HashMap<String, ArrayList<String>>> getQuizMap() {
    	return mQuizMap;
    }
    
   // public static void updateScore(){
   // 	mScore++;
   // }
    
    public static int getScore(){
    	return mScore;
    }
    
    public void onPause() {
    	super.onPause();
    }
    
    public static void questionAnswered(){
    	mQuestionAnswered = true;
    	//mBarrier.notifyAll();
    }
    
	private void updateScore() {
		TextView txScore = (TextView)findViewById(R.id.TextView_Score);
		//txScore.setText("Your Score = "+mScore +"/"+mNumQuestions);
		txScore.setText("Your Score = "+QuizScore.getQuizScore().getScore());
	}
}
