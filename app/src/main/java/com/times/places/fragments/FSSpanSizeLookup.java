package com.times.places.fragments;

import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;

public class FSSpanSizeLookup extends SpanSizeLookup {

	public interface SpanSizeLookupListener {
		public int getSpanSize(int position, int totalSpanSize);
	}

	private SpanSizeLookupListener listener;
	private int totalSpanSize = 1;

	public FSSpanSizeLookup(SpanSizeLookupListener listener,
							int totalSpanSize) {
		this.listener = listener;
		this.totalSpanSize = totalSpanSize;
	}

	public void setListener(SpanSizeLookupListener listener) {
		this.listener = listener;
	}

	@Override
	public int getSpanSize(int position) {
		if (listener!=null) {
			return listener.getSpanSize(position, totalSpanSize);
		}
		return totalSpanSize;
	}
}