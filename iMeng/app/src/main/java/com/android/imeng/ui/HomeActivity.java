package com.android.imeng.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.emoji_btn, R.id.camera_btn, R.id.choose_pic_btn, R.id.photo_album_view, R.id.me_favorite_view})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.emoji_btn: // 自定义表情
                startActivity(new Intent(this, SelectSexActivity.class));
                overridePendingTransition(R.anim.fade_int, R.anim.fade_out);
                break;
            case R.id.camera_btn: // 魔法相机

                break;
            case R.id.choose_pic_btn: // 照片变身

                break;
            case R.id.photo_album_view: // 表情相册

                break;
            case R.id.me_favorite_view: // 我的收藏

                break;
        }
    }
}
