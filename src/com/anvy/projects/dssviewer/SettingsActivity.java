package com.anvy.projects.dssviewer;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.config.Settings.Orientation;
import com.anvy.projects.dssviewer.data.AdsItem;
import com.anvy.projects.dssviewer.image.ImageCache;
import com.anvy.projects.dssviewer.image.ImageWorker;
import com.anvy.projects.dssviewer.interfaces.RotationSettingCallback;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;
import com.anvy.projects.dssviewer.util.Util;
import com.anvy.projects.dssviewer.view.RotatedFrameLayout.Rotation;

public class SettingsActivity extends BaseActivity implements
		OnItemClickListener {

	private static final String KEY_AUTO_PLAY = "key:autoPlay";
	private String KEY_ACTIVE_POSITION = "key:activePosition";

	private AdsItemAdapter mAdapter;
	private ListView mListView;
	private int mActivePosition;
	private boolean isAutoPlay;
	private ImageWorker mImageWorker;
	private ImageView mImageView;
	private int mImgPreWidth;
	private int mImgPreHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting_land);
		
		mActivePosition = SharedPreferenceUtil.getInt(KEY_ACTIVE_POSITION);
		isAutoPlay = SharedPreferenceUtil.getBoolean(KEY_AUTO_PLAY);

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
				startDisplayActivity(item.getOrientation(), item.getHtmlPath());
			}
		});

		if (Settings.isFirstEnter && isAutoPlay) {
			final AdsItem item = (AdsItem) mAdapter.getItem(mActivePosition);
			startDisplayActivity(item.getOrientation(), item.getHtmlPath());
		}

		// Event when click select storage button
		findViewById(R.id.storageSelect).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingsActivity.this,
								GoogleDriveFileBrowserActivity.class));
					}
				});

		findViewById(R.id.buttonRotationSettings).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showRotationSettings(new RotationSettingCallback() {

							@Override
							public void onSetting(Rotation rotation) {
								setRotation(rotation);
							}
						});
					}
				});
		CheckBox checkBox = (CheckBox) findViewById(R.id.checkboxAutoplay);
		checkBox.setChecked(isAutoPlay);

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isAutoPlay = isChecked;
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class

		mImgPreWidth = Settings.sScreenHeight * 2 / 3;
		mImgPreHeight = (int) (mImgPreWidth / Settings.SCREEN_RATIO);

		mImageWorker = new ImageWorker(this);
		mImageWorker.addImageCache(getFragmentManager(), cacheParams);
		mImageWorker.setLoadingImage(R.drawable.empty_photo);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mImageWorker.setExitTasksEarly(false);
		mListView.setAdapter(mAdapter);
		setActivatedPosition(mActivePosition);
		final AdsItem item = (AdsItem) mAdapter.getItem(mActivePosition);
		displayPreview(item.getOrientation(), item.getImgPreviewPath());
	}

	public ImageWorker getImageWorker() {
		return mImageWorker;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageWorker.setExitTasksEarly(true);
		SharedPreferenceUtil.putInt(KEY_ACTIVE_POSITION, mActivePosition);
		SharedPreferenceUtil.putBoolean(KEY_AUTO_PLAY, isAutoPlay);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mImageWorker.clearCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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

	private void startDisplayActivity(int orientation, String htmlPath) {
		final Intent i = new Intent(this, DisplayActivity.class);
		i.putExtra(DisplayActivity.ARG_HTML_PATH, htmlPath);
		i.putExtra(DisplayActivity.ARG_ORIENTATION, orientation);
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

}
