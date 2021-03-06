package studio.archangel.toolkit3.utils.ui;

import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

/**
 * Created by xumingke on 2017/6/29.
 */

public class AngelFadeInDownAnimator extends FadeInDownAnimator {
	public AngelFadeInDownAnimator() {
		super();
	}

	public AngelFadeInDownAnimator(Interpolator interpolator) {
		super(interpolator);
	}


	@Override
	public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
	                             int fromX, int fromY, int toX, int toY) {
		if (oldHolder != null) {
			dispatchChangeFinished(oldHolder, true);
		} else if ((newHolder != null) && (newHolder != oldHolder)) {
			dispatchChangeFinished(newHolder, false);
		}

		return false;
	}
}
