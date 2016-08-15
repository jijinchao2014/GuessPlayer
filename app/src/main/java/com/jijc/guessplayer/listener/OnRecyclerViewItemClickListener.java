package com.jijc.guessplayer.listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description:代替adapter中的回调，给recyclerview设置点击和长按事件
 *          这会出现背景选择器失效的问题，暂时找不到方案解决，因此还是不用了
 *          用法：recyclerView.addOnItemTouchListener(new OnRecyclerViewItemClickListener(recyclerView) {}
 * Created by jijc on 2016/8/15.
 * PackageName: com.jijc.guessplayer.listener
 */
public abstract class OnRecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener {
    private final RecyclerView recyclerView;
    private final GestureDetectorCompat mGestureDetector;

    public OnRecyclerViewItemClickListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder,int position);

    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder,int position);

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onSingleTapUp(MotionEvent event) {
            View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
            if (child != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(child);
                onItemClick(viewHolder,viewHolder.getAdapterPosition());
            }
            return true;
        }

        public void onLongPress(MotionEvent event) {
            View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
            if (child != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(child);
                onItemLongClick(viewHolder,viewHolder.getAdapterPosition());
            }
        }
    }
}
