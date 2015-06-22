package com.android.imeng.ui.decorate.cartoon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.imeng.R;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.logic.model.HairInfo;
import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.ShareActivity;
import com.android.imeng.ui.base.adapter.HairAdpater;
import com.android.imeng.ui.base.adapter.PictureAdpater;
import com.android.imeng.ui.base.adapter.ViewPagerAdapter;
import com.android.imeng.ui.decorate.cartoon.adapter.BigClothesAdpater;
import com.android.imeng.ui.decorate.cartoon.adapter.DecorationAdpater;
import com.android.imeng.ui.decorate.cartoon.adapter.SayAdpater;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.nineoldandroids.animation.ObjectAnimator;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装饰界面
 *
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-15 20:57]
 */
public class CartoonDecorateActivity extends BasicActivity implements AdapterView.OnItemClickListener{
    @ViewInject(R.id.image_wall)
    private View imageWall; // 背景墙
    @ViewInject(R.id.image_view)
    private ImageView imageView; // 形象展示View
    @ViewInject(R.id.say_edit)
    private EditText sayEdit; // 文字输入框
    @ViewInject(R.id.viewpagertab)
    private SmartTabLayout smartTabLayout; // 指示器
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    private NetLogic netLogic;

    private int sex; // 性别
    private String clothesPath; // 衣服路径
    private int clothesCategoryId; // 衣服类别id
    // key 0：后面的头发  1：衣服   2：脸   3：前面的头发    4：眼睛    5：眉毛    6：嘴   7：眉毛   8:装饰   9：文字
    private Map<Integer, Drawable> drawableMap = new HashMap<Integer, Drawable>();
    private final int TOTAL_LAYER_COUNT = 10; // 总计图层数量

    private GridView hairGrid;
    private HairAdpater hairAdpater; // 头发
    private int hairIndex;

    private GridView faceGrid;
    private PictureAdpater faceAdapter; // 脸型
    private int faceIndex;

    private GridView eyebrowGrid;
    private PictureAdpater eyebrowAdapter; // 眉毛
    private int eyebrowIndex;

    private GridView eyeGrid;
    private PictureAdpater eyeAdapter; // 眼睛
    private int eyeIndex;

    private GridView mouthGrid;
    private PictureAdpater mouthAdapter; // 嘴巴
    private int mouthIndex;

    private GridView actionGrid;
    private BigClothesAdpater actionAdapter; // 动作

    private GridView decorationGrid;
    private DecorationAdpater decorationAdapter; // 装饰
    private int decorationIndex;

    private GridView sayGrid;
    private SayAdpater sayAdapter; // 文字

    private float scale = Constants.PIC_THUMBNAIL_WIDTH / (Constants.PIC_THUMBNAIL_HEIGHT * 1.0f);
    /**
     * 记录请求状态
     * key=0,1,2,3,4,5,6
     * value:0正在请求  1请求成功   2请求失败
     */
    private Map<Integer, REQUEST_STATUS> requestStatus = new HashMap<Integer, REQUEST_STATUS>();
    private enum REQUEST_STATUS
    {
        REQUESTING,
        REQUEST_SUCCESSED,
        REQUEST_FAILURED
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterAll(netLogic);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "捏表情", true);
        leftBtn.setText("返回");
        rightBtn.setVisibility(View.VISIBLE);

        sex = getIntent().getIntExtra("sex", 0);
        clothesPath = getIntent().getStringExtra("clothesPath");
        clothesCategoryId = getIntent().getIntExtra("clothesCategoryId", -1);

        netLogic = new NetLogic(this);

        // 调整宽高
        adjustView();
        // 背景墙
        adjustWall();
        // 加载形象
        loadDefault();
        // 初始化GridView
        loadGridView();

