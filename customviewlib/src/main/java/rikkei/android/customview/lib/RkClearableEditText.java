package rikkei.android.customview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import rikkei.android.customview.lib.util.Utils;

/**
 * Created by cuongvv on 5/26/2016.
 * <p/>
 * This is a EditText <br>
 * If EditText Content not empty, It will display a clearable icon at LEFT or RIGHT position<br>
 * When User clicked on clearable Icon then EditText content will be cleared<br>
 *
 * <p/>attributes:<br>
 * - cet_icon: drawable icon, (default: android.R.drawable.ic_delete)<br>
 * - cet_icon_location: LEFT or RIGHT (default: RIGHT)
 * <p/>
 */
public class RkClearableEditText extends EditText implements View.OnTouchListener, View.OnFocusChangeListener {


    private Location mClearDrawableLocation = Location.RIGHT;
    private Drawable mClearDrawable;

    private enum Location {
        LEFT(0), RIGHT(2);

        final int mPosIndex;

        Location(int idx) {
            this.mPosIndex = idx;
        }
    }

    private final String EMPTY = "";

    public RkClearableEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public RkClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public RkClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle, 0);
    }

    /**
     * init clearable icon and register touch listener to catch event user touch on clearable icon
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        initIcon(context, attrs, defStyleAttr, defStyleRes);
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
    }

    /**
     * Init clearable icon of edit text
     *
     * @param context application context
     * @param attrs AttributeSet
     * @param defStyleAttr defStyleAttr
     * @param defStyleRes defStyleRes
     */
    private void initIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RkClearableEditText, defStyleAttr, defStyleRes);

        mClearDrawable = a.getDrawable(R.styleable.RkClearableEditText_cet_icon);

        /*not set icon set by default*/
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_delete);
        }
        int locationIndex = a.getInt(R.styleable.RkClearableEditText_cet_icon_location, Location.RIGHT.mPosIndex);
        if (locationIndex == Location.LEFT.mPosIndex) {
            mClearDrawableLocation = Location.LEFT;
        }
        a.recycle();

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        int min = getPaddingTop() + mClearDrawable.getIntrinsicHeight() + getPaddingBottom();

        /*reset minimum height*/
        if (getSuggestedMinimumHeight() < min) {
            setMinimumHeight(min);
        }
    }

    /**
     * setVisibility of clearable icon
     *
     * @param visible if true then show clearable icon, else not show
     */
    protected void setClearIconVisible(boolean visible) {

        /*get drawable left,right,top,bottom of this edittext*/
        Drawable[] cd = getCompoundDrawables();

        /*check if clearable icon displayed at current position */
        Drawable displayed = getCompoundDrawables()[mClearDrawableLocation.mPosIndex];
        boolean wasVisible = (displayed != null);
        if (visible != wasVisible) {
            Drawable x = visible ? mClearDrawable : null;
            super.setCompoundDrawables((mClearDrawableLocation == Location.LEFT) ? x : cd[0], cd[1],
                (mClearDrawableLocation == Location.RIGHT) ? x : cd[2], cd[3]);
        }
    }

    /**
     * calculate to catch event user touch at clearable icon<br>
     * if so, set current EditText empty, then onTextChanged() will be called
     *
     * @param v view has event
     * @param event The motion event.
     * @return true if event handled, false otherwise
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (isFocused() && event.getAction() == MotionEvent.ACTION_UP) {

            /*calculate if user touch up at clearable icon*/
            int x = (int) event.getX();
            int y = (int) event.getY();
            int left = (mClearDrawableLocation == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth();
            int right = (mClearDrawableLocation == Location.LEFT) ? getPaddingLeft() + mClearDrawable.getIntrinsicWidth() : getWidth();

            boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
            if (tappedX) {
                setText(EMPTY);
                return true;
            }
        }

        return false;
    }


    /**
     * @Override EditText
     * This method is called when the text is changed, in case any subclasses
     * would like to know.
     *
     * @param text The text the TextView is displaying
     * @param start The offset of the start of the range of the text that was
     * modified
     * @param lengthBefore The length of the former text that has been replaced
     * @param lengthAfter The length of the replacement modified text
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (isFocused()) {
            setClearIconVisible(Utils.isNotEmpty(text));
        }
    }

    /**
     * Called when the focus state of a view has changed.
     *
     * @param v view has event
     * @param hasFocus True if the View has focus; false otherwise.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(Utils.isNotEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
    }
}
