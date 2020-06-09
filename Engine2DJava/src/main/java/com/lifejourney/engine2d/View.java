package com.lifejourney.engine2d;

import android.view.MotionEvent;

public interface View {

    /**
     *
     */
    void close();

    /**
     *
     */
    void update();

    /**
     *
     */
    void commit();

    /**
     *
     */
    void show();

    /**
     *
     */
    void hide();

    /**
     *
     * @param event
     * @return
     */
    boolean onTouchEvent(MotionEvent event);
}
