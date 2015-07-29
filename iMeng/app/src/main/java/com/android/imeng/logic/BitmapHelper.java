package com.android.imeng.logic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;

import com.android.imeng.AppDroid;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 图像操作帮助类
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-01 21:33]
 */
public class BitmapHelper {

    /**
     * 叠加Drawable
     * @param drawables
     * @return
     */
    public static Drawable overlayDrawable(Drawable... drawables)
    {
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        return layerDrawable;
    }

    /**
     * 按顺序叠加图片, 0在最下面, 以此类推
     * @param drawableMap
     * @param totalCount 总计多少层
     * @return
     */
    public static Drawable overlay(Map<Integer, Drawable> drawableMap, int totalCount)
    {
        Drawable drawable = null;
        for(int i = 0; i < totalCount; i++)
        {
            if (drawable == null)
            {
                if (drawableMap.get(i) == null)
                {
                    continue;
                }
                drawable = overlayDrawable(drawableMap.get(i));
            }
            else
            {
                if (drawableMap.get(i) == null)
                {
                    continue;
                }
                drawable = overlayDrawable(drawable, drawableMap.get(i));
            }
        }
        return drawable;
    }

    /**
     * Drawable转Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap
                .createBitmap(
                        intrinsicWidth,
                        intrinsicHeight,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth,
                intrinsicHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap存文件
     * @param bitmap
     * @param filePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void bitmap2File(Bitmap bitmap, String filePath) throws FileNotFoundException, IOException
    {
        File f = new File(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } finally {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * 返回服务器对应的已下载的本地文件路径
     * @param serverUrl
     * @return null表示本地不存在
     */
    public static String getLocalPath(String serverUrl)
    {
        if (TextUtils.isEmpty(serverUrl))
        {
            return null;
        }
        String localPath = APKUtil.stringToMD5(serverUrl);
        File dir = APKUtil.getDiskCacheDir(AppDroid.getInstance().getApplicationContext(), Constants.DOWNLOAD_DIR);
        File file = new File(dir, localPath);
        if (file.exists())
        {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
    * 使用文件通道的方式复制文件
    * @param s
    *            源文件
    * @param t
    *            复制到的新文件
    */
    public static void copyFile(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
