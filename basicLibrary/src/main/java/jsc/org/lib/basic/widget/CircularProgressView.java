package jsc.org.lib.basic.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import jsc.org.lib.basic.R;

public class CircularProgressView extends View {

    //窟窿圆形画笔
    private final Paint mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //窟窿圆形路径
    private Path path = null;
    //窟窿圆形直径计算标准
    private int _2RadiusBaseOn = 0;
    //窟窿圆形直径
    private int _2Radius;
    //窟窿圆形直径占宽百分比
    private float _2RadiusPercent;
    //窟窿圆形排版位置
    private int _2RadiusGravity = 3;
    //窟窿圆形X坐标
    private float centerX;
    //窟窿圆形Y坐标
    private float centerY;

    //阴影圆形画笔
    private final Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //阴影圆形线宽
    private float shadowStrokeWidth = 0.0f;
    //阴影圆形半径
    private float mShadowRadius;

    //进度圆弧画笔
    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //进度圆弧矩形坐标
    private final RectF mProgressRectF = new RectF();
    //进度圆弧背景
    private Shader outShader = null;
    //进度圆弧背景开始颜色
    private int progressStartColor = Color.TRANSPARENT;
    //进度圆弧背景结束颜色
    private int progressEndColor = Color.TRANSPARENT;
    //进度圆弧线宽
    private float progressStrokeWidth = 0.0f;
    //进度圆弧起始角度
    private float startAngle = -90f;
    //进度圆弧覆盖角度
    private float sweepAngle = 0f;
    //进度圆弧覆盖角度动画时间
    private long sweepDuration = 0L;
    //进度圆弧当前覆盖角度
    private float curSweepAngle = 0f;

