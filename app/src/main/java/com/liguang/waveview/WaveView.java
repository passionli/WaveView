package com.liguang.waveview;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/24.
 */
public class WaveView extends View {
    private static final String TAG = WaveView.class.getSimpleName();
    private Paint mPaint = new Paint();
    private List<Circle> mCircles = new ArrayList<Circle>();
    private int mX;
    private int mY;
    private double mMin;
    private TimeInterpolator mTimeInterpolator = new LinearInterpolator();
    private long mInterval = 250;
    private boolean mStarted;
    private Runnable mProduceCircleRunnable;
    private long mDuration = 5000;
    private int mColor = Color.BLUE;

    public void setTimeInterpolator(TimeInterpolator interpolator){
        this.mTimeInterpolator = interpolator;
    }

    public void setInterval(long interval){
        this.mInterval = interval;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mMin = Math.min(mX, mY);
        mPaint.setColor(mColor);
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
                postDelayed(this, mInterval);
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
        Iterator<Circle> iterator = mCircles.iterator();
        while (iterator.hasNext()){
            Circle circle = iterator.next();
            if (isRunning())
                circle.Compute();
            mPaint.setAlpha(circle.getAlpha());
            canvas.drawCircle(mX, mY, circle.getRadius(),mPaint);
            if (circle.isExpired()){
                iterator.remove();
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

            if (now - startTime > mDuration){
                isExpired = true;
                return;
            }

            float radio = WaveView.this.mTimeInterpolator.getInterpolation((float) (now - startTime)/mDuration);
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
