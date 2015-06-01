package com.android.imeng.logic;

import android.net.Uri;
import android.text.TextUtils;

import com.android.imeng.AppDroid;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;

/**
 * 图片基本信息
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-01 21:57]
 */
public class PictureInfo {
    private String thumbnailUrl; // 图片缩略图
    private String originalUrl; // 图片原图（大图）

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    /**
     * 获得大图本地路径
     * @return
     */
    public Uri getOriginalLocalUri()
    {
        if (!TextUtils.isEmpty(originalUrl))
        {
            return null;
        }
        String localPath = APKUtil.stringToMD5(originalUrl);

        File dir = APKUtil.getDiskCacheDir(AppDroid.getInstance().getApplicationContext(), Constants.DOWNLOAD_DIR);
        File file = new File(dir, localPath);
        if (!file.exists())
        {
            return null;
        }
        else
        {
            return Uri.fromFile(file);
        }
    }
}
