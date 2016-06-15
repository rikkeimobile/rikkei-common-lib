package rikkei.android.customview.lib.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import rikkei.android.customview.lib.R;

/**
 * Created by cuongvv on 19/05/2016.
 * <p/>
 * This is Background Drawable of FlatButton
 * <p/>
 * <p/>
 */
public class RkFlatDrawable extends Drawable implements Animatable, OnTouchListener {

    private int mRippleColor;                          //color of ripple in button
    private int mRippleAnimDuration;                   //time run ripple effect
    private Path mBackground;                          //to draw ripple effect
    private int mBackgroundColorRipple;                //background of ripple effect, has alpha change when ripple effect is running
    private int mBackgroundAnimDuration;               //anim time of ripple effect background with alpha change
    private RectF mBackgroundBounds;                   //area to draw ripple effect background
    private float mBackgroundAlphaPercent;             //current alpha percent of ripple effect background
    private float mRippleAlphaPercent;                 //current alpha percent of ripple effect
    private Interpolator mInterpolator;
    private Drawable mBackgroundDrawable;


    private long mStartTime;                                   //time begin animation
    private int mState = STATE_OUT;                            //current state

    /**
     * time delay to redraw
     */
    public static final long FRAME_DURATION = 1000 / 60;       //time delay to call draw()

    private static final int STATE_OUT = 0;                    //no anim no press
    private static final int STATE_PRESS = 1;                  //user pressed
    private static final int STATE_HOVER = 2;                  //after ripple anim finish and user pressing button
    private static final int STATE_RELEASE_ON_HOLD = 3;        //state: clicked before ripple anim finish
    private static final int STATE_RELEASE = 4;                //state: ripple animation finished
    private static int MAX_ALPHA_COLOR = 255;

    final float[] cornerRadius = new float[8];                 //corner radius of background

    private Paint mShaderPaint;
    private Paint mFillPaint;
    private RadialGradient mInShader;
    private Matrix mMatrix;

    private PointF mRipplePoint;                               //central cicle to draw ripple effect
    private float mRippleRadius;                               //current cicle radius of ripple effect
    private int mMaxRippleRadius;                              //max cicle radius of ripple effect

    private static final float[] GRADIENT_STOPS = new float[]{0f, 0.99f, 1f};        // draw ripple effect with gradient
    private static final float GRADIENT_RADIUS = 16;
    private static final int RELEASE_TIME = 100;

    private boolean mRunning = false;

    private RkFlatDrawable(Drawable backgroundDrawable, int backgroundAnimDuration,
                           int backgroundColor, int rippleAnimDuration, int rippleColor, Interpolator inInterpolator) {

        mBackgroundDrawable = backgroundDrawable;

        mBackgroundAnimDuration = backgroundAnimDuration;
        mBackgroundColorRipple = backgroundColor;


        mRippleAnimDuration = rippleAnimDuration;
        mRippleColor = rippleColor;

        mInterpolator = inInterpolator;

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);

        mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderPaint.setStyle(Paint.Style.FILL);

        mBackground = new Path();
        mBackgroundBounds = new RectF();

        mRipplePoint = new PointF();

        mMatrix = new Matrix();

