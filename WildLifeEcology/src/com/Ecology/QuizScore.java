package com.Ecology;

public class QuizScore {
	static QuizScore mQs = null;
	int mAttendedQuestions;
	int mCorrect;
	
	public QuizScore() {
		mAttendedQuestions = 0;
		mCorrect = 0;
	}
	
	public static QuizScore getQuizScore() {
		if(mQs == null) {
			mQs = new QuizScore();
		}
		return mQs;
	}
	
	public void updateScore(boolean correct) {
		if(correct) mCorrect++;
		mAttendedQuestions++;
	}
	
	public String getScore() {
		return mCorrect + "/" + mAttendedQuestions;
	}
	
	public void resetScore() {
		mCorrect = 0;
		mAttendedQuestions = 0;
	}
}
