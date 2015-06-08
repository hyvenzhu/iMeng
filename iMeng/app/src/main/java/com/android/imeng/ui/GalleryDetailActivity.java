package com.android.imeng.ui;

import android.content.Context;
import android.content.Intent;

import com.android.imeng.framework.ui.BasicActivity;

/**
 * 相册详情界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-08 23:01]
 */
public class GalleryDetailActivity extends BasicActivity {

    /**
     * 跳转
     * @param galleryDir 相册文件夹
     * @param activity
     */
    public static void actionStart(String galleryDir, Context activity)
    {
        Intent intent = new Intent(activity, GalleryDetailActivity.class);
        intent.putExtra("galleryDir", galleryDir);
        activity.startActivity(intent);
    }
}
