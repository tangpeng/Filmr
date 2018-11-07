package com.tangxiaopeng.videoeditdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.apeng.permissions.EsayPermissions;
import com.apeng.permissions.OnPermission;
import com.apeng.permissions.Permission;
import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;
import com.tangxiaopeng.videoeditdemo.utils.Tools;

import java.util.List;

/**
 * @author fanqie
 * @dec
 * @date 2018/10/10 15:34
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;
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
        EsayPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//                .permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA, Permission.RECORD_AUDIO)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Tools.showToast(context, "获取权限成功");

                        } else {
                            Tools.showToast(context, "获取权限成功，部分权限未正常授予");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Tools.showToast(context, "被永久拒绝授权，请手动授予权限");
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            EsayPermissions.gotoPermissionSettings(context);
                        } else {
                            Tools.showToast(context, "获取权限失败");
                        }
                    }
                });
        return true;
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
