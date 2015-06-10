package com.android.imeng.ui.home.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.ui.base.OptListener;
import com.android.imeng.util.SPDBHelper;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * 墙纸适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-10 21:20]
 */
public class WallpaperAdpater extends BasicAdapter<String> {
    private int width; // 宽
    private int height; // 高
    private String selectCoverPath = String.valueOf(R.drawable.default_wallpaper); // 默认
    private OptListener optListener;
    public WallpaperAdpater(Context context, List<String> data, int resourceId, OptListener optListener) {
        super(context, data, resourceId);
        this.optListener = optListener;
    }

    public void select(String selectCoverPath)
    {
        this.selectCoverPath = selectCoverPath;
        new SPDBHelper().putString("wallpaper", selectCoverPath);
        notifyDataSetChanged();
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void getView(final int position, View convertView) {
        // 调整宽高
        if (convertView.getTag() == null) // convertView刚创建
        {
            convertView.setLayoutParams(new AbsListView.LayoutParams(width, height));
        }
        SimpleDraweeView coverView = findViewById(convertView, R.id.cover_view);
        RadioButton imageChoose = findViewById(convertView, R.id.image_choose);

        final String localPath = getItem(position);
        if (localPath.equals(String.valueOf(R.drawable.default_wallpaper)))
        {
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(R.drawable.default_wallpaper))
                    .build();
            coverView.setImageURI(uri);
        }
        else
        {
            coverView.setImageURI(Uri.fromFile(new File(localPath)));
        }

        imageChoose.setOnCheckedChangeListener(null);
        if (localPath.equals(selectCoverPath))
        {
            imageChoose.setChecked(true);
        }
        else
        {
            imageChoose.setChecked(false);
        }
        imageChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    select(localPath);
                    if (optListener != null)
                    {
                        optListener.onOpt(null, null);
                    }
                }
            }
        });
    }
}
