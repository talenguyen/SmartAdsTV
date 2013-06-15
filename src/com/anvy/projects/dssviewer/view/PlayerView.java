package com.anvy.projects.dssviewer.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.anvy.projects.dssviewer.interfaces.Playback;

public class PlayerView extends RotatedFrameLayout {

	private int width, height;
	private List<Playback> playbacks;
	private int index = -1;

	private LayoutParams layoutParam = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	private Runnable playbackRunnable = new Runnable() {

		@Override
		public void run() {
			if (index >= 0 && index < playbacks.size()) {
				stop(playbacks.get(index));
				index++;
				final Playback playback = playbacks.get(index);
				play(playback);
				postDelayed(playbackRunnable, playback.getDuration());
			}
		}
	};

	public PlayerView(Context context) {
		super(context);
	}

	public PlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	/**
	 * Set position of this view in parent
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		setTranslationX(x);
		setTranslationY(y);
	}

	/**
	 * Set size of this view.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setPlayback(List<Playback> playbacks) {
		this.playbacks = playbacks;
	}

	public void play() {
		if (playbacks == null || playbacks.size() == 0) {
			return;
		}

		index = 0;
		final Playback playback = playbacks.get(index);
		play(playback);
		postDelayed(playbackRunnable, playback.getDuration());
	}

	public void stop() {
		stop(playbacks.get(index));
		removeCallbacks(playbackRunnable);
		index = -1;
	}

	private void play(Playback playback) {
		addView(playback.getView(getContext()), layoutParam);
		playback.play();
	}

	private void stop(Playback playback) {
		removeViewAt(0);
		playback.stop();
	}

}
