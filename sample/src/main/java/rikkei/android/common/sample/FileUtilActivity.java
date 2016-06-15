package rikkei.android.common.sample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import rikkei.android.common.lib.RkFileUtils;

public class FileUtilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_util);
    }

    public void listFileAssets(View view) {
        String[] files = RkFileUtils.getListFileFromAsset(this);

        String result = "";
        for (int i = 0; i < files.length; i++) {
            result += "\n" + files[i];
        }
        showResult(result);
    }

    public void listFilePathAssets(View view) {
        String path = "images";
        String[] files = RkFileUtils.getListFileFromAssetsByPath(this, path);
        String result = "";
        for (int i = 0; i < files.length; i++) {
            result += "\n" + files[i];
        }
        showResult(result);
    }

    public void listFileSd(View view) {
        File[] files = RkFileUtils.getListFileFromSdCard(null);
        String result = "";
        for (int i = 0; i < files.length; i++) {
            result += "\n" + files[i].getName();
        }
        showResult(result);
    }

    public void readTextFileAssets(View view) {
        String file = "hello.txt";
        String content = RkFileUtils.readAssetStringFile(this, file, null);
        showResult(content);
    }

    public void readTextFileSd(View view) {
        String file = "json.txt";
        String content = RkFileUtils.readStringFromSdCard(file);
        showResult(content);
    }

    public void readBitmapFileAssets(View view) {
        String file = "images/ic_launcher.png";
        Bitmap content = RkFileUtils.getBitmapFromAsset(this, file);
        showResult(content);
    }

    public void readBitmapFileSd(View view) {
        String file = "images/20150406_0937.jpg";
        Bitmap content = RkFileUtils.getBitmapFromSdCard(file);
        showResult(content);
    }

    public void getInputStreamAssets(View view) {

    }

    public void getInputStreamSd(View view) {

    }

    private void showResult(Bitmap result) {
        ImageView bmResult = new ImageView(this);
        bmResult.setAdjustViewBounds(true);
        bmResult.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        FrameLayout layout = (FrameLayout) findViewById(R.id.fm_result);
        if (layout != null) {
            layout.removeAllViews();
            layout.addView(bmResult);
            bmResult.setImageBitmap(result);
        }
    }

    private void showResult(String result) {
        TextView tvResult = new TextView(this);
        tvResult.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        FrameLayout layout = (FrameLayout) findViewById(R.id.fm_result);
        if (layout != null) {
            layout.removeAllViews();
            layout.addView(tvResult);
            tvResult.setText(result);
        }
    }
}
