package com.android.imeng.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 基础工具类 [尽量减少类似Util的类存在]
 * 
 * @author hiphonezhu@gmail.com
 * @version [Android-BaseLine, 2014-8-29]
 */
public class APKUtil {
    /**
     * 获得版本号
     *
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getApplicationContext().getPackageManager()
                    .getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    /**
     * 获得版本名称
     *
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getApplicationContext().getPackageManager()
                    .getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获得APP包名
     *
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    /**
     * 获得磁盘缓存目录 [PS：应用卸载后会被自动删除]
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getApplicationContext().getExternalCacheDir().getPath();
        } else {
            cachePath = context.getApplicationContext().getFilesDir().getPath();
        }
        File dir = new File(cachePath + File.separator + uniqueName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 组装参数
     *
     * @param parameters
     * @return
     */
    public static String getParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = parameters.keySet();
        Iterator<String> keysIt = keys.iterator();
        while (keysIt.hasNext()) {
            String key = keysIt.next();
            if (!TextUtils.isEmpty(key)) {
                Object value = parameters.get(key);
                if (value == null) {
                    value = "";
                }
                sb.append(key + "=" + value + "&");
            }
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * dp转px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将字符串转成MD5值
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 保存流到文件
     * @param is
     * @param savePath
     * @throws IOException
     */
    public static void save2File(InputStream is, String savePath) throws IOException {
        File saveFile = new File(savePath);
        if (!saveFile.exists())
        {
            saveFile.createNewFile();
        }
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(saveFile);
            byte[] buffer = new byte[2048];
            int len = -1;
            while((len = is.read(buffer)) != -1)
            {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
            if (is != null)
            {
                is.close();
            }
        }
    }

    /**
     * 根据Uri查找对应的文件路径
     * @return
     */
    public static String uri2LocalPath(Uri uri, Context context)
    {
        if (uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex("_data");
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                cursor = null;
                if (picturePath == null || picturePath.equals("null")) {
                    return null;
                } else {
                    return picturePath;
                }
            } else {
                File file = new File(uri.getPath());
                if (!file.exists()) {
                    return null;
                } else {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * 根据图片名称加载图片id
     * @param context
     * @param drawableName
     * @return
     */
    public static int getDrawableByIdentify(Context context, String drawableName)
    {
        Resources resources = context.getApplicationContext().getResources();
        int indentify = resources.getIdentifier(context.getPackageName() + ":drawable/" + drawableName, null, null);
        return indentify;
    }

    /**
     * 删除文件、文件夹
     * @param path
     * @return
     */
    public static boolean deleteFile(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return false;
        }
        File file = new File(path);
        if (!file.isDirectory() && file.exists())
        {
            return file.delete();
        }
        else
        {
            File[] files = file.listFiles();
            if (files == null)
            {
                return false;
            }
            for(File fileIn : files)
            {
                deleteFile(fileIn.getAbsolutePath());
            }
            return file.delete();
        }
    }

    /**
     * 返回当前时间段(左开右闭)
     * @return 1：06:00-08:00  2：08:00-11:30  3：11:30-12:30  4：12:30-14:00  5：14:00-18:00
     * 6：18:00-22:00  7：22:00-06:00
     */
    public static int getTimeSlot()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hhmm =  sdf.format(new Date());
        if (hhmm.compareTo("06:00") > 0 && hhmm.compareTo("08:00") <= 0)
        {
            return 1;
        }
        else if (hhmm.compareTo("08:00") > 0 && hhmm.compareTo("11:30") <= 0)
        {
            return 2;
        }
        else if (hhmm.compareTo("11:30") > 0 && hhmm.compareTo("12:30") <= 0)
        {
            return 3;
        }
        else if (hhmm.compareTo("12:30") > 0 && hhmm.compareTo("14:00") <= 0)
        {
            return 4;
        }
        else if (hhmm.compareTo("14:00") > 0 && hhmm.compareTo("18:00") <= 0)
        {
            return 5;
        }
        else if (hhmm.compareTo("18:00") > 0 && hhmm.compareTo("22:00") <= 0)
        {
            return 6;
        }
        else
        {
            return 7;
        }

    }
}
