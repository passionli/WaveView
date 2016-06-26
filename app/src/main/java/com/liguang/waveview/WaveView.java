package com.liguang.waveview;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sample:
 *
 * Created by liguang on 2016/6/24.
 * @Author passionli@vip.qq.com
 */
public class WaveView extends View {
    private static final String TAG = WaveView.class.getSimpleName();
    private Paint mPaint = new Paint();
    private List<Circle> mCircles = new ArrayList<Circle>();
    private int mX;
    private int mY;
    private double mMin;
    // The time interpolator to be used if none is set on the animation
    private static final TimeInterpolator sDefaultInterpolator =
            new AccelerateDecelerateInterpolator();
    private TimeInterpolator mInterpolator = sDefaultInterpolator;
    private static final long sDefaultDelay = 1000;
    private long mDelay = sDefaultDelay;
    private boolean mStarted;
    private Runnable mProduceCircleRunnable;
    private long mDuration = 3000;
    private int mColor = Color.BLUE;
    private Paint.Style mStyle = Paint.Style.FILL;

    /**
     * Like {@link android.animation.ValueAnimator#setInterpolator(TimeInterpolator) ValueAnimator.setInterpolator(TimeInterpolator)}
     * @param interpolator
     */
    public void setInterpolator(TimeInterpolator interpolator){
        if (interpolator != null){
            mInterpolator = interpolator;
        }else {
            mInterpolator = new LinearInterpolator();
        }
    }

    /**
     * Like
     * {@link android.view.animation.LayoutAnimationController#setDelay(float) LayoutAnimationController.setDelay(float)}
     * @param delay for every circle
     */
    public void setDelay(long delay){
        this.mDelay = delay;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mDelay = (long) typedArray.getFloat(R.styleable.WaveView_wave_delay, sDefaultDelay);
        mColor = typedArray.getColor(R.styleable.WaveView_wave_color,Color.BLUE);
        mDuration = (long) typedArray.getFloat(R.styleable.WaveView_wave_duration, 3000);
        if (typedArray.hasValue(R.styleable.WaveView_wave_style))
            mStyle = Paint.Style.valueOf(typedArray.getString(R.styleable.WaveView_wave_style));
        final int resID = typedArray.getResourceId(
                R.styleable.WaveView_wave_interpolator,
                android.R.anim.accelerate_decelerate_interpolator); // default to linear interpolator
        if (resID > 0) {
            setInterpolator(AnimationUtils.loadInterpolator(context, resID));
        }

        typedArray.recycle();
        init();
    }

    private void init() {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mMin = Math.min(mX, mY);
        mPaint.setColor(mColor);
        mPaint.setStyle(mStyle);
    }

    public void start(){
        if (mStarted){
            return;
        }

        mStarted = true;

        mProduceCircleRunnable = new Runnable() {
            @Override
            public void run() {
                mCircles.add(new Circle());
                invalidate();
                postDelayed(this, mDelay);
            }
        };
        post(mProduceCircleRunnable);
    }

    public void cancel(){
        mStarted = false;
        mCircles.clear();
        removeCallbacks(mProduceCircleRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"size = " + mCircles.size());
        Iterator<Circle> iterator = mCircles.iterator();
        while (iterator.hasNext()){
            Circle circle = iterator.next();
            circle.Compute();
            if (circle.isExpired()){
                iterator.remove();
            }else{
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(mX, mY, circle.getRadius(),mPaint);
            }
        }

        if (!mCircles.isEmpty())
            invalidate();
    }

    public void setCircleColor(int color) {
        mColor = color;
    }

    public boolean isRunning() {
        return mStarted == true;
    }

    public void setStyle(Paint.Style style) {
        this.mStyle = style;
    }

    private class  Circle{
        float radius = 0;
        int alpha = 255;
        long startTime = 0;
        private boolean isExpired = false;

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        public void Compute() {
            long now = SystemClock.uptimeMillis();
            if (startTime == 0){
                startTime = now;
            }

            if (now - startTime >= mDuration){
                isExpired = true;
            }

            float radio = WaveView.this.mInterpolator.getInterpolation((float) (now - startTime)/mDuration);
            if (radio > 1){
                radio = 1;
            }
            if (radio < 0){
                radio = 0;
            }
            if (radio == 0 || radio == 1){
                startTime = now;
            }
            radius = (float) (mMin * radio);
            alpha = (int) (255 * (1-radio));
        }

        public boolean isExpired() {
            return isExpired;
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "radius=" + radius +
                    ", alpha=" + alpha +
                    ", startTime=" + startTime +
                    ", isExpired=" + isExpired +
                    '}';
        }
    }
}
