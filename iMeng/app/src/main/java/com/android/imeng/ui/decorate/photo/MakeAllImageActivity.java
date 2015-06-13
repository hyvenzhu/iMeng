package com.android.imeng.ui.decorate.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.android.imeng.R;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.model.ClothesAndExpression;
import com.android.imeng.logic.ImageChooseListener;
import com.android.imeng.logic.model.ImageInfo;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.ui.decorate.photo.adapter.ImageAdpater;
import com.android.imeng.ui.base.adapter.ViewPagerAdapter;
import com.android.imeng.ui.gallery.ImageGalleryActivity;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成所有形象界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 21:16]
 */
public class MakeAllImageActivity extends BasicActivity implements AdapterView.OnItemClickListener,
        ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, ImageChooseListener{
    /**
     * 跳转
     * @param sex 性别 0：女 1：男
     * @param categoryId 衣服类别
     * @param faceShape 脸型
     * @param hairBackground 背后头发
     * @param hairFont 前面头发
     * @param decoration 装饰
     * @param faceUrl 脸地址
     * @param context 上下文
     */
    public static void actionStart(int sex, int categoryId, int faceShape,
               String hairBackground, String hairFont, String decoration, String faceUrl, Context context)
    {
        Intent intent = new Intent(context, MakeAllImageActivity.class);
        intent.putExtra("sex", sex);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("faceShape", faceShape);
        intent.putExtra("hairBackground", hairBackground);
        intent.putExtra("hairFont", hairFont);
        intent.putExtra("decoration", decoration);
        intent.putExtra("faceUrl", faceUrl);
        context.startActivity(intent);
    }

    @ViewInject(R.id.draw_view)
    private ImageView drawView; // 正在绘画提示
    @ViewInject(R.id.draw_lay)
    private View drawLay;
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager; // 形象容器
    @ViewInject(R.id.select_indicator)
    private RadioGroup selectIndicator; // 指示器
    @ViewInject(R.id.save_btn)
    private Button saveBtn; // 保存表情相册

    private int sex; // 性别
    private int categoryId; // 衣服类别
    private int faceShape; // 脸型
    private String hairBackground; // 背后头发
    private String hairFont; // 前面头发
    private String decoration; // 装饰
    private String faceUrl; // 脸地址
    private List<ClothesAndExpression> clothesAndExpressions;
    private NetLogic netLogic;
    // 图片合成文件夹
    File imageDir;
    private List<ImageInfo> choosedImageInfos = new ArrayList<ImageInfo>(); // 选择的形象
    private List<ImageInfo> dailyImageInfos = new ArrayList<>(); // 每天的7张形象

    AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_all_image);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterAll(netLogic);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "", false);
        leftBtn.setText("返回");

        sex = getIntent().getIntExtra("sex", 0);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        faceShape = getIntent().getIntExtra("faceShape", 0);
        hairBackground = getIntent().getStringExtra("hairBackground");
        hairFont = getIntent().getStringExtra("hairFont");
        decoration = getIntent().getStringExtra("decoration");
        faceUrl = getIntent().getStringExtra("faceUrl");
        netLogic = new NetLogic(this);

        // 加载表情和衣服
        loadFaceAndClothes();
    }

    /**
     * 加载表情和衣服
     */
    private void loadFaceAndClothes()
    {
        drawView.setVisibility(View.VISIBLE);
        animationDrawable = (AnimationDrawable)drawView.getDrawable();
        animationDrawable.start();

        netLogic.clothesAndExpression(sex, categoryId, faceShape);
    }

    private void loadGridView()
    {
        final int viewWidth = viewPager.getMeasuredWidth();
        int numColumns = 2;
        int hoizontalSpacing = APKUtil.dip2px(this, 10);
        int itemSize = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));

        GridView[] gridViews = new GridView[5];
        for(int i = 0; i < 5; i++)
        {
            GridView grid = new GridView(this);
            grid.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            grid.setHorizontalSpacing(APKUtil.dip2px(this, 10));
            grid.setVerticalSpacing(APKUtil.dip2px(this, 10));
            grid.setNumColumns(2);
            grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
            grid.setOnItemClickListener(this);
            gridViews[i] = grid;

            ImageAdpater imageAdpater = new ImageAdpater(this, localPaths.subList(i * 4, i * 4 + 4),
                    R.layout.layout_item_image, this, i);
            imageAdpater.setSize(itemSize);
            grid.setAdapter(imageAdpater);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)viewPager.getLayoutParams();
        params.height = 2 * itemSize + hoizontalSpacing;
        viewPager.setLayoutParams(params);
        viewPager.setAdapter(new ViewPagerAdapter(gridViews));
        viewPager.setOnPageChangeListener(this);
        // 选中第一个
        selectIndicator.check(R.id.radio1);
        selectIndicator.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.title_left_btn, R.id.save_btn})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_left_btn:
                startActivity(new Intent(this, ImageGalleryActivity.class));
                finish();
                break;
            case R.id.save_btn:
                if (choosedImageInfos.size() == 0)
                {
                    showToast("至少选择一个");
                    return;
                }
                showProgress("保存中...");
                netLogic.save2Gallery(choosedImageInfos, dailyImageInfos, APKUtil.getDiskCacheDir(this, Constants.IMAGE_DIR  + File.separator + System.currentTimeMillis()).getAbsolutePath());
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.radio1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.radio2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.radio3:
                viewPager.setCurrentItem(2);
                break;
            case R.id.radio4:
                viewPager.setCurrentItem(3);
                break;
            case R.id.radio5:
                viewPager.setCurrentItem(4);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageAdpater imageAdpater = (ImageAdpater)parent.getAdapter();
        imageAdpater.toggleState(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 避免多触发一次
        selectIndicator.setOnCheckedChangeListener(null);
        switch (position)
        {
            case 0:
                selectIndicator.check(R.id.radio1);
                break;
            case 1:
                selectIndicator.check(R.id.radio2);
                break;
            case 2:
                selectIndicator.check(R.id.radio3);
                break;
            case 3:
                selectIndicator.check(R.id.radio4);
                break;
            case 4:
                selectIndicator.check(R.id.radio5);
                break;
        }
        selectIndicator.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean isChoosed(ImageInfo imageInfo) {
        return choosedImageInfos.contains(imageInfo);
    }

    @Override
    public void choose(ImageInfo imageInfo) {
        if (!choosedImageInfos.contains(imageInfo))
        {
            choosedImageInfos.add(imageInfo);
        }
        else
        {
            choosedImageInfos.remove(imageInfo);
        }
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.clothesAndExpression:
                if (checkResponse(msg))
                {
                    InfoResult infoResult = (InfoResult)msg.obj;
                    clothesAndExpressions = (List<ClothesAndExpression>)infoResult.getExtraObj();

                    netLogic.download(clothesAndExpressions);
                }
                else
                {
                    drawView.setVisibility(View.GONE);
                    animationDrawable.stop();
                }
                break;
            case R.id.downClothesAndExpression:
                if (checkResponse(msg))
                {
                    new Thread(encodeRunnable).start();
                }
                else
                {
                    drawView.setVisibility(View.GONE);
                    animationDrawable.stop();
                }
                break;
            case R.id.save2Gallery:
                if(checkResponse(msg))
                {
                    // 表情相册
                    startActivity(new Intent(this, ImageGalleryActivity.class));
                    finish();
                }
                break;
        }
    }

    // 存储图片信息
    private List<ImageInfo> localPaths = new ArrayList<ImageInfo>();
    /**
     * 生成Bitmap
     */
    private Runnable encodeRunnable = new Runnable() {
        @Override
        public void run() {
            localPaths.clear();
            if (imageDir == null)
            {
                imageDir = APKUtil.getDiskCacheDir(MakeAllImageActivity.this, Constants.TEMP_DIR);
            }

            // 存储7个形象(04衣服-019脸、05衣服-017脸、019衣服-019脸、09衣服-09脸、018衣服-014脸、020衣服-015脸、04衣服-09脸)
            ImageInfo imageInfo1 = null;
            ImageInfo imageInfo2 = null;
            ImageInfo imageInfo3 = null;
            ImageInfo imageInfo4 = null;
            ImageInfo imageInfo5 = null;
            ImageInfo imageInfo6 = null;
            ImageInfo imageInfo7 = null;
            for (int i = 0; i < 7; i++)
            {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setHairBackground(hairBackground);
                imageInfo.setHairFont(hairFont);
                switch (i)
                {
                    case 0:
                        imageInfo1 = imageInfo;
                        break;
                    case 1:
                        imageInfo2 = imageInfo;
                        break;
                    case 2:
                        imageInfo3 = imageInfo;
                        break;
                    case 3:
                        imageInfo4 = imageInfo;
                        break;
                    case 4:
                        imageInfo5 = imageInfo;
                        break;
                    case 5:
                        imageInfo6 = imageInfo;
                        break;
                    case 6:
                        imageInfo7 = imageInfo;
                        break;
                }
                dailyImageInfos.add(imageInfo);
            }

            for (int j = 0; j < clothesAndExpressions.size(); j++)
            {
                ClothesAndExpression clothesAndExpression = clothesAndExpressions.get(j);
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setSex(sex);
                imageInfo.setHairBackground(hairBackground);
                imageInfo.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                imageInfo.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                String drawableName = null;
                switch (sex)
                {
                    case 0:
                        drawableName = "male_say";
                        break;
                    case 1:
                        drawableName = "female_say";
                        break;
                }
                drawableName += j + 1;
                imageInfo.setSayDrawableId(APKUtil.getDrawableByIdentify(MakeAllImageActivity.this, drawableName));
                if (TextUtils.isEmpty(imageInfo.getFace())) // 为空使用上个页面传递的脸地址
                {
                    imageInfo.setFace(faceUrl);
                }
                imageInfo.setHairFont(hairFont);
                if (j == 0) // 第一个表情有装饰, 其他不要
                {
                    imageInfo.setDecoration(decoration);
                }
                // Drawable 2 Bitmap
                String fileName = APKUtil.stringToMD5(imageInfo.toString());
                File file = new File(imageDir, fileName);
                String localPath = null;
                if (file.exists())
                {
                    localPath = file.getAbsolutePath();
                }
                else
                {
                    localPath = imageDir.getAbsolutePath() + File.separator + fileName;
                    final Drawable drawable = imageInfo.getOverlayDrawable(getResources());
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapHelper.drawable2Bitmap(drawable);
                        BitmapHelper.bitmap2File(bitmap, localPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bitmap != null && !bitmap.isRecycled())
                        {
                            bitmap.recycle();
                        }
                        // 清除引用, 释放内存
                        imageInfo.clearDrawable();
                    }
                }
                imageInfo.setIndex(j);
                imageInfo.setLocalPath(localPath);
                localPaths.add(imageInfo);

                // 7个形象衣服和脸
                switch (sex)
                {
                    case 0:
                        {
                            if (j == 3)
                            {
                                imageInfo1.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo7.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 4)
                            {
                                imageInfo2.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 18)
                            {
                                imageInfo3.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo1.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                                imageInfo3.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 8)
                            {
                                imageInfo4.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo4.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                                imageInfo7.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 17)
                            {
                                imageInfo5.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 19)
                            {
                                imageInfo6.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 16)
                            {
                                imageInfo2.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 13)
                            {
                                imageInfo5.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 14)
                            {
                                imageInfo6.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            break;
                        }
                    case 1:
                        {
                            if (j == 2)
                            {
                                imageInfo1.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 13)
                            {
                                imageInfo1.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                                imageInfo4.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo4.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 3)
                            {
                                imageInfo2.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 11)
                            {
                                imageInfo2.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 8)
                            {
                                imageInfo3.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo3.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 9)
                            {
                                imageInfo5.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                            }
                            else if (j == 18)
                            {
                                imageInfo5.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 6)
                            {
                                imageInfo6.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo6.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            else if (j == 4)
                            {
                                imageInfo7.setClothes(clothesAndExpression.getClothesInfo().getOriginalUrl());
                                imageInfo7.setFace(clothesAndExpression.getExpressionInfo().getOriginalUrl());
                            }
                            break;
                        }
                }

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawView.setVisibility(View.GONE);
                    animationDrawable.stop();
                    drawLay.setVisibility(View.VISIBLE);
                    loadGridView();
                }
            });
        }
    };
}
