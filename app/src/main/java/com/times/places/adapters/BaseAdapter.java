package com.times.places.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.times.places.adapters.BaseAdapter.BaseViewHolder;
import com.times.places.fragments.FSSpanSizeLookup.SpanSizeLookupListener;
import com.times.places.fragments.OnRecyclerViewItemClickListener;
import com.times.places.objects.Base;

/**
 * Created by sumon.chatterjee on 1/16/16.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder>
		implements SpanSizeLookupListener {

	private ImageLoader imageLoader;

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

	public OnRecyclerViewItemClickListener getOnRecyclerViewItemClickListener() {
		return onRecyclerViewItemClickListener;
	}

	public void setOnRecyclerViewItemClickListener(
			OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
		this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
	}

	@Override
	public int getSpanSize(int position, int totalSpanSize) {
		return totalSpanSize;
	}

	public Object getItem(int position) {
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		Object item= getItem(position);
		if (item instanceof Base){
			return ((Base)item).getViewType();
		}
		return 0;
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		holder.bindViews(position,getItem(position));
	}


	@Override
	public void onViewRecycled(BaseViewHolder holder) {
		super.onViewRecycled(holder);
		holder.mBindPayload = null;
	}



	public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

		private T mBindPayload;

		///make private onclick
		private BaseViewHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(clickListener);
		}

		protected BaseViewHolder(int layout, ViewGroup parent) {
			this(LayoutInflater.from(parent.getContext()).inflate(layout,
					parent, false));
		}

		public void bindViews(int position,T t) {
			mBindPayload = t;
		}

		private OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onRecyclerViewItemClickListener != null) {
					onRecyclerViewItemClickListener.onRecyclerViewClick(
							BaseViewHolder.this, getAdapterPosition());
				}
			}
		};


	}
}
