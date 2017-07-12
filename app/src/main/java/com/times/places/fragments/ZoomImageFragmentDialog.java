package com.times.places.fragments;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.times.places.R;
import com.times.places.widget.ZoomImageView;

/**
 * Created by sumon.chatterjee on 2/8/16.
 */
public class ZoomImageFragmentDialog extends BaseFragment implements OnPageChangeListener {

	private static final String SELECTED_POSITION = "";
	private static final String ARRAY_URLS = "ARRAY_URLS";

	private int mSelectedPosition;
	private ArrayList<String> arrayUrls;

	private RadioGroup radioGroup;

	public static ZoomImageFragmentDialog newInstance(int selectedPosition, ArrayList<String> arrayList) {
		Bundle args = new Bundle();
		args.putInt(SELECTED_POSITION, selectedPosition);
		args.putStringArrayList(ARRAY_URLS, arrayList);
		ZoomImageFragmentDialog fragment = new ZoomImageFragmentDialog();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.zoom_dialog);
		arrayUrls = getArguments().getStringArrayList(ARRAY_URLS);
		mSelectedPosition = getArguments().getInt(SELECTED_POSITION);
		if (savedInstanceState != null) {
			mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION, mSelectedPosition);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putInt(SELECTED_POSITION, mSelectedPosition);
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		return dialog;
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.zoom_image_dialog, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.crossIcon).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		prepareViewPage(view);
	}

	private void prepareViewPage(View view) {
		if (arrayUrls.size() > 0) {
			ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(getChildFragmentManager());
			ViewPager viewPager = (ViewPager)view. findViewById(R.id.imageViewPager);
			viewPager.setAdapter(imageViewPagerAdapter);
			imageViewPagerAdapter.setPhotos(arrayUrls,true);
			radioGroup = (RadioGroup) view.findViewById(R.id.pageIndicator);
			radioGroup.removeAllViews();
			for (int i = 0; i < arrayUrls.size(); i++) {
				ViewGroup vg = (ViewGroup) View.inflate(getActivity(), R.layout.dot,
						null);
				RadioButton rb = (RadioButton) vg.getChildAt(0);
				rb.setId(i);
				rb.setClickable(false);
				radioGroup.addView(vg);
				rb.setChecked(0 == i);
			}
			radioGroup.check(0);
			viewPager.removeOnPageChangeListener(this);
			viewPager.addOnPageChangeListener(this);
			viewPager.setCurrentItem(mSelectedPosition,true);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		radioGroup.check(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}


	public class ZoomImagePagerAdatper extends PagerAdapter {

		public ZoomImagePagerAdatper() {
			super();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((FrameLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View itemView = LayoutInflater.from(container.getContext()).inflate(
					R.layout.zoom_image_layout, container, false);
			ZoomImageView imageView = (ZoomImageView) itemView
					.findViewById(R.id.imageView);
			imageView.setImageUrl(arrayUrls.get(position),getImageLoader());
			imageView.setNetworkImage(R.drawable.ic_action_place,
					arrayUrls.get(position), getImageLoader());
			container.addView(itemView);
			return itemView;
		}

		@Override
		public int getCount() {
			return arrayUrls.size();
		}

		@Override
		final public void destroyItem(ViewGroup container, int position,
									  Object object) {
			View view = (View) object;

			container.removeView(view);
			// super.destroyItem(container, position, object);
		}
	}
}

