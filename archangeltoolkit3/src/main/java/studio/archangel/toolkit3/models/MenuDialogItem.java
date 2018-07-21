package studio.archangel.toolkit3.models;

import android.graphics.drawable.Drawable;

/**
 * Created by xmk on 2017/3/14.
 */

public class MenuDialogItem {
	public int id;
	public String text;
	public int icon_res;
	public Drawable icon_drawable;
	public int icon_size_px;
	public Object tag;

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
}
