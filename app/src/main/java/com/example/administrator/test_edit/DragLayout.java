package com.example.administrator.test_edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class DragLayout extends RelativeLayout {
    private final String TAG = "DragLayout";
    private Context mContext;
    private OnDragListener mListener;
    private int mDownX;
    private int mDownY;
    private int screenWidth;
    private int screenHeight;
    private int height;
    private int width;

    private Paint mPaint;
    private Rect mCenterRect;
    private Rect mTopLeftRect;
    private Rect mTopRightRect;
    private Rect mBottomRightRect;
    private PointF mMovePointF = new PointF();

    private int margin;
    private int center_line_width;

    private boolean notMove = false;

    private Bitmap btn1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_editer_sticker_frame_delete);
    private Bitmap btn2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_editer_sticker_frame_transform);
    private Bitmap btn3 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_editer_sticker_frame_enlarge);

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLayout(Context context) {
        super(context);
        init(context);
    }

    public void setMainSize(int w, int h) {
        screenWidth = w;
        screenHeight = h;
    }

    private void init(Context context) {
        mContext = context;

        margin = dip2px(12);
        center_line_width = dip2px(2);

        mPaint = new Paint();
        mPaint.setColor(0xfff0ff05);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(center_line_width);
        mPaint.setStyle(Paint.Style.STROKE);

        mCenterRect = new Rect();
        mTopLeftRect = new Rect();
        mTopRightRect = new Rect();
        mBottomRightRect = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = getMeasuredHeight();
        width = getMeasuredWidth();

        mCenterRect.set(margin, margin, width - margin, height - margin);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getRawX();
                mDownY = (int) event.getRawY();

                if(mTopLeftRect.contains((int)event.getX(), (int)event.getY())) {
                    Log.e(TAG, "btn1");
                    notMove = true;
                    if(mListener != null)
                        mListener.topLeftTouched();
                    return false;
                } else if(mTopRightRect.contains((int)event.getX(), (int)event.getY())) {
                    Log.e(TAG, "btn2");
                    notMove = true;
                    if(mListener != null)
                        mListener.topRightTouched();
                    return false;
                } else if(mBottomRightRect.contains((int)event.getX(), (int)event.getY())) {
                    Log.e(TAG, "btn3");
                    notMove = true;
                    if(mListener != null)
                        mListener.bottomRightTouched();
                    return false;
                } else {
                    notMove = false;
                    if(mListener != null)
                        mListener.centerTouched();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(notMove)
                    break;
                int dx = (int)event.getRawX() - mDownX;
                int dy = (int)event.getRawY() - mDownY;
                mDownX = (int) event.getRawX();
                mDownY = (int) event.getRawY();

                Log.i(TAG, "dx: " + dx +" dy: " + dy);
                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                if(left < 0){
                    left = 0;
                    right = left + getWidth();
                }
                if(right > screenWidth){
                    right = screenWidth;
                    left = right - getWidth();
                }
                if(top < 0){
                    top = 0;
                    bottom = top + getHeight();
                }
                if(bottom > screenHeight){
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                mMovePointF.set(dx, dy);
                if(mListener != null)
                    mListener.dragMove(mMovePointF);
                layout(left, top, right, bottom);

                Log.i(TAG, "position: " + getLeft() +", " + getTop() + ", " + getRight() + ", " + getBottom());
                Log.i(TAG, "position: " + left +", " + top + ", " + right + ", " + bottom);

                break;
            case MotionEvent.ACTION_UP:


                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(mCenterRect, mPaint);

        canvas.drawBitmap(btn1, 0, 0, mPaint);
        mTopLeftRect.set(0 , 0, btn1.getWidth(), btn1.getHeight());

        canvas.drawBitmap(btn2, width - btn2.getWidth(), 0, mPaint);
        mTopRightRect.set(width - btn2.getWidth(), 0, width, btn2.getHeight());

        canvas.drawBitmap(btn3, width - btn3.getWidth(), height - btn3.getHeight(), mPaint);
        mBottomRightRect.set(width - btn3.getWidth(), height - btn3.getHeight(), width, height);

        super.onDraw(canvas);
    }

    private int ifIsOnView(Bitmap view, Point point) {
        int left = getLeft();
        int right = getRight();
        int bottom = getBottom();
        int top = getTop();

        if(point.x < view.getWidth() && point.y < view.getHeight()) {
            return 1;
        } else if(point.x > right - left - view.getWidth() && point.y < view.getHeight()) {
            return 2;
        } else if(point.x > right - left - view.getWidth() && point.y > bottom - top - view.getHeight()) {
            return 3;
        }
        return 0;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOnDragListener(OnDragListener listener) {
        mListener = listener;
    }
    public interface OnDragListener {
        void topLeftTouched();
        void topRightTouched();
        void centerTouched();
        void bottomRightTouched();
        void dragMove(PointF point);
    }
}
