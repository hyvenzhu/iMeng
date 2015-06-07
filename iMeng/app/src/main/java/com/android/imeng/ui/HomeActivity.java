package com.android.imeng.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

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

    private Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO 设置个人形象
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File picFile = new File(APKUtil.getDiskCacheDir(this, Constants.TEMP_DIR), System.currentTimeMillis() + ".jpg");
                photoUri = Uri.fromFile(picFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                break;
            case R.id.choose_pic_btn: // 照片变身
                intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK);
                break;
            case R.id.photo_album_view: // 表情相册
                startActivity(new Intent(this, ImageGalleryActivity.class));
                break;
            case R.id.me_favorite_view: // 我的收藏

                break;
        }
    }

    public final int REQUEST_CODE_CAPTURE = 100; // 拍照
    public final int REQUEST_CODE_PICK = 101; // 相册
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CODE_PICK:
                    if (data != null)
                    {
                        photoUri = data.getData();
                    }
                    break;
            }


            if (photoUri != null)
            {
                Intent faceIntent = new Intent(this, FaceDetectiveActivity.class);
                faceIntent.putExtra("photoUri", photoUri);
                startActivity(faceIntent);
            }
        }
    }
}
