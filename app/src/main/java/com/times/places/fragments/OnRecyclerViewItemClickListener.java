package com.times.places.fragments;

import com.times.places.adapters.BaseAdapter.BaseViewHolder;

public interface OnRecyclerViewItemClickListener {

	public void onRecyclerViewClick(BaseViewHolder vh, int position);
}