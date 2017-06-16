package studio.archangel.toolkit3.utils;

import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

/**
 * Created by xmk on 2017/6/16.
 */
public class AdapterUpdateCallback implements ListUpdateCallback {
	int insert_position = -1;
	RecyclerView.Adapter adapter = null;

	public AdapterUpdateCallback(RecyclerView.Adapter adapter) {
		this.adapter = adapter;
	}

	public int getInsertPosition() {
		return insert_position;
	}

	public void onChanged(int position, int count, Object payload) {
		adapter.notifyItemRangeChanged(position, count, payload);
	}

	public void onInserted(int position, int count) {
		insert_position = position;
		adapter.notifyItemRangeInserted(position, count);
	}

	public void onMoved(int fromPosition, int toPosition) {
		adapter.notifyItemMoved(fromPosition, toPosition);
	}

	public void onRemoved(int position, int count) {
		adapter.notifyItemRangeRemoved(position, count);
	}
}
