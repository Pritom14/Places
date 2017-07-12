package com.times.places.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.network.ImageRequestManager;
import com.network.VolleyQueueWrapper;
import com.network.utils.ApiEndPoints;
import com.times.places.R;


/**
 * Created by sumon.chatterjee on 1/16/16.
 * Using dialog fragment because Dialog fragment can be embedded as normal fragment
 */
public abstract class BaseFragment extends DialogFragment {

	private View progressBarLoader;
	private VolleyQueueWrapper volleyQueueWrapper;
	private ImageRequestManager imageRequestManager;

	public VolleyQueueWrapper getVolleyQueueWrapper() {
		return volleyQueueWrapper;
	}

	public ImageLoader getImageLoader() {
		return imageRequestManager.getImageLoader();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		volleyQueueWrapper = VolleyQueueWrapper.newInstance(getActivity(), ApiEndPoints.BASE_URL);
		imageRequestManager = ImageRequestManager.getInstance(getActivity());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.progressBarLoader = view.findViewById(R.id.progressBarLoader);
	}

	public void showLoader() {
		if (progressBarLoader != null) {
			progressBarLoader.setVisibility(View.VISIBLE);
		}
	}

	public void hideLoader() {
		if (progressBarLoader != null) {
			progressBarLoader.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onDestroyView() {
		volleyQueueWrapper.cancel();
		super.onDestroyView();
	}
	/*******/
}
