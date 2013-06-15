package com.anvy.projects.dssviewer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.config.Settings.Orientation;
import com.anvy.projects.dssviewer.data.AdsItem;

public class Util {

	private Util() {
	}

	public static List<AdsItem> getAdsItems(File adsDir) {
		List<AdsItem> items = new ArrayList<AdsItem>();
		collectValidItem(items, adsDir);
		return items;
	}

	public static void collectValidItem(List<AdsItem> collector, File fileDir) {
		if (fileDir != null && fileDir.isDirectory()) {
			if (fileDir.getName().startsWith("land_")
					|| fileDir.getName().startsWith("port_")) {
				collector.add(getItemFromFile(fileDir));
			} else {
				for (File file : fileDir.listFiles()) {
					collectValidItem(collector, file);
				}
			}
		}
	}

	public static AdsItem getItemFromFile(File file) {
		final String name = file.getName();
		final String imgPreviewPath = file.getPath() + "/"
				+ Settings.IMG_PREVIEW_NAME;
		final String htmlPath = file.getPath() + "/" + Settings.HTML_NAME;
		int orientation = Orientation.LANDSCAPE;
		if (name.toLowerCase().startsWith("port_")) {
			orientation = Orientation.PORTRAIT;
		}
		return new AdsItem(orientation, name, imgPreviewPath, htmlPath);
	}

	public static void rotateLayout(ViewGroup layout, int animId) {
		Animation rotateAnim = AnimationUtils.loadAnimation(
				layout.getContext(), animId);
		LayoutAnimationController animController = new LayoutAnimationController(
				rotateAnim, 0);
		layout.setLayoutAnimation(animController);
		layout.invalidate(0, 0, Settings.sScreenWidth, Settings.sScreenHeight);
		layout.requestLayout();
	}
}
