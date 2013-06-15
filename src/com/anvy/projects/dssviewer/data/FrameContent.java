package com.anvy.projects.dssviewer.data;

public class FrameContent extends Frame {

	/**
	 * The x position of this {@link FrameContent} inside the {@link Template}.
	 */
	private final int x;

	/**
	 * The y position of this {@link FrameContent} inside the {@link Template}.
	 */
	private final int y;

	/**
	 * The id of the {@link Playlist} content will be play
	 */
	private final String playlistId;

	public FrameContent(int x, int y, int width, int height, String playlistId) {
		super(width, height);
		this.x = x;
		this.y = y;
		this.playlistId = playlistId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getPlaylistId() {
		return playlistId;
	}

}
