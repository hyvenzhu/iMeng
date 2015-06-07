package com.android.imeng.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
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
import com.android.imeng.logic.ClothesAndExpression;
import com.android.imeng.logic.ImageInfo;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成所有形象界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 21:16]
 */
public class MakeAllImageActivity extends BasicActivity implements AdapterView.OnItemClickListener,
        ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener{
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

            List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
            List<ClothesAndExpression> smallClothesAndExpressions = clothesAndExpressions.subList(i * 4, i * 4 + 4);
            for (int j = 0; j < smallClothesAndExpressions.size(); j++)
            {
                ClothesAndExpression clothesAndExpression = smallClothesAndExpressions.get(j);
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
                drawableName += i * 4 + (j + 1);
                imageInfo.setSayDrawableId(APKUtil.getDrawableByIdentify(this, drawableName));
                if (TextUtils.isEmpty(imageInfo.getFace())) // 为空使用上个页面传递的脸地址
                {
                    imageInfo.setFace(faceUrl);
                }
                imageInfo.setHairFont(hairFont);
                if (i == 0 && j == 0) // 第一个表情有装饰, 其他不要
                {
                    imageInfo.setDecoration(decoration);
                }
                imageInfos.add(imageInfo);
            }
            ImageAdpater imageAdpater = new ImageAdpater(this, imageInfos, R.layout.layout_item_image);
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
                finish();
                break;
            case R.id.save_btn:
                if (imageDir == null)
                {
                    imageDir = APKUtil.getDiskCacheDir(this, Constants.IMAGE_DIR + File.separator + System.currentTimeMillis());
                }
                File[] files = imageDir.listFiles();
                for (File file : files)
                {
                    file.delete();
                }

                ViewPagerAdapter pagerAdapter = (ViewPagerAdapter)viewPager.getAdapter();
                GridView[] gridViews = pagerAdapter.getViews();
                List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
                for(GridView gridView : gridViews)
                {
                    ImageAdpater imageAdpater = (ImageAdpater)gridView.getAdapter();
                    List<ImageInfo> choosedImageInfos = imageAdpater.getChoosedImageInfos();
                    imageInfos.addAll(choosedImageInfos);
                }

                if (imageInfos.size() == 0)
                {
                    showToast("至少选择一个");
                    return;
                }

                // 保存到形象文件夹
                for(int i = 0; i < imageInfos.size(); i++)
                {
                    ImageInfo imageInfo = imageInfos.get(i);
                    Bitmap bitmap = BitmapHelper.drawable2Bitmap(imageInfo.getOverlayDrawable(getResources()));
                    try {
                        BitmapHelper.bitmap2File(bitmap, imageDir.getAbsolutePath() + File.separator + (i + 1) + ".png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bitmap != null)
                        {
                            bitmap.recycle();
                        }
                    }
                }

                // 表情相册
                startActivity(new Intent(this, ImageGalleryActivity.class));
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
                drawView.setVisibility(View.GONE);
                animationDrawable.stop();
                if (checkResponse(msg))
                {
                    drawLay.setVisibility(View.VISIBLE);
                    loadGridView();
                }
                break;
        }
    }
}
