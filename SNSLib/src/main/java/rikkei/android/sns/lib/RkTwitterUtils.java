package rikkei.android.sns.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

/**
 * Created by cuongvv on 5/31/2016.
 * <p/>
 * This class allow twitter handle fuctions: Login, Logout, post status to timeline, get profile...
 * <p/>
 * How to use:<p>
 *
 * 1. Goto https://fabric.io/kits/android/twitterkit/install and Follow the instructions
 *     to setup Twitter kit by modifing build.gradle file and setup API key
 *
 * 2. Sign Up an account, login at https://apps.twitter.com/ and create an Application to get
 *     consumerKey, consumerSecret keys will used later<p>
 *
 * 3. Put statement: RkTwitterUtils.init(Context context, String consumerKey , String consumerSecret)<p>
 *     to first line of onCreate() method of every Activity<p>
 *
 * 4. Put statement:  RkTwitterUtils.getInstance().onActivityResult(requestCode, resultCode, data);<p>
 *     to onActivityResult()  of activity<p>
 *
 * 5. Call functions: RkTwitterUtils.login(...), RkTwitterUtils.logout(...), RkTwitterUtils.post(...)<p>
 */
public class RkTwitterUtils {

    public static final String ERR_NO_DATA = "Nothing to post";
    public static final String ERR_POST_CANCEL = "Post canceled";
    private static final String LOGIN_FAIL = "Not authenticated";
    private static TwitterAuthClient sAuthClient;
    private static RkTwitterUtils sRkTwitter;

    private RkTwitterUtils() {
        if (sAuthClient == null) {
            sAuthClient = new TwitterAuthClient();
        }
    }

    public static RkTwitterUtils getInstance() {
        if (sRkTwitter == null) {
            sRkTwitter = new RkTwitterUtils();
        }
        return sRkTwitter;
    }

