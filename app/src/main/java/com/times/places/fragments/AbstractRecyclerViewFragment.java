package com.times.places.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;

import com.times.places.R;
import com.times.places.adapters.BaseAdapter;
import com.times.places.adapters.BaseAdapter.BaseViewHolder;

/**
 * Created by sumon.chatterjee on 1/16/16. Generic fragment class for RecyclerView
 * provide some common functionality
 */
public abstract class AbstractRecyclerViewFragment extends BaseFragment
		implements
			OnRecyclerViewItemClickListener {

	private RecyclerView recyclerView;
	private BaseAdapter mAdapter;
	private boolean paginationEnable = false;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.recyclerView = (RecyclerView) view
				.findViewById(getRecyclerViewId());
		if (this.recyclerView == null) {
			throw new RuntimeException(
					"RecyclerView not found for given id "
							+ getRecyclerViewId());
		}
		this.recyclerView.setLayoutManager(createLayoutManager());
		this.recyclerView.addOnScrollListener(onScrollListener);
		if (mAdapter != null) {
			this.recyclerView.setAdapter(mAdapter);
		}

		if (paginationEnable) {
			loadMore();
		}
	}

	@Override
	public void onDestroyView() {
		this.recyclerView = null;
		super.onDestroyView();
	}

	public RecyclerView getRecyclerView() {
		return recyclerView;
	}

	/***
	 * we are fetching first position using recylerview instead of using layout
	 * manager Because some one may provide Custom LayoutManager. But we want
	 * some generic function.
	 * ***/
	public int getFirstVisibleItemPosition() {
		if (recyclerView != null) {
			return recyclerView.getChildAdapterPosition(recyclerView
					.getChildAt(0));
		}
		return RecyclerView.NO_POSITION;
	}

	/***
	 * we are fetching last position using recylerview instead of using layout
	 * manager Because some one may provide Custom LayoutManager. But we want
	 * some generic function.
	 * ***/
	public int getLastVisibleItemPosition() {

		if (recyclerView != null) {
			return recyclerView.getChildAdapterPosition(recyclerView
					.getChildAt(recyclerView.getChildCount() - 1));
		}
		return RecyclerView.NO_POSITION;
	}

	public abstract LayoutManager createLayoutManager();

	public int getRecyclerViewId() {
		return R.id.recyclerView;
	}

	public void setAdapter(BaseAdapter mAdapter) {
		if (this.mAdapter != mAdapter) {
			this.mAdapter = mAdapter;
		}

		if (this.mAdapter != null) {
			this.mAdapter.setOnRecyclerViewItemClickListener(this);
			this.mAdapter.setImageLoader(getImageLoader());
		}

		if (this.recyclerView != null) {
			if (this.recyclerView.getAdapter() != mAdapter) {
				LayoutManager layoutManager = this.recyclerView
						.getLayoutManager();
				if (layoutManager != null
						&& layoutManager instanceof GridLayoutManager) {
					GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
					SpanSizeLookup spanSizeLookup = gridLayoutManager
							.getSpanSizeLookup();
					if (spanSizeLookup == null) {
						spanSizeLookup = new FSSpanSizeLookup(this.mAdapter,
								gridLayoutManager.getSpanCount());
						gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
					} else if (spanSizeLookup instanceof FSSpanSizeLookup) {
						((FSSpanSizeLookup) spanSizeLookup)
								.setListener(this.mAdapter);
					} else {
						Log.i("SpanSizeLookup:", "User has implemented");
					}
				}
				this.recyclerView.setAdapter(mAdapter);
			}
		}
	}

	public boolean isPaginationEnable() {
		return paginationEnable;
	}

	public void setPaginationEnable(boolean paginationEnable) {
		this.paginationEnable = paginationEnable;
	}

	@Override
	public void onRecyclerViewClick(BaseViewHolder vh, int position) {

	}

	private RecyclerView.OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void
				onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			if (mAdapter != null && paginationEnable) {
				int lastVisible = getLastVisibleItemPosition();
				if ((lastVisible) >= (mAdapter.getItemCount() - 2)) {
					setPaginationEnable(false);
					loadMore();
				}
			}
		}
	};

	protected void loadMore() {
	}

}
