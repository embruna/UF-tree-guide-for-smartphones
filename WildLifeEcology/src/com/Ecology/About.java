package com.Ecology;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends MainMenu {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

	    TextView tvCopyInfo = (TextView) findViewById(R.id.TextView_copyInfo);
	    tvCopyInfo.setMovementMethod(LinkMovementMethod.getInstance());


	}
    
}


