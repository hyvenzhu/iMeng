package com.android.imeng.ui.base;

import android.os.Bundle;
import android.os.Message;
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
import com.android.imeng.ui.base.adapter.FavoriteAdpater;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 收藏
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-13 21:11]
 */
public class FavoriteActivity extends BasicActivity implements OptListener, AdapterView.OnItemClickListener {

    @ViewInject(R.id.image_grid)
    private GridView favoriteGrid;
    private FavoriteAdpater.Mode mMode = FavoriteAdpater.Mode.NORMAL;

    List<String> coverPaths = null;
    private FavoriteAdpater favoriteAdpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "收藏表情", false);
        leftBtn.setText("返回");
        rightBtn.setText("编辑");

        // 加载收藏图片
        loadFavorites();

        // 加载视图
        loadGridView();

        // 注册分享界面取消收藏通知
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.cancelFavorite: // 取消收藏
                String path = (String)msg.obj;
                onOpt(null, path);
                break;
        }
    }

    /**
     * 加载收藏数据
     */
    private void loadFavorites()
    {
        coverPaths = new ArrayList<String>();
        File[] files = APKUtil.getDiskCacheDir(this, Constants.FAVORITE_DIR).listFiles();
        if (files != null && files.length > 0)
        {
            for(File file : files)
            {
                coverPaths.add(file.getAbsolutePath());
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
            favoriteGrid.post(new Runnable() {
                @Override
                public void run() {
                    final int viewWidth = favoriteGrid.getWidth();
                    int numColumns = favoriteGrid.getNumColumns();
                    int hoizontalSpacing = APKUtil.dip2px(FavoriteActivity.this, 1);
                    int columnWidth = (int) ((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));

                    favoriteAdpater = new FavoriteAdpater(FavoriteActivity.this, coverPaths,
                            R.layout.layout_item_favorite, FavoriteActivity.this);
                    favoriteAdpater.setSize(columnWidth, columnWidth);
                    favoriteGrid.setAdapter(favoriteAdpater);
                    favoriteGrid.setOnItemClickListener(FavoriteActivity.this);
                }
            });
            rightBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path = coverPaths.get(position);
        ShareActivity.actionStart(this, path, path.endsWith("_0")? 0 : 1, true);
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
                if (mMode == FavoriteAdpater.Mode.NORMAL)
                {
                    mMode = FavoriteAdpater.Mode.DELETE;
                    rightBtn.setText("完成");
                }
                else
                {
                    mMode = FavoriteAdpater.Mode.NORMAL;
                    rightBtn.setText("编辑");
                }
                favoriteAdpater.setMode(mMode);
                break;
        }
    }

    @Override
    public void onOpt(Object tag, Object value) {
        // 删除
        APKUtil.deleteFile(new File(value.toString()).getAbsolutePath());
        coverPaths.remove(value.toString());
        favoriteAdpater.notifyDataSetChanged();

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
            ((ViewGroup) favoriteGrid.getParent()).addView(emptyView);
            favoriteGrid.setEmptyView(emptyView);
            return true;
        }
        return false;
    }
}
