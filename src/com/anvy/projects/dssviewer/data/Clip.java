package com.anvy.projects.dssviewer.data;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.anvy.projects.dssviewer.interfaces.Playback;

/**
 * 
 * @author giang.nguyen@anvydigital.com
 * 
 */
public class Clip implements Playback {

	/**
	 * Id of Clip
	 */
	private final String id;

	/**
	 * MIME type of content. If the MIME type of content is kind of plain/text,
	 * the data would be text. If not the data should be link to resource.
	 */
	private final MimeType mimeType;

	/**
	 * Base on MIME type, the data will be text or a link to resource.
	 */
	private final String data;

	/**
	 * Duration of this {@link Template}. If <code>duration</code> is <b>-1</b>
	 * it mean <code>duration</code> is infinity.
	 */
	private final long duration;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param mimeType
	 * @param data
	 * @param duration
	 */
	public Clip(String id, MimeType mimeType, String data, long duration) {
		this.id = id;
		this.mimeType = mimeType;
		this.data = data;
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public String getData() {
		return data;
	}

	private View view = null;

	@Override
	public View getView(Context context) {
		switch (mimeType) {
		case video:
			view = new VideoView(context);
			break;

		case html:
			view = new WebView(context);
			break;

		case image:
			view = new ImageView(context);

		case text:
			view = new TextView(context);

		}
		return view;
	}

	@Override
	public void play() {
		if (view == null || data == null || data.length() == 0) {
			return;
		}

		switch (mimeType) {
		case video:
			VideoView videoview = (VideoView) view;
			videoview.setVideoURI(Uri.parse(data));
			videoview.start();
			break;

		case html:
			WebView webview = (WebView) view;
			webview.loadUrl(data);
			break;

		case image:
			// TODO: load image for imageView

		case text:
			((TextView) view).setText(data);

		}
	}

	@Override
	public void stop() {
		if (view instanceof VideoView) {
			((VideoView) view).stopPlayback();
		}
	}

	@Override
	public long getDuration() {
		return duration;
	}

}
