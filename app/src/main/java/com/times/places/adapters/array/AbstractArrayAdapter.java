package com.times.places.adapters.array;

import java.util.ArrayList;
import java.util.Collection;

import com.times.places.adapters.BaseAdapter;
import com.times.places.objects.Base;


/**
 * Created by sumon.chatterjee on 1/16/16.
 */
public abstract class AbstractArrayAdapter<T extends Base> extends BaseAdapter {

	private final ArrayList<T> mArray;

	public AbstractArrayAdapter() {
		mArray = new ArrayList<T>();
	}

	@Override
	public int getItemCount() {
		return mArray.size();
	}

	@Override
	public T getItem(int position) {
		return mArray.get(position);
	}

	public void addItem(T item) {
		final int oldCount = this.mArray.size();
		this.mArray.add(item);
		this.notifyItemInserted(oldCount);
	}

	public void addItem(int index, T item) {
		this.mArray.add(index, item);
		notifyItemInserted(index);
	}

	public void removeItem(int index) {
		this.mArray.remove(index);
		notifyItemRemoved(index);
	}

	public void clear() {
		int count = getItemCount();
		this.mArray.clear();
		notifyItemRangeRemoved(0, count);
	}

	public void addAll(Collection<T> items) {
		final int oldCount = getItemCount();
		this.mArray.addAll(items);
		notifyItemRangeInserted(oldCount, items.size());
	}

	public void addAll(int index, Collection<T> items) {
		this.mArray.addAll(index, items);
		notifyItemRangeInserted(index, items.size());
	}

	public void addUniqueItem(T t) {
		if (!this.mArray.contains(t)) {
			addItem(t);
		} else {
			int i = this.mArray.indexOf(t);
			if (i != -1) {
				this.notifyItemChanged(i);
			}
		}
	}

	public void removeItem(T t) {
		if (this.mArray.contains(t)) {
			int i = this.mArray.indexOf(t);
			if (i != -1) {
				removeItem(i);
			}
		}
	}

	public int indexOf(T t) {
		return this.mArray.indexOf(t);
	}
}