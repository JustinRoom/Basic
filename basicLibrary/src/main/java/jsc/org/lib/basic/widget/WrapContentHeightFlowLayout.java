package jsc.org.lib.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapContentHeightFlowLayout extends ViewGroup {

    private final Map<String, List<View>> viewListMap = new HashMap<>();

    public WrapContentHeightFlowLayout(Context context) {
        super(context);
    }

    public WrapContentHeightFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentHeightFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //遍历去调用所有子元素的measure方法（child.getMeasuredHeight()才能获取到值，否则为0）
        viewListMap.clear();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        int rowIndex = 0;
        int rowMaxWidth = widthSize - getPaddingRight();
        int rowStart = getPaddingLeft();
        int viewIndex = 0;
        while (viewIndex < childCount) {
            List<View> rowViewList = viewListMap.get(String.valueOf(rowIndex));
            if (rowViewList == null) {
                rowViewList = new ArrayList<>();
                viewListMap.put(String.valueOf(rowIndex), rowViewList);
            }
            View child = getChildAt(viewIndex);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            //如果start + childMaxWidth > rowMaxWidth，则换行
            if (!rowViewList.isEmpty() && (rowStart + childWidth > rowMaxWidth)) {
                rowIndex++;
                rowStart = getPaddingLeft();
            } else {
                rowViewList.add(child);
                rowStart += childWidth;
                viewIndex++;
            }
        }

        int contentHeight = getPaddingTop() + getPaddingBottom();
        int rowCount = viewListMap.size();
        for (int i = 0; i < rowCount; i++) {
            List<View> viewList = viewListMap.get(String.valueOf(i));
            int rowMaxHeight = 0;
            for (View child : viewList) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                rowMaxHeight = Math.max(rowMaxHeight, childHeight);
            }
            contentHeight += rowMaxHeight;
        }

        //设置flow的宽高
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int rowCount = viewListMap.size();
        int top = getPaddingTop();
        for (int i = 0; i < rowCount; i++) {
            List<View> viewList = viewListMap.get(String.valueOf(i));
            int left = getPaddingLeft();
            int rowMaxHeight = 0;
            for (View child : viewList) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                left += layoutParams.leftMargin;
                int right = left + child.getMeasuredWidth();
                int tempTop = top + layoutParams.topMargin;
                int bottom = tempTop + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                left = right + layoutParams.rightMargin;
            }
            top += rowMaxHeight;
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
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

    public static class LayoutParams extends MarginLayoutParams {

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
}
