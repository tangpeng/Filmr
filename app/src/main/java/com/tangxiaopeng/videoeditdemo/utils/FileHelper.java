package com.tangxiaopeng.videoeditdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.qiniu.android.storage.KeyGenerator;
import com.tangxiaopeng.videoeditdemo.TuApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * 文件处理工具类
 *
 * @author JY
 * @Date 2016-3-17
 * @Time 下午3:22:01
 */
public class FileHelper {
    public final static String HEAD_TYPE = "head";

    public final static String HEAD_ROUND_TYPE = "head_round";

    public final static String THUMBNAIL_TYPE = "thumbnail";

    public final static String ORGINAL_TYPE = "orginal";

    public final static String CHAT_TYPE = "chat";

    public final static String ADS_TYPE = "ads";

    public final static String LOCAL_TYPE = "local";

    public final static String CAMERA_TYPE = "camera";

    public final static String VIDEO_TYPE = "video";

    public final static String VOICE_TYPE = "video";

    public final static String RECORDER_TYPE = "recorder";

    public final static String CLIP_TYPE = "clip";

    public final static String AUDIO_TYPE = "audio";

    public final static String DYNAMIC_PAGE_TYPE = "dynamic_page";

    private static FileHelper instance;

    /**
     * 用户手动保存的图片目录
     */
    private static String mSavePath;

    /**
     * 默认的缓存目录
     */
    private String cachePath;

    /**
     * 是否存在SD卡
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static FileHelper getInstance() {
        if (instance == null) {
            instance = new FileHelper();
        }
        instance.refreshPath();
        return instance;
    }

    public static FileHelper getInstanceForNative() {
        if (instance == null) {
            instance = new FileHelper();
        }
        instance.refreshPath2();
        return instance;
    }

    private FileHelper() {
    }

    private void refreshPath() {
        Context context = TuApplication.getInstance();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
            mSavePath = Environment.getExternalStorageDirectory().getPath() + "/YiSai";
        } else {
            cachePath = context.getCacheDir().getAbsolutePath();
            mSavePath = null;
        }
    }

    private void refreshPath2() {
        Context context = TuApplication.getInstance();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
            mSavePath = Environment.getExternalStorageDirectory().getPath();
        } else {
            cachePath = context.getCacheDir().getAbsolutePath();
            mSavePath = null;
        }
    }

    /**
     * 用户手动保存Image的方法，有sd卡存储到sd卡，没有不保存
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public boolean savaBitmap(String fileName, Bitmap bitmap) throws Exception {
        if (bitmap == null || mSavePath == null) {
            return false;
        }
        File folderFile = new File(mSavePath);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String path = mSavePath + File.separator + fileName;
        FileOutputStream fos = new FileOutputStream(path);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        // MediaStore.Images.Media.insertImage(TeaApplication.getInstance().getContentResolver(), path, fileName, "");
        TuApplication.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + path)));
        return true;
    }

    public String getBitmapPath(String type, String name) {
        if (type.equals(HEAD_ROUND_TYPE)) {
            type = HEAD_TYPE;
        }
        String path = cachePath + File.separator + type;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path + File.separator + name;
    }

    public String getDirectory(String type) {
        if (type.equals(HEAD_ROUND_TYPE)) {
            type = HEAD_TYPE;
        }
        String path = mSavePath + File.separator + type;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * @return 获取视频保存路径
     */
    public String getVideoSavePath() {
        return getFileStoragePath(VIDEO_TYPE) + File.separator + System.currentTimeMillis() + ".mp4";
    }

    /**
     * 断点记录文件保存的文件夹位置
     */
    public String getRecorderPath() {
        return getFileStoragePath(RECORDER_TYPE);
    }

    /**
     * 获取图片保存路径
     *
     * @return
     */
    public String getImageSaveDirectory() {
        return getDirectory(CAMERA_TYPE);
    }

    /**
     * 获取图片保存路径
     *
     * @return
     */
    public String getVoiceSaveDirectory() {
        return getDirectory(VOICE_TYPE);
    }

    /**
     * 获取视频保存路径
     */
    public String getVideoSaveDirectory() {
        String localPath;
        if (checkSDCardAvailable()) {
            localPath = Environment.getExternalStorageDirectory() + "/YiSai/";
        } else {
            localPath = TuApplication.getInstance().getFilesDir().toString() + "/YiSai/";
        }
        localPath = localPath + VIDEO_TYPE;
        Log.d(TAG, "mSavePath=" + localPath);
        return localPath;
    }

    public String getStoreCameraPath(String path) {
        String parent = getDirectory("DCIM");
        return parent + "/" +  "Camera" + "/"+ path;
    }

    public String getCurrentPhotoPath() {
        String parent = getDirectory(CAMERA_TYPE);
        return parent + File.separator + System.currentTimeMillis() + ".jpg";
    }
    public String getCurrentPhotoName() {
        return "android_"+System.currentTimeMillis() + ".jpg";
    }

