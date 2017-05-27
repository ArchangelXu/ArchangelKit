package studio.archangel.toolkit3.views.dialogs;

import android.content.Context;

import studio.archangel.toolkit3.R;

/**
 * Created by xmk on 16/7/13.
 */

public class AngelHintDialogFactory {
	public static AngelHintDialog createHintDialog(Context context, int layout_id, int background_view_id) {
		return new AngelHintDialog(context, layout_id, background_view_id, R.style.AnimDialog);
	}

	public static AngelHintDialog createHintDialog(Context context, int layout_id) {
		return new AngelHintDialog(context, layout_id, R.style.AnimDialog);
	}

	public static AngelHintDialog createFullscreenHintDialog(Context context, int layout_id, int background_view_id) {
		return new AngelHintDialog(context, layout_id, background_view_id, R.style.AnimDialogFullscreen);
	}

	public static AngelHintDialog createFullscreenHintDialog(Context context, int layout_id) {
		return new AngelHintDialog(context, layout_id, R.style.AnimDialogFullscreen);
	}
}
