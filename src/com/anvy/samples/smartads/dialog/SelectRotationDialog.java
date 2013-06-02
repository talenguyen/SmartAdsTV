package com.anvy.samples.smartads.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.anvy.samples.smartads.R;
import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.config.Settings.Orientation;
import com.anvy.samples.smartads.view.RotatedFrameLayout.Rotation;

public class SelectRotationDialog extends DialogFragment implements
		OnClickListener {

	public interface SelectRotationDialogListener {
		void onDismissListener();
	}

	private SelectRotationDialogListener listener = null;

	public SelectRotationDialog() {
	}

	public void setSelectRotationDialogListener(
			SelectRotationDialogListener listener) {
		this.listener = listener;
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

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (listener != null) {
			listener.onDismissListener();
		}
	}
	
}