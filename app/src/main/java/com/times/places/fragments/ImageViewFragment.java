package com.times.places.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.times.places.PlaceDetailActivity;
import com.times.places.R;
import com.times.places.widget.ZoomImageView;

/**
 * Created by sumon.chatterjee on 2/8/16.
 */
public class ImageViewFragment extends BaseFragment implements OnClickListener {

	public static final String IMAGE_URL = "image_url";
	public static final String IS_ZOOMVIEW = "IS_ZOOMVIEW";

	private NetworkImageView networkImageView;

	private String mImageUrl = "";
	private boolean isZoomView;

	public static ImageViewFragment newInstance(String url, boolean isZoomView) {
		Bundle args = new Bundle();
		args.putString(IMAGE_URL, url);
		args.putBoolean(IS_ZOOMVIEW, isZoomView);
		ImageViewFragment fragment = new ImageViewFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments().getString(IMAGE_URL);
		isZoomView = getArguments().getBoolean(IS_ZOOMVIEW);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (isZoomView) {
			return inflater.inflate(R.layout.zoom_image_layout, container,
					false);
		}
		return inflater.inflate(R.layout.image_view_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		networkImageView = (NetworkImageView) view.findViewById(R.id.imageView);
		if (!isZoomView) {
			networkImageView.setImageUrl(mImageUrl, getImageLoader());
			networkImageView.setOnClickListener(this);
		} else {
			((ZoomImageView) networkImageView).setNetworkImage(
					R.drawable.ic_action_place, mImageUrl, getImageLoader());
		}
	}

	@Override
	public void onClick(View v) {
		if (getActivity() instanceof PlaceDetailActivity && !isZoomView) {
			((PlaceDetailActivity) getActivity()).onImageClick(mImageUrl);
		}
	}
}
