package com.bokecc.vod.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ItemDecoration
 *
 * @author Zhang
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {

    private final int itemSpace;
    private final int itemNum;

    /**
     * @param itemSpace item间隔
     * @param itemNum   每行item的个数
     */
    public ItemDecoration(int itemSpace, int itemNum) {
        this.itemSpace = itemSpace;
        this.itemNum = itemNum;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = itemSpace / 2;
        outRect.bottom = itemSpace / 2;
        if (parent.getChildLayoutPosition(view) % itemNum == 0) {
            //parent.getChildLayoutPosition(view) 获取view的下标
            outRect.left = itemSpace;
            outRect.right = itemSpace / 2;
        } else {
            outRect.left = itemSpace / 2;
            outRect.right = itemSpace;
        }
    }
}
