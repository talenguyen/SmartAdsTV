package com.anvy.projects.dssviewer.dialog;

import com.anvy.projects.dssviewer.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadingDialogFragment extends BaseDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getText(R.string.loading));
		return dialog;
	}
}
