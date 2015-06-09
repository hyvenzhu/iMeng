package com.android.imeng.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 表情相册
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-07 14:01]
 */
public class ImageGalleryActivity extends BasicActivity implements AdapterView.OnItemClickListener,
        GalleryDeleteListener {

    @ViewInject(R.id.image_grid)
    private GridView coverGrid;
    private GalleryAdpater.Mode mMode = GalleryAdpater.Mode.NORMAL;

    List<String> coverPaths = null;
    private GalleryAdpater galleryAdpater;
    private float scale = (1.0f * Constants.IMAGE_WIDTH_HEIGHT_WITHOUT_LEFT_EREA) / Constants.IMAGE_WIDTH_HEIGHT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "表情相册", false);
        leftBtn.setText("返回");
        rightBtn.setText("编辑");

        // 加载封面
        loadGalleryCover();

        // 加载视图
        loadGridView();
    }

    /**
     * 加载相册封面
     */
    private void loadGalleryCover()
    {
        coverPaths = new ArrayList<String>();
        File[] gallertDirs = APKUtil.getDiskCacheDir(this, Constants.IMAGE_DIR).listFiles();
        for(File dir : gallertDirs)
        {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return Constants.GALLERY_COVER.equals(filename);
                }
            });
            if (files != null && files.length > 0)
            {
                coverPaths.add(files[0].getAbsolutePath());
            }
        }
    }

    /**
     * 加载视图
     */
    private void loadGridView()
    {
        if (!adjustEmpty())
        {
            coverGrid.post(new Runnable() {
                @Override
                public void run() {
                    final int viewWidth = coverGrid.getWidth();
                    int numColumns = coverGrid.getNumColumns();
                    int hoizontalSpacing = coverGrid.getHorizontalSpacing();
                    int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                    int columnHeight = (int)(columnWidth / scale) - APKUtil.dip2px(ImageGalleryActivity.this, 50);

                    galleryAdpater = new GalleryAdpater(ImageGalleryActivity.this, coverPaths,
                            R.layout.layout_item_gallery, ImageGalleryActivity.this);
                    galleryAdpater.setSize(columnWidth, columnHeight);
                    coverGrid.setAdapter(galleryAdpater);
                    coverGrid.setOnItemClickListener(ImageGalleryActivity.this);
                }
            });
            rightBtn.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.title_left_btn, R.id.title_right_btn})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn: // 编辑
                if (mMode == GalleryAdpater.Mode.NORMAL)
                {
                    mMode = GalleryAdpater.Mode.DELETE;
                    rightBtn.setText("完成");
                }
                else
                {
                    mMode = GalleryAdpater.Mode.NORMAL;
                    rightBtn.setText("编辑");
                }
                galleryAdpater.setMode(mMode);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String coverPath = galleryAdpater.getItem(position);
        // 相册文件夹
        String galleryDir = new File(coverPath).getParent();
        GalleryDetailActivity.actionStart(galleryDir, this);
    }

    @Override
    public void onDelete(String coverPath) {
        // 删除相册文件夹
        APKUtil.deleteFile(new File(coverPath).getParentFile().getAbsolutePath());
        coverPaths.remove(coverPath);
        galleryAdpater.notifyDataSetChanged();

        if (adjustEmpty())
        {
            // 隐藏右侧按钮
            rightBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 无数据
     * @return 是否有数据
     */
    private boolean adjustEmpty()
    {
        if (coverPaths == null || coverPaths.size() == 0)
        {
            // 设置空视图
            ImageView emptyView = new ImageView(this);
            emptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            emptyView.setBackgroundResource(R.drawable.gallery_empty);
            ((ViewGroup)coverGrid.getParent()).addView(emptyView);
            coverGrid.setEmptyView(emptyView);
            return true;
        }
        return false;
    }
}
