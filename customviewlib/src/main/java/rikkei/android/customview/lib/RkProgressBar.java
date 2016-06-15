package rikkei.android.customview.lib;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tuyenpx on 26/05/2016.
 * <p/>
 * support to create circle progressbar view like IOS .
 * progressStyle : allow user custom theme for dialog , Otherwise will be used as default .
 * themeStyle : them style of your activity , if user used Dark Theme , themeStyle should be DARK_THEME
 * and the other should be LIGHT_DARK
 */



public class RkProgressBar extends Dialog {

    public static int sDefaultResourceID = 0;

    public static final int DARK_THEME = 0;
    public static final int LIGHT_THEME = 1;


    private RkProgressBar(Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private RkProgressBar(Context context, int theme) {
        super(context, theme);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();

    }

    public void updateMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }


    /**
     * @param message        : Message need to show in progressbar
     * @param cancelable     : allow user cancel or not when progress showing .
     * @param cancelListener : listener for user cancel progress .
     * @param progressStyle  : resource id style of RkProgress or use 0 for default .
     * @param themeStyle     : theme of your activity .
     */


    public static RkProgressBar show(Context context, CharSequence message, boolean cancelable,
                                     OnCancelListener cancelListener, int progressStyle, int themeStyle) {
        RkProgressBar dialog;
        if (progressStyle != sDefaultResourceID) {
            dialog = new RkProgressBar(context, progressStyle);
        } else {
            dialog = new RkProgressBar(context);
        }

        if (themeStyle == DARK_THEME) {
            dialog.setContentView(R.layout.rkprogress_layout_light);
        } else {
            dialog.setContentView(R.layout.rkprogress_layout_dark);
        }
        dialog.setTitle("");
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.message);
            txt.setText(message);
        }
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }
}