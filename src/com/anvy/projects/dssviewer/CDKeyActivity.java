package com.anvy.projects.dssviewer;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.config.KeyKeeper;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a enter CD Key screen to the user.
 */
public class CDKeyActivity extends Activity {

	public static final String SAMPLE_CD_KEY = "12345-ABCDE-67890-FGHIJ";
	public static final int KEY_LENTH = 23;
	public static final int KEY_BLOCK = 5;

	private ActiveCDKeyTask mAuthTask = null;

	private String mKey;

	// UI references.
	private EditText mTextViewKey;
	private View mLoadingContainer;

	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cd_key);

		mLoginFormView = findViewById(R.id.enterKeyForm);
		mTextViewKey = (EditText) findViewById(R.id.key);
		mTextViewKey
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						KEY_LENTH) });
		mTextViewKey
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.active || id == EditorInfo.IME_NULL) {
							attemptActive();
							return true;
						}
						return false;
					}
				});
		mTextViewKey.addTextChangedListener(new TextWatcher() {

			int length = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				length = count;
			}

			@Override
			public void afterTextChanged(Editable s) {
				final int selectionEnd = mTextViewKey.getSelectionEnd();
				final boolean isDeleted = length > s.length();
				length = s.length();
				if (selectionEnd == length
						&& ((length / 5) - (length % 5) == 1)) {
					if (isDeleted) {
						mTextViewKey.setText(s.subSequence(0, length - 1));
						mTextViewKey.setSelection(selectionEnd - 1);
					} else if (s.length() < KEY_LENTH) {
						mTextViewKey.setText(s + "-");
						mTextViewKey.setSelection(selectionEnd + 1);
					}
				}
			}
		});

		mLoadingContainer = findViewById(R.id.loadingContainer);

		findViewById(R.id.activeButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptActive();
					}
				});
	}

	protected void attemptActive() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mTextViewKey.setError(null);

		// Store values at the time of the login attempt.
		mKey = mTextViewKey.getText().toString();

		// Check for a valid key entered
		if (TextUtils.isEmpty(mKey)) {
			mTextViewKey.setError(getString(R.string.error_field_required));
			mTextViewKey.requestFocus();
			return;
		} else if (mKey.length() < KEY_LENTH) {
			mTextViewKey.setError(String.format(
					getString(R.string.error_field_length), KEY_LENTH));
			mTextViewKey.requestFocus();
			return;
		}

		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		showProgress(true);
		mAuthTask = new ActiveCDKeyTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoadingContainer.setVisibility(View.VISIBLE);
			mLoadingContainer.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoadingContainer.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mTextViewKey.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ActiveCDKeyTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt active against a network service.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (mKey.equals(SAMPLE_CD_KEY)) {
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				SharedPreferenceUtil.putString(KeyKeeper.KEY_TOKEN, mKey);
				startActivity(new Intent(CDKeyActivity.this,
						LoadingActivity.class));
				finish();
			} else {
				mTextViewKey
						.setError(getString(R.string.error_incorrect_cd_key));
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