        requestStatus.put(0, REQUEST_STATUS.REQUESTING);
        requestStatus.put(1, REQUEST_STATUS.REQUESTING);
        requestStatus.put(2, REQUEST_STATUS.REQUESTING);
        requestStatus.put(3, REQUEST_STATUS.REQUESTING);
        requestStatus.put(4, REQUEST_STATUS.REQUESTING);
        requestStatus.put(5, REQUEST_STATUS.REQUESTING);
        requestStatus.put(6, REQUEST_STATUS.REQUESTING);
        // 头发
        netLogic.hairs(sex, hairIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 脸型
        netLogic.faceShapes(sex, faceIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 眉毛
        netLogic.eyebrows(sex, eyebrowIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 眼睛
        netLogic.eyes(sex, eyeIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 嘴巴
        netLogic.mouths(sex, mouthIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 动作(同一类别的衣服)
        netLogic.bigClothes(sex, clothesCategoryId);
        // 装饰
        netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
        // 文字
        loadSay();
    }

    @OnClick({R.id.title_left_btn, R.id.title_right_btn})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn:
                sayEdit.clearFocus();
                sayEdit.setFocusable(false);
                sayEdit.setFocusableInTouchMode(false);
                hideSoftInput();

                Drawable drawable = BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT);
                // drawable 2 bitmap
                String fileName = String.valueOf(System.currentTimeMillis());
                File imageDir = APKUtil.getDiskCacheDir(this, Constants.TEMP_DIR);
                String localPath = imageDir.getAbsolutePath() + File.separator + fileName;
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapHelper.drawable2Bitmap(drawable);
                    // 是否有自定义文字
                    if (sayEdit.getVisibility() == View.VISIBLE)
                    {
                        // 获得文字图片
                        sayEdit.setDrawingCacheEnabled(true);
                        sayEdit.buildDrawingCache();
                        Bitmap sayBitmap = sayEdit.getDrawingCache();
                        // 按照比例缩放
                        Matrix matrix = new Matrix();
                        matrix.postScale(Constants.SAY_CONTENT_WIDTH / sayBitmap.getWidth(), Constants.SAY_CONTENT_HEIGHT / sayBitmap.getHeight()); //长和宽放大缩小的比例
                        sayBitmap = Bitmap.createBitmap(sayBitmap, 0, 0, sayBitmap.getWidth(), sayBitmap.getHeight(), matrix, true);
                        // 合并文字与形象图片
                        Canvas canvas = new Canvas(bitmap);
                        float x = bitmap.getWidth() * (Constants.SAY_EMPTY_HORIZONTAL_X - Constants.SAY_CONTENT_WIDTH / 2f) / Constants.IMAGE_WIDTH_HEIGHT;
                        float y = (bitmap.getHeight() - sayBitmap.getHeight()) / 2f;
                        canvas.drawBitmap(sayBitmap, x, y, null);
                    }
                    // 保存到文件
                    BitmapHelper.bitmap2File(bitmap, localPath);
                    // 跳转到分享界面
                    ShareActivity.actionStart(this, localPath, sex);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bitmap != null && !bitmap.isRecycled())
                    {
                        bitmap.recycle();
                    }
                }
                break;
        }
    }

    /**
     * 调整宽高
     */
    private void adjustView() {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                // 1、调整背景墙, 如果宽度>500且高度<500，则高度调节为500
                int wallWidth = imageWall.getMeasuredWidth();
                int wallHeight = imageWall.getMeasuredHeight();
                if (wallWidth > Constants.IMAGE_WIDTH_HEIGHT && wallHeight < Constants.IMAGE_WIDTH_HEIGHT) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageWall.getLayoutParams();
                    layoutParams.height = Constants.IMAGE_WIDTH_HEIGHT;
                    layoutParams.weight = 0;
                    imageWall.setLayoutParams(layoutParams);
                    imageWall.requestLayout();
                }

                // 2、调整形象，取宽与高的较小值，如果较小值仍大于500，则设置宽高都为500，否则设置宽和高为较小值
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                int viewWidth = imageView.getMeasuredWidth();
                int viewHeight = imageView.getMeasuredHeight();

                int minSize = Math.min(viewWidth, viewHeight);
                if (minSize > Constants.IMAGE_WIDTH_HEIGHT) {
                    minSize = Constants.IMAGE_WIDTH_HEIGHT;
                }
                layoutParams.width = minSize;
                layoutParams.height = minSize;
                imageView.setLayoutParams(layoutParams);
                // x轴往左平移
                imageView.setTranslationX(-minSize * Constants.TRANSLATE_X_PERCENT);

                // 设置输入框宽高
                RelativeLayout.LayoutParams sayParams = (RelativeLayout.LayoutParams)sayEdit.getLayoutParams();
                sayParams.height = (int)(minSize * Constants.SAY_CONTENT_HEIGHT * 1.0f / Constants.IMAGE_WIDTH_HEIGHT);
                sayParams.width = (int)(minSize * Constants.SAY_CONTENT_WIDTH * 1.0f / Constants.IMAGE_WIDTH_HEIGHT);
                // 往左平移, 保证在图片气泡内
                sayEdit.setTranslationX(-Constants.SAY_INPUT_TRANSLATE_X_PERCENT * minSize);
            }
        });
    }

    /**
     * 性别调节背景墙
     */
    private void adjustWall() {
        if (sex == 0) {
            imageWall.setBackgroundColor(Color.parseColor("#CAE4F3"));
        } else {
            imageWall.setBackgroundColor(Color.parseColor("#f3bec4"));
        }
    }

    /**
     * 加载形象
     */
    private void loadDefault() {
        drawableMap.put(1, new BitmapDrawable(getResources(), clothesPath));
        drawableMap.put(2, getResources().getDrawable(R.drawable.default_face));
        drawableMap.put(6, getResources().getDrawable(R.drawable.default_mouth));
        switch (sex) {
            case 0:
                drawableMap.put(3, getResources().getDrawable(R.drawable.default_boy_hair));
                drawableMap.put(4, getResources().getDrawable(R.drawable.default_boy_eye));
                drawableMap.put(5, getResources().getDrawable(R.drawable.default_boy_eyebrow));
                break;
            case 1:
                drawableMap.put(0, getResources().getDrawable(R.drawable.default_girl_hair_background));
                drawableMap.put(3, getResources().getDrawable(R.drawable.default_girl_hair_font));
                drawableMap.put(4, getResources().getDrawable(R.drawable.default_girl_eye));
                drawableMap.put(5, getResources().getDrawable(R.drawable.default_girl_eyebrow));
                break;
        }
        imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
    }

    /**
     * 初始化GridView
     */
    private void loadGridView()
    {
        final LayoutInflater inflater = LayoutInflater.from(this);
        smartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup viewGroup, int i, PagerAdapter pagerAdapter) {
                View v =  inflater.inflate(R.layout.layout_decorate_tab, viewGroup, false);
                ImageView iconImage = (ImageView)v.findViewById(R.id.icon_image);
                switch (i)
                {
                    case 0:
                        iconImage.setImageResource(R.drawable.tab_hair_bg);
                        break;
                    case 1:
                        iconImage.setImageResource(R.drawable.tab_face_bg);
                        break;
                    case 2:
                        iconImage.setImageResource(R.drawable.tab_eyebrow_bg);
                        break;
                    case 3:
                        iconImage.setImageResource(R.drawable.tab_eye_bg);
                        break;
                    case 4:
                        iconImage.setImageResource(R.drawable.tab_mouth_bg);
                        break;
                    case 5:
                        iconImage.setImageResource(R.drawable.tab_action_bg);
                        break;
                    case 6:
                        iconImage.setImageResource(R.drawable.tab_decoration_bg);
                        break;
                    case 7:
                        iconImage.setImageResource(R.drawable.tab_say_bg);
                        break;
                }
                return v;
            }
        });
        for(int i = 0; i < 8; i++)
        {
            GridView grid = new GridView(this);
            grid.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            grid.setHorizontalSpacing(APKUtil.dip2px(this, 2));
            grid.setVerticalSpacing(APKUtil.dip2px(this, 2));
            grid.setNumColumns(3);
            grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
            grid.setOnItemClickListener(this);
            switch (i)
            {
                case 0:
                    hairGrid = grid;
                    break;
                case 1:
                    faceGrid = grid;
                    break;
                case 2:
                    eyebrowGrid = grid;
                    break;
                case 3:
                    eyeGrid = grid;
                    break;
                case 4:
                    mouthGrid = grid;
                    break;
                case 5:
                    actionGrid = grid;
                    break;
                case 6:
                    decorationGrid = grid;
                    break;
                case 7:
                    sayGrid = grid;
                    break;
            }
        }
        viewPager.setAdapter(new ViewPagerAdapter(hairGrid, faceGrid, eyebrowGrid, eyeGrid,
                mouthGrid, actionGrid, decorationGrid, sayGrid));
        smartTabLayout.setViewPager(viewPager);
        smartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                sayEdit.clearFocus();
                sayEdit.setFocusable(false);
                sayEdit.setFocusableInTouchMode(false);
                hideSoftInput();
            }

            @Override
            public void onPageSelected(int i) {
                // 请求失败, 重试
                switch (i)
                {
                    case 0:
                        if (requestStatus.get(0) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(0, REQUEST_STATUS.REQUESTING);
                            // 头发
                            netLogic.hairs(sex, hairIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 1:
                        if (requestStatus.get(1) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(1, REQUEST_STATUS.REQUESTING);
                            // 脸型
                            netLogic.faceShapes(sex, faceIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 2:
                        if (requestStatus.get(2) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(2, REQUEST_STATUS.REQUESTING);
                            // 眉毛
                            netLogic.eyebrows(sex, eyebrowIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 3:
                        if (requestStatus.get(3) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(3, REQUEST_STATUS.REQUESTING);
                            // 眼睛
                            netLogic.eyes(sex, eyeIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 4:
                        if (requestStatus.get(4) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(4, REQUEST_STATUS.REQUESTING);
                            // 嘴巴
                            netLogic.mouths(sex, mouthIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 5:
                        if (requestStatus.get(5) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(5, REQUEST_STATUS.REQUESTING);
                            // 动作(同一类别的衣服)
                            netLogic.bigClothes(sex, clothesCategoryId);
                        }
                        break;
                    case 6:
                        if (requestStatus.get(6) == REQUEST_STATUS.REQUEST_FAILURED)
                        {
                            requestStatus.put(6, REQUEST_STATUS.REQUESTING);
                            // 装饰
                            netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
                        }
                        break;
                    case 7:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 加载文字
     */
    private void loadSay()
    {
        List<PictureInfo> pictureInfos = new ArrayList<PictureInfo>();
        for(int i = 0; i < 20; i++)
        {
            PictureInfo pictureInfo = new PictureInfo();
            switch (sex)
            {
                case 0: // 男
                    pictureInfo.setThumbnailUrl("male_say" + (i + 1) + "_small");
                    pictureInfo.setOriginalUrl("male_say" + (i + 1));
                    break;
                case 1: // 女
                    pictureInfo.setThumbnailUrl("female_say" + (i + 1) + "_small");
                    pictureInfo.setOriginalUrl("female_say" + (i + 1));
                    break;
            }
            pictureInfos.add(pictureInfo);
        }
        int count = pictureInfos.size();
        sayAdapter = new SayAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                final int viewWidth = viewPager.getWidth();
                int numColumns = 3;
                int hoizontalSpacing = APKUtil.dip2px(CartoonDecorateActivity.this, 2);
                int columnWidth = (int) ((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                int columnHeight = (int) (columnWidth * scale);
                sayAdapter.setSize(columnWidth, columnHeight);
                sayGrid.setAdapter(sayAdapter);
                sayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
        if (adapter == hairAdpater) // 头发
        {
            if (hairAdpater.isMore(position) && requestStatus.get(0) != REQUEST_STATUS.REQUESTING) // More
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
                            }
                            else if (pictureInfo.getNo() == 2) // 后面的头发
                            {
                                index = 0;
                            }
                            drawableMap.put(index, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                        }
                        imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                    }
                }
            }
        }
        else if (adapter == faceAdapter) // 脸型
        {
            if (faceAdapter.isMore(position) && requestStatus.get(1) != REQUEST_STATUS.REQUESTING) // More
            {
                netLogic.faceShapes(sex, faceIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
            }
            else
            {
                PictureInfo pictureInfo = faceAdapter.getItem(position);
                if (!faceAdapter.hasDownload(position)) // 未下载
                {
                    netLogic.download(pictureInfo);
                    faceAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(2, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
        else if (adapter == eyebrowAdapter) // 眉毛
        {
            if (eyebrowAdapter.isMore(position) && requestStatus.get(2) != REQUEST_STATUS.REQUESTING) // More
            {
                netLogic.eyebrows(sex, eyebrowIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
            }
            else
            {
                PictureInfo pictureInfo = eyebrowAdapter.getItem(position);
                if (!eyebrowAdapter.hasDownload(position)) // 未下载
                {
                    netLogic.download(pictureInfo);
                    eyebrowAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(5, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
        else if (adapter == eyeAdapter) // 眼睛
        {
            if (eyeAdapter.isMore(position) && requestStatus.get(3) != REQUEST_STATUS.REQUESTING) // More
            {
                netLogic.eyes(sex, eyeIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
            }
            else
            {
                PictureInfo pictureInfo = eyeAdapter.getItem(position);
                if (!eyeAdapter.hasDownload(position)) // 未下载
                {
                    netLogic.download(pictureInfo);
                    eyeAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(4, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
        else if (adapter == mouthAdapter) // 嘴巴
        {
            if (mouthAdapter.isMore(position) && requestStatus.get(4) != REQUEST_STATUS.REQUESTING) // More
            {
                netLogic.mouths(sex, mouthIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
            }
            else
            {
                PictureInfo pictureInfo = mouthAdapter.getItem(position);
                if (!mouthAdapter.hasDownload(position)) // 未下载
                {
                    netLogic.download(pictureInfo);
                    mouthAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(6, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
        else if (adapter == actionAdapter) // 动作
        {
            if (actionAdapter.isMore(position) && requestStatus.get(5) != REQUEST_STATUS.REQUESTING) // More
            {
                // 衣服没有分页
//                netLogic.bigClothes(sex, clothesCategoryId);
            }
            else
            {
                PictureInfo pictureInfo = actionAdapter.getItem(position);
                if (!actionAdapter.hasDownload(position)) // 未下载
                {
                    netLogic.download(pictureInfo);
                    actionAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(1, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
        else if (adapter == decorationAdapter) // 装饰
        {
            if (decorationAdapter.isMore(position) && requestStatus.get(6) != REQUEST_STATUS.REQUESTING) // More
            {
                netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
            }
            else
            {
                if (position == 0) // 删除装饰
                {
                    drawableMap.put(8, null);
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
                else
                {
                    PictureInfo pictureInfo = decorationAdapter.getItem(position - 1);
                    if (!decorationAdapter.hasDownload(position)) // 未下载
                    {
                        netLogic.download(pictureInfo);
                        decorationAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        drawableMap.put(8, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                        imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                    }
                }
            }
        }
        else if (adapter == sayAdapter) // 文字
        {
            if (position == 0) // 删除装饰
            {
                sayEdit.setVisibility(View.INVISIBLE);
                hideSoftInput();

                if (imageView.getTranslationX() >= 0)
                {
                    // x轴往左平移
                    imageView.setTranslationX(-imageView.getMeasuredWidth() * Constants.TRANSLATE_X_PERCENT);
                }
                drawableMap.put(9, null);
                imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
            }
            else if (position == 1) // 自定义文字
            {
                sayEdit.setFocusable(true);
                sayEdit.setFocusableInTouchMode(true);
                sayEdit.setVisibility(View.VISIBLE);
                sayEdit.requestFocus();

                if (imageView.getTranslationX() < 0)
                {
                    // x轴往右平移
                    imageView.setTranslationX(0);
                }
                drawableMap.put(9, getResources().getDrawable(APKUtil.getDrawableByIdentify(this, "say_empty")));
                imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
            }
            else
            {
                sayEdit.setVisibility(View.INVISIBLE);
                hideSoftInput();

                if (imageView.getTranslationX() < 0)
                {
                    // x轴往右平移
                    imageView.setTranslationX(0);
                }
                PictureInfo pictureInfo = sayAdapter.getItem(position - 2);
                if (!sayAdapter.hasDownload(position)) // 未下载
                {
                    // 文字不需要下载
//                    netLogic.download(pictureInfo);
//                    sayAdapter.notifyDataSetChanged();
                }
                else
                {
                    drawableMap.put(9, getResources().getDrawable(APKUtil.getDrawableByIdentify(this, pictureInfo.getOriginalUrl())));
                    imageView.setImageDrawable(BitmapHelper.overlay(drawableMap, TOTAL_LAYER_COUNT));
                }
            }
        }
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.downloadOriginal:
                if (hairAdpater != null)
                {
                    hairAdpater.notifyDataSetChanged();
                }
                if (faceAdapter != null)
                {
                    faceAdapter.notifyDataSetChanged();
                }
                if (eyebrowAdapter != null)
                {
                    eyebrowAdapter.notifyDataSetChanged();
                }
                if (eyeAdapter != null)
                {
                    eyeAdapter.notifyDataSetChanged();
                }
                if (mouthAdapter != null)
                {
                    mouthAdapter.notifyDataSetChanged();
                }
                if (actionAdapter != null)
                {
                    actionAdapter.notifyDataSetChanged();
                }
                if (decorationAdapter != null)
                {
                    decorationAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.hairs: // 头发
                if (checkResponse(msg))
                {
                    requestStatus.put(0, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<HairInfo> hairInfos = (List<HairInfo>)infoResult.getExtraObj();
                    hairIndex++;

                    if (hairAdpater == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        hairAdpater = new HairAdpater(this, hairInfos, R.layout.layout_item_picture, count);

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
                else
                {
                    requestStatus.put(0, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.faceShapes: // 脸型
                if (checkResponse(msg))
                {
                    requestStatus.put(1, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    faceIndex++;

                    if (faceAdapter == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        faceAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        faceAdapter.setSize(columnWidth, columnHeight);
                        faceGrid.setAdapter(faceAdapter);
                    }
                    else
                    {
                        faceAdapter.getDataSource().addAll(pictureInfos);
                    }
                    faceAdapter.notifyDataSetChanged();
                }
                else
                {
                    requestStatus.put(1, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.eyebrows: // 眉毛
                if (checkResponse(msg))
                {
                    requestStatus.put(2, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    eyebrowIndex++;

                    if (eyebrowAdapter == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        eyebrowAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        eyebrowAdapter.setSize(columnWidth, columnHeight);
                        eyebrowGrid.setAdapter(eyebrowAdapter);
                    }
                    else
                    {
                        eyebrowAdapter.getDataSource().addAll(pictureInfos);
                    }
                    eyebrowAdapter.notifyDataSetChanged();
                }
                else
                {
                    requestStatus.put(2, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.eyes: // 眼睛
                if (checkResponse(msg))
                {
                    requestStatus.put(3, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    eyeIndex++;

                    if (eyeAdapter == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        eyeAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        eyeAdapter.setSize(columnWidth, columnHeight);
                        eyeGrid.setAdapter(eyeAdapter);
                    }
                    else
                    {
                        eyeAdapter.getDataSource().addAll(pictureInfos);
                    }
                    eyeAdapter.notifyDataSetChanged();
                }
                else
                {
                    requestStatus.put(3, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.mouths: // 嘴巴
                if (checkResponse(msg))
                {
                    requestStatus.put(4, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    mouthIndex++;

                    if (mouthAdapter == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        mouthAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        mouthAdapter.setSize(columnWidth, columnHeight);
                        mouthGrid.setAdapter(mouthAdapter);
                    }
                    else
                    {
                        mouthAdapter.getDataSource().addAll(pictureInfos);
                    }
                    mouthAdapter.notifyDataSetChanged();
                }
                else
                {
                    requestStatus.put(4, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.bigClothes: // 动作
                if (checkResponse(msg))
                {
                    requestStatus.put(5, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();

                    if (actionAdapter == null)
                    {
                        int count = pictureInfos.size(); // 动作不需要分页
                        actionAdapter = new BigClothesAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

                        final int viewWidth = viewPager.getWidth();
                        int numColumns = 3;
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        actionAdapter.setSize(columnWidth, columnHeight);
                        actionGrid.setAdapter(actionAdapter);
                    }
                    else
                    {
                        actionAdapter.getDataSource().addAll(pictureInfos);
                    }
                    actionAdapter.notifyDataSetChanged();
                }
                else
                {
                    requestStatus.put(5, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
            case R.id.decorations: // 装饰
                if (checkResponse(msg))
                {
                    requestStatus.put(6, REQUEST_STATUS.REQUEST_SUCCESSED);
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    decorationIndex++;

                    if (decorationAdapter == null)
                    {
                        int count = Integer.parseInt(infoResult.getOtherObj().toString());
                        decorationAdapter = new DecorationAdpater(this, pictureInfos, R.layout.layout_item_picture, count);

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
                else
                {
                    requestStatus.put(6, REQUEST_STATUS.REQUEST_FAILURED);
                }
                break;
        }
    }
}
