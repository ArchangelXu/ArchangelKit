package studio.archangel.toolkit3.utils.ui;

/**
 * Created by xmk on 2016/12/20.
 */

import android.view.MotionEvent;

/**
 * lock scroll direction to horizontal or vertical
 */
public class ScrollTouchHelper {
	float last_x = 0;
	float last_y = 0;
	boolean first_touch = false;
	boolean vertical = false;

	public MotionEvent fixDirectionFor(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			last_x = x;
			last_y = y;
			first_touch = true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
			if (first_touch) {
				vertical = Math.abs(x - last_x) <= Math.abs(y - last_y);
			}
			if (vertical) {
				event.setLocation(last_x, y);
			} else {
				event.setLocation(x, last_y);
			}
		}
		return event;
	}
}
