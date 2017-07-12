package com.times.places;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.network.ImageRequestManager;
import com.network.VolleyQueueWrapper;
import com.network.utils.ApiEndPoints;

/**
 * Created by sumon.chatterjee on 2/7/16.
 */
public class BaseActivity extends AppCompatActivity {

	private VolleyQueueWrapper volleyQueueWrapper;
	private ImageRequestManager imageRequestManager;
	private View progressBarLoader;

	public VolleyQueueWrapper getVolleyQueueWrapper() {

		if (volleyQueueWrapper == null) {
			volleyQueueWrapper = VolleyQueueWrapper.newInstance(this,
					ApiEndPoints.BASE_URL);
		}
		return volleyQueueWrapper;
	}

	public ImageRequestManager getImageRequestManager() {
		if (imageRequestManager == null) {
			imageRequestManager = ImageRequestManager.getInstance(this);
		}
		return imageRequestManager;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void setUpToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
		setSupportActionBar(toolbar);
		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_action_place);
		ab.setDisplayHomeAsUpEnabled(true);
		this.progressBarLoader = findViewById(R.id.progressBarLoader);
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

	/*******/
	public static void addToBackStack(final FragmentManager manager,
			Fragment fragment, boolean addToBackStack) {

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fragment_container, fragment, fragment
				.getClass().getName());
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (volleyQueueWrapper != null) {
			volleyQueueWrapper.cancel();
		}
	}
}
