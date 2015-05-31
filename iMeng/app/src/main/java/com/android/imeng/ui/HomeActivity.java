package com.android.imeng.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 首页
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-05-30 23:58]
 */
public class HomeActivity extends BasicActivity {
    // 小形象
    @ViewInject(R.id.small_self_image_view)
    private SimpleDraweeView smallSelfImageView;
    // 背景墙
    @ViewInject(R.id.image_wallpaper_view)
    private View imageWallpaperView;
    // 个人形象大
    @ViewInject(R.id.self_image_view)
    private ImageView selfImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