    public String getCurrentVoicePath() {
        String parent = getDirectory(VOICE_TYPE);
        return parent + File.separator + System.currentTimeMillis() + ".mp3";
    }


    public String getClipPhotoPath() {
        String parent = getDirectory(CLIP_TYPE);
        return parent + File.separator + System.currentTimeMillis() + ".jpg";
    }
    public String getPhotoPath() {
        return "android_"+System.currentTimeMillis() + ".jpg";
    }

    public String getCurrentRecordPath(String name) {
        String parent = getDirectory(AUDIO_TYPE);
        return parent + File.separator + name + ".amr";
    }

    public static boolean saveBitmap(Bitmap bmp, File saveTo) {
        try {
            FileOutputStream fos = new FileOutputStream(saveTo);
            return saveBitmap(bmp, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bmp, OutputStream saveTo) {

        try {
            boolean success = bmp.compress(CompressFormat.JPEG, 100, saveTo);
            saveTo.flush();
            saveTo.close();
            return success;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 图片下载成功后保存到本地
     */
    public boolean saveBitmap(InputStream is, String type, String name) {
        if (is == null) {
            return false;
        }
        String path = getBitmapPath(type, name);
        Log.d("FileHelper", "path=" + path);
        File file = new File(path);
        FileOutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = is.read(buffer)) > 0) {
                out.write(buffer, 0, readLength);
            }
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean saveBitmap(InputStream is, String name) {
        if (is == null) {
            return false;
        }
        String path = getBitmapPath(name);
        File file = new File(path);
        FileOutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = is.read(buffer)) > 0) {
                out.write(buffer, 0, readLength);
            }
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getBitmapPath(String name) {
        String path = cachePath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path + File.separator + name;
    }

    /**
     * 语音下载成功后保存到本地
     */
    public boolean saveVoice(InputStream is, String recordId) {
        if (is == null) {
            return false;
        }
        String path = getCurrentRecordPath(recordId);
        File file = new File(path);
        FileOutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = is.read(buffer)) > 0) {
                out.write(buffer, 0, readLength);
            }
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 递归删除一个目录中的所有文件
     */
    public static void delAllFiles(String filepath) {
        File f = new File(filepath);
        if (f.exists() && f.isDirectory()) {
            File[] delFile = f.listFiles();
            if (delFile == null || delFile.length == 0) {
                return;
            }
            File[] childFiles = f.listFiles();
            int i = null != childFiles ? childFiles.length : 0;
            for (int j = 0; j < i; j++) {
                if (delFile[j].isDirectory()) {
                    delAllFiles(delFile[j].getAbsolutePath());
                }
                delFile[j].delete();
            }
        }
    }

    private static final String TAG = "FileHelper";

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long byte字节
     * @throws Exception
     */
    public long getFolderSize(File file) {
        if (null == file) {
            return 0;
        }
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            size = 0;
            Log.e(TAG, "计算图片缓存大小失败！：" + e.getMessage());
        }
        return size;
    }


    /**
     * 把文件读入内存，转变成字节数组的形式
     */
    public static byte[] fileToBytes(String filePath) {
        byte[] b = null;
        try {
            File file = new File(filePath);
            if (file != null) {
                FileInputStream fis = new FileInputStream(file);
                if (fis != null) {
                    int len = fis.available();
                    b = new byte[len];
                    fis.read(b);
                }
                fis.close();
            }
        } catch (Exception e) {
        }
        return b;
    }

    /**
     * Check the SD card
     *
     * @return 是否存在SDCard
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getFileStoragePath(String type) {
        String localPath;
        if (checkSDCardAvailable()) {//检查是否安装SD卡
            localPath = Environment.getExternalStorageDirectory() + "/YiSai/";
        } else {
            localPath = TuApplication.getInstance().getFilesDir().toString() + "/YiSai/";
        }
        localPath = localPath + type;
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return localPath;
    }

    /**
     *
     * @param key 文件名    1493863989621.mp4
     * @param file 文件的绝对路径  /storage/emulated/YiSai/video/1493863989621.mp4
     * @return
     */
    public String getRecoderFileName(String key, File file) {
        KeyGenerator keyGen = (key1, file1) -> {
            // 不必使用url_safe_base64转换，uploadManager内部会处理
            // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
            return key1 + "_._" + new StringBuffer(file1.getAbsolutePath()).reverse();
        };
        return keyGen.gen(key, file);
    }


    public String getRecoderFileAbsolutePath(String key,File file) {
        return getRecorderPath() + "/" + hash(getRecoderFileName(key,file));
    }




    public static String hash(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(base.getBytes());
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                hexString.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            return hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 主要是输入流的使用，最常用的写法
     * @param filePath
     * @return
     */
    public String read(String filePath)
    {
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        byte[] b = new byte[8 * 1024];
        InputStream in = null;
        try
        {
            // 文件输入流
            in = new FileInputStream(filePath);
            while (in.read(b) != -1)
            {
                // 字符串拼接
                txtContent.append(new String(b));
            }
            // 关闭流
            in.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return txtContent.toString();
    }
}
