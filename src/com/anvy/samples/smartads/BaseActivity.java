package com.anvy.samples.smartads;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.view.RotatedFrameLayout;
import com.anvy.samples.smartads.view.RotatedFrameLayout.Rotation;

public abstract class BaseActivity extends FragmentActivity {

	private RotatedFrameLayout mLayout;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		getActionBar().hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_base);

		mLayout = (RotatedFrameLayout) findViewById(R.id.contentView);
		mLayout.addView(getContentView());
		mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		mLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
					}
				}, 1000);
				return false;
			}
		});
		
		setRotation(Settings.sRotation);
	}

	/**
	 * Implement this method instead of using setContentView(...) method to
	 * setting your view.
	 * 
	 * @return The {@link View} object.
	 */
	protected abstract View getContentView();

	protected void setRotation(Rotation rotation) {
		mLayout.rotate(rotation);
	}

}
