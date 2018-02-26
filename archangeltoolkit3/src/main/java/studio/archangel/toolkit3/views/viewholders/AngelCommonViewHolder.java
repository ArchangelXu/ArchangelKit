package studio.archangel.toolkit3.views.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TintContextWrapper;
import android.view.View;

/**
 * Created by Administrator on 2015/11/16.
 */
public abstract class AngelCommonViewHolder<T> extends RecyclerView.ViewHolder {
	protected Context context;
	T model;

	public AngelCommonViewHolder(View itemView) {
		super(itemView);
		context = itemView.getContext();
		if (context instanceof TintContextWrapper) {
			context = ((TintContextWrapper) context).getBaseContext();
		}
	}

	public T getModel() {
		return model;
	}

	public void bindModel(T model) {
		this.model = model;
		render(this.model);
	}

	public abstract void render(T model);

//    public void onClick(Context context, Object model) {
//
//    }
}
