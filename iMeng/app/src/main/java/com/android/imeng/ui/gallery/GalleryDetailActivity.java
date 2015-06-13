package com.android.imeng.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.ui.base.adapter.ViewPagerAdapter;
import com.android.imeng.ui.gallery.adapter.GalleryDetailAdpater;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 相册详情界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-08 23:01]
 */
public class GalleryDetailActivity extends BasicActivity implements RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener{

    /**
     * 跳转
     * @param galleryDir 相册文件夹
     * @param activity
     */
    public static void actionStart(String galleryDir, Context activity)
    {
        Intent intent = new Intent(activity, GalleryDetailActivity.class);
        intent.putExtra("galleryDir", galleryDir);
        activity.startActivity(intent);
    }

    @ViewInject(R.id.small_cover_view)
    private SimpleDraweeView smallCoverView; // 个人头像小
    @ViewInject(R.id.title_txt)
    private TextView titleTxt; // 标题
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;
    @ViewInject(R.id.select_indicator)
    private RadioGroup selectIndicator;

    private final int PAGE_SIZE = 4; // 每页数量
    int totalPage = 0; // 总页数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);
    }

    @Override
    protected void init() {
        super.init();
        titleTxt.setText("表情详情");

        // 相册文件夹
        String galleryDir = getIntent().getStringExtra("galleryDir");
        // 封面图片
        File coverFile = new File(galleryDir, Constants.GALLERY_COVER);
        smallCoverView.setImageURI(Uri.fromFile(coverFile));

        // 相册所有图片
        final File[] files = new File(galleryDir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // 排除掉每日形象文件夹以及封面
                return !pathname.isDirectory() && !pathname.getName().startsWith(Constants.GALLERY_COVER);
            }
        });

        // 计算页数
        if (files != null && files.length > 0)
        {
            if (files.length % PAGE_SIZE == 0)
            {
                totalPage = files.length / PAGE_SIZE;
            }
            else
            {
                totalPage = files.length / PAGE_SIZE + 1;
            }

            final GridView[] gridViews = new GridView[totalPage];
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    final int viewWidth = viewPager.getMeasuredWidth();
                    int numColumns = 2;
                    int hoizontalSpacing = APKUtil.dip2px(GalleryDetailActivity.this, 10);
                    int itemSize = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));

                    // 添加指示器
                    for(int i = 0; i < totalPage; i++)
                    {
                        RadioButton radioButton = new RadioButton(GalleryDetailActivity.this);
                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(APKUtil.dip2px(GalleryDetailActivity.this, 15),
                                APKUtil.dip2px(GalleryDetailActivity.this, 15));
                        if (i != 0)
                        {
                            layoutParams.leftMargin = APKUtil.dip2px(GalleryDetailActivity.this, 10);
                        }
                        radioButton.setLayoutParams(layoutParams);
                        radioButton.setChecked(false);
                        radioButton.setButtonDrawable(android.R.color.transparent);
                        radioButton.setBackgroundResource(R.drawable.circle_indicator);
                        radioButton.setId(i);
                        selectIndicator.addView(radioButton);

                        int numOfCurrentPage = PAGE_SIZE; // 当前页数据数量
                        if (files.length % PAGE_SIZE != 0)
                        {
                            if (i == totalPage - 1) // 最后一页
                            {
                                numOfCurrentPage = files.length % PAGE_SIZE;
                            }
                        }

                        List<String> onePage = new ArrayList<String>(); // 存储当前页数据
                        for(int j = i * PAGE_SIZE; j < i * PAGE_SIZE + numOfCurrentPage; j++)
                        {
                            onePage.add(files[j].getAbsolutePath());
                        }

                        // 添加GridView
                        GridView grid = new GridView(GalleryDetailActivity.this);
                        grid.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        grid.setHorizontalSpacing(APKUtil.dip2px(GalleryDetailActivity.this, 10));
                        grid.setVerticalSpacing(APKUtil.dip2px(GalleryDetailActivity.this, 10));
                        grid.setNumColumns(2);
                        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
                        grid.setOnItemClickListener(GalleryDetailActivity.this);
                        gridViews[i] = grid;

                        GalleryDetailAdpater imageAdpater = new GalleryDetailAdpater(GalleryDetailActivity.this, onePage,
                                R.layout.layout_item_gallery_detail, 0);
                        imageAdpater.setSize(itemSize);
                        grid.setAdapter(imageAdpater);
                    }

                    // 调整ViewPager高度
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)viewPager.getLayoutParams();
                    params.height = 2 * itemSize + hoizontalSpacing;
                    viewPager.setLayoutParams(params);
                    viewPager.setAdapter(new ViewPagerAdapter(gridViews));
                    viewPager.setOnPageChangeListener(GalleryDetailActivity.this);

                    // 默认选中第一个
                    selectIndicator.check(0);
                    selectIndicator.setOnCheckedChangeListener(GalleryDetailActivity.this);
                }
            });
        }
    }

    @OnClick({R.id.left_area_lay})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_area_lay:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        viewPager.setCurrentItem(checkedId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 跳转到分享界面
        
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        selectIndicator.check(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
