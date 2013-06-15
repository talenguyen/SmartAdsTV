package com.anvy.projects.dssviewer.async;

import android.os.AsyncTask;

import com.anvy.projects.dssviewer.util.Logger;

public abstract class RequestAsyncTask<T> extends AsyncTask<Void, Void, T> {

	private static final String TAG = RequestAsyncTask.class.getSimpleName();
	
	public interface RequestCallback<T> {
		void onPreExecute();

		void onPostExecute(T results);
	}

	public RequestCallback<T> mCallback = null;

	public RequestAsyncTask(RequestCallback<T> callback) {
		mCallback = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mCallback != null) {
			mCallback.onPreExecute();
		}
	}

	@Override
	protected void onPostExecute(T result) {
		super.onPostExecute(result);
		if (isCancelled()) {
			Logger.log(TAG, "canceled!");
			return;
		}

		if (mCallback != null) {
			mCallback.onPostExecute(result);
			Logger.log(TAG, "callback is not null!");
		} else {
			Logger.log(TAG, "callback is null!");
		}

	}

}
