package com.anvy.projects.dssviewer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.config.Settings.Orientation;
import com.anvy.projects.dssviewer.dialog.LoadingDialogFragment;
import com.anvy.projects.dssviewer.dialog.BaseDialogFragment.DialogDismissListener;
import com.anvy.projects.dssviewer.interfaces.RotationSettingCallback;
import com.anvy.projects.dssviewer.view.RotatedFrameLayout;
import com.anvy.projects.dssviewer.view.RotatedFrameLayout.Rotation;

public class BaseActivity extends FragmentActivity implements
		OnClickListener {

	private RotatedFrameLayout mContentView;
	private LoadingDialogFragment mLoadingDialog;
	private View mRotationSettings;
	private RotationSettingCallback mRotationSettingsCallback;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		getActionBar().hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_base);

		mLoadingDialog = new LoadingDialogFragment();

		initRotationSettings();

		mContentView = (RotatedFrameLayout) findViewById(R.id.contentView);
		setRotation(Settings.sRotation);
	}

	@Override
	public void setContentView(int layoutResID) {
		if (layoutResID == R.layout.activity_base) {
			super.setContentView(layoutResID);
		} else {
			super.setContentView(getLayoutInflater().inflate(layoutResID,
					mContentView, false));
		}
	}

	@Override
	public void setContentView(View view) {
		if (view != null) {
			mContentView.addView(view);
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (view != null) {
			mContentView.addView(view, params);
		}
	}

	protected void showLoadingDialog(DialogDismissListener listener) {
		mLoadingDialog.setOnDismissListener(listener);
		mLoadingDialog.show(getSupportFragmentManager(), "Loading...");
	}

	protected void dismissLoadingDialog() {
		mLoadingDialog.dismiss();
	}

	protected void setRotation(Rotation rotation) {
		mContentView.rotate(rotation);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void showRotationSettings(RotationSettingCallback callback) {
		mRotationSettingsCallback = callback;
		mRotationSettings.setVisibility(View.VISIBLE);
	}

	protected void hideRotationSettings() {
		mRotationSettings.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonTop0:
			Settings.sRotation = Rotation.Degrees_0;
			Settings.sOrientation = Orientation.LANDSCAPE;
			break;
		case R.id.buttonTop90:
			Settings.sRotation = Rotation.Degrees_90;
			Settings.sOrientation = Orientation.PORTRAIT;
			break;
		case R.id.buttonTop180:
			Settings.sRotation = Rotation.Degrees_180;
			Settings.sOrientation = Orientation.LANDSCAPE;
			break;
		case R.id.buttonTop270:
			Settings.sRotation = Rotation.Degrees_270;
			Settings.sOrientation = Orientation.PORTRAIT;
			break;
		}
		hideRotationSettings();
		if (mRotationSettingsCallback != null) {
			mRotationSettingsCallback.onSetting(Settings.sRotation);
		}
	}

	private void initRotationSettings() {
		mRotationSettings = findViewById(R.id.rotationSettings);
		mRotationSettings.findViewById(R.id.buttonTop0)
				.setOnClickListener(this);
		mRotationSettings.findViewById(R.id.buttonTop90).setOnClickListener(
				this);
		mRotationSettings.findViewById(R.id.buttonTop180).setOnClickListener(
				this);
		mRotationSettings.findViewById(R.id.buttonTop270).setOnClickListener(
				this);
	}

	@Override
	public void onBackPressed() {
		if (mRotationSettings.getVisibility() == View.VISIBLE) {
			hideRotationSettings();
		} else {
			super.onBackPressed();
		}
	}

}
