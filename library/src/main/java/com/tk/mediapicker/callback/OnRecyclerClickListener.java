package com.tk.mediapicker.callback;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by TK on 2016/6/13.
 * recyclerview 点击
 */
public abstract class OnRecyclerClickListener implements RecyclerView.OnItemTouchListener {
    private RecyclerView recyclerView;
    private GestureDetectorCompat gestureDetectorCompat;

    public OnRecyclerClickListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public abstract void onClick(int position);

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                onClick(recyclerView.getChildAdapterPosition(child));
            }
            return true;
        }
    }
}
