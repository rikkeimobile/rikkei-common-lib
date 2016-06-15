package rikkei.android.common.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Support handle Dialog
 * Created by datpt2 on 5/4/2016.
 */
public class RkDialog {

    public static final int DEFAULT_RESID = 0;

    /**
     * Instance of RkDialog Object
     */
    private static RkDialog sInstance;

    /**
     * Callback to handle listener of dialog
     */
    public interface Callback {
        void onNegativeButtonClick(DialogInterface dialog, int which);

        void onPositiveButtonClick(DialogInterface dialog, int which);

        void onCancel(DialogInterface dialog);
    }

    /**
     * link to activity
     */
    private WeakReference<Activity> mWeakReference;

    /**
     * progress dialog to handle
     */
    private ProgressDialog mProgressDialog;

    /**
     * Get instance of RkDialog object
     *
     * @param activity
     * @return Return an instance of RkDialog Object
     */
    public static RkDialog getInstance(Activity activity) {
        if (sInstance == null) {
            sInstance = new RkDialog(activity);
        }
        return sInstance;
    }

    /**
     * Create new instance of RkDialog
     *
     * @param activity
     */
    public RkDialog(Activity activity) {
        mWeakReference = new WeakReference<Activity>(activity);
        mProgressDialog = new ProgressDialog(activity);
    }

    /**
     * Show dialog
     *
     * @param titleResId      title resource id of dialog
     * @param msgResId        message resource id of dialog
     * @param negBtnResId     negative button resource id of dialog
     * @param posResId        positive button resource id of dialog
     * @param themeResId      theme resource id of dialog
     * @param customviewResId customview resource id of dialog
     * @param isCancelable    flag to check dialog cancelable
     * @param callback        callback to handle listener of dialog
     * @return Return dialog to show
     */
    public AlertDialog show(int titleResId, int msgResId, int negBtnResId, int posResId, int themeResId, int customviewResId, boolean isCancelable, final Callback callback) {
        Activity activity = mWeakReference.get();

        // activity is unavailable
        if (activity == null) {
            RkLogger.e("Dialog", "Activity be finished or not exist");
            return null;
        }

        // Define alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, themeResId);

        // check title is available or not
        if (titleResId != DEFAULT_RESID) {
            builder.setTitle(titleResId);
        }

        // check message is available or not
        if (msgResId != DEFAULT_RESID) {
            builder.setMessage(msgResId);
        }

        // check negative button is available or not
        if (negBtnResId != DEFAULT_RESID) {
            builder.setNegativeButton(negBtnResId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onNegativeButtonClick(dialog, which);
                    }
                }
            });
        }

        // check positive button is available or not
        if (posResId != DEFAULT_RESID) {
            builder.setPositiveButton(posResId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onPositiveButtonClick(dialog, which);
                    }
                }
            });
        }

        // Set customview
        if (customviewResId != DEFAULT_RESID) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(customviewResId, null, false);
            builder.setView(customView);
        }

        // set cancelable & OnCancaleListener
        builder.setCancelable(isCancelable).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (callback != null) {
                    callback.onCancel(dialog);
                }
            }
        });

        // show dialog on UI thread
        final AlertDialog dialog = builder.create();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        return dialog;
    }

    /**
     * Show default dialog without custom cancelable
     *
     * @param titleResId  title resource id of dialog
     * @param msgResId    message resource id of dialog
     * @param negBtnResId negative button resource id of dialog
     * @param posResId    positive button resource id of dialog
     * @param themeResId  theme resource id of dialog
     * @param callback    callback to handle listener of dialog
     * @return Return dialog to show
     */
    public AlertDialog show(int titleResId, int msgResId, int negBtnResId, int posResId, int themeResId, final Callback callback) {
        return show(titleResId, msgResId, negBtnResId, posResId, themeResId, DEFAULT_RESID, false, callback);
    }

    /**
     * Show default dialog without custom theme
     *
     * @param titleResId   title resource id of dialog
     * @param msgResId     message resource id of dialog
     * @param negBtnResId  negative button resource id of dialog
     * @param posResId     positive button resource id of dialog
     * @param isCancelable flag to check dialog cancelable
     * @param callback     callback to handle listener of dialog
     * @return Return dialog to show
     */
    public AlertDialog show(int titleResId, int msgResId, int negBtnResId, int posResId, boolean isCancelable, final Callback callback) {
        return show(titleResId, msgResId, negBtnResId, posResId, DEFAULT_RESID, DEFAULT_RESID, isCancelable, callback);
    }

    /**
     * Show default dialog without custom theme and cancelable
     *
     * @param titleResId  title resource id of dialog
     * @param msgResId    message resource id of dialog
     * @param negBtnResId negative button resource id of dialog
     * @param posResId    positive button resource id of dialog
     * @param callback    callback to handle listener of dialog
     * @return Return dialog to show
     */
    public AlertDialog show(int titleResId, int msgResId, int negBtnResId, int posResId, final Callback callback) {
        return show(titleResId, msgResId, negBtnResId, posResId, DEFAULT_RESID, DEFAULT_RESID, false, callback);
    }

    /**
     * Show progress dialog
     *
     * @param msg          message of progress dialog
     * @param isCancelable flag to check dialog cancelable
     */
    public void showProgress(String msg, boolean isCancelable) {
        Activity activity = mWeakReference.get();

        // activity is unavailable
        if (activity == null) {
            return;
        }

        // progress dialog unavailable or already showing
        if (mProgressDialog == null || mProgressDialog.isShowing()) {
            return;
        }

        mProgressDialog.setCancelable(isCancelable);
        if (msg != null) {
            mProgressDialog.setMessage(msg);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
            }
        });
    }

    /**
     * Show progress dialog without cancelable
     *
     * @param msg message of progress dialog
     */
    public void showProgress(String msg) {
        showProgress(msg, false);
    }

    /**
     * Show progress dialog without message
     *
     * @param isCancelable flag to check dialog cancelable
     */
    public void showProgress(boolean isCancelable) {
        showProgress(null, isCancelable);
    }

    /**
     * Show progress dialog without message, cancelable
     */
    public void showProgress() {
        showProgress(null, false);
    }

    /**
     * Dismiss progress dialog
     */
    public void dismissProgress() {
        Activity activity = mWeakReference.get();

        // activity is unavailable
        if (activity == null) {
            return;
        }

        // progress dialog is unavailable or already dismiss
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    /**
     * Reset instance of RkDialog to null
     */
    public void release() {
        if (sInstance != null) {
            sInstance = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog = null;
        }
    }

}
