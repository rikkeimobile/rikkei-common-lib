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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import rikkei.android.customview.lib.R;
import rikkei.android.customview.lib.util.Utils;

/**
 * Created by cuongvv on 19/05/2016.
 * <p/>
 * This is Background Drawable of RaisedButton ,Button has raised effect.<br>
 * this drawable catch touch events from button to draw raised effect
 * <p/>
 * <p/>
 */
public class RkRaisedDrawable extends Drawable implements Animatable, OnTouchListener {

    private int mRippleColor;                          //color of ripple in button
    private int mRippleAnimDuration;                   //time run ripple effect
    private Drawable mBackgroundShadow;                //background for raised effect, layer 1 - behind
    private Drawable mBackgroundDrawable;              //background of button, layer 2 - middle
    private Path mBackground;                          //to draw ripple effect, layer 3 - front
    private int mBackgroundColorRipple;                //background of ripple effect, has alpha change when ripple effect is running
    private int mBackgroundAnimDuration;               //anim time of ripple effect background with alpha change
    private RectF mBackgroundBounds;                   //area to draw ripple effect background
    private float mBackgroundAlphaPercent;             //current alpha percent of ripple effect background
    private float mBackgroundSdAlphaPercent;           //current alpha percent of raised effect background
    private float mRippleAlphaPercent;                 //current alpha percent of ripple effect
    private Interpolator mInterpolator;


    private long mStartTime;                                   //time begin animation
    private int mState = STATE_OUT;                            //current state

    public static final long FRAME_DURATION = 1000 / 60;       //time delay to call draw()

    private static final int STATE_OUT = 0;                    //no anim no press
    private static final int STATE_PRESS = 1;                  //user pressed
    private static final int STATE_HOVER = 2;                  //after ripple anim finish and user pressing button
    private static final int STATE_RELEASE_ON_HOLD = 3;        //state: clicked before ripple anim finish
    private static final int STATE_RELEASE = 4;                //state: ripple animation finished
    private static int MAX_ALPHA_COLOR = 255;


    final int left;                                            //padding left of background
    final int top;                                             //padding top of background
    final int right;                                           //padding right of background
    final int bottom;                                          //padding bottom of background
    final float[] cornerRadius = new float[8];                 //padding left of background

    private Paint mShaderPaint;
    private Paint mFillPaint;
    private RadialGradient mInShader;
    private Matrix mMatrix;

    private PointF mRipplePoint;                               //central cicle to draw ripple effect
    private float mRippleRadius;                               //current cicle radius of ripple effect
    private int mMaxRippleRadius;                              //max cicle radius of ripple effect

    private static final float[] GRADIENT_STOPS = new float[]{0f, 0.99f, 1f};        // draw ripple effect with gradient
    private static final float GRADIENT_RADIUS = 16;
    private static final int RELEASE_TIME=200;
    private static final int ALPHA_SHADOW=30;

    private boolean mRunning = false;

