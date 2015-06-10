package com.android.imeng.logic.model;

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
    private int no; // 头发编号
    private int categoryId; // 衣服类别
    private State mState = State.INIT;

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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获得大图本地路径
     * @return
     */
    public String getOriginalLocalPath()
    {
        if (TextUtils.isEmpty(originalUrl))
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
            return file.getAbsolutePath();
        }
    }

    public State getmState() {
        return mState;
    }

    public void setmState(State mState) {
        this.mState = mState;
    }

    public enum State
    {
        INIT, // 未下载
        DOWNLOADING,// 下载中
        ERROR,// 下载失败
        SUCCESS, // 下载成功
    }
}
