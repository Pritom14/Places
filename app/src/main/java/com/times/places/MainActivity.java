package com.times.places;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.times.places.fragments.search.SearchVenueFragment;

public class MainActivity extends BaseActivity
		implements
			ConnectionCallbacks,
			OnConnectionFailedListener {

	private static final int PERMISSION_REQUEST_LOCATION = 90;
	private View mLayout;
	private GoogleApiClient mGoogleApiClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLayout = findViewById(R.id.main_content);
		setUpToolbar();
		if (savedInstanceState == null) {
			addToBackStack(getSupportFragmentManager(),
					new SearchVenueFragment(), false);
		}

		showLocationPreview();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_LOCATION) {
			if (grantResults.length == 1
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Snackbar.make(mLayout,
						"Location permission was granted. Starting preview.",
						Snackbar.LENGTH_SHORT).show();
				listenerForLocation();
			} else {
				Snackbar.make(mLayout,
						"Location permission request was denied.",
						Snackbar.LENGTH_SHORT).show();
			}
		}
	}

	private void showLocationPreview() {

		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			Snackbar.make(mLayout,
					"Location permission is available. Starting preview.",
					Snackbar.LENGTH_SHORT).show();
			listenerForLocation();
		} else {
			requestLocationPermission();
		}
	}

	private void requestLocationPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				permission.ACCESS_FINE_LOCATION)) {
			Snackbar.make(mLayout,
					"Location access is required to search places nearby you.",
					Snackbar.LENGTH_INDEFINITE)
					.setAction("OK", new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							// Request the permission
							ActivityCompat
									.requestPermissions(
											MainActivity.this,
											new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
											PERMISSION_REQUEST_LOCATION);
						}
					}).show();

		} else {
			Snackbar.make(
					mLayout,
					"Permission is not available. Requesting Location permission.",
					Snackbar.LENGTH_SHORT).show();
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSION_REQUEST_LOCATION);
		}
	}

	private void listenerForLocation() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API).build();
		}
	}

	protected void onStart() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
		super.onStart();
	}

	protected void onStop() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		getLastKnowLocation();
	}

	public Location getLastKnowLocation() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			return LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
		}
		return null;
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}
}
