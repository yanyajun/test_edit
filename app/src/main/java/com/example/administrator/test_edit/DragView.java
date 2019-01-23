package com.example.administrator.test_edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/1/29 0029.
 */

public class DragView extends View{
    private final String TAG = "DragView";
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
    private TimeLineEditor2.OnSeekBarChanged mListener;

    private int lastX;
    private int lastY;

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragView(Context context) {
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPointLeft = mValueLeft / mValueMax * width;
        mPointRight = mValueRight / mValueMax * width;

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
        // 获取当前触摸的绝对坐标
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 上一次离开时的坐标
                lastX = rawX;
                lastY = rawY;

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
                // 两次的偏移量
                int offsetX = rawX - lastX;
                int offsetY = rawY - lastY;

                if (mLeftBarTouched) {
                     ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                     layoutParams.width = getWidth() + offsetX;
                     setLayoutParams(layoutParams);
                } else if (mRightBarTouched) {

                } else if(mCenterTouched) {


                    moveView(offsetX, offsetY);
                }


                // 不断修改上次移动完成后坐标
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                mRightBarTouched = false;
                mLeftBarTouched = false;
                mCenterTouched = false;
                break;
            default:
                break;
        }
        return true;
    }

    private void moveView(int offsetX, int offsetY) {
         offsetLeftAndRight(offsetX);
        // offsetTopAndBottom(offsetY);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
