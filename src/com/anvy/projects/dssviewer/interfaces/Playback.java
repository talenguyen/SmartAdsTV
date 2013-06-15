package com.anvy.projects.dssviewer.interfaces;

import android.content.Context;
import android.view.View;

/**
 * 
 * @author giang.nguyen@anvydigital.com
 * 
 */
public interface Playback {

	/**
	 * Get the {@link View} control that contain the media content.
	 * 
	 * @param context
	 *            The context that view will be created.
	 * @return {@link View} object
	 */
	View getView(Context context);

	/**
	 * Do play media content.
	 */
	void play();

	/**
	 * Stop media content
	 */
	void stop();

	/**
	 * Get duration of playback
	 */
	long getDuration();

}
