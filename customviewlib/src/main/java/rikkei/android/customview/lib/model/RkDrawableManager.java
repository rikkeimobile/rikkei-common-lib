package rikkei.android.customview.lib.model;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import rikkei.android.customview.lib.RkRaisedButton;

/**
 * Created by cuongvv on 15/06/2016.
 */
public class RkDrawableManager implements View.OnClickListener {

    private static final String xmlns = "http://schemas.android.com/apk/res/android";
    private static final String BG_ATTRIBUTE = "background";
    private static final int BG_COLOR_DEFAULT = 0x00000000;
    private static final int BG_RAISED_COLOR_DEFAULT = 0xFFCCCCCC;
    private View.OnClickListener mOnClickListener;
    private boolean mClickScheduled = false;

    /**
     * init button with a drawable implement ripple effect and set background
     *
     * @param context      application context
     * @param v            view to init
     * @param attrs        Attribute Set
     * @param defStyleAttr defStyleAttr
     * @param defStyleRes  defStyleRes
     */
    public void init(Context context, View v, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        if (!isBackgroundSet(attrs)) {
            if (v instanceof RkRaisedButton) {
                setBackground(v, new ColorDrawable(BG_RAISED_COLOR_DEFAULT));
            } else {
                setBackground(v, new ColorDrawable(BG_COLOR_DEFAULT));
            }
        }

        /* Build ripple background from attributes */
        Drawable drawable;
        if (v instanceof RkRaisedButton) {
            drawable = new RkRaisedDrawable.Builder(context, attrs, defStyleAttr, defStyleRes).backgroundDrawable(getBackground(v)).build();
        } else {
            drawable = new RkFlatDrawable.Builder(context, attrs, defStyleAttr, defStyleRes).backgroundDrawable(getBackground(v)).build();
        }

        /* Set ripple background*/
        setBackground(v, drawable);
    }

    /**
     * check if background did set
     *
     * @param attrs AttributeSet
     * @return true if already set background from xml
     */
    private boolean isBackgroundSet(AttributeSet attrs) {

        return attrs.getAttributeValue(xmlns, BG_ATTRIBUTE) != null;
    }

    /**
     * Set background drawable of view
     *
     * @param v:       view to set background
     * @param drawable background drawable for set
     */
    public static void setBackground(View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(drawable);
        } else {
            v.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Get Background of raised button
     *
     * @param v view to get background
     * @return background drawable
     */
    private Drawable getBackground(View v) {
        Drawable background = v.getBackground();
        if (background == null) {
            return null;
        }

        if (background instanceof RkRaisedDrawable) {
            return ((RkRaisedDrawable) background).getBackgroundDrawable();
        }

        return background;
    }

    /**
     * register event onClick from outside
     *
     * @param clickListener listener onClicked Event
     */
    public void setOnClickListener(View.OnClickListener clickListener) {
        mOnClickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        Drawable background = v.getBackground();
        long delay = 0;        // delaytime to finish animation

        if (background != null) {

            /* get time remain to finish animation if running*/
            if (background instanceof RkFlatDrawable) {
                delay = ((RkFlatDrawable) background).getClickDelayTime();
            } else if (background instanceof RkRaisedDrawable) {
                delay = ((RkRaisedDrawable) background).getClickDelayTime();
            }
        }

        if (delay > 0 && !mClickScheduled) {
            mClickScheduled = true;

            /*wait delay milisecond to trigger click event */
            v.postDelayed(new ClickRunnable(v), delay);
        } else {
            /* dispatch click event to listener immediately*/
            dispatchClickEvent(v);
        }
    }


    /**
     * dispatch click event to listener
     *
     * @param v view to dispatch
     */
    private void dispatchClickEvent(View v) {

        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }

    public boolean onTouchEvent(View view, MotionEvent event) {

        /* dispatch touch events into RkFlatDrawable*/
        Drawable background = view.getBackground();
        if (background != null) {
            if (background instanceof RkRaisedDrawable) {
                return ((RkRaisedDrawable) background).onTouch(view, event);
            }
            return ((RkFlatDrawable) background).onTouch(view, event);

        }
        return false;
    }

    /**
     * Runnable handle dispatch click event
     */
    class ClickRunnable implements Runnable {
        View mView;

        public ClickRunnable(View v) {
            mView = v;
        }

        @Override
        public void run() {
            mClickScheduled = false;
            dispatchClickEvent(mView);
        }
    }


    /**
     * cancel animation if running
     *
     * @param view
     */
    public void cancel(View view) {
        Drawable background = view.getBackground();
        if (background instanceof RkRaisedDrawable) {
            ((RkRaisedDrawable) background).cancel();
        } else if (background instanceof RkFlatDrawable) {
            ((RkFlatDrawable) background).cancel();
        }
    }

}