    private RkRaisedDrawable(Drawable backgroundDrawable, Drawable backgroundShadow, int backgroundAnimDuration,
                             int backgroundColor, int rippleAnimDuration, int rippleColor, Interpolator inInterpolator,
                             int topLeftCornerRadius, int topRightCornerRadius, int bottomRightCornerRadius,
                             int bottomLeftCornerRadius, int left, int top, int right, int bottom) {
        setBackgroundDrawable(backgroundDrawable);
        mBackgroundAnimDuration = backgroundAnimDuration;
        mBackgroundColorRipple = backgroundColor;

        mBackgroundShadow = backgroundShadow;
        mBackgroundShadow.setBounds(getBounds());

        mRippleAnimDuration = rippleAnimDuration;
        mRippleColor = rippleColor;

        mInterpolator = inInterpolator;

        cornerRadius[0] = topLeftCornerRadius;
        cornerRadius[1] = topLeftCornerRadius;

        cornerRadius[2] = topRightCornerRadius;
        cornerRadius[3] = topRightCornerRadius;

        cornerRadius[4] = bottomRightCornerRadius;
        cornerRadius[5] = bottomRightCornerRadius;

        cornerRadius[6] = bottomLeftCornerRadius;
        cornerRadius[7] = bottomLeftCornerRadius;

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

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
     *     use this event to change background bound of button
     * @param bounds rectangle bound
     */
    @Override
    protected void onBoundsChange(Rect bounds) {

        mBackgroundShadow.setBounds(bounds);
        Rect rectBound = new Rect();
        if (mBackgroundDrawable != null) {

            rectBound.left = bounds.left + left;
            rectBound.top = bounds.top + top;
            rectBound.right = bounds.right - right;
            rectBound.bottom = bounds.bottom - bottom;
            mBackgroundDrawable.setBounds(rectBound);

            if (mBackgroundDrawable instanceof ColorDrawable) {
                ColorDrawable colorDrawable = (ColorDrawable) mBackgroundDrawable;
                int color = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    color = colorDrawable.getColor();
                }
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{color,
                    color, color});
                gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gradientDrawable.setCornerRadii(cornerRadius);
                gradientDrawable.setGradientCenter(0f, 0f);
                mBackgroundDrawable=gradientDrawable;
            } else if (mBackgroundDrawable instanceof LayerDrawable) {
                // loop through layers to and set drawable attrs
                LayerDrawable ld = ((LayerDrawable) mBackgroundDrawable);
                for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {

                    if(ld.getDrawable(i) instanceof  ColorDrawable){
                        ColorDrawable colorDrawable = (ColorDrawable) ld.getDrawable(i);
                        int color = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                            color = colorDrawable.getColor();
                        }
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{color,
                            color, color});
                        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                        gradientDrawable.setCornerRadii(cornerRadius);
                        gradientDrawable.setGradientCenter(0f, 0f);
                        mBackgroundDrawable=gradientDrawable;
                    }
                }
            }
            mBackgroundDrawable.setBounds(rectBound);
        }

        mBackgroundBounds.set(rectBound.left, rectBound.top, rectBound.right, rectBound.bottom);
        mBackground.reset();
        mBackground.addRoundRect(mBackgroundBounds, cornerRadius, Path.Direction.CW);

    }

    /**
     * Redraw background,raised effect, ripple effect after call invalidate()
     * @param canvas Canvas to draw
     */
    @Override
    public void draw(Canvas canvas) {

        /*check and draw raised effect*/
        if (mBackgroundShadow != null) {
            mBackgroundShadow.setAlpha(ALPHA_SHADOW);

            if (mBackgroundSdAlphaPercent > ALPHA_SHADOW / 255f) {
                mBackgroundShadow.setAlpha(Math.round(MAX_ALPHA_COLOR * mBackgroundSdAlphaPercent));
            }
            mBackgroundShadow.draw(canvas);
        }

        /*check and draw button background*/
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.draw(canvas);
        }

        drawRippleEffect(canvas);
    }


    /**
     * Draw ripple effect with params changed on updateRaise function
     * @param canvas Canvas to draw
     */
    private void drawRippleEffect(Canvas canvas) {
        if (mState != STATE_OUT) {
//            Log.e("Background ALpha",mBackgroundAlphaPercent+"");
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
     * @param x x position to draw ripple effect
     * @param y x position to draw ripple effect
     * @return max radius
     */
    private int getMaxRippleRadius(float x, float y) {
        float x1 = x < mBackgroundBounds.centerX() ? mBackgroundBounds.right : mBackgroundBounds.left;
        float y1 = y < mBackgroundBounds.centerY() ? mBackgroundBounds.bottom : mBackgroundBounds.top;

        return (int) Math.round(Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2))) + 2;
    }

    /**
     * handle touch event from button<br>
     *     calculate to change state of raised effect
     * @param v view
     * @param event motion event
     * @return true if motion event was handled, false otherwise
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

                    } else {
                        setRippleState(STATE_RELEASE_ON_HOLD);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * Set new state of raised effect<br>
     *     calculate to stop or start animation of effect
     * @param state new state to change into
     */
    private void setRippleState(int state) {
        if (mState != state) {
            // Only change state to STATE_PRESS if current is STATE_OUT
            if (mState == STATE_OUT && state != STATE_PRESS){
                return;
            }

//            Log.v(RkRaisedDrawable.class.getSimpleName(), "state: " + mState + " " + state);

            mState = state;

            if (mState == STATE_OUT || mState == STATE_HOVER) {
                stop();
            }else {
                start();
            }
        }
    }

    /**
     * Change params of ripple effect
     * @param x x position to draw ripple effect
     * @param y x position to draw ripple effect
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
     * @return true if running, false otherwise
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
     * @param what Runnable to run
     * @param when delaytime to run
     */
    @Override
    public void scheduleSelf(Runnable what, long when) {
        mRunning = true;
        super.scheduleSelf(what, when);
    }

    /**
     * Runnable to update state of raised effect <br>
     *     and redraw this button
     */
    private final Runnable mUpdater = new Runnable() {

        @Override
        public void run() {
            updateRaise();
        }
    };

    /**
     * update params to draw raised effect
     */
    private void updateRaise() {

        if (mState != STATE_RELEASE) {
            /*calculate to change alpha of background and shadow*/
            float backgroundProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / mBackgroundAnimDuration);
            mBackgroundAlphaPercent = mInterpolator.getInterpolation(backgroundProgress) * Color.alpha(mBackgroundColorRipple) / 255f;
            mBackgroundSdAlphaPercent = mInterpolator.getInterpolation(backgroundProgress);

            /*calculate to change alpha of ripple */
            float touchProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / mRippleAnimDuration);
            mRippleAlphaPercent =  Color.alpha(mRippleColor) / 255f;

            /*set new state of ripple effect*/
            setRippleEffect(mRipplePoint.x, mRipplePoint.y, mMaxRippleRadius * mInterpolator.getInterpolation(touchProgress));

            /*check if animation need to stop or continue*/
            if (backgroundProgress == 1f && touchProgress == 1f) {
                mStartTime = SystemClock.uptimeMillis();
                setRippleState(mState == STATE_PRESS ? STATE_HOVER : STATE_RELEASE);
            }
        } else {
            /*change param to run effect after event user do ACTION_UP or CANCEL*/
            float backgroundProgress = Math.min(1f, (float) (SystemClock.uptimeMillis() - mStartTime) / RELEASE_TIME);
            mBackgroundAlphaPercent = 0;
            mBackgroundSdAlphaPercent = 1f - mInterpolator.getInterpolation(backgroundProgress);

            mRippleAlphaPercent = mBackgroundSdAlphaPercent;
            mBackgroundAlphaPercent = mBackgroundSdAlphaPercent * Color.alpha(mBackgroundColorRipple) / 255f;

            if (backgroundProgress == 1f) {
                setRippleState(STATE_OUT);
            }
        }

        if (isRunning()){
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        }

        invalidateSelf();
    }

    /**
     * Calculate time finish raised effect to dispatch click event on button
     * @return delaytime in milisecond
     */
    public long getClickDelayTime() {
        if (mState == STATE_RELEASE_ON_HOLD){
            return mRippleAnimDuration
                - (SystemClock.uptimeMillis() - mStartTime) + RELEASE_TIME;
        }
        else if (mState == STATE_RELEASE) {
            return RELEASE_TIME - (SystemClock.uptimeMillis() - mStartTime);
        }
        return -1;
    }

    /**
     * Cancel all effect on button
     */
    public void cancel() {
        setRippleState(STATE_OUT);
    }

    /**
     * Indicates whether this drawable will change its appearance based on state.<br>
     *     Clients can use this to determine whether it is necessary to calculate their state and call setState.
     * @return
     */
    @Override
    public boolean isStateful() {
        return mBackgroundDrawable != null && mBackgroundDrawable.isStateful();
    }

    /**
     * event state of background changed
     * @param state state
     * @return true if state has changed, false  otherwise
     */
    @Override
    protected boolean onStateChange(int[] state) {
        return mBackgroundDrawable != null && mBackgroundDrawable.setState(state);
    }

    /**
     * set background of button
     * @param backgroundDrawable
     */
    public void setBackgroundDrawable(Drawable backgroundDrawable) {
        mBackgroundDrawable = backgroundDrawable;
    }

    /**
     * get background of button
     * @return background drawable
     */
    public Drawable getBackgroundDrawable() {
        return mBackgroundDrawable;
    }

    /**
     * set alpha to this button
     * @param alpha opacity of view
     */
    @Override
    public void setAlpha(int alpha) {
        MAX_ALPHA_COLOR = alpha;
    }

    /**
     * set colorfilter to paints
     * @param filter color filter of paint
     */
    @Override
    public void setColorFilter(ColorFilter filter) {
        mFillPaint.setColorFilter(filter);
        mShaderPaint.setColorFilter(filter);
    }

    /**
     * get Opacity
     * @return a format that supports translucency
     */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * This class to support build new RippbleDrawable of RaisedButton with parameters when user declare button from xml layout<br>
     *     setup with default values if user was not declared
     *
     */
    public static class Builder {
        private Drawable mBackgroundShadow;
        private Drawable mBackgroundDrawable;
        private int mBackgroundAnimDuration = 200;
        private int mBackgroundColor;

        private int mRippleAnimDuration = 500;
        private int mRippleColor;

        private Interpolator mInterpolator;

        private int mTopLeftCornerRadius;
        private int mTopRightCornerRadius;
        private int mBottomLeftCornerRadius;
        private int mBottomRightCornerRadius;
        private int mPaddingLeft;
        private int mPaddingTop;
        private int mPaddingRight;
        private int mPaddingBottom;

        private static final int PADDING_LEFT_DEFAULT = 7;
        private static final int PADDING_RIGHT_DEFAULT = 7;
        private static final int PADDING_TOP_DEFAULT = 7;
        private static final int PADDING_BOTTOM_DEFAULT = 7;

        private static final int TOPLEFT_CORRNER_DEFAULT = 2;
        private static final int TOPRIGHT_CORRNER_DEFAULT = 2;
        private static final int BOTTOMLEFT_CORRNER_DEFAULT = 3;
        private static final int BOTTOMRIGHT_CORRNER_DEFAULT = 3;

        private static final int CORNER_RADIUS_DEFAULT = 0;

        public Builder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RkRaisedButton, defStyleAttr, defStyleRes);
            int resId;


            Drawable bg_shadow = a.getDrawable(R.styleable.RkRaisedButton_rk_background_shadow);
            if (bg_shadow == null) {
                bg_shadow = ContextCompat.getDrawable(context,R.drawable.abc_menu_dropdown_panel_holo_light);
            }

            backgroundShadow(bg_shadow);

            backgroundColor(a.getColor(R.styleable.RkRaisedButton_rk_background_pressed, 0));
            backgroundAnimDuration(a.getInteger(R.styleable.RkRaisedButton_rk_backgroundAnimDuration, mBackgroundAnimDuration));

            rippleColor(a.getColor(R.styleable.RkRaisedButton_rk_ripple_color, ContextCompat.getColor(context, R.color.ripple_raised_color)));
            rippleAnimDuration(a.getInteger(R.styleable.RkRaisedButton_rk_ripple_anim_duration, mRippleAnimDuration));
            if ((resId = a.getResourceId(R.styleable.RkRaisedButton_rk_interpolator, 0)) != 0){
                 inInterpolator(AnimationUtils.loadInterpolator(context, resId));
            }

            cornerRadius(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_cornerRadius,  Utils.dpToPx(context, CORNER_RADIUS_DEFAULT)));
            topLeftCornerRadius(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_topLeftCornerRadius, mTopLeftCornerRadius == 0 ? Utils.dpToPx(context, TOPLEFT_CORRNER_DEFAULT) : mTopLeftCornerRadius));
            topRightCornerRadius(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_topRightCornerRadius, mTopRightCornerRadius== 0 ? Utils.dpToPx(context, TOPRIGHT_CORRNER_DEFAULT) : mTopRightCornerRadius));
            bottomRightCornerRadius(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_bottomRightCornerRadius, mBottomRightCornerRadius== 0 ? Utils.dpToPx(context, BOTTOMRIGHT_CORRNER_DEFAULT) : mBottomRightCornerRadius));
            bottomLeftCornerRadius(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_bottomLeftCornerRadius, mBottomLeftCornerRadius== 0 ? Utils.dpToPx(context, BOTTOMLEFT_CORRNER_DEFAULT) : mBottomLeftCornerRadius));
            padding(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_padding, 0));
            left(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_leftPadding, mPaddingLeft == 0 ? Utils.dpToPx(context, PADDING_LEFT_DEFAULT) : mPaddingLeft));
            right(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_rightPadding, mPaddingTop == 0 ?  Utils.dpToPx(context, PADDING_RIGHT_DEFAULT) : mPaddingTop));
            top(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_topPadding, mPaddingRight == 0 ?  Utils.dpToPx(context, PADDING_TOP_DEFAULT) : mPaddingRight));
            bottom(a.getDimensionPixelSize(R.styleable.RkRaisedButton_rk_bottomPadding, mPaddingBottom == 0 ?  Utils.dpToPx(context, PADDING_BOTTOM_DEFAULT) : mPaddingBottom));

            a.recycle();
        }

        public RkRaisedDrawable build() {
            if (mInterpolator == null){
                mInterpolator = new DecelerateInterpolator();
            }

            return new RkRaisedDrawable(mBackgroundDrawable, mBackgroundShadow, mBackgroundAnimDuration, mBackgroundColor, mRippleAnimDuration, mRippleColor, mInterpolator, mTopLeftCornerRadius, mTopRightCornerRadius, mBottomRightCornerRadius, mBottomLeftCornerRadius, mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }

        public Builder backgroundDrawable(Drawable drawable) {
            mBackgroundDrawable = drawable;
            return this;
        }

        public Builder backgroundShadow(Drawable drawable) {
            mBackgroundShadow = drawable;
            return this;
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

        public Builder cornerRadius(int radius) {
            mTopLeftCornerRadius = radius;
            mTopRightCornerRadius = radius;
            mBottomLeftCornerRadius = radius;
            mBottomRightCornerRadius = radius;
            return this;
        }

        public Builder topLeftCornerRadius(int radius) {
            mTopLeftCornerRadius = radius;
            return this;
        }

        public Builder topRightCornerRadius(int radius) {
            mTopRightCornerRadius = radius;
            return this;
        }

        public Builder bottomLeftCornerRadius(int radius) {
            mBottomLeftCornerRadius = radius;
            return this;
        }

        public Builder bottomRightCornerRadius(int radius) {
            mBottomRightCornerRadius = radius;
            return this;
        }

        public Builder padding(int padding) {
            mPaddingLeft = padding;
            mPaddingTop = padding;
            mPaddingRight = padding;
            mPaddingBottom = padding;
            return this;
        }

        public Builder left(int padding) {
            mPaddingLeft = padding;
            return this;
        }

        public Builder top(int padding) {
            mPaddingTop = padding;
            return this;
        }

        public Builder right(int padding) {
            mPaddingRight = padding;
            return this;
        }

        public Builder bottom(int padding) {
            mPaddingBottom = padding;
            return this;
        }
    }
}
