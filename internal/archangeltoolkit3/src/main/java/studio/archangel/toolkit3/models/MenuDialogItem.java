package studio.archangel.toolkit3.models;

/**
 * Created by xmk on 2017/3/14.
 */

public class MenuDialogItem {
	public int id;
	public String text;
	public int icon_res;
	public int icon_size_px;

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
