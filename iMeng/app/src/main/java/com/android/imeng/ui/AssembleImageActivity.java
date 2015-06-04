package com.android.imeng.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.imeng.R;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.logic.PictureInfo;
import com.android.imeng.util.Constants;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 组装个人形象界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/04 17:09]
 */
public class AssembleImageActivity extends BasicActivity{

    /**
     * 跳转
     * @param faceUrl 脸地址
     * @param sex 性别 0：女 1：男
     * @param context 上下文
     */
    public static void actionStart(String faceUrl, int sex, Context context)
    {
        Intent intent = new Intent(context, AssembleImageActivity.class);
        intent.putExtra("faceUrl", faceUrl);
        intent.putExtra("sex", sex);
        context.startActivity(intent);
    }

    @ViewInject(R.id.image_wall)
    private View imageWall; // 背景墙
    @ViewInject(R.id.image_view)
    private ImageView imageView; // 形象展示View
    @ViewInject(R.id.face_btn)
    private Button faceBtn; // 表情Tab
    @ViewInject(R.id.clothes_btn)
    private Button clothesBtn; // 衣服Tab
    @ViewInject(R.id.decoration_btn)
    private Button decorationBtn; // 表情Tab
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    private String faceUrl; // 脸地址
    private int sex; // 性别
    private NetLogic netLogic;
    // key 0：脸  1：衣服   2：装饰
    private Map<Integer, Drawable> drawableMap = new HashMap<Integer, Drawable>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemble_image);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "捏表情", true);
        leftBtn.setText("返回");
        rightBtn.setVisibility(View.VISIBLE);

//        faceBtn.post(new Runnable() {
//            @Override
//            public void run() {
//                Drawable faceBottomDrawable =  faceBtn.getCompoundDrawables()[3];
//                faceBottomDrawable.setBounds(0, 0, faceBtn.getMeasuredWidth(), faceBottomDrawable.getIntrinsicHeight());
//            }
//        });

        faceUrl = getIntent().getStringExtra("faceUrl");
        sex = getIntent().getIntExtra("sex", 0);
        netLogic = new NetLogic(this);

        // 调整宽高
        adjustView();
        // 背景墙
        adjustWall();
        // 加载表情
        loadFace();
    }

    /**
     * 调整宽高
     */
    private void adjustView()
    {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                // 1、调整背景墙, 如果宽度>500且高度<500，则高度调节为500
                int wallWidth = imageWall.getMeasuredWidth();
                int wallHeight = imageWall.getMeasuredHeight();
                if (wallWidth > Constants.IMAGE_WIDTH_HEIGHT && wallHeight < Constants.IMAGE_WIDTH_HEIGHT)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)imageWall.getLayoutParams();
                    layoutParams.height = Constants.IMAGE_WIDTH_HEIGHT;
                    layoutParams.weight= 0;
                    imageWall.setLayoutParams(layoutParams);
                    imageWall.requestLayout();
                }

                // 2、调整形象，取宽与高的较小值，如果较小值仍大于500，则设置宽高都为500，否则设置宽和高为较小值
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                int viewWidth = imageView.getMeasuredWidth();
                int viewHeight = imageView.getMeasuredHeight();

                int minSize = Math.min(viewWidth, viewHeight);
                if (minSize > Constants.IMAGE_WIDTH_HEIGHT)
                {
                    minSize = Constants.IMAGE_WIDTH_HEIGHT;
                }
                layoutParams.width = minSize;
                layoutParams.height = minSize;
                imageView.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * 性别调节背景墙
     */
    private void adjustWall()
    {
        if (sex == 0)
        {
            imageWall.setBackgroundColor(Color.parseColor("#CAE4F3"));
        }
        else
        {
            imageWall.setBackgroundColor(Color.parseColor("#f3bec4"));
        }
    }

    /**
     * 加载脸
     */
    private void loadFace()
    {
        String facePath = BitmapHelper.getLocalPath(faceUrl);
        if (!TextUtils.isEmpty(facePath))
        {
            Drawable faceDrawable = new BitmapDrawable(getResources(), facePath);
            drawableMap.put(0, faceDrawable);
            imageView.setImageDrawable(overlay());
        }
        else
        {
            netLogic.downloadImage(faceUrl);
        }
    }

    /**
     * 图层叠加
     * @return
     */
    private Drawable overlay()
    {
        Drawable faceDrawable = drawableMap.get(0);
        Drawable clothesDrawable = drawableMap.get(1);
        Drawable decorationDrawable = drawableMap.get(2);
        Drawable drawable = null;
        if (faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(faceDrawable);
        }

        if (drawable != null && clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, clothesDrawable);
        }
        else if (clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(clothesDrawable);
        }

        if (drawable != null && decorationDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, decorationDrawable);
        }
        else if (decorationDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(decorationDrawable);
        }
        return drawable;
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.download:
                if (checkResponse(msg))
                {
                    PictureInfo pictureInfo = (PictureInfo)(((InfoResult)msg.obj).getExtraObj());
                    Drawable faceDrawable = new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath());
                    drawableMap.put(0, faceDrawable);
                    imageView.setImageDrawable(overlay());
                }
                break;
        }
    }
}
