package com.nhaarman.listviewanimations.itemmanipulation;

import android.view.MotionEvent;

public interface TouchEventHandler {

	boolean onTouchEvent(MotionEvent event);

	boolean isInteracting();

}
