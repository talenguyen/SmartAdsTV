package com.anvy.projects.dssviewer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.config.Settings;

public class DisplayActivity extends BaseActivity {
	public static final String ARG_HTML_PATH = "arg:htmlPath";
	public static final String ARG_ORIENTATION = "arg:orientation";
	private String mHtmlPath;
	private WebView mWebView;

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		final int orientation = getIntent().getExtras().getInt(ARG_ORIENTATION);
//		if (orientation == Orientation.LANDSCAPE) {
//			setRotation(Rotation.Degrees_0);
//		} else {
//			setRotation(Rotation.Degrees_270);
//		}

		Settings.isFirstEnter = false;
		getActionBar().hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_display);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		mWebView.setOnTouchListener(new OnTouchListener() {

			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mWebView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mWebView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
					}
				}, 1000);
				return false;
			}
		});

		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setScrollbarFadingEnabled(false);
		mWebView.setInitialScale(1);

		WebSettings websettings = mWebView.getSettings();
		websettings.setJavaScriptEnabled(true);
		websettings.setPluginState(PluginState.ON_DEMAND);

		/* Enable zooming */
		websettings.setSupportZoom(true);
		websettings.setBuiltInZoomControls(true);
		websettings.setUseWideViewPort(true);
		websettings.setLoadWithOverviewMode(true);

		mHtmlPath = getIntent().getStringExtra(ARG_HTML_PATH);
		mWebView.loadUrl("file://" + mHtmlPath);
	}

}