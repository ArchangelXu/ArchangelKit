package studio.archangel.toolkit3.adapters;

/**
 * Created by Administrator on 2015/11/16.
 */

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.views.viewholders.AngelCommonViewHolder;


public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	protected static final int TYPE_HEADER = -1;
	protected static final int TYPE_ITEM = 0;

	View header;
	protected List<T> items;
	protected List<T> old_items;
	protected int[] item_layout;
	boolean multi_type = false;

	OnItemClickListener<T> listener;
	OnItemLongClickListener<T> listener_long;

	public CommonRecyclerAdapter() {
	}

	public CommonRecyclerAdapter(int layout_id, List<T> i) {
		this(layout_id, null, i);
	}

	public CommonRecyclerAdapter(int layout_id, View header, List<T> i) {
		this(new int[]{layout_id}, header, i, false);
	}

	public CommonRecyclerAdapter(int[] layout_ids, View header, List<T> i, boolean m) {
		multi_type = m;
//		context = c;
		this.header = header;
		this.items = i;
		old_items = new ArrayList<>(items);
//		this.temp = i;
//		items = new ArrayList<>(temp);
		item_layout = layout_ids;
	}

	protected boolean isHeader(int position) {
		return hasHeader() && position == 0;
	}

	public boolean hasHeader() {
		return header != null;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_HEADER) {
//            if (header != null) {
//                attachListenerFor(header);
//            }
			return onCreateHeaderVH(header);
		}
		if (!multi_type) {
			final View view = LayoutInflater.from(parent.getContext()).inflate(item_layout[0], parent, false);
			AngelCommonViewHolder<T> holder = onCreateItemVH(view);
			attachListenerFor(holder);
			return holder;
		} else {
			try {
				AngelCommonViewHolder<T> holder = onCreateTypedItemVH(parent, viewType);
				attachListenerFor(holder);
				return holder;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}


	void attachListenerFor(final AngelCommonViewHolder<T> holder) {
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
//					Integer tag = (Integer) holder.itemView.getTag();
//					Integer tag = items.indexOf(holder.getModel());
//					if (tag == -1) {
//						Logger.err("adapter onItemClick failed");
//						return;
//					}
//					tag += (hasHeader() ? 1 : 0);
					listener.onItemClick(holder.getModel(), holder);
				}
			}
		});
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				if (listener_long != null) {
//					Integer tag = (Integer) holder.itemView.getTag();
//					Integer tag = items.indexOf(holder.getModel());
//					if (tag == -1) {
//						Logger.err("adapter onItemLongClick failed");
//						return false;
//					}
//					tag += (hasHeader() ? 1 : 0);
					return listener_long.onItemLongClick(holder.getModel(), holder);
				} else {
					return false;

				}
			}
		});
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (isHeader(position)) {
			return;
		}
//		holder.itemView.setTag(position);
//		((AngelCommonViewHolder<T>) holder).render(items.get(holder.getLayoutPosition() - (hasHeader() ? 1 : 0)));
		((AngelCommonViewHolder<T>) holder).bindModel(items.get(holder.getLayoutPosition() - (hasHeader() ? 1 : 0)));
	}


	@Override
	public int getItemViewType(int position) {
		return isHeader(position) ? TYPE_HEADER : TYPE_ITEM;
	}

	@Override
	public int getItemCount() {
		return items.size() + (hasHeader() ? 1 : 0);
	}

	public void setOnItemClickListener(OnItemClickListener<T> listener) {
		this.listener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
		this.listener_long = listener;
	}

	//	public void setRecyclerView(RecyclerView owner) {
//		this.owner = owner;
//	}
	protected abstract boolean areItemsTheSame(T oldItem, T newItem);

	protected boolean areContentsTheSame(T oldItem, T newItem) {
		return false;
	}

	public void notifyDiff() {
		notifyDiff(false);
	}

	public void notifyDiff(boolean should_detect_move) {
		DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
			@Override
			public int getOldListSize() {
//				Logger.out("areItemsTheSame getOldListSize=" + (temp.size() + (hasHeader() ? 1 : 0)));
				return old_items.size() + (hasHeader() ? 1 : 0);
			}

			@Override
			public int getNewListSize() {
//				Logger.out("areItemsTheSame getNewListSize=" + (items.size() + (hasHeader() ? 1 : 0)));
				return items.size() + (hasHeader() ? 1 : 0);
			}

			// 判断是否是同一个 item
			@Override
			public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//				Logger.out("areItemsTheSame oldItemPosition=" + oldItemPosition + " newItemPosition=" + newItemPosition);
				if (hasHeader()) {
					if (isHeader(oldItemPosition) ^ isHeader(newItemPosition)) {
						return false;
					} else if (isHeader(oldItemPosition)) {
						return true;
					} else {
						return CommonRecyclerAdapter.this.areItemsTheSame(old_items.get(oldItemPosition - 1), items.get(newItemPosition - 1));
					}
				} else {
					return CommonRecyclerAdapter.this.areItemsTheSame(old_items.get(oldItemPosition), items.get(newItemPosition));
				}
			}

			// 如果是同一个 item 判断内容是否相同
			@Override
			public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
				if (hasHeader()) {
					if (isHeader(oldItemPosition)) {
						return true;
					}
				}
				return CommonRecyclerAdapter.this.areContentsTheSame(old_items.get(oldItemPosition - (hasHeader() ? 1 : 0)), items.get(newItemPosition - (hasHeader() ? 1 : 0)));
			}
		}, should_detect_move);
		old_items = new ArrayList<>(items);
		diff.dispatchUpdatesTo(this);
		// 通知刷新了之后，要更新副本数据到最新
//		notifyItemRangeChanged(hasHeader() ? 1 : 0, items.size());
	}

	protected abstract AngelCommonViewHolder<T> onCreateHeaderVH(View v);

	protected abstract AngelCommonViewHolder<T> onCreateItemVH(View v);

	protected AngelCommonViewHolder<T> onCreateTypedItemVH(ViewGroup context, int type) throws Exception {
		if (multi_type) {
			throw new Exception("you should override method \"onCreateTypedItemVH\" to use multi-type adapter.");
		}
		return null;
	}

	public interface OnItemClickListener<T> {
		//		void onItemClick(int position, AngelCommonViewHolder<T> holder);
		void onItemClick(T model, AngelCommonViewHolder<T> holder);
	}

	public interface OnItemLongClickListener<T> {
		//		boolean onItemLongClick(int position, AngelCommonViewHolder<T> holder);
		boolean onItemLongClick(T model, AngelCommonViewHolder<T> holder);
	}

}