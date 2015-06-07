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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
     * Drawable转Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
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
}