        mInShader = new RadialGradient(0, 0, GRADIENT_RADIUS,
            new int[]{mRippleColor, mRippleColor, 0}, GRADIENT_STOPS,
            Shader.TileMode.CLAMP);
    }

    /**
     * event when bound of this drawable changed,<br>
     * use this event to change background bound of button
     *
     * @param rectBound Rect
     */
    @Override
    protected void onBoundsChange(Rect rectBound) {

        mBackgroundBounds.set(rectBound.left, rectBound.top, rectBound.right, rectBound.bottom);
        mBackground.reset();
        mBackground.addRoundRect(mBackgroundBounds, cornerRadius, Path.Direction.CW);

        mBackgroundDrawable.setBounds(rectBound);
    }

    /**
     * Redraw  ripple effect after call invalidate()
     *
     * @param canvas Canvas
     */
    @Override
    public void draw(Canvas canvas) {

        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.draw(canvas);
        }
        drawRippleEffect(canvas);
    }


    /**
     * Draw ripple effect with params changed on updateRipple function
     *
     * @param canvas Canvas
     */
    private void drawRippleEffect(Canvas canvas) {
        if (mState != STATE_OUT) {
//            Log.e("Background ALpha",mBackgroundAlphaPercent*MAX_ALPHA_COLOR+"");
            if (mBackgroundAlphaPercent > 0) {
                mFillPaint.setColor(mBackgroundColorRipple);
                mFillPaint.setAlpha(Math.round(MAX_ALPHA_COLOR * mBackgroundAlphaPercent));
                canvas.drawPath(mBackground, mFillPaint);
            }

            if (mRippleRadius > 0 && mRippleAlphaPercent > 0) {
                mShaderPaint.setAlpha(Math.round(MAX_ALPHA_COLOR * mRippleAlphaPercent));
                mShaderPaint.setShader(mInShader);
                canvas.drawPath(mBackground, mShaderPaint);
            }
        }
    }

    /**
     * get max radius in pixed of ripple effect after user touch
     *
     * @param x x position
     * @param y y position
     * @return max radius
     */
    private int getMaxRippleRadius(float x, float y) {
        float x1 = x < mBackgroundBounds.centerX() ? mBackgroundBounds.right : mBackgroundBounds.left;
        float y1 = y < mBackgroundBounds.centerY() ? mBackgroundBounds.bottom : mBackgroundBounds.top;

        return (int) Math.round(Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2))) + 2;
    }

    /**
     * handle touch event from button<br>
     * calculate to change state of ripple effect
     *
     * @param v     View
     * @param event MotionEvent
     * @return true if touch event be handled, false otherwise
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.v(RkRaisedDrawable.class.getSimpleName(), "touch: " + event.getAction() + " " + mState);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mState == STATE_OUT || mState == STATE_RELEASE) {

                    mMaxRippleRadius = getMaxRippleRadius(event.getX(), event.getY());

                    setRippleEffect(event.getX(), event.getY(), 0);
                    setRippleState(STATE_PRESS);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mState != STATE_OUT) {
                    if (mState == STATE_HOVER) {

                        setRippleEffect(mRipplePoint.x, mRipplePoint.y, 0);
                        setRippleState(STATE_RELEASE);

                    } else
                        setRippleState(STATE_RELEASE_ON_HOLD);
                }
                break;
        }
        return true;
    }

    /**
     * Set new state of ripple effect<br>
     * calculate to stop or start animation of effect
     *
     * @param state new state to change into
     */
    private void setRippleState(int state) {
        if (mState != state) {
            // Only change state to STATE_PRESS if current is STATE_OUT
            if (mState == STATE_OUT && state != STATE_PRESS)
                return;

//            Log.v(RkRaisedDrawable.class.getSimpleName(), "state: " + mState + " " + state);

            mState = state;

            if (mState == STATE_OUT || mState == STATE_HOVER)
                stop();
            else
                start();
        }
    }

    /**
     * Change params of ripple effect
     *
     * @param x      x position to draw ripple effect
     * @param y      x position to draw ripple effect
     * @param radius radius of cicle to draw ripple effect
     * @return true if new state diff with current state
     */
    private boolean setRippleEffect(float x, float y, float radius) {
        if (mRipplePoint.x != x || mRipplePoint.y != y || mRippleRadius != radius) {
            mRipplePoint.set(x, y);
            mRippleRadius = radius;
            radius = mRippleRadius / GRADIENT_RADIUS;
            mMatrix.reset();
            mMatrix.postTranslate(x, y);
            mMatrix.postScale(radius, radius, x, y);
            mInShader.setLocalMatrix(mMatrix);

            return true;
        }

        return false;
    }

    /**
     * check if animation running
     *
     * @return true if animation is running, false otherwise
     */
    @Override
    public boolean isRunning() {
        return mState != STATE_OUT && mState != STATE_HOVER && mRunning;
    }

    /**
     * assign start time of animation
     */
    private void resetAnimation() {
        mStartTime = SystemClock.uptimeMillis();
    }

    /**
     * start new animation
     */
    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        resetAnimation();
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();
    }

    /**
     * stop animation
     */
    @Override
    public void stop() {
        mRunning = false;
        unscheduleSelf(mUpdater);
        invalidateSelf();
    }

    /**
     * schedule to run Runnable after delay time
     *
     * @param what Runnable to run
     * @param when delaytime to run
     */
    @Override
    public void scheduleSelf(Runnable what, long when) {
        mRunning = true;
        super.scheduleSelf(what, when);
    }

    /**
     * Runnable to update state of ripple effect <br>
     * and redraw this button
     */
    private final Runnable mUpdater = new Runnable() {

        @Override
        public void run() {
            updateRipple();
        }
    };

    /**
     * update params to draw ripple effect
     */
    private void updateRipple() {

        if (mState != STATE_RELEASE) {

            /*calculate to change alpha of background and shadow*/
            float backgroundProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / mBackgroundAnimDuration);
            mBackgroundAlphaPercent = backgroundProgress * Color.alpha(mBackgroundColorRipple) / 255f;

            /*calculate to change alpha of ripple */
            float touchProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / mRippleAnimDuration);
            mRippleAlphaPercent = backgroundProgress * Color.alpha(mRippleColor) / 255f;

            /*set new state of ripple effect*/
            setRippleEffect(mRipplePoint.x, mRipplePoint.y, mMaxRippleRadius * mInterpolator.getInterpolation(touchProgress));

            /*check if animation need to stop or continue*/
            if (backgroundProgress == 1f && touchProgress == 1f) {
                mStartTime = SystemClock.uptimeMillis();
                setRippleState(mState == STATE_PRESS ? STATE_HOVER : STATE_RELEASE);
            }
        } else {
            /* change param to run effect after event user do ACTION_UP or CANCEL */
            float backgroundProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / RELEASE_TIME);
            mBackgroundAlphaPercent = (1 - backgroundProgress) * Color.alpha(mBackgroundColorRipple) / 255f;
