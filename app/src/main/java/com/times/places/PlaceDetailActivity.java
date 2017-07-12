package com.times.places;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.network.VolleyQueueWrapper;
import com.network.requests.GsonRequest;
import com.times.places.fragments.ImageViewFragment;
import com.times.places.fragments.ImageViewPagerAdapter;
import com.times.places.fragments.ZoomImageFragmentDialog;
import com.times.places.response.GooglePlaceResponse;
import com.times.places.response.Photo;
import com.times.places.response.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sumon.chatterjee on 2/8/16.
 */
public class PlaceDetailActivity extends BaseActivity
		implements
			Listener<GooglePlaceResponse>,
			ErrorListener,
			OnPageChangeListener {

	private CollapsingToolbarLayout collapsingToolbar;
	private static int screenWidth = 320;

	public static final String PLACE_ID = "place_id";

	private RadioGroup radioGroup;

	private ArrayList<String> photosUrls;
	private String placeId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail_layout);
		setUpToolbar();

		photosUrls = new ArrayList<>();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle("Place");
		loadPlaceDetail();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadPlaceDetail() {
		showLoader();
		String placeId = getIntent().getStringExtra(PLACE_ID);
		String url = VolleyQueueWrapper.placeDetailUrl(placeId);
		GsonRequest<GooglePlaceResponse> gsonRequest = new GsonRequest<>(url,
				GooglePlaceResponse.class, null, this, this);
		getVolleyQueueWrapper().addRequest(gsonRequest);

		// for testing purpose only
		if (TextUtils.isEmpty(placeId)) {
			Gson gson = new Gson();
			try {
				onResponse(gson.fromJson(
						JSONHandler.parseResource(this, R.raw.detail_response),
						GooglePlaceResponse.class));
			} catch (Exception ex) {
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		hideLoader();
		Toast.makeText(this, "Error Occured!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResponse(GooglePlaceResponse response) {
		hideLoader();
		if (!TextUtils.isEmpty(response.getStatus())
				&& response.getStatus().equalsIgnoreCase("ok")
				&& response.getResult() != null) {

			bindViews(response.getResult());
		} else if (!TextUtils.isEmpty(response.getErrorMessage())) {
			Toast.makeText(getApplicationContext(), response.getErrorMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private void bindViews(final Place place) {
		collapsingToolbar.setTitle(place.getName());
		prepareViewPage(place);

		// /addreess
		TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
		addressTextView.setText(place.getFormattedAddress());

		TextView phoneTextView = (TextView) findViewById(R.id.phoneTextView);
		phoneTextView.setText(place.getFormattedPhoneNumber());
		phoneTextView.append("\n");
		phoneTextView.append("\n");
		phoneTextView.append(place.getInternationalPhoneNumber());
		Linkify.addLinks(phoneTextView, Linkify.PHONE_NUMBERS);

		TextView webSiteTextView = (TextView) findViewById(R.id.websiteTextView);
		webSiteTextView.setText(place.getWebsite());

		TextView openingHoursTextView = (TextView) findViewById(R.id.openingHoursTextView);

		if (place.getOpeningHours() != null
				&& place.getOpeningHours().getWeekdayText() != null
				&& place.getOpeningHours().getWeekdayText().size() > 0) {
			openingHoursTextView.setText("");
			for (String text : place.getOpeningHours().getWeekdayText()) {
				openingHoursTextView.append(" - " + text);
				openingHoursTextView.append("\n");
				openingHoursTextView.append("\n");
			}
		}

		if (place.getGeometry() != null
				&& place.getGeometry().getLocation() != null) {

			final double lat = place.getGeometry().getLocation().getLat();
			final double lng = place.getGeometry().getLocation().getLng();

			String url = "http://maps.google.com/maps/api/staticmap?center="
					+ lat + "," + lng + "&zoom=17&size=" + screenWidth + "+x+"
					+ screenWidth + "&sensor=true";
			NetworkImageView mapImageView = (NetworkImageView) findViewById(R.id.mapImageView);
			mapImageView.setImageUrl(url, getImageRequestManager()
					.getImageLoader());

			mapImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lat);
					Intent mapIntent = new Intent(Intent.ACTION_VIEW,
							gmmIntentUri);
					if (mapIntent.resolveActivity(getPackageManager()) != null) {
						startActivity(mapIntent);
					}
				}
			});
		}

		FloatingActionButton shortListBtn = (FloatingActionButton) findViewById(R.id.shortListFlotingbtn);
		shortListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Shortlisted",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void prepareViewPage(Place place) {
		photosUrls.clear();

		//String url = "https://lh3.googleusercontent.com/-84w_4jOIxCI/Tp3ESjuqxnI/AAAAAAAAAEo/s8XWJPZs7xY/w506-h327/That%2Blooks%2Blike%2Ba%2Bawesome%2Bplace.Great%2Bshot.png";
		List<Photo> photos = place.getPhotos();
		for (Photo photo : photos) {
			 photosUrls.add(VolleyQueueWrapper.imageUrl(
			 photo.getPhotoReference(), screenWidth));
		//	photosUrls.add(url);
		}

		if (photosUrls.size() > 0) {
			ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(
					getSupportFragmentManager());
			ViewPager viewPager = (ViewPager) findViewById(R.id.imageViewPager);
			viewPager.setAdapter(imageViewPagerAdapter);
			imageViewPagerAdapter.setPhotos(photosUrls, false);
			radioGroup = (RadioGroup) findViewById(R.id.pageIndicator);
			radioGroup.removeAllViews();
			for (int i = 0; i < photosUrls.size(); i++) {
				ViewGroup vg = (ViewGroup) View.inflate(this, R.layout.dot,
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
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		radioGroup.check(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public void onImageClick(String url) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		ZoomImageFragmentDialog newFragment = ZoomImageFragmentDialog
				.newInstance(photosUrls.indexOf(url), photosUrls);
		newFragment.show(fragmentManager, "dialog");
	}

}
