package com.android.imeng.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.android.imeng.util.FastBlur;
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
    // 高斯模糊显示
    @ViewInject(R.id.blur_view)
    View blurView;
    private Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        blurView.setVisibility(View.INVISIBLE);
        // TODO 设置个人形象
    }

    @OnClick({R.id.emoji_btn, R.id.camera_btn, R.id.choose_pic_btn, R.id.photo_album_view, R.id.me_favorite_view})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.emoji_btn: // 自定义表情
                // 高斯模糊
                applyBlur();
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

    /**
     * 高斯模糊整个界面
     */
    private void applyBlur() {
        blurView.setBackground(null);
        blurView.setVisibility(View.VISIBLE);

        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        /**
         * 获取当前窗口快照，相当于截屏
         */
        Bitmap bmp1 = view.getDrawingCache();
        int height = getOtherHeight();
        /**
         * 除去状态栏和标题栏
         */
        Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);
        blur(bmp2, blurView);
    }

    @SuppressLint("NewApi")
    private void blur(Bitmap bkg, View view) {
        float scaleFactor = 8;//图片缩放比例；
        float radius = 20;//模糊程度

        Bitmap blurBitmap = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blurBitmap);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()/ scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        blurBitmap = FastBlur.doBlur(blurBitmap, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), blurBitmap));
    }

    /**
     * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
     * @return
     */
    private int getOtherHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        int titleBarHeight = contentTop - statusBarHeight;
        int titleBarHeight = 0;
        return statusBarHeight + titleBarHeight;
    }
}
