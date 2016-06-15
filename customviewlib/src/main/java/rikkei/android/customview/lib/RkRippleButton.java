
/*
 * Copyright (C) 2016 Rikkeisoft Co., Ltd.
 */

package rikkei.android.customview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tuyenpx on 16/05/2016.
 * <p/>
 * This class support to custom RippleButton ,Button have ripple effect .
 * <p/>
 * <p/>
 * - ripple_color : color of ripple in button , default is #ffeeeeee
 * - ripple_alpha : to set Alpha value(0 - 255) for color ripple .default value is 47
 * - ripple_duration : time in millisecond for ripple effect ,default is 500.
 */

@SuppressWarnings("UnusedDeclaration")
public class RkRippleButton extends Button {

    private int mWidth;
    private int mHeight;
    private int mRippleDuration;
    private int mRippleRadius;
    private float pointX;
    private float pointY;

    private Paint mPaint;
    private Paint mRipplePaint;
    private RectF mRectF;
    private Path mPath;
    private Timer mTimer;
    private TimerTask mTask;
    private MyHandler mHandler;
    private int mRippleColor;
    private int mRippleAlpha;


    /**
     * @return color of ripple
     */
    public int getRippleColor() {
        return mRippleColor;
    }

    /**
     * @param mRippleColor : color of ripple need to set
     */

    public void setRippleColor(int mRippleColor) {
        this.mRippleColor = mRippleColor;
        invalidate();
        requestLayout();
    }

    /**
     * @return : return ripple alpha value .
     */

    public int getRippleAlpha() {
        return mRippleAlpha;
    }

    /**
     * @param mRippleAlpha : set alpha value in setAlpha for ripple .
     */
    public void setRippleAlpha(int mRippleAlpha) {
        this.mRippleAlpha = mRippleAlpha;
        invalidate();
        requestLayout();
    }


    /**
     * @return : get ripple duration .
     */

    public int getRippleDuration() {
        return mRippleDuration;
    }

    /**
     * @param mRippleDuration : time in milli second of ripple effect .
     */

    public void setRippleDuration(int mRippleDuration) {
        this.mRippleDuration = mRippleDuration;
        invalidate();
        requestLayout();
    }


    public RkRippleButton(Context context) {
        super(context);
        if (isInEditMode()) {
            return;
        }
        mHandler = new MyHandler(this);
    }

    public RkRippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init(context, attrs);
        mHandler = new MyHandler(this);
    }

    public RkRippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        init(context, attrs);
        mHandler = new MyHandler(this);
    }

    private final static int RIPPLE_ALPHA = 47;
    private final static int MESSAGE_DRAW_COMPLETE = 101;


    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RkRippleButton);
        mRippleColor = typedArray.getColor(R.styleable.RkRippleButton_rikkei_ripple_color,
                ContextCompat.getColor(context, R.color.ripple_color));
        mRippleAlpha = typedArray.getInteger(R.styleable.RkRippleButton_rikkei_ripple_alpha,
                RIPPLE_ALPHA);
        if (mRippleAlpha < 0 && mRippleAlpha > 255) {
            throw new AndroidRuntimeException("Ripple AlPha Must Be from 0 to 255");
        }
        mRippleDuration = typedArray.getInteger(R.styleable.RkRippleButton_rikkei_ripple_duration,
                500);
        typedArray.recycle();
        mRipplePaint = new Paint();
        mRipplePaint.setColor(mRippleColor);
        mRipplePaint.setAlpha(mRippleAlpha);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setAntiAlias(true);
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.transparent));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        this.setWillNotDraw(false);
        this.setDrawingCacheEnabled(true);
        this.setClickable(true);

        mPath = new Path();
        mRectF = new RectF();
        pointY = pointX = -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint == null) {
            return;
        }
        // draw rectangle button
        mRectF.set(0, 0, mWidth, mHeight);
        // canvas.drawRoundRect(mRectF, 0, 0, mPaint);
        drawFillCircle(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isEnabled()) {
                isFinish = false;
                pointX = event.getX();
                pointY = event.getY();
                onStartDrawRipple();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean isSaveEnabled() {
        return super.isSaveEnabled();
    }

    /**
     * Draw ripple effect
     */

    private boolean isFinish = false;

    private void drawFillCircle(Canvas canvas) {
        if (canvas != null && pointX >= 0 && pointY >= 0 && !isFinish) {
            int rbX = canvas.getWidth();
            int rbY = canvas.getHeight();
            float longDis = Math.max(pointX, pointY);
            longDis = Math.max(longDis, Math.abs(rbX - pointX));
            longDis = Math.max(longDis, Math.abs(rbY - pointY));
            if (mRippleRadius > longDis) {
                isFinish = true;
                onCompleteDrawRipple();
                return;
            }
            final float drawSpeed = longDis / mRippleDuration * 35;
            mRippleRadius += drawSpeed;

            canvas.save();
            mPath.reset();
            canvas.clipPath(mPath);
            mPath.addRoundRect(mRectF, 0, 0, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.REPLACE);
            canvas.drawCircle(pointX, pointY, mRippleRadius, mRipplePaint);
            canvas.restore();
        }
    }

    /**
     * Start draw ripple effect
     */
    private void onStartDrawRipple() {
        onCompleteDrawRipple();
        mTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MESSAGE_DRAW_COMPLETE);
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 30);
    }

    /**
     * Stop draw ripple effect
     */
    private void onCompleteDrawRipple() {
        mHandler.removeMessages(MESSAGE_DRAW_COMPLETE);
        if (mTimer != null) {
            if (mTask != null) {
                mTask.cancel();
            }
            mTimer.cancel();
        }
        mRippleRadius = 0;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RkRippleButton> mWeakReference;

        public MyHandler(RkRippleButton rkRippleButton) {
            mWeakReference = new WeakReference<>(rkRippleButton);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_DRAW_COMPLETE) {
                RkRippleButton rkRippleButton = mWeakReference.get();
                if (rkRippleButton != null) {
                    rkRippleButton.invalidate();
                }
            }

        }
    }

}
