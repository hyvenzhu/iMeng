package com.android.imeng.ui.decorate.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.imeng.R;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.model.HairInfo;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.HairAdpater;
import com.android.imeng.ui.base.PictureAdpater;
import com.android.imeng.ui.base.ViewPagerAdapter;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组装个人形象界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/04 17:09]
 */
public class AssembleImageActivity extends BasicActivity implements ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener{

    /**
     * 跳转
     * @param faceUrl 脸地址
     * @param sex 性别 0：女 1：男
     * @param faceShape 脸型
     * @param context 上下文
     */
    public static void actionStart(String faceUrl, int sex, int faceShape, Context context)
    {
        Intent intent = new Intent(context, AssembleImageActivity.class);
        intent.putExtra("faceUrl", faceUrl);
        intent.putExtra("sex", sex);
        intent.putExtra("faceShape", faceShape);
        context.startActivity(intent);
    }

    @ViewInject(R.id.image_wall)
    private View imageWall; // 背景墙
    @ViewInject(R.id.image_view)
    private ImageView imageView; // 形象展示View
    @ViewInject(R.id.face_btn)
    private Button faceBtn; // 表情Tab
    @ViewInject(R.id.face_indicator)
    private Button faceIndicator;
    @ViewInject(R.id.clothes_btn)
    private Button clothesBtn; // 衣服Tab
    @ViewInject(R.id.clothes_indicator)
    private Button clothesIndicator;
    @ViewInject(R.id.decoration_btn)
    private Button decorationBtn; // 表情Tab
    @ViewInject(R.id.decoration_indicator)
    private Button decorationIndicator;
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    private String faceUrl; // 脸地址
    private int sex; // 性别
    private int faceShape; // 脸型
    private NetLogic netLogic;
    // key 0：后面的头发  1：衣服   2：脸   3：前面的头发   4：装饰
    private Map<Integer, Drawable> drawableMap = new HashMap<Integer, Drawable>();

    private GridView hairGrid;
    private HairAdpater hairAdpater; // 头发
    private int hairIndex;
    private GridView clothesGrid;
    private PictureAdpater clothesAdapter; // 衣服
    private int clothesIndex;
    private GridView decorationGrid;
    private PictureAdpater decorationAdapter; // 装饰
    private int decorationIndex;

    private float scale = Constants.PIC_THUMBNAIL_WIDTH / (Constants.PIC_THUMBNAIL_HEIGHT * 1.0f); // GridView item宽高比
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

        faceUrl = getIntent().getStringExtra("faceUrl");
        sex = getIntent().getIntExtra("sex", 0);
        faceShape = getIntent().getIntExtra("faceShape", 0);
        netLogic = new NetLogic(this);

        // 调整宽高
        adjustView();
        // 背景墙
        adjustWall();
        // 加载表情
        loadFace();
        // 初始化GridView
        loadGridView();

        // 查询头发、衣服、装饰列表
        netLogic.hairs(sex, hairIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        netLogic.clothes(sex, clothesIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterAll(netLogic);
    }

    /**
     * 初始化GridView
     */
    private void loadGridView()
    {
        for(int i = 0; i < 3; i++)
        {
            GridView grid = new GridView(this);
            grid.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            grid.setHorizontalSpacing(APKUtil.dip2px(this, 2));
            grid.setVerticalSpacing(APKUtil.dip2px(this, 2));
            grid.setNumColumns(3);
            grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
            grid.setOnItemClickListener(this);
            if (i == 0)
            {
                hairGrid = grid;
            }
            else if (i == 1)
            {
                clothesGrid = grid;
            }
            else
            {
                decorationGrid = grid;
            }
        }
        viewPager.setAdapter(new ViewPagerAdapter(hairGrid, clothesGrid, decorationGrid));
        viewPager.setOnPageChangeListener(this);
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
            drawableMap.put(2, faceDrawable);
            imageView.setImageDrawable(overlay());
        }
        else
        {
            netLogic.download(faceUrl);
        }
    }

