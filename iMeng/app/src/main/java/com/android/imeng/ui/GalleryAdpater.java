package com.android.imeng.ui;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * 表情相册适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-07 21:20]
 */
public class GalleryAdpater extends BasicAdapter<String> {
    private int width; // 宽
    private int height; // 高
    public GalleryAdpater(Context context, List<String> data, int resourceId) {
        super(context, data, resourceId);
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void getView(int position, View convertView) {
        // 调整宽高
        convertView.setLayoutParams(new AbsListView.LayoutParams(width, height));
        SimpleDraweeView coverView = findViewById(convertView, R.id.cover_view);

        final String localPath = getItem(position);
        coverView.setImageURI(Uri.fromFile(new File(localPath)));
    }
}
