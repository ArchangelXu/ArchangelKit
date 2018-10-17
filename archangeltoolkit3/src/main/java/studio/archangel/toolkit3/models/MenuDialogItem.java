package studio.archangel.toolkit3.models;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Objects;

/**
 * Created by xumingke on 2017/3/14.
 */

public class MenuDialogItem {
	public int id;
	public String text;
	public int icon_res;
	public Drawable icon_drawable;
	public int icon_size_px;
	public Object tag;

	public View.OnClickListener listener;

	public MenuDialogItem() {
	}

	public MenuDialogItem(int id, String text) {
		this(id, text, 0, 0);
	}

	public MenuDialogItem(int id, String text, int icon_res, int icon_size_px) {
		this.id = id;
		this.text = text;
		this.icon_res = icon_res;
		this.icon_size_px = icon_size_px;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MenuDialogItem that = (MenuDialogItem) o;
		return id == that.id &&
				icon_res == that.icon_res &&
				Objects.equals(text, that.text) &&
				Objects.equals(tag, that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text, icon_res, tag);
	}
}
