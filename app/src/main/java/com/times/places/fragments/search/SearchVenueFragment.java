package com.times.places.fragments.search;

import java.util.List;
import java.util.UUID;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.network.VolleyQueueWrapper;
import com.network.requests.GsonRequest;
import com.times.places.MainActivity;
import com.times.places.PlaceDetailActivity;
import com.times.places.R;
import com.times.places.adapters.BaseAdapter.BaseViewHolder;
import com.times.places.fragments.AbstractRecyclerViewFragment;
import com.times.places.response.GooglePlaceResponse;
import com.times.places.response.Place;

/**
 * Created by sumon.chatterjee on 2/7/16.
 */
public class SearchVenueFragment extends AbstractRecyclerViewFragment
		implements
			Listener<GooglePlaceResponse>,
			ErrorListener,
			OnQueryTextListener {

	public static final String SEARCH_QUERY = "SEARCH_QUERY";

	private String mQueryString;
	private String mPageToken;
	private SearchAdapter searchAdapter;

	private Loader mLoader = new Loader();

	double lnt = 28.6451518;
	double lng = 76.8098805;

	private TextView mInfoTextView;

	public static SearchVenueFragment newInstance(String query) {
		Bundle args = new Bundle();
		args.putString(SEARCH_QUERY, query);
		SearchVenueFragment fragment = new SearchVenueFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searchAdapter = new SearchAdapter();
		setAdapter(searchAdapter);

		if (getArguments() != null) {
			mQueryString = getArguments().getString(SEARCH_QUERY);
		}
		if (savedInstanceState != null) {
			mQueryString = savedInstanceState.getString(SEARCH_QUERY,
					mQueryString);
		}
		setPaginationEnable(!TextUtils.isEmpty(mQueryString));
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.search_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mInfoTextView = (TextView) view.findViewById(R.id.infoTextView);
		if (TextUtils.isEmpty(mQueryString)) {
			mInfoTextView.setVisibility(View.VISIBLE);
			mInfoTextView.setText(R.string.please_search_places);
			hideLoader();
		} else {
			mInfoTextView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putString(SEARCH_QUERY, mQueryString);
		}
	}

	@Override
	public LayoutManager createLayoutManager() {
		return new LinearLayoutManager(getActivity());
	}

	@Override
	protected void loadMore() {
		super.loadMore();
		if (searchAdapter.getItemCount() == 0) {
			showLoader();
		} else {
			searchAdapter.addItem(mLoader);
		}

		if (getActivity() instanceof MainActivity) {
			Location location = ((MainActivity) getActivity())
					.getLastKnowLocation();
			if (location != null) {
				lnt = location.getLatitude();
				lng = location.getLongitude();
			}
		}

		String url = VolleyQueueWrapper.buildSearchUrl(mQueryString, lnt, lng,
				mPageToken);

		Log.e("url", url);

		GsonRequest<GooglePlaceResponse> gsonRequest = new GsonRequest<>(url,
				GooglePlaceResponse.class, null, this, this);
		getVolleyQueueWrapper().addRequest(gsonRequest);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_item, menu);
		final SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setQuery(mQueryString, false);
		searchView.setQueryHint("Search places");
		MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search),
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						searchView.post(new Runnable() {

							@Override
							public void run() {
								searchView.setQuery(mQueryString, false);
							}
						});
						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						return true;
					}
				});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mQueryString = query;
		if (!TextUtils.isEmpty(mQueryString)) {
			mPageToken = null;
			getVolleyQueueWrapper().cancel();
			setPaginationEnable(false);
			searchAdapter.clear();
			loadMore();
		}
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		setPaginationEnable(false);
		hideLoader();
	}

	@Override
	public void onResponse(GooglePlaceResponse response) {
		hideLoader();

		searchAdapter.removeItem(mLoader);
		boolean hasPlaces = false;
		if (!TextUtils.isEmpty(response.getStatus())
				&& response.getStatus().equalsIgnoreCase("OK")) {
			mInfoTextView.setVisibility(View.INVISIBLE);
			List<Place> places = response.getResults();
			if (places != null && places.size() > 0) {
				hasPlaces = true;
				searchAdapter.addAll(places);
			}
			mPageToken = response.getNextPageToken();
			setPaginationEnable(!TextUtils.isEmpty(mPageToken));
		} else if (!TextUtils.isEmpty(response.getErrorMessage())) {
			Toast.makeText(getContext(), response.getErrorMessage(),
					Toast.LENGTH_LONG).show();
		}

		if (!hasPlaces) {
			mPageToken = null;
			setPaginationEnable(false);
			if (searchAdapter.getItemCount() == 0) {
				mInfoTextView.setVisibility(View.VISIBLE);
				mInfoTextView.setText(R.string.no_result_found);
			} else {
				mInfoTextView.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onRecyclerViewClick(BaseViewHolder vh, int position) {
		super.onRecyclerViewClick(vh, position);
		Place place = searchAdapter.getItem(position);
		if (!TextUtils.isEmpty(place.getPlaceId())) {
			Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
			intent.putExtra(PlaceDetailActivity.PLACE_ID, place.getPlaceId());
			startActivity(intent);
		}
	}

	public static class Loader extends Place {

		public static final int VIEW_TYPE = 100;

		public Loader() {
			setId(UUID.randomUUID().toString());
		}

		@Override
		public int getViewType() {
			return VIEW_TYPE;
		}
	}
}
