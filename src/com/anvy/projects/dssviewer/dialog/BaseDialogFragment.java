package com.anvy.projects.dssviewer.dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {
	
	public interface DialogDismissListener {
		void onDismiss(DialogInterface dialog);
	}

	private DialogDismissListener listener = null;

	public void setOnDismissListener(DialogDismissListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (listener != null) {
			listener.onDismiss(dialog);
		}
	}
	
}
