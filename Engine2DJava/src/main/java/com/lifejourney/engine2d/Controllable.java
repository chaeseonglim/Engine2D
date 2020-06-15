package com.lifejourney.engine2d;

import android.view.MotionEvent;

public interface Controllable {

    boolean onTouchEvent(MotionEvent event);

    int getLayer();

    float getDepth();
}
