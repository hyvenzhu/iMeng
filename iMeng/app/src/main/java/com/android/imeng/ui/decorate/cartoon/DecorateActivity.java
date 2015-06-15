package com.android.imeng.ui.decorate.cartoon;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.util.Constants;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * 装饰界面
 *
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-15 20:57]
 */
public class DecorateActivity extends BasicActivity {
    @ViewInject(R.id.image_wall)
    private View imageWall; // 背景墙
    @ViewInject(R.id.image_view)
    private ImageView imageView; // 形象展示View
    @ViewInject(R.id.viewpagertab)
    private SmartTabLayout smartTabLayout; // 指示器
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    private NetLogic netLogic;

    private int sex; // 性别
    private String clothesPath; // 衣服路径
    // key 0：后面的头发  1：衣服   2：脸   3：前面的头发    4：眼睛    5：眉毛    6：嘴
    private Map<Integer, Drawable> drawableMap = new HashMap<Integer, Drawable>();

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

        netLogic = new NetLogic(this);

        // 调整宽高
        adjustView();
        // 背景墙
        adjustWall();
        // 加载形象
        loadDefault();

        smartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup viewGroup, int i, PagerAdapter pagerAdapter) {
                return null;
            }
        });

//        // 初始化GridView
//        loadGridView();
//
//        // 查询头发、衣服、装饰列表
//        netLogic.hairs(sex, hairIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
//        netLogic.clothes(sex, clothesIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
//        netLogic.decorations(sex, decorationIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
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
        imageView.setImageDrawable(BitmapHelper.overlay(drawableMap));
    }
}
