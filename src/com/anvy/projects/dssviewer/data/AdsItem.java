package com.anvy.projects.dssviewer.data;

public class AdsItem {

	private final int orientation;
	private final String name;
	private final String imgPreviewPath;
	private final String htmlPath;

	public String getName() {
		return name;
	}

	public String getImgPreviewPath() {
		return imgPreviewPath;
	}

	public int getOrientation() {
		return orientation;
	}
	
	public String getHtmlPath() {
		return htmlPath;
	}
	
	public AdsItem(int orientation, String name, String imgPreviewPath, String htmlPath) {
		this.orientation = orientation;
		this.name = name;
		this.imgPreviewPath = imgPreviewPath;
		this.htmlPath = htmlPath;
	}

}
