package studio.archangel.toolkit3.utils.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import studio.archangel.toolkit3.utils.Logger;
import studio.archangel.toolkit3.utils.UIUtil;

public class AndroidBug5497Workaround {


	// For more information, see https://code.google.com/p/android/issues/detail?id=5497
	// To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

	public static void assistActivity(Activity activity) {
		new AndroidBug5497Workaround(activity);
	}

	private View mChildOfContent;
	private int usableHeightPrevious;
	private ViewGroup.LayoutParams frameLayoutParams;

	private AndroidBug5497Workaround(Activity activity) {
		FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
		mChildOfContent = content;
//		mChildOfContent = content.getChildAt(0);
		mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				possiblyResizeChildOfContent();
			}
		});
		frameLayoutParams = mChildOfContent.getLayoutParams();
	}

	private void possiblyResizeChildOfContent() {
		int usableHeightNow = computeUsableHeight();
		int navigation_bar_height = UIUtil.getNavigationBarSize(mChildOfContent.getContext()).y;
		if (usableHeightNow != usableHeightPrevious) {
			int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
			int heightDifference = usableHeightSansKeyboard - usableHeightNow;
			if (heightDifference > (usableHeightSansKeyboard / 4)) {
				// keyboard probably just became visible
				frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
			} else {
				// keyboard probably just became hidden
				frameLayoutParams.height = usableHeightSansKeyboard - navigation_bar_height;
			}
			mChildOfContent.requestLayout();
			usableHeightPrevious = usableHeightNow;
		}
	}

	private int computeUsableHeight() {
		Rect r = new Rect();
		mChildOfContent.getWindowVisibleDisplayFrame(r);
		r.top = 0;//on some device you will get r.top != 0...really weird
		return (r.bottom - r.top);
	}
}