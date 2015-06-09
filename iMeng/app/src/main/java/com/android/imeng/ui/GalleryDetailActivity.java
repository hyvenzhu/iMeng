package com.android.imeng.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.util.Constants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileFilter;

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

    @ViewInject(R.id.small_cover_view)
    private SimpleDraweeView smallCoverView; // 个人头像小
    @ViewInject(R.id.title_txt)
    private TextView titleTxt; // 标题
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    @ViewInject(R.id.select_indicator)
    private ViewPager selectIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);
    }

    @Override
    protected void init() {
        super.init();
        titleTxt.setText("表情相册");

        // 相册文件夹
        String galleryDir = getIntent().getStringExtra("galleryDir");
        // 封面图片
        File coverFile = new File(galleryDir, Constants.GALLERY_COVER);
        smallCoverView.setImageURI(Uri.fromFile(coverFile));

        // 相册所有图片
        File[] files = new File(galleryDir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // 排除掉每日形象文件夹以及封面
                return !pathname.isDirectory() && !pathname.getName().startsWith(Constants.GALLERY_COVER);
            }
        });
        if (files != null && files.length > 0)
        {

        }
    }

    @OnClick({R.id.left_area_lay})
    public void onViewClick(View v)
    {

    }
}
