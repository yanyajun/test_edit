package com.example.administrator.test_edit;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yyj on 2018/1/22.
 */

public class TimeLineEditor2 extends View {
    private final String TAG = "TimeLineEditor2";
    private Context mContext;
    private Paint mPaint;
    private Bitmap mThumbBarLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.thumb_l);
    private Bitmap mThumbBarRight = BitmapFactory.decodeResource(getResources(), R.mipmap.thumb_r);
    private RectF mThumbBarRectRight;
    private RectF mThumbBarRectLeft;
    private RectF mCenterRect;
    float mPointLeft, mPointRight;
    private float mValueLeft = 0, mValueRight = 100, mValueMax = 100;
    private boolean mLeftBarTouched = false, mRightBarTouched = false, mCenterTouched = false;
    private int width, height;
    private OnSeekBarChanged mListener;

    // move
    private float lastX;
    private float lastY;

    public TimeLineEditor2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TimeLineEditor2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeLineEditor2(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mThumbBarRectRight = new RectF();
        mThumbBarRectLeft = new RectF();
        mCenterRect = new RectF();
    }

    public void setOnSeekBarChangedListener(OnSeekBarChanged mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getWidth();
        height = getHeight();
        mPointLeft = 0;
        mPointRight = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画背景
        mPaint.setColor(0xfff0ff05);
        mPaint.setStrokeWidth(dip2px(getContext(), 6));
        mCenterRect.set(mPointLeft, 0, mPointRight, getHeight());
        canvas.drawRect(mCenterRect, mPaint);

        //画滑块
        mThumbBarRectLeft.set(mPointLeft, 0, mPointLeft + mThumbBarLeft.getWidth(), getHeight());
        mThumbBarRectRight.set(mPointRight - mThumbBarRight.getWidth(), 0, mPointRight, getHeight());
        canvas.drawBitmap(mThumbBarLeft, null, mThumbBarRectLeft, mPaint);
        canvas.drawBitmap(mThumbBarRight, null, mThumbBarRectRight, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 上一次离开时的坐标
                lastX = event.getX();
                lastY = event.getY();

                if (mThumbBarRectLeft.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "left thumb bar touched");
                    mLeftBarTouched = true;
                } else if (mThumbBarRectRight.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "right thumb bar touched");
                    mRightBarTouched = true;
                } else if(mCenterRect.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "center touched");
                    mCenterTouched = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 根据滑块水平滑动的x坐标值计算比例
                float x_span = event.getX() / width;
                if(x_span < 0)
                    x_span = 0;
                else if(x_span > 1)
                    x_span = 1;
                float percent_value = mValueMax * x_span;

                if (mLeftBarTouched) {
                    if (percent_value < mValueRight) {
                        if (mListener != null) {
                            mListener.onLeftValueChange(percent_value);
                        }
                        mValueLeft = percent_value;
                        mPointLeft = mValueLeft / mValueMax * width;
                        invalidate();
                    }
                } else if (mRightBarTouched) {
                    if (percent_value > mValueLeft) {
                        if (mListener != null) {
                            mListener.onRightValueChange(percent_value);
                        }
                        mValueRight = percent_value;
                        mPointRight = mValueRight / mValueMax * width;
                        invalidate();
                    }
                } else if(mCenterTouched) {
                    // 两次的偏移量
                    float offsetX = event.getX() - lastX;
                    float offsetY = event.getY() - lastY;
                    lastX = event.getX();
                    lastY = event.getY();

                    Log.i(TAG, "X: " + event.getX() + " lastX: " + lastX + " offsetX: " + offsetX);
//                    if(mPointRight != width)
//                        mPointLeft = mPointLeft + offsetX;
//                    Log.i(TAG, "mPointLeft: " + mPointLeft);
//                    if(mPointLeft < 0) {
//                        mPointLeft = 0;
//                    }
//                    if(mPointLeft != 0)
//                        mPointRight = mPointRight + offsetX;
//                    Log.i(TAG, "mPointRight: " + mPointRight);
//                    if(mPointRight > width) {
//                        mPointRight = width;
//                    }
//                    invalidate();

                    mPointLeft = mPointLeft + offsetX;
                    if(mPointLeft <= 0) {
                        mPointLeft = 0;
                    } else {
                        mPointRight = mPointRight + offsetX;
                        if(mPointRight >= width) {
                            mPointRight = width;
                            mPointLeft = mPointLeft - offsetX;
                        }
                    }
                    Log.i(TAG, "mPointLeft: " + mPointLeft);
                    Log.i(TAG, "mPointRight: " + mPointRight);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mRightBarTouched = false;
                mLeftBarTouched = false;
                mCenterTouched = false;
                break;
        }
        return true;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnSeekBarChanged {
        void onLeftValueChange(float var);
        void onRightValueChange(float var);
        void onCenterTouched();
    }
}