    private final Rect mCropRect = new Rect();
    private OnSweepProgressListener mSweepProgressListener = null;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //开启硬件加速
        setLayerType(LAYER_TYPE_HARDWARE, null);
        float defaultShadowStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        float defaultProgressStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView, defStyleAttr, 0);
        int backgroundColor = a.getColor(R.styleable.CircularProgressView_cpvBackgroundColor, Color.WHITE);
        sweepAngle = a.getFloat(R.styleable.CircularProgressView_cpvSweepAngle, 0f);
        _2RadiusBaseOn = a.getInt(R.styleable.CircularProgressView_cpv2RadiusBaseOn, 0);
        _2Radius = a.getDimensionPixelSize(R.styleable.CircularProgressView_cpv2Radius, 0);
        _2RadiusPercent = a.getFloat(R.styleable.CircularProgressView_cpv2RadiusPercent, 0.5f);
        _2RadiusGravity = a.getInt(R.styleable.CircularProgressView_cpv2RadiusGravity, 3);
        int shadowColor = a.getColor(R.styleable.CircularProgressView_cpvShadowColor, 0xFFDCEEFF);
        shadowStrokeWidth = a.getDimension(R.styleable.CircularProgressView_cpvShadowStrokeWidth, defaultShadowStrokeWidth);
        progressStartColor = a.getColor(R.styleable.CircularProgressView_cpvProgressStartColor, 0xFF8AE6FC);
        progressEndColor = a.getColor(R.styleable.CircularProgressView_cpvProgressEndColor, 0xFF8DC5FE);
        progressStrokeWidth = a.getDimension(R.styleable.CircularProgressView_cpvProgressStrokeWidth, defaultProgressStrokeWidth);
        a.recycle();

        //窟窿圆形
        mPathPaint.setColor(backgroundColor);

        //阴影圆形
        mShadowPaint.setColor(shadowColor);
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setShadowLayer(shadowStrokeWidth, 0F, 0F, shadowColor);
        mShadowPaint.setStrokeWidth(shadowStrokeWidth);

        //进度圆弧
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(progressStrokeWidth);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        curSweepAngle = sweepAngle;
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    public void set2Radius(int _2Radius) {
        this._2Radius = _2Radius;
        path = null;
        startSweep();
    }

    public void _2RadiusPercent(float percent) {
        _2Radius = 0;
        _2RadiusPercent = percent;
        path = null;
        startSweep();
    }

    public void sweepAngle(float sweepAngle, long duration) {
        this.sweepAngle = sweepAngle;
        sweepDuration = duration;
        startSweep();
    }

    private ValueAnimator animator = null;
    private boolean animatorCanceled = false;

    private void startSweep() {
        cancelSweep();
        if (sweepAngle == 0) {
            curSweepAngle = sweepAngle;
            postInvalidate();
            return;
        }

        if (animator == null) {
            animator = new ValueAnimator();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mSweepProgressListener != null && !animatorCanceled) {
                        mSweepProgressListener.onEnd();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animatorCanceled = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = animation.getAnimatedFraction();
                    curSweepAngle = sweepAngle * fraction;
                    postInvalidate();
                }
            });
            animator.setInterpolator(new LinearInterpolator());
        }
        animatorCanceled = false;
        animator.setFloatValues(0, sweepAngle);
        animator.setDuration(sweepDuration);
        animator.start();
    }

    public void cancelSweep() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public void setOnSweepProgressListener(OnSweepProgressListener sweepProgressListener) {
        this.mSweepProgressListener = sweepProgressListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path = null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelSweep();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (_2Radius <= 0 && _2RadiusPercent <= 0) return;

        if (path == null) {
            //窟窿圆形半径
            float radius;
            if (_2RadiusBaseOn == 0) {
                radius = _2Radius > 0 ? _2Radius / 2.0f : getWidth() * _2RadiusPercent / 2;
            } else {
                radius = _2Radius > 0 ? _2Radius / 2.0f : getHeight() * _2RadiusPercent / 2;
            }
            switch (_2RadiusGravity) {
                case 0://topLeft
                    centerX = getPaddingStart() + radius;
                    centerY = getPaddingTop() + radius;
                    break;
                case 1://topCenter
                    centerX = getWidth() / 2.0f;
                    centerY = getPaddingTop() + radius;
                    break;
                case 2://topRight
                    centerX = getWidth() - getPaddingEnd() - radius;
                    centerY = getPaddingTop() + radius;
                    break;
                case 3://center
                    centerX = getWidth() / 2.0f;
                    centerY = getHeight() / 2.0f;
                    break;
                case 4://bottomLeft
                    centerX = getPaddingStart() + radius;
                    centerY = getHeight() - getPaddingBottom() - radius;
                    break;
                case 5://bottomCenter
                    centerX = getWidth() / 2.0f;
                    centerY = getHeight() - getPaddingBottom() - radius;
                    break;
                case 6://bottomRight
                    centerX = getWidth() - getPaddingEnd() - radius;
                    centerY = getHeight() - getPaddingBottom() - radius;
                    break;
            }

            float left = centerX - radius;
            float top = centerY - radius;
            float right = centerX + radius;
            float bottom = centerY + radius;
            mCropRect.set((int) left, (int) top, (int) right, (int) bottom);

            mShadowRadius = radius + shadowStrokeWidth / 2;
            float progressRadius = radius + progressStrokeWidth / 2;
            float offset = (shadowStrokeWidth - progressStrokeWidth) / 2;
            mProgressRectF.set(
                    centerX - progressRadius - offset,
                    centerY - progressRadius - offset,
                    centerX + progressRadius + offset,
                    centerY + progressRadius + offset);

            Matrix matrix = new Matrix();
            matrix.setRotate(180, centerX, centerY);
            outShader = new SweepGradient(centerX, centerY, progressStartColor, progressEndColor);
            outShader.setLocalMatrix(matrix);
            mProgressPaint.setShader(outShader);

            path = new Path();
            //添加矩形
            path.addRect(0F, 0F, getWidth(), getHeight(), Path.Direction.CW);
            //添加圆
            path.addCircle(centerX, centerY, radius, Path.Direction.CCW);
        }

        //绘制矩形遮罩
        canvas.drawPath(path, mPathPaint);
        //绘制阴影
        canvas.drawCircle(centerX, centerY, mShadowRadius, mShadowPaint);
        //绘制外部圆弧
        canvas.drawArc(mProgressRectF, startAngle, curSweepAngle, false, mProgressPaint);
    }

    public interface OnSweepProgressListener {
        void onEnd();
    }
}