//          mBackgroundSdAlphaPercent = 1f - mInterpolator.getInterpolation(backgroundProgress);

            mRippleAlphaPercent = 0;
//          mBackgroundAlphaPercent = mBackgroundSdAlphaPercent * Color.alpha(mBackgroundColorRipple) / 255f;

            if (backgroundProgress == 1f) {
                setRippleState(STATE_OUT);
            }
        }

        if (isRunning()) {
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        }

        invalidateSelf();
    }

    /**
     * Calculate time finish ripple effect to dispatch click event on button
     *
     * @return delaytime in milisecond
     */
    public long getClickDelayTime() {
        if (mState == STATE_RELEASE_ON_HOLD)
            return mRippleAnimDuration
                - (SystemClock.uptimeMillis() - mStartTime) + RELEASE_TIME;
        else if (mState == STATE_RELEASE)
            return RELEASE_TIME - (SystemClock.uptimeMillis() - mStartTime);
        return -1;
    }

    /**
     * Cancel all effect on button
     */
    public void cancel() {
        setRippleState(STATE_OUT);
    }


    /**
     * set alpha to this button
     *
     * @param alpha opacity
     */
    @Override
    public void setAlpha(int alpha) {
        MAX_ALPHA_COLOR = alpha;
    }

    /**
     * set colorfilter to paints
     *
     * @param filter ColorFilter
     */
    @Override
    public void setColorFilter(ColorFilter filter) {
        mFillPaint.setColorFilter(filter);
        mShaderPaint.setColorFilter(filter);
    }

    /**
     * get Opacity
     *
     * @return a format that supports translucency
     */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * This class to support build new RippbleDrawable of FlatButton with parameters when user declare button from xml layout<br>
     * setup with default values if user was not declared
     */
    public static class Builder {

        private Drawable mBackgroundDrawable;

        private int mBackgroundAnimDuration = 300;
        private int mBackgroundColor;

        private int mRippleAnimDuration = 500;
        private int mRippleColor;

        private Interpolator mInterpolator;

        public Builder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RkRaisedButton, defStyleAttr, defStyleRes);
            int resId;

            backgroundColor(a.getColor(R.styleable.RkRaisedButton_rk_background_pressed, ContextCompat.getColor(context, R.color.bg_flat_color)));
            backgroundAnimDuration(a.getInteger(R.styleable.RkRaisedButton_rk_backgroundAnimDuration, mBackgroundAnimDuration));

            rippleColor(a.getColor(R.styleable.RkRaisedButton_rk_ripple_color, ContextCompat.getColor(context, R.color.ripple_raised_color)));
            rippleAnimDuration(a.getInteger(R.styleable.RkRaisedButton_rk_ripple_anim_duration, mRippleAnimDuration));
            if ((resId = a.getResourceId(R.styleable.RkRaisedButton_rk_interpolator, 0)) != 0) {
                inInterpolator(AnimationUtils.loadInterpolator(context, resId));
            }

            a.recycle();
        }

        public RkFlatDrawable build() {
            if (mInterpolator == null)
                mInterpolator = new AccelerateDecelerateInterpolator();

            return new RkFlatDrawable(mBackgroundDrawable, mBackgroundAnimDuration, mBackgroundColor, mRippleAnimDuration, mRippleColor, mInterpolator);
        }


        public Builder backgroundAnimDuration(int duration) {
            mBackgroundAnimDuration = duration;
            return this;
        }

        public Builder backgroundColor(int color) {
            mBackgroundColor = color;
            return this;
        }

        public Builder rippleAnimDuration(int duration) {
            mRippleAnimDuration = duration;
            return this;
        }

        public Builder rippleColor(int color) {
            mRippleColor = color;
            return this;
        }

        public Builder inInterpolator(Interpolator interpolator) {
            mInterpolator = interpolator;
            return this;
        }

        public Builder backgroundDrawable(Drawable background) {
            mBackgroundDrawable = background;
            return this;
        }
    }
}
