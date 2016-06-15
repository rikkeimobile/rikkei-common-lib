package rikkei.android.customview.lib;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by cuongvv on 19/05/2016.
 * <p/>
 * This is button with raised effect:<br>
 *  attributes:<br>
 *  rk_ripple_color:  color of ripple in button , default is #8F666666<br>
 *  rk_ripple_anim_duration:  time in millisecond for ripple effect ,default is 500.<br>
 * <p/>
 * <p/>
 */
public class RkRaisedButton extends RkFlatButton {

    public RkRaisedButton(Context context) {
        super(context);
    }

    public RkRaisedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RkRaisedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
