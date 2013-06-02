package com.anvy.samples.smartads;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.config.Settings.Orientation;
import com.anvy.samples.smartads.data.AdsItem;
import com.anvy.samples.smartads.image.ImageCache;
import com.anvy.samples.smartads.image.ImageWorker;
import com.anvy.samples.smartads.util.Util;

public class SettingsActivity extends BaseActivity implements
		OnItemClickListener {

	private AdsItemAdapter mAdapter;
	private ListView mListView;
	private int mActivePosition;
	private String KEY_ACTIVE_POSITION = "key:activePosition";
	private ImageWorker mImageWorker;
	private ImageView mImageView;
	private int mImgPreWidth;
	private int mImgPreHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivePosition = getPreferences(MODE_PRIVATE).getInt(
				KEY_ACTIVE_POSITION, 0);
		final List<AdsItem> items = Util.getAdsItems(Settings.sContentDir);

		mAdapter = new AdsItemAdapter(getLayoutInflater(), items);
		mListView = (ListView) findViewById(R.id.listAds);
		mListView.setOnItemClickListener(this);
		mImageView = (ImageView) findViewById(R.id.imgPreview);
		findViewById(R.id.apply).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AdsItem item = (AdsItem) mAdapter
						.getItem(mActivePosition);
				startDisplayActivity(item.getHtmlPath());
			}
		});

		if (Settings.isFirstEnter) {
			final AdsItem item = (AdsItem) mAdapter.getItem(mActivePosition);
			startDisplayActivity(item.getHtmlPath());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class

		mImgPreWidth = Settings.sScreenHeight - 100;
		mImgPreHeight = (int) (mImgPreWidth / Settings.SCREEN_RATIO);

		mImageWorker = new ImageWorker(this);
		mImageWorker.addImageCache(getFragmentManager(), cacheParams);
		mImageWorker.setLoadingImage(R.drawable.empty_photo);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mListView.setAdapter(mAdapter);
		setActivatedPosition(mActivePosition);
		final AdsItem item = (AdsItem) mAdapter.getItem(mActivePosition);
		displayPreview(item.getOrientation(), item.getImgPreviewPath());
		mImageWorker.setExitTasksEarly(false);
	}

	public ImageWorker getImageWorker() {
		return mImageWorker;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageWorker.setExitTasksEarly(true);
		getPreferences(MODE_PRIVATE).edit()
				.putInt(KEY_ACTIVE_POSITION, mActivePosition).commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mImageWorker.clearCache();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mActivePosition = position;
		final AdsItem item = (AdsItem) mAdapter.getItem(mActivePosition);
		displayPreview(item.getOrientation(), item.getImgPreviewPath());
	}

	private void displayPreview(int orientation, String imgPath) {
		if (orientation == Orientation.LANDSCAPE) {
			mImageView.getLayoutParams().width = mImgPreWidth;
			mImageView.getLayoutParams().height = mImgPreHeight;
			mImageWorker.loadImage(imgPath, mImageView, mImgPreWidth,
					mImgPreHeight);
		} else {
			mImageView.getLayoutParams().width = mImgPreHeight;
			mImageView.getLayoutParams().height = mImgPreWidth;
			mImageWorker.loadImage(imgPath, mImageView, mImgPreHeight,
					mImgPreWidth);
		}
	}

	private void startDisplayActivity(String htmlPath) {
		final Intent i = new Intent(this, DisplayActivity.class);
		i.putExtra(DisplayActivity.ARG_HTML_PATH, htmlPath);
		startActivity(i);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mListView.setItemChecked(position, false);
		} else {
			mListView.setSelection(position);
			mListView.setItemChecked(position, true);
		}
	}

	private static class AdsItemAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		private final List<AdsItem> mItems;

		public AdsItemAdapter(LayoutInflater inflater, List<AdsItem> items) {
			mInflater = inflater;
			mItems = items;
		}

		@Override
		public int getCount() {
			if (mItems != null) {
				return mItems.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if (getCount() > 0) {
				return mItems.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						android.R.layout.simple_list_item_single_choice,
						parent, false);
			}
			((TextView) convertView).setText(((AdsItem) getItem(position))
					.getName());
			return convertView;
		}

	}

	@Override
	protected View getContentView() {
		if (Settings.sOrientation == Orientation.LANDSCAPE) {
			return getLayoutInflater().inflate(R.layout.activity_setting_land, null);
		} else {
			return getLayoutInflater().inflate(R.layout.activity_setting_port, null);
		}
	}

}