    /**
     * Init FabricConfig
     *
     * @param context application context
     * @param twitterKey create an application and get at https://apps.twitter.com/
     * @param secretKey create an application and get at https://apps.twitter.com/
     */
    public static void init(Context context, String twitterKey, String secretKey) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitterKey, secretKey);
        Fabric.with(context, new Twitter(authConfig));

    }

    /**
     * Post a status with text and image to Twitter
     * <br>
     * Note: Only post image if twitter app installed
     *
     * @param context    activity context
     * @param textStatus content to post
     * @param imageUri   image uri to post
     * @throws Exception throw exception if nothing to post
     */
    public void post(final Activity context, final String textStatus, final Uri imageUri) throws Exception {

        if (TextUtils.isEmpty(textStatus) && imageUri == null) {
            throw new Exception(ERR_NO_DATA);
        }

        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        PackageManager pm = context.getPackageManager();

        if (twitterSession == null) {

            login(context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    handlePost(context, textStatus, imageUri);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, ERR_POST_CANCEL, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            handlePost(context, textStatus, imageUri);
        }

    }

    /**
     * compose date and call action post
     *
     * @param context    activity context
     * @param textStatus content to post
     * @param imageUri   image uri to post
     */
    private static void handlePost(Activity context, String textStatus, Uri imageUri) {

        TweetComposer.Builder builder = new TweetComposer.Builder(context);
        if (!TextUtils.isEmpty(textStatus)) {

            builder.text(textStatus);
        }
        if (imageUri != null) {

            builder.image(imageUri);
        }

        builder.show();
    }

    /**
     * alway call this method from Activity.onActivityResult()
     * that call RkTwitterUtils.post(), RkTwitterUtils.login()
     *
     * @param requestCode request code
     * @param resultCode  status of result
     * @param data        result data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == sAuthClient.getRequestCode()) {
            sAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Handle login to Twitter<br>
     * when call this function need to put command
     * 'RkTwitterUtils.getInstance().onActivityResult(requestCode,resultCode,data);'
     * into function onActivityResult() of Activity call this method
     *
     * @param activity               context to call login
     * @param twitterSessionCallback Callback to be executed with result
     */
    public void login(Activity activity, Callback<TwitterSession> twitterSessionCallback) {

        sAuthClient = new TwitterAuthClient();
        sAuthClient.authorize(activity, twitterSessionCallback);
    }

    /**
     * logout twitter and clear all active session
     */
    public static void logout() {

        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
    }

    /**
     * get user profile, if session is null, request login first
     *
     * @param context      Activity context
     * @param userCallback Callback to be executed with result
     */
    public void getProfile(final Activity context, final Callback<User> userCallback) {

        final TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterSession == null) {
            login(context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    getProfile(TwitterCore.getInstance().getSessionManager().getActiveSession(), userCallback);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            getProfile(twitterSession, userCallback);
        }
    }

    /**
     * Call API to get user profile
     *
     * @param twitterSession twitter session authenticated
     * @param userCallback   (required) Callback to be executed with result
     */
    private void getProfile(TwitterSession twitterSession, Callback<User> userCallback) {

        Twitter.getApiClient(twitterSession).getAccountService()
            .verifyCredentials(true, false, userCallback);
    }

    /**
     * Get list tweets which content contain tagName
     *
     * @param context    Activity context
     * @param tagName    is a String start with # character
     * @param count      The number of tweets to return per page, up to a maximum of 100. Defaults to 15
     * @param maxTweetId Returns results with an Tweet ID less than (that is, older than) or equal to maxTweetId.
     * @param callback   (required) Callback to be executed with result
     */
    public void searchTweetByTag(final Activity context, final String tagName, final Integer count,
                                 final Long maxTweetId, final Callback<Search> callback) {

        final TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterSession == null) {
            login(context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    search(twitterSession, tagName, count, maxTweetId, callback);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            search(twitterSession, tagName, count, maxTweetId, callback);
        }
    }

    /**
     * Call API to get list tweet by tagName
     *
     * @param twitterSession twitter session authenticated
     * @param tagName        is a String start with # character
     * @param count          The number of tweets to return per page, up to a maximum of 100. Defaults to 15
     * @param maxTweetId     Returns results with an Tweet ID less than (that is, older than) or equal to maxTweetId.
     * @param callback       (required) Callback to be executed with result
     */
    private void search(TwitterSession twitterSession, String tagName, Integer count, Long maxTweetId,
                        Callback<Search> callback) {

        Twitter.getApiClient(twitterSession).getSearchService().tweets(tagName, null, null, null, null,
            count, null, null, maxTweetId, true, callback);

    }

    /**
     * Retweet a Tweet.
     *
     * @param context       activity context
     * @param originTweetId tweetId to retweet
     * @param tweetCallback callback after retweet
     * @param tweetCallback (required) Callback to be executed with result
     */
    public void reTweet(final Activity context, final Long originTweetId, final Callback<Tweet> tweetCallback) {

        final TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterSession == null) {

            login(context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    reTweetByTweetId(twitterSession, originTweetId, tweetCallback);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            reTweetByTweetId(twitterSession, originTweetId, tweetCallback);
        }

    }

    /**
     * Call API to ReTweet
     *
     * @param twitterSession
     * @param tweetId        tweetId to retweet
     * @param tweetCallback  callback after retweet
     * @param tweetCallback  (required) Callback to be executed with result
     */
    private void reTweetByTweetId(TwitterSession twitterSession, Long tweetId,
                                  Callback<Tweet> tweetCallback) {
        Twitter.getApiClient(twitterSession).getStatusesService().retweet(tweetId, true, tweetCallback);

    }


    /**
     * like or destroy a like existed
     *
     * @param context           twitter session authenticated
     * @param tweetId           tweet id want to like
     * @param isLike            if true is like, else destroy a like existed
     * @param likeTweetCallback required) Callback to be executed with result
     */
    public void likeTweet(final Activity context, final Long tweetId, final boolean isLike, final Callback<Tweet> likeTweetCallback) {
        final TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterSession == null) {

            login(context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    like(twitterSession, tweetId, isLike, likeTweetCallback);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(context, LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            like(twitterSession, tweetId, isLike, likeTweetCallback);
        }
    }

    /**
     * Call api to like or destroy a like
     *
     * @param twitterSession twitter session authenticated
     * @param tweetId        tweet id want to like
     * @param isLike         if true is like, else destroy a like existed
     * @param callback       (required) Callback to be executed with result
     */
    private void like(TwitterSession twitterSession, Long tweetId, boolean isLike, Callback<Tweet> callback) {
        if (isLike) {
            Twitter.getApiClient(twitterSession).getFavoriteService().create(tweetId, true, callback);
        } else {
            Twitter.getApiClient(twitterSession).getFavoriteService().destroy(tweetId, true, callback);
        }
    }

}
