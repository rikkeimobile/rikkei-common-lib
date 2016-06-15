package rikkei.android.common.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.io.IOException;

import rikkei.android.sns.lib.RkTwitterUtils;


/**
 * Created by cuongvv on 6/3/2016.
 */
public class DemoTwitterActivity extends Activity implements View.OnClickListener {

    /*Sign Up an account and create application keys at https://apps.twitter.com/ */
    private static final String TWITTER_KEY = "ZAulWh7QSpgqbfui4q09zJKkR";
    private static final String TWITTER_SECRET = "plreedHIA5bxvQlLIyQxU29iB7HrCCmHsOTeSp7N2Oi8MFLdFy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RkTwitterUtils.init(this, TWITTER_KEY, TWITTER_SECRET);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_test);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_post_image).setOnClickListener(this);
        findViewById(R.id.btn_get_info).setOnClickListener(this);
        findViewById(R.id.btn_retweet).setOnClickListener(this);
        findViewById(R.id.btn_gettweet).setOnClickListener(this);
        findViewById(R.id.btn_like).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                testLogin();
                break;
            case R.id.btn_post:
                testPostStatus();
                break;
            case R.id.btn_post_image:
                testPostImage();
                break;
            case R.id.btn_get_info:
                testGetProfile();
                break;
            case R.id.btn_gettweet:
                testGetTweet();
                break;
            case R.id.btn_retweet:
                testReTweet();
                break;
            case R.id.btn_like:
                testLike();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void testLike() {
        boolean isLike = true;//true is like or false is destroy a like exist
        RkTwitterUtils.getInstance().likeTweet(this, 740075429487206401l, isLike, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Log.d("testLike", "" + result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("testLike", "Exception");
            }
        });
    }

    private void testReTweet() {
        RkTwitterUtils.getInstance().reTweet(this, 740076633680318468l, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(DemoTwitterActivity.this, "Retweet success", Toast.LENGTH_SHORT).show();
                Log.d("testReTweet", "" + result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(DemoTwitterActivity.this, "Retweet fail", Toast.LENGTH_SHORT).show();
                Log.d("testReTweet", "Exception");
            }
        });
    }


    private void testGetTweet() {
        RkTwitterUtils.getInstance().searchTweetByTag(this, "#Obama", 20, 740076681747005441l - 1, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                Toast.makeText(DemoTwitterActivity.this, "Search tag #Obama success", Toast.LENGTH_SHORT).show();
                Log.e("testGetTweet", "" + result.data.tweets);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(DemoTwitterActivity.this, "Retweet fail", Toast.LENGTH_SHORT).show();
                Log.e("testGetTweet", "failure");
            }
        });
    }

    private void logout() {
        RkTwitterUtils.logout();
        Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
    }

    private void testGetProfile() {

        RkTwitterUtils.getInstance().getProfile(this, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {

                User user = userResult.data;
                Log.d("imageurl", user.profileImageUrl);
                Log.d("name", user.name);
                Log.d("des", user.description);
                Log.d("followers ", String.valueOf(user.followersCount));
                Log.d("createdAt", user.createdAt);
                Log.d("friendsCount", user.friendsCount + "");
                Log.d("location", user.location);
                Log.d("followRequestSent", user.followRequestSent + "");
                Log.d("entities", user.entities + "");
                Log.d("statusesCount", user.statusesCount + "");
                Toast.makeText(DemoTwitterActivity.this, "name=" + user.name + ", user.description="
                    + user.description + ", createdAt=" + user.createdAt + ",statusesCount="
                    + user.statusesCount + " ..... ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("testGetProfile", "error");
            }
        });
    }

    private void testPostStatus() {

        try {
            RkTwitterUtils.getInstance().post(this, "Hello Twitter! " + System.currentTimeMillis(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void testPostImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
            Intent.createChooser(intent,
                "choose picture"),
            1000);
    }

    private void testLogin() {
        RkTwitterUtils.getInstance().login(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(DemoTwitterActivity.this, "Login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(DemoTwitterActivity.this, "Login fail or canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        RkTwitterUtils.getInstance().onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {

            try {
                Uri uri = data.getData();
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (photo == null) {
                    Toast.makeText(this, "ERROR choose photo", Toast.LENGTH_SHORT).show();
                }

                try {

                    PackageManager pm = getPackageManager();
                    boolean app_installed;
                    try {
                        pm.getPackageInfo("com.twitter.android", PackageManager.GET_ACTIVITIES);
                        app_installed = true;
                    } catch (PackageManager.NameNotFoundException e) {
                        app_installed = false;
                    }

                    if (app_installed == false) {
                        Toast.makeText(this, "You don't have Twitter App installed,So you cannot Tweet with Picture", Toast.LENGTH_SHORT).show();
                    }

                    RkTwitterUtils.getInstance().post(this, "upload image test" + System.currentTimeMillis(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
