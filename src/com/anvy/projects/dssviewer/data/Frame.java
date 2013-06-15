package com.anvy.projects.dssviewer.data;

/**
 * @author giang.nguyen@anvydigital.com
 */
public class Frame {
	
	/**
	 * Width of this {@link Frame}
	 */
	private final int width;
	
	/**
	 * Height of this {@link Frame}
	 */
	private final int height;

	public Frame(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
