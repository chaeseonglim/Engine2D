package com.lifejourney.engine2d;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class Engine2DSurfaceView extends SurfaceView {

    public interface Event {
        boolean onViewTouchEvent(MotionEvent event);
    }

    public Engine2DSurfaceView(Context context) {
        super(context);
    }

    public Engine2DSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Engine2DSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEventHandler(Event eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return eventHandler.onViewTouchEvent(event);
    }

    private Event eventHandler;
}
