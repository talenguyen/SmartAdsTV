package com.anvy.samples.smartads;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.view.RotatedFrameLayout.Rotation;

public class DisplayActivity extends BaseActivity {
	public static final String ARG_HTML_PATH = "arg:htmlPath";
	private String mHtmlPath;
	private WebView mWebView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Settings.isFirstEnter = false;
		getActionBar().hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		mWebView.setOnTouchListener(new OnTouchListener() {

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

		/* Enable zooming */
		websettings.setSupportZoom(true);
		websettings.setBuiltInZoomControls(true);
		websettings.setUseWideViewPort(true);
		websettings.setLoadWithOverviewMode(true);

		mHtmlPath = getIntent().getStringExtra(ARG_HTML_PATH);
		loadWebPage("file://" + mHtmlPath);
	}

	public void loadWebPage(String targetURL) {
		if (targetURL == null) {
			return;
		}

		/*
		 * Fix URL if it doesn't begin with 'http' or 'file:'. WebView will not
		 * load URLs which do not specify protocol.
		 */
		if (targetURL.indexOf("http") != 0 && targetURL.indexOf("file:") != 0) {
			targetURL = "http://" + targetURL;
		}

		setTitle("Loading " + targetURL);

		mWebView.loadUrl(targetURL);
	}

	@Override
	protected View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_display, null);
	}

}