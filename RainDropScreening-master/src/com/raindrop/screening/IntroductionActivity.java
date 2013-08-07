package com.raindrop.screening;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.raindrop.customview.NavigationBar;

public class IntroductionActivity extends Activity {

	NavigationBar mynb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		init();
	}
	public void init(){
		mynb=(NavigationBar)findViewById(R.id.introductionNb);
		mynb.setBtnRightVisble(false);
		mynb.setTvTitle("ΩÈ…‹3");
		mynb.setBtnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				IntroductionActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_introduction, menu);
		return true;
	}

}
