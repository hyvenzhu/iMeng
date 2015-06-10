package com.android.imeng.ui.home;

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
import com.android.imeng.ui.base.OptListener;
import com.android.imeng.ui.home.adapter.WallpaperAdpater;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.android.imeng.util.SPDBHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择墙纸
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-10 21:29]
 */
public class WallpaperActivity extends BasicActivity implements AdapterView.OnItemClickListener,
        OptListener{
    @ViewInject(R.id.image_grid)
    private GridView coverGrid;

    List<String> coverPaths = null;
    private WallpaperAdpater wallpaperAdpater;
    private float scale = (1.0f * Constants.IMAGE_WIDTH_HEIGHT_WITHOUT_LEFT_EREA) / Constants.IMAGE_WIDTH_HEIGHT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
    }

    @Override
    protected void init() {
        super.init();
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
        coverPaths.add(0, String.valueOf(R.drawable.default_wallpaper));
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
                    int columnWidth = (int) ((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                    int columnHeight = (int) (columnWidth / scale) - APKUtil.dip2px(WallpaperActivity.this, 50);

                    wallpaperAdpater = new WallpaperAdpater(WallpaperActivity.this, coverPaths,
                            R.layout.layout_item_wallpaper, WallpaperActivity.this);
                    wallpaperAdpater.setSize(columnWidth, columnHeight);
                    String wallpaper = new SPDBHelper().getString("wallpaper", String.valueOf(R.drawable.default_wallpaper));
                    wallpaperAdpater.select(wallpaper);
                    coverGrid.setAdapter(wallpaperAdpater);
                    coverGrid.setOnItemClickListener(WallpaperActivity.this);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        wallpaperAdpater.select(wallpaperAdpater.getItem(position));
        onBackPressed();
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

    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cancel_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.top_out);
    }

    @Override
    public void onOpt(Object tag, Object value) {
        onBackPressed();
    }
}
