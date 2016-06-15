package rikkei.android.customview.lib.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;

import rikkei.android.customview.lib.R;

/**
 * Created by cuongvv on 5/25/2016.
 */
public class Utils {

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }


    public static int getColor(int baseColor, float alphaPercent) {
        int alpha = Math.round(Color.alpha(baseColor) * alphaPercent);

        return (baseColor & 0x00FFFFFF) | (alpha << 24);
    }

    public static int getType(TypedArray array, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return array.getType(index);
        else {
            TypedValue value = array.peekValue(index);
            return value == null ? TypedValue.TYPE_NULL : value.type;
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorControlHighlight(Context context, int defaultValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorControlHighlight, defaultValue);

        return getColor(context, R.attr.colorControlHighlight, defaultValue);
    }

    private static int getColor(Context context, int id, int defaultValue) {

        TypedValue value = new TypedValue();

        try {
            Resources.Theme theme = context.getTheme();
            if (theme != null && theme.resolveAttribute(id, value, true)) {
                if (value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT)
                    return value.data;
                else if (value.type == TypedValue.TYPE_STRING)
                    return ContextCompat.getColor(context, value.resourceId);
            }
        } catch (Exception ex) {
            Log.e("RippbleDrawable", ex.getMessage());
        }

        return defaultValue;
    }

}
