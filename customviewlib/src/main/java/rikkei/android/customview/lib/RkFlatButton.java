package rikkei.android.customview.lib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import rikkei.android.customview.lib.model.RkDrawableManager;

/**
 * Created by cuongvv on 27/05/2016.
 * <p/>
 * This is Flat Button with ripple effect<br>
 * attributes:<br>
 * rk_ripple_color:  color of ripple in button , default is #8F666666<br>
 * rk_ripple_anim_duration:  time in millisecond for ripple effect ,default is 500.
 * <p/>
 * <p/>
 */
public class RkFlatButton extends android.widget.Button {

    private RkDrawableManager mRkDrawableManager = new RkDrawableManager();

    public RkFlatButton(Context context) {
        super(context);
        mRkDrawableManager.init(context, this, null, 0, 0);
    }

    public RkFlatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRkDrawableManager.init(context, this, attrs, 0, 0);
    }

    public RkFlatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRkDrawableManager.init(context, this, attrs, defStyleAttr, 0);
    }

    /**
     * catch event button detached from window and dismiss animation
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mRkDrawableManager.cancel(this);
    }

    /**
     * register event onClick from outside
     *
     * @param clickListener listener onClicked Event
     */
    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        super.setOnClickListener(mRkDrawableManager);
        mRkDrawableManager.setOnClickListener(clickListener);
    }


    /**
     * Implement this method to handle touch screen motion events.
     * handle touch screen motion events.
     *
     * @param event - The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        boolean result = super.onTouchEvent(event);
        if (!isEnabled()) {
            return result;
        }

        return mRkDrawableManager.onTouchEvent(this, event) || result;
    }


}
