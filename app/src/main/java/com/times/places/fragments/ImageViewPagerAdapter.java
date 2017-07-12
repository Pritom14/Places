package com.times.places.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<String> photos;
	private boolean isZoomView;

	public void setPhotos(ArrayList<String> photos,boolean isZoomView) {
		this.photos = photos;
		this.isZoomView = isZoomView;
		notifyDataSetChanged();
	}

	public ImageViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return ImageViewFragment.newInstance(photos.get(position),isZoomView);
	}

	@Override
	public int getCount() {
		if (photos != null) {
			return photos.size();
		}
		return 0;
	}
}
