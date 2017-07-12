package com.times.places.fragments.search;

import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.network.VolleyQueueWrapper;
import com.times.places.R;
import com.times.places.adapters.array.AbstractArrayAdapter;
import com.times.places.fragments.search.SearchVenueFragment.Loader;
import com.times.places.response.Place;

/**
 * Created by sumon.chatterjee on 2/7/16.
 */
public class SearchAdapter extends AbstractArrayAdapter<Place> {


	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == Loader.VIEW_TYPE) {
			return new LoaderViewHolder(R.layout.loader_row, parent);
		} else {
			return new PlaceViewHolder(R.layout.place_row, parent);
		}
	}

	private class PlaceViewHolder extends BaseViewHolder<Place> {

		private TextView mNameTextView;
		private TextView mAddressTextView;
		private NetworkImageView imageView;
		private int imageWidth;

		public PlaceViewHolder(int layout, ViewGroup parent) {
			super(layout, parent);
			mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
			mAddressTextView = (TextView) itemView
					.findViewById(R.id.addressTextView);
			imageView = (NetworkImageView) itemView
					.findViewById(R.id.imageView);
			imageWidth = parent.getContext().getResources()
					.getDimensionPixelSize(R.dimen.search_image_width);
		}

		@Override
		public void bindViews(int position, Place place) {
			super.bindViews(position, place);
			mNameTextView.setText(place.getName());
			mAddressTextView.setText(place.getVicinity());
			imageView.setErrorImageResId(R.drawable.ic_action_place);
			imageView.setDefaultImageResId(R.drawable.ic_action_place);
			if (place.getPhotos() != null && place.getPhotos().size() > 0) {
				String photoRef = place.getPhotos().get(0).getPhotoReference();
				imageView.setImageUrl(
						VolleyQueueWrapper.imageUrl(photoRef, imageWidth*2),
						getImageLoader());
			}
		}
	}

	private class LoaderViewHolder extends BaseViewHolder {

		public LoaderViewHolder(int layout, ViewGroup parent) {
			super(layout, parent);
		}
	}

}
