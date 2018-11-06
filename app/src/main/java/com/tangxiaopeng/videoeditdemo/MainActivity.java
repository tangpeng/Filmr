package com.tangxiaopeng.videoeditdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.PermissionChecker;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;

/**
 * @author fanqie
 * @dec
 * @date 2018/10/10 15:34
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isQiniu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void onClickMinYisaiDemo(View v) {
        if (isPermissionOK()) {
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("url", "https://www.baidu.com/");
            startActivity(intent);
        }
    }


    public void onClickFilmrDemo(View view) {
        if (isPermissionOK()) {
//            jumpToActivity(VideoFilmrActivity.class);
//            jumpToActivity(WebAddShareActivity.class);
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
//          intent.putExtra("url","http://120.25.121.111/static/eysai-app/result.html?device=android");
//          intent.putExtra("url","http://www.miaopai.com/show/jQmcX-VwHfW0ECwTqgcT6TMbir2Ujq~G1hUiYQ__.htm");
            intent.putExtra("url", "http://120.25.121.111/static/eysai-app/videoresult.html?device=android");
            startActivity(intent);
        }
    }

    public void onClickFilmrDemos(View view) {
        if (isPermissionOK()) {
            Intent intent = new Intent(MainActivity.this, WebVideoActivity.class);
            intent.putExtra("url", "http://120.25.121.111/static/eysai-app/videoresult.html?device=android");
            startActivity(intent);
        }
    }

    public void onClickFilmrFragmentDemo(View view) {
        if (isPermissionOK()) {
            addLoacalVideo();
        }
    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            ToastUtils.s(this, "Some permissions is not approved !!!");
        }
        return isPermissionOK;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = GetPathFromUri.getPath(MainActivity.this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                try {

                    Intent intent = new Intent(MainActivity.this, BekidMainActivity.class);
                    intent.putExtra("url", selectedFilepath);
                    startActivity(intent);

                } catch (Exception e) {
                    Log.i(TAG, "e=" + e.getMessage());
                }
            }
        } else {
        }
    }

    private void addLoacalVideo() {
        Intent intentvideo = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentvideo.setAction(Intent.ACTION_GET_CONTENT);
            intentvideo.setType("video/*");
        } else {
            intentvideo.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intentvideo.addCategory(Intent.CATEGORY_OPENABLE);
            intentvideo.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intentvideo, "选择要导入的视频"), 0);
    }

}