    /**
     * 图层叠加
     * @return
     */
    private Drawable overlay()
    {
        Drawable hairBackgroundDrawable = drawableMap.get(0);
        Drawable clothesDrawable = drawableMap.get(1);
        Drawable faceDrawable = drawableMap.get(2);
        Drawable hairFontDrawable = drawableMap.get(3);
        Drawable decorationDrawable = drawableMap.get(4);
        Drawable drawable = null;
        // 背后的头发
        if (hairBackgroundDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairBackgroundDrawable);
        }

        // 衣服
        if (drawable != null && clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, clothesDrawable);
        }
        else if (clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(clothesDrawable);
        }

        // 脸
        if (drawable != null && faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, faceDrawable);
        }
        else if (faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(faceDrawable);
        }

        // 前面的头发
        if (drawable != null && hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, hairFontDrawable);
        }
        else if (hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairFontDrawable);
        }

        // 装饰
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

    @OnClick({R.id.hair_lay, R.id.face_btn, R.id.clothes_lay, R.id.clothes_btn, R.id.decoration_lay, R.id.decoration_btn,
              R.id.title_right_btn, R.id.title_left_btn})
    public void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.hair_lay: // 头发
            case R.id.face_btn:
                viewPager.setCurrentItem(0);
                break;
            case R.id.clothes_lay: // 衣服
            case R.id.clothes_btn:
                viewPager.setCurrentItem(1);
                break;
            case R.id.decoration_lay: // 装饰
            case R.id.decoration_btn:
                viewPager.setCurrentItem(2);
                break;
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn:
                if (!drawableMap.containsKey(3))
                {
                    showToast("请选择头发");
                    return;
                }
                else if (!drawableMap.containsKey(1))
                {
                    showToast("请选择衣服");
                    return;
                }
                // 制作界面
                MakeAllImageActivity.actionStart(sex, choosedClothesCategroyId, faceShape,
                    choosedHairBackground, choosedHairFont, choosedDecoration, faceUrl, this);
                finish();
                break;
        }
    }

    private int choosedClothesCategroyId; // 选择的衣服类别Id
    private String choosedHairBackground; // 选择的背后头发
    private String choosedHairFont; // 选择的前面头发
    private String choosedDecoration; // 选择的装饰
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
         if (adapter == hairAdpater) // 头发
         {
             if (hairAdpater.isMore(position)) // More
             {
                 netLogic.hairs(sex, hairIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
             }
             else
             {
                 HairInfo hairInfo = hairAdpater.getItem(position);
                 List<PictureInfo> hairInfos = hairInfo.getOriginalInfos();
                 if (!hairAdpater.hasDownload(position)) // 未下载
                 {
                     for(PictureInfo pictureInfo : hairInfos)
                     {
                         if (TextUtils.isEmpty(pictureInfo.getOriginalLocalPath()))
                         {
                             netLogic.download(pictureInfo);
                         }
                         hairAdpater.notifyDataSetChanged();
                     }
                 }
                 else
                 {
                     if (hairInfos != null && hairInfos.size() > 0)
                     {
                         drawableMap.remove(0);
                         drawableMap.remove(3);
                         for(int i = 0; i < hairInfos.size(); i++)
                         {
                             PictureInfo pictureInfo =  hairInfos.get(i);
                             int index = 0;
                             if (pictureInfo.getNo() == 1) // 前面的头发
                             {
                                 index = 3;
                                 choosedHairFont = pictureInfo.getOriginalLocalPath();
                             }
                             else if (pictureInfo.getNo() == 2) // 后面的头发
                             {
                                 index = 0;
                                 choosedHairBackground = pictureInfo.getOriginalLocalPath();
                             }
                             drawableMap.put(index, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                         }
                         imageView.setImageDrawable(overlay());
                     }
                 }
             }
         }
         else if (adapter == clothesAdapter) // 衣服
         {
             if (clothesAdapter.isMore(position)) // More
             {
                 netLogic.clothes(sex, clothesIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
             }
             else
             {
                 PictureInfo pictureInfo = clothesAdapter.getItem(position);
                 if (!clothesAdapter.hasDownload(position)) // 未下载
                 {
                     netLogic.download(pictureInfo);
                     clothesAdapter.notifyDataSetChanged();
                 }
                 else
                 {
                     choosedClothesCategroyId = pictureInfo.getCategoryId();
                     drawableMap.put(1, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                     imageView.setImageDrawable(overlay());
                 }
             }
         }
         else if (adapter == decorationAdapter) // 装饰
         {
             if (decorationAdapter.isMore(position)) // More
             {
                 netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
             }
             else
             {
                 PictureInfo pictureInfo = decorationAdapter.getItem(position);
                 if (!decorationAdapter.hasDownload(position)) // 未下载
                 {
                     netLogic.download(pictureInfo);
                     decorationAdapter.notifyDataSetChanged();
                 }
                 else
                 {
                     choosedDecoration = pictureInfo.getOriginalLocalPath();
                     drawableMap.put(4, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                     imageView.setImageDrawable(overlay());
                 }
             }
         }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position)
        {
            case 0:
                faceBtn.setEnabled(false);
                faceIndicator.setEnabled(false);
                clothesBtn.setEnabled(true);
                clothesIndicator.setEnabled(true);
                decorationBtn.setEnabled(true);
                decorationIndicator.setEnabled(true);
                break;
            case 1:
                faceBtn.setEnabled(true);
                faceIndicator.setEnabled(true);
                clothesBtn.setEnabled(false);
                clothesIndicator.setEnabled(false);
                decorationBtn.setEnabled(true);
                decorationIndicator.setEnabled(true);
                break;
            case 2:
                faceBtn.setEnabled(true);
                faceIndicator.setEnabled(true);
                clothesBtn.setEnabled(true);
                clothesIndicator.setEnabled(true);
                decorationBtn.setEnabled(false);
                decorationIndicator.setEnabled(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.downloadFace:
                if (checkResponse(msg))
                {
                    PictureInfo pictureInfo = (PictureInfo)(((InfoResult)msg.obj).getExtraObj());
                    Drawable faceDrawable = new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath());
                    drawableMap.put(2, faceDrawable);
                    imageView.setImageDrawable(overlay());
                }
                break;
            case R.id.downloadOriginal:
                if (hairAdpater != null)
                {
                    hairAdpater.notifyDataSetChanged();
                }
                if (clothesAdapter != null)
                {
                    clothesAdapter.notifyDataSetChanged();
                }
                if (decorationAdapter != null)
                {
                    decorationAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.hairs: // 头发
                if (checkResponse(msg))
                {
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<HairInfo> hairInfos = (List<HairInfo>)infoResult.getExtraObj();
                    hairIndex++;

                    if (hairAdpater == null)
                    {
                        int hairCount = Integer.parseInt(infoResult.getOtherObj().toString());
                        hairAdpater = new HairAdpater(this, hairInfos, R.layout.layout_item_picture, hairCount);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        hairAdpater.setSize(columnWidth, columnHeight);
                        hairGrid.setAdapter(hairAdpater);
                    }
                    else
                    {
                        hairAdpater.getDataSource().addAll(hairInfos);
                    }
                    hairAdpater.notifyDataSetChanged();
                }
                break;
            case R.id.clothes: // 衣服
                if (checkResponse(msg))
                {
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    clothesIndex++;

                    if (clothesAdapter == null)
                    {
                        int clothesCount = Integer.parseInt(infoResult.getOtherObj().toString());
                        clothesAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, clothesCount);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        clothesAdapter.setSize(columnWidth, columnHeight);
                        clothesGrid.setAdapter(clothesAdapter);
                    }
                    else
                    {
                        clothesAdapter.getDataSource().addAll(pictureInfos);
                    }
                    clothesAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.decorations: // 装饰
                if (checkResponse(msg))
                {
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    decorationIndex++;

                    if (decorationAdapter == null)
                    {
                        int decorationCount = Integer.parseInt(infoResult.getOtherObj().toString());
                        decorationAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, decorationCount);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        decorationAdapter.setSize(columnWidth, columnHeight);
                        decorationGrid.setAdapter(decorationAdapter);
                    }
                    else
                    {
                        decorationAdapter.getDataSource().addAll(pictureInfos);
                    }
                    decorationAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
