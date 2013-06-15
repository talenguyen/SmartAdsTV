package com.anvy.projects.dssviewer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.config.Settings.Orientation;
import com.anvy.projects.dssviewer.view.RotatedFrameLayout.Rotation;

public class SelectRotationDialog extends BaseDialogFragment implements
		OnClickListener {

	public SelectRotationDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_select_rotation, null);
		view.findViewById(R.id.buttonTop0).setOnClickListener(this);
		view.findViewById(R.id.buttonTop90).setOnClickListener(this);
		view.findViewById(R.id.buttonTop180).setOnClickListener(this);
		view.findViewById(R.id.buttonTop270).setOnClickListener(this);

		Dialog dialog = new Dialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(view);
		return dialog;
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
		dismiss();
	}
	
}