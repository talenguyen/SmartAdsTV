package com.anvy.projects.dssviewer.data;

import java.util.List;

import com.anvy.projects.dssviewer.interfaces.Playback;

/**
 * 
 * @author giang.nguyen@anvydigital.com
 * 
 */
public class Playlist {

	/**
	 * The id of this {@link Playlist}
	 */
	private final String id;
	/**
	 * The name of this {@link Playlist}
	 */
	private final String name;
	/**
	 * List of {@link Clip} that will be play by this {@link Playlist}
	 */
	private final List<Playback> playbacks;

	public Playlist(String id, String name, List<Playback> playbacks) {
		this.id = id;
		this.name = name;
		this.playbacks = playbacks;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Playback> getPlaybacks() {
		return playbacks;
	}
}
