package rikkei.android.sns.lib;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.internal.LoginAuthorizationType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.lang.ref.WeakReference;

/**
 * Created by tuyenpx on 31/05/2016.
 */
@SuppressWarnings("UnusedDeclaration")

public class RkFaceBookUtils {


    private static RkFaceBookUtils sInstance;
    private final WeakReference<Object> mActivity;

    /**
     * @param mActivity : can be Activity or Fragment object .
     * @return : instance of RkFaceBookUtils
     */

    public static RkFaceBookUtils getInstance(Object mActivity) {
        if (sInstance == null)
            sInstance = new RkFaceBookUtils(mActivity);
        return sInstance;
    }

    private RkFaceBookUtils(Object mActivity) {
        this.mActivity = new WeakReference<>(mActivity);
    }


    /**
     * support user can post status to facebook .
     *
     * @param ContentObject : content of status ( should be instanceof ShareLinkContent or SharePhotoContent
     */


    public void postStatus(Object ContentObject, CallbackManager callbackManager, FacebookCallback<Sharer.Result> shareCallback) {
        if (!isLogin()) {
            throw new RuntimeException("can not post status , login first please");
        }
        ShareDialog shareDialog;
        if (mActivity.get() instanceof Activity) {
            shareDialog = new ShareDialog((Activity) mActivity.get());
        } else if (mActivity.get() instanceof android.support.v4.app.Fragment) {
            shareDialog = new ShareDialog((android.support.v4.app.Fragment) mActivity.get());
        } else {
            shareDialog = new ShareDialog((Fragment) mActivity.get());
        }
        shareDialog.registerCallback(callbackManager, shareCallback);
        Profile profile = Profile.getCurrentProfile();
        if (ContentObject instanceof ShareLinkContent) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {

                shareDialog.show((ShareLinkContent) ContentObject);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share((ShareLinkContent) ContentObject, shareCallback);
            } else {
                throw new RuntimeException("can not post status");
            }
        } else if (ContentObject instanceof SharePhotoContent) {
            if (ShareDialog.canShow(SharePhotoContent.class)) {
                shareDialog.show((SharePhotoContent) ContentObject);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share((SharePhotoContent) ContentObject, shareCallback);
            } else {
                throw new RuntimeException("can not post status");
            }
        } else {
            throw new RuntimeException("can not post status");
        }


    }

    /**
     * String fields : example : id, first_name, last_name, email,gender, birthday, location , these are fields in profile
     *
     * @param callback : to response personel profile as in Json object .
     */

    public void getProfile(String fields, GraphRequest.GraphJSONObjectCallback callback) {
        if (!isLogin())
            throw new RuntimeException("can not get profile , please login first");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), callback);
        Bundle parameters = new Bundle();
        parameters.putString("fields", fields);
        request.setParameters(parameters);
        request.executeAsync();

    }

    /**
     * @param callback : to reponse list checkin with Json Object .
     */

    public void getListCheckIn(GraphRequest.Callback callback) {
        if (!isLogin())
            throw new RuntimeException("can not get profile , please login first");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "me/tagged_places",
                null,
                HttpMethod.GET,
                callback
        ).executeAsync();

    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    /**
     * Login Account .
     *
     * @param properties : property to setup permission to access facebook .
     */

    public void logIn(RkFaceBookLoginProperty properties, CallbackManager callbackManager, FacebookCallback<LoginResult> callback) {

        if (isLogin()) {
            callback.onSuccess(new LoginResult(AccessToken.getCurrentAccessToken(), null, null));
            return;
        }
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.setDefaultAudience(properties.getDefaultAudience());
        loginManager.setLoginBehavior(properties.getLoginBehavior());
        loginManager.registerCallback(callbackManager, callback);
        if (LoginAuthorizationType.PUBLISH.equals(properties.getAuthorizationType())) {
            if (mActivity.get() != null && mActivity.get() instanceof Fragment) {
                loginManager.logInWithPublishPermissions((Fragment) mActivity.get(), properties.getPermissions());
            } else if (mActivity.get() != null && mActivity.get() instanceof android.support.v4.app.Fragment) {
                loginManager.logInWithPublishPermissions((android.support.v4.app.Fragment) mActivity.get(), properties.getPermissions());
            } else {
                loginManager.logInWithPublishPermissions((Activity) mActivity.get(), properties.getPermissions());
            }
        } else {
            if (mActivity.get() != null && mActivity.get() instanceof Fragment) {
                loginManager.logInWithReadPermissions((Fragment) mActivity.get(), properties.getPermissions());
            } else if (mActivity.get() != null && mActivity.get() instanceof android.support.v4.app.Fragment) {
                loginManager.logInWithReadPermissions((android.support.v4.app.Fragment) mActivity.get(), properties.getPermissions());
            } else {
                loginManager.logInWithReadPermissions((Activity) mActivity.get(), properties.getPermissions());
            }
        }
    }

    /**
     * Logout account .
     */

    public void logout() {
        if (isLogin()) {
            LoginManager.getInstance().logOut();
        }
    }

    /**
     * @return : status of login manager , facebook account is logged in or not .
     */


    public boolean isLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    /**
     * after finish activity call login , please call release function .
     */

    public void release() {
        if (sInstance != null) {
            sInstance = null;
        }
    }


}
