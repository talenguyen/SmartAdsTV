package com.anvy.samples.smartads.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.data.AdsItem;

public class Util {

	private Util() {
	}

	public static List<AdsItem> getAdsItems(File adsDir) {
		List<AdsItem> items = new ArrayList<AdsItem>();
		for (File file : adsDir.listFiles()) {
			final String name = file.getName();
			final String imgPreviewPath = file.getPath() + "/"
					+ Settings.IMG_PREVIEW_NAME;
			final String htmlPath = file.getPath() + "/" + Settings.HTML_NAME;
			if (name.toLowerCase().startsWith("land_")) {
				items.add(new AdsItem(Settings.Orientation.LANDSCAPE, name,
						imgPreviewPath, htmlPath));
			} else if (name.toLowerCase().startsWith("port_")) {
				items.add(new AdsItem(Settings.Orientation.PORTRAIT, name,
						imgPreviewPath, htmlPath));
			}
		}
		return items;
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
