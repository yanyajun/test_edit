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
 * Created by Administrator on 2018/1/29 0029.
 */
public class CutAudioView extends View{
    public static String TAG = CutAudioView.class.getCanonicalName();
    private Context mContext;

    private Paint mPaint;
    private Bitmap mThumbBarLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.thumb_l);
    private Bitmap mThumbBarRight = BitmapFactory.decodeResource(getResources(), R.mipmap.thumb_r);
    private RectF mThumbBarRightRect, mThumbBarLeftRect, mCenterRect;
    private float mLeftPosintion, mRightPosintion, mLeftToRight, mPositonSpan;
    private boolean mLeftBarTouched = false, mRightBarTouched = false, mCenterTouched = false;
    private int width, height, center_width, border_width, center_out_width;
    private PointF mStartPosition = new PointF(0,0);
    private OnSeekBarChanged mListener;
    private int mMaxDuration, mMinDuration = 0;

    public CutAudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mThumbBarRightRect = new RectF();
        mThumbBarLeftRect = new RectF();
        mCenterRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        border_width = dip2px(getContext(), 6);
        center_out_width = dip2px(getContext(), 4);
        width = getWidth();
        height = getHeight();
        center_width = width - mThumbBarLeft.getWidth() - mThumbBarRight.getWidth();

        mLeftPosintion = 0;
        mRightPosintion = width;
        mLeftToRight = mRightPosintion - mLeftPosintion;

        if(mMaxDuration != 0)
            mPositonSpan = (float) mMinDuration / mMaxDuration * center_width;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //画矩形框超出的背景
        mPaint.setColor(0xff222222);
        mPaint.setStrokeWidth(center_out_width);
        canvas.drawRect(0, 0, width, height, mPaint);

        //画滑块
        mThumbBarLeftRect.set(mLeftPosintion, 0, mLeftPosintion + mThumbBarLeft.getWidth(), getHeight());
        mThumbBarRightRect.set(mRightPosintion - mThumbBarRight.getWidth(), 0, mRightPosintion, getHeight());
        canvas.drawBitmap(mThumbBarLeft, null, mThumbBarLeftRect, mPaint);
        canvas.drawBitmap(mThumbBarRight, null, mThumbBarRightRect, mPaint);

        //画背景
        mPaint.setColor(0xfff0ff05);
        mPaint.setStrokeWidth(border_width);
        mCenterRect.set(mLeftPosintion + mThumbBarLeft.getWidth(), 0, mRightPosintion - mThumbBarRight.getWidth(), getHeight());
        canvas.drawRect(mCenterRect, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                mStartPosition.x = event.getX();

                if (mThumbBarLeftRect.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "left thumb bar touched");
                    mLeftBarTouched = true;
                } else if (mThumbBarRightRect.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "right thumb bar touched");
                    mRightBarTouched = true;
                } else if(mCenterRect.contains(event.getX(), event.getY())) {
                    Log.i(TAG, "center touched");
                    mCenterTouched = true;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (mLeftBarTouched) {
                    mLeftPosintion += event.getX() - mStartPosition.x;
                    Log.i(TAG, "mPositonSpan: " + mPositonSpan);

                    if(mLeftPosintion < 0) {
                        mLeftPosintion = 0;
                    } else if(mLeftPosintion + mThumbBarLeft.getWidth() + border_width + mThumbBarRight.getWidth() + mPositonSpan > mRightPosintion) {
                        mLeftPosintion = mRightPosintion - mThumbBarRight.getWidth() - border_width - mThumbBarLeft.getWidth() - mPositonSpan;
                    }
                    this.postInvalidate();

                    if (mListener != null) {
                        mListener.onLeftValueChange((mCenterRect.left - mThumbBarLeft.getWidth()) / center_width);
                    }
                    mStartPosition.x = event.getX();
                } else if (mRightBarTouched) {
                    mRightPosintion += event.getX() - mStartPosition.x;

                    if(mRightPosintion > this.getWidth()) {
                        mRightPosintion = this.getWidth();
                    } else if(mRightPosintion - mThumbBarRight.getWidth() - border_width - mThumbBarLeft.getWidth() - mPositonSpan < mLeftPosintion) {
                        mRightPosintion = mLeftPosintion + mThumbBarLeft.getWidth() + border_width + mThumbBarRight.getWidth() + mPositonSpan;
                    }
                    this.postInvalidate();

                    if (mListener != null) {
                        mListener.onRightValueChange((mCenterRect.right - mThumbBarRight.getWidth()) / center_width);
                    }
                    mStartPosition.x = event.getX();

                } else if(mCenterTouched) {
                    float offset = event.getX() - mStartPosition.x;
                    mLeftPosintion += offset;
                    mRightPosintion += offset;

                    if(mLeftPosintion >= 0 && mRightPosintion <= this.getWidth()) {
                        mLeftToRight = mRightPosintion - mLeftPosintion;
                    }

                    if(mLeftPosintion <= 0) {
                        mLeftPosintion = 0;
                        mRightPosintion = mLeftPosintion + mLeftToRight;
                    }
                    if(mRightPosintion >= this.getWidth()) {
                        mRightPosintion = this.getWidth();
                        mLeftPosintion = mRightPosintion - mLeftToRight;
                    }
                    this.postInvalidate();

                    if(mListener != null)
                        mListener.onCenterTouched((mCenterRect.left - mThumbBarLeft.getWidth()) / center_width, (mCenterRect.right - mThumbBarRight.getWidth()) / center_width);

                    mStartPosition.x = event.getX();
                }
                break;

            case MotionEvent.ACTION_UP:
                mRightBarTouched = false;
                mLeftBarTouched = false;
                mCenterTouched = false;
                break;
            default:
        }
        return true;
    }

    public int getMaxDuration() {
        return mMaxDuration;
    }

    public void setMaxDuration(int mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
    }

    public int getMinDuration() {
        return mMinDuration;
    }

    public void setMinDuration(int mMinDuration) {
        this.mMinDuration = mMinDuration;
    }

    public void setOnSeekBarChangedListener(OnSeekBarChanged mListener) {
        this.mListener = mListener;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnSeekBarChanged {
        void onLeftValueChange(float var);
        void onRightValueChange(float var);
        void onCenterTouched(float left, float right);
    }

}
