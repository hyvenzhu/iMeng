package com.android.imeng.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.ui.base.FavoriteActivity;
import com.android.imeng.ui.decorate.cartoon.SelectSexActivity;
import com.android.imeng.ui.decorate.photo.FaceDetectiveActivity;
import com.android.imeng.ui.gallery.ImageGalleryActivity;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.android.imeng.util.FastBlur;
import com.android.imeng.util.SPDBHelper;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

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
        setWallpaper();
    }

    String wallpaperIdentifier = null;
    /**
     * 设置背景墙
     */
    private void setWallpaper()
    {
        String wallpaper = new SPDBHelper().getString("wallpaper", String.valueOf(R.drawable.default_wallpaper));
        int timeSlot = APKUtil.getTimeSlot();
        String bgWallIdentifier = "home_head_wallpaper" + timeSlot; // 背景
        wallpaperIdentifier = null; // 形象
        boolean isDefault = false;
        if (!new File(wallpaper).exists() || String.valueOf(R.drawable.default_wallpaper).equals(wallpaper)) // 默认
        {
            wallpaperIdentifier = "default_wallpaper" + timeSlot;
            isDefault = true;
        }
        else // 每日形象文件夹
        {
            wallpaperIdentifier = "_wallpaper" + timeSlot;
            isDefault = false;
        }
        if (isDefault)
        {
            selfImageView.setImageResource(APKUtil.getDrawableByIdentify(this, wallpaperIdentifier));

            // 小形象
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(R.drawable.home_small_default_self_image))
                    .build();
            smallSelfImageView.setImageURI(uri);
        }
        else
        {
            File[] dirs = new File(wallpaper).getParentFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().equals(Constants.DAILY_DIR);
                }
            });
            if (dirs != null && dirs.length == 1)
            {
                File dailyDir = dirs[0];
                File[] files =  dailyDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(wallpaperIdentifier);
                    }
                });
                if (files != null && files.length == 1)
                {
                    wallpaperIdentifier = files[0].getAbsolutePath();
                    String wallpaperSay = "wallpaper_say" + timeSlot;
                    Drawable imageDrawable = new BitmapDrawable(getResources(), wallpaperIdentifier);
                    Drawable sayDrawable = getResources().getDrawable(APKUtil.getDrawableByIdentify(this, wallpaperSay));
                    Drawable wallpaperDrawable = BitmapHelper.overlayDrawable(imageDrawable, sayDrawable);
                    selfImageView.setImageDrawable(wallpaperDrawable);

                    // 小形象
                    smallSelfImageView.setImageURI(Uri.fromFile(new File(wallpaper)));
                }
                else
                {
                    wallpaperIdentifier = "default_wallpaper" + timeSlot;
                    selfImageView.setImageResource(APKUtil.getDrawableByIdentify(this, wallpaperIdentifier));

                    // 小形象
                    Uri uri = new Uri.Builder()
                            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                            .path(String.valueOf(R.drawable.home_small_default_self_image))
                            .build();
                    smallSelfImageView.setImageURI(uri);
                }
            }
            else
            {
                wallpaperIdentifier = "default_wallpaper" + timeSlot;
                selfImageView.setImageResource(APKUtil.getDrawableByIdentify(this, wallpaperIdentifier));

                // 小形象
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.drawable.home_small_default_self_image))
                        .build();
                smallSelfImageView.setImageURI(uri);
            }
        }
        smallSelfImageView.getHierarchy().setActualImageFocusPoint(new PointF(0.4f, 0.4f));
        imageWallpaperView.setBackgroundResource(APKUtil.getDrawableByIdentify(this, bgWallIdentifier));
    }

    @OnClick({R.id.emoji_btn, R.id.camera_btn, R.id.choose_pic_btn, R.id.photo_album_view,
            R.id.me_favorite_view, R.id.small_lay})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.emoji_btn: // 自定义表情
                startActivity(new Intent(this, SelectSexActivity.class));
                overridePendingTransition(R.anim.fade_in, 0);
                // 高斯模糊
                applyBlur();
                break;
            case R.id.camera_btn: // 魔法相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File picFile = new File(APKUtil.getDiskCacheDir(this, Constants.TEMP_DIR), System.currentTimeMillis() + ".jpg");
                photoUri = Uri.fromFile(picFile);
                intent.putExtra("camerasensortype", 2); // 前置摄像头
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
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.small_lay: // 设置背景墙
                // 高斯模糊
                applyBlur();
                startActivity(new Intent(this, WallpaperActivity.class));
                overridePendingTransition(R.anim.top_in, 0);
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
        blurView.setBackgroundDrawable(null);
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
        float radius = 10;//模糊程度

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
