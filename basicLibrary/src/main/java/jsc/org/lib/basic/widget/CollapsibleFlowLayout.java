package jsc.org.lib.basic.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollapsibleFlowLayout extends ViewGroup {

    boolean signalLine = true;
    View mMoreView;
    Rect moreRect = new Rect();

    static class Row {
        int top;
        int maxHeight;
        List<View> views = new ArrayList<>();
        List<Rect> rectArray = new ArrayList<>();

        boolean noChildren() {
            return views.isEmpty();
        }
    }

    public CollapsibleFlowLayout(Context context) {
        super(context);
    }

    public CollapsibleFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsibleFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMoreView(View mView) {
        if (this.mMoreView != null) {
            removeView(this.mMoreView);
        }
        this.mMoreView = mView;
        addView(mView);
        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signalLine = !signalLine;
                if (v instanceof TextView) {
                    ((TextView) v).setText(signalLine ? "展开>" : "<收起");
                }
                requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //遍历去调用所有子元素的measure方法（child.getMeasuredHeight()才能获取到值，否则为0）
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //由于计算子view所占宽度，这里传值需要自身减去PaddingRight宽度，PaddingLeft会在接下来计算子元素位置时加上
        compute(widthSize);

        if (heightMode == MeasureSpec.EXACTLY) {
            //设置flow的宽高
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else if (heightMode == MeasureSpec.AT_MOST
                || heightMode == MeasureSpec.UNSPECIFIED) {
            int baseHeight = getPaddingTop() + getPaddingBottom();
            int height = 0;
            if (signalLine) {
                height = cache.isEmpty() ? 0 : cache.get("0").maxHeight;
            } else {
                for (Row row : cache.values()) {
                    height = height + row.maxHeight;
                }
            }
            if (mMoreView != null && cache.size() > 1) {
                int right = getPaddingRight();
                MarginLayoutParams mParams = (MarginLayoutParams) mMoreView.getLayoutParams();
                int moreWidth = mParams.leftMargin + mMoreView.getMeasuredWidth() + mParams.rightMargin;
                int moreHeight = mParams.topMargin + mMoreView.getMeasuredHeight() + mParams.bottomMargin;
                Row row = cache.get(String.valueOf(signalLine ? 0 : cache.size() - 1));
                int index = row.views.size() - 1;
                Rect rect = row.rectArray.get(index);
                View child = row.views.get(index);
                MarginLayoutParams lParams = (MarginLayoutParams) child.getLayoutParams();
                if (rect.right + moreWidth + right <= widthSize) {
                    moreRect.left = rect.right + lParams.rightMargin + mParams.leftMargin;
                    moreRect.top = rect.top;
                    moreRect.right = moreRect.left + mMoreView.getMeasuredWidth();
                    moreRect.bottom = moreRect.top + mParams.topMargin + mMoreView.getMeasuredHeight();
                    row.maxHeight = Math.max(row.maxHeight, moreHeight);
                } else {
                    //new row
                    height = height + moreHeight;
                    moreRect.left = getPaddingLeft() + mParams.leftMargin;
                    moreRect.top = row.top + row.maxHeight;
                    moreRect.right = moreRect.left + mMoreView.getMeasuredWidth();
                    moreRect.bottom = moreRect.top + mParams.topMargin + mMoreView.getMeasuredHeight();
                }
            }
            setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(baseHeight + height, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int rowCount = cache.size();
        for (int i = 0; i < rowCount; i++) {
            Row row = cache.get(String.valueOf(i));
            int len = row.views.size();
            if (signalLine && i > 0) {
                for (int j = 0; j < len; j++) {
                    View child = row.views.get(j);
                    child.layout(0, 0, 0, 0);
                }
            } else {
                for (int j = 0; j < len; j++) {
                    View child = row.views.get(j);
                    MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                    int childHeight = params.topMargin + child.getMeasuredHeight() + params.bottomMargin;
                    Rect rect = row.rectArray.get(j);
                    int top = row.top + (childHeight - row.maxHeight) / 2 + params.topMargin;
                    int bottom = top + child.getMeasuredHeight();
                    child.layout(rect.left, top, rect.right, bottom);
                }
            }
        }
        //add more view
        if (mMoreView == null) {
            return;
        }
        if (cache.size() > 1) {
            mMoreView.layout(moreRect.left, moreRect.top, moreRect.right, moreRect.bottom);
        } else {
            mMoreView.layout(0, 0, 0, 0);
        }
    }

    Map<String, Row> cache = new HashMap<>();

    /**
     * 测量过程
     *
     * @param totalWidth 该view的宽度
     * @return 返回子元素总所占宽度和高度（用于计算Flowlayout的AT_MOST模式设置宽高）
     */
    private void compute(int totalWidth) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        int mLeft = left;
        int mTop = top;
        //当前行所有子元素的最大高度（用于换行累加高度）

        int rowIndex = 0;
        for (int i = 0; i < getChildCount(); i++) {
            String key = String.valueOf(rowIndex);
            Row row = cache.get(key);
            if (row == null) {
                row = new Row();
                cache.put(key, row);
            }
            View child = getChildAt(i);
            if (child == mMoreView) continue;

            //获取元素测量宽度和高度
            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();
            //获取元素的margin
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            //子元素所占宽度
            int childWidth = params.leftMargin + params.rightMargin + measuredWidth;
            int childHeight = params.topMargin + params.bottomMargin + measuredHeight;

            if (row.noChildren() || mLeft + childWidth + right <= totalWidth) {
                row.top = mTop;
                row.views.add(child);
                row.rectArray.add(new Rect(mLeft + params.leftMargin,
                        mTop + params.topMargin,
                        mLeft + childWidth - params.rightMargin,
                        mTop + childHeight - params.bottomMargin));
                row.maxHeight = Math.max(row.maxHeight, childHeight);
            } else {
                mTop = mTop + row.maxHeight;
                rowIndex++;
                Row nextRow = new Row();
                cache.put(String.valueOf(rowIndex), nextRow);
                mLeft = left;
                nextRow.top = mTop;
                nextRow.views.add(child);
                nextRow.rectArray.add(new Rect(mLeft + params.leftMargin,
                        mTop + params.topMargin,
                        mLeft + childWidth - params.rightMargin,
                        mTop + childHeight - params.bottomMargin));
                nextRow.maxHeight = Math.max(nextRow.maxHeight, childHeight);
            }
            mLeft = mLeft + childWidth;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
