package jsc.org.lib.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import jsc.org.lib.basic.R;

public class ProportionallyFillableGridLayout extends ViewGroup {

    private int rowCount;
    private int columnCount;
    private int rowSpace;
    private int columnSpace;
    private String itemBaseOn;//w,h
    private int itemWRatio;
    private int itemHRatio;
    private boolean centerHorizontal;
    private boolean centerVertical;
    private int itemWidth = 0;
    private int itemHeight = 0;
    private int leftMargin = 0;
    private int topMargin = 0;

    public ProportionallyFillableGridLayout(Context context) {
        this(context, null);
    }

    public ProportionallyFillableGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProportionallyFillableGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProportionallyFillableGridLayout, 0, 0);
        rowCount = a.getInteger(R.styleable.ProportionallyFillableGridLayout_fglRowCount, 1);
        columnCount = a.getInteger(R.styleable.ProportionallyFillableGridLayout_fglColumnCount, 1);
        rowSpace = a.getDimensionPixelSize(R.styleable.ProportionallyFillableGridLayout_fglRowSpace, 0);
        columnSpace = a.getDimensionPixelSize(R.styleable.ProportionallyFillableGridLayout_fglColumnSpace, 0);
        centerHorizontal = a.getBoolean(R.styleable.ProportionallyFillableGridLayout_fglCenterHorizontal, false);
        centerVertical = a.getBoolean(R.styleable.ProportionallyFillableGridLayout_fglCenterVertical, false);
        if (a.hasValue(R.styleable.ProportionallyFillableGridLayout_fglItemRatio)) {
            String itemRatio = a.getString(R.styleable.ProportionallyFillableGridLayout_fglItemRatio).trim();
            String[] splits0 = itemRatio.split(",", -1);
            if (splits0.length == 2) {
                String[] splits1 = splits0[1].split(":", -1);
                if (splits1.length == 2) {
                    itemBaseOn = splits0[0].trim();
                    try {
                        itemWRatio = Integer.parseInt(splits1[0]);
                        itemHRatio = Integer.parseInt(splits1[1]);
                        itemWRatio = Math.max(1, itemWRatio);
                        itemHRatio = Math.max(1, itemHRatio);
                    } catch (NumberFormatException ignore) {

                    }
                }
            }
        }
        a.recycle();
    }

    public void setRowCount(@IntRange(from = 1) int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(@IntRange(from = 1) int columnCount) {
        this.columnCount = columnCount;
    }

    public void setRowSpace(@IntRange(from = 0) int rowSpace) {
        this.rowSpace = rowSpace;
    }

    public void setColumnSpace(@IntRange(from = 0) int columnSpace) {
        this.columnSpace = columnSpace;
    }

    public void setItemRatio(@NonNull String itemBaseOn,
                             @IntRange(from = 1) int itemWRatio,
                             @IntRange(from = 1) int itemHRatio) {
        this.itemBaseOn = itemBaseOn;
        this.itemWRatio = itemWRatio;
        this.itemHRatio = itemHRatio;
    }

    public void setCenterHorizontal(boolean centerHorizontal) {
        this.centerHorizontal = centerHorizontal;
    }

    public void setCenterVertical(boolean centerVertical) {
        this.centerVertical = centerVertical;
    }

    public <T, L extends OnClickListener> void updateItems(T[] items, OnCreateItemViewListener<T> listener1, L listener2) {
        removeAllViews();
        if (items != null) {
            for (T item : items) {
                TextView view = new TextView(getContext());
                view.setGravity(Gravity.CENTER);
                view.setTextColor(0xFF333333);
                view.setTag(R.id.base_view_key, item);
                view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                addView(view);
                view.setText(item.toString());
                if (listener1 != null) {
                    listener1.onItemViewCreated(view, item);
                }
                view.setOnClickListener(listener2);
            }
        }
        requestLayout();
    }

    public final void singleSelected(View view) {
        if (view.isSelected()) return;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.setSelected(child == view);
        }
    }

    public final void singleSelected(int index) {
        int count = getChildCount();
        if (index < 0 || index >= count) {
            return;
        }
        View child = getChildAt(index);
        if (child.isSelected()) return;
        for (int i = 0; i < count; i++) {
            getChildAt(i).setSelected(i == index);
        }
    }

    public final void toggleSelected(int index) {
        int count = getChildCount();
        if (index < 0 || index >= count) {
            return;
        }
        View child = getChildAt(index);
        child.setSelected(!child.isSelected());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int tempItemWidth = (availableWidth - (columnCount - 1) * columnSpace) / columnCount;
        int tempItemHeight = (availableHeight - (rowCount - 1) * rowSpace) / rowCount;
        switch (itemBaseOn) {
            case "w":
                //去除余数
                itemWidth = tempItemWidth / itemWRatio * itemWRatio;
                itemHeight = Math.min(tempItemHeight, itemWidth * itemHRatio / itemWRatio);
                break;
            case "h":
                //去除余数
                itemHeight = tempItemHeight / itemHRatio * itemHRatio;
                itemWidth = Math.min(tempItemWidth, itemHeight * itemWRatio / itemHRatio);
                break;
            default:
                itemWidth = tempItemWidth;
                itemHeight = tempItemHeight;
                break;
        }
        if (itemWidth > 0 && itemHeight > 0) {
            leftMargin = (availableWidth - itemWidth * columnCount - (columnCount - 1) * columnSpace) / 2;
            topMargin = (availableHeight - itemHeight * rowCount - (rowCount - 1) * rowSpace) / 2;
            int count = getChildCount();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    int index = i * columnCount + j;
                    if (index < count) {
                        View child = getChildAt(index);
                        ViewGroup.LayoutParams params = child.getLayoutParams();
                        params.width = itemWidth;
                        params.height = itemHeight;
                    }
                }
            }
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int tempLeftMargin = centerHorizontal ? leftMargin : 0;
        int tempTopMargin = centerVertical ? topMargin : 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                int index = i * columnCount + j;
                if (index < count) {
                    int left = getPaddingLeft() + tempLeftMargin + j * (itemWidth + columnSpace);
                    int top = getPaddingTop() + tempTopMargin + i * (itemHeight + rowSpace);
                    View child = getChildAt(index);
                    child.layout(left, top, left + itemWidth, top + itemHeight);
                }
            }
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface OnCreateItemViewListener<T> {
        void onItemViewCreated(TextView view, T item);
    }
}
