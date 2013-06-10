package com.Ecology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuizQuestions extends MainMenu implements 
RadioGroup.OnCheckedChangeListener, OnClickListener {
	int mNumQuestionToDisplay = 1;
	int mNumQuestions = 0;
	boolean mIsChoiceSelected = false;
	String mChoiceSelected;
	ArrayList<String> mChoices;
	String mTopic;
	volatile int mScore = 0;
	int mChoicesDisplayed = 0;
	
	private final static String TAG = "MyActivity"; 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quizquestions);
		mTopic = getIntent().getExtras().getString("Topic");	
		TextView txTopic = (TextView)findViewById(R.id.TextView_Topic);
		txTopic.setText(mTopic);
		
		displayQuestion();
	}

	private void displayQuestion() {
		HashMap<String,ArrayList<String>> questionsMap = Quiz.getQuizMap().get(mTopic);
		Set<String> keys = questionsMap.keySet();
		Iterator<String> iter = keys.iterator();
		int count = 1;
		mNumQuestions = keys.size();

		String question = "";
		while(iter.hasNext()){
			if(count == mNumQuestionToDisplay){
				question = (String) iter.next();
				break;
			}
			count++;
			iter.next();
		}
		TextView txQuestion = (TextView)findViewById(R.id.TextView_Question);
		txQuestion.setText(mNumQuestionToDisplay+". "+question);
		
		TextView txScore = (TextView)findViewById(R.id.TextView_Score);
		updateScore();

		mChoices = questionsMap.get(question);
		RadioGroup radioChoices = (RadioGroup)findViewById(R.id.RadioGroup_Choices);
		radioChoices.setOnCheckedChangeListener(this);
		radioChoices.removeAllViews();
		int id = 1;
		//for(int index = 0; index < mChoices.size()-1; index++) {
		for(int index = mChoices.size()-2; index >=0; index--) {
			if(!mChoices.get(index).equals("null")){
				RadioButton newRadioButton = new RadioButton(this);
		        newRadioButton.setText(mChoices.get(index));
		        newRadioButton.setTextColor(Color.rgb(0, 33, 165));
		        newRadioButton.setMaxWidth(400);
		        newRadioButton.setInputType(0x00020001);
		        newRadioButton.setId(id++);
		        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
		                RadioGroup.LayoutParams.WRAP_CONTENT,
		                RadioGroup.LayoutParams.WRAP_CONTENT);
		        radioChoices.addView(newRadioButton, 0, layoutParams);
		        ++mChoicesDisplayed;
			}
		}
	}

	//public void onPause() {
	//	super.onPause();
	//	finish();
	//}
	@Override
	public void onClick(View v) {
		RadioGroup rg = (RadioGroup)findViewById(R.id.RadioGroup_Choices);
		rg.clearCheck();		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG,"checkedId = "+checkedId);
		boolean correct = false;
		RadioButton rb = (RadioButton)group.findViewById(checkedId);
		if(rb.isPressed() || rb.isChecked()) {
			String answer = mChoices.get(mChoices.size()-1);
			Log.d(TAG,"checkedId = "+(mChoicesDisplayed-checkedId+1)+"; answer = "+Integer.parseInt(answer));
			if((mChoicesDisplayed-checkedId+1) == Integer.parseInt(answer)){
				//mScore++;
				correct = true;
			}
		}
		QuizScore.getQuizScore().updateScore(correct);
		mNumQuestionToDisplay++;
		mChoicesDisplayed = 0;
		if(mNumQuestionToDisplay <= mNumQuestions){
			displayQuestion();
			//finish();
		}
		else {
			//pop up to display final score
			//displayFinalScore();
			QuizQuestions.this.finish();		
		}
	}
	
	private void displayFinalScore() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//builder.setMessage("Final Score = "+mScore +"/"+mNumQuestions)
		builder.setMessage("Final Score = "+QuizScore.getQuizScore().getScore())
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                QuizQuestions.this.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateScore() {
		TextView txScore = (TextView)findViewById(R.id.TextView_Score);
		//txScore.setText("Your Score = "+mScore +"/"+mNumQuestions);
		txScore.setText("Your Score = "+QuizScore.getQuizScore().getScore());
	}
}
