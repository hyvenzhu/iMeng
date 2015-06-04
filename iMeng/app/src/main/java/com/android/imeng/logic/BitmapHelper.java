package com.android.imeng.logic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.android.imeng.AppDroid;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;

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
     * 返回服务器对应的已下载的本地文件路径
     * @param serverUrl
     * @return null表示本地不存在
     */
    public static String getLocalPath(String serverUrl)
    {
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
