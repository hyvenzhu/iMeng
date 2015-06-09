package com.android.imeng.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * 相册详情适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 23:13]
 */
public class GalleryDetailAdpater extends BasicAdapter<String> {
    private int size; // 宽高
    private int sex;
    public GalleryDetailAdpater(Context context, List<String> data, int resourceId, int sex) {
        super(context, data, resourceId);
        this.sex = sex;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    @Override
    protected void getView(int position, View convertView) {
        // 调整宽高
        if (convertView.getTag() == null) // convertView刚创建
        {
            convertView.setLayoutParams(new AbsListView.LayoutParams(size, size));
        }
        SimpleDraweeView imagView = findViewById(convertView, R.id.image_view);

        final String filePath = getItem(position);
        imagView.setImageURI(Uri.fromFile(new File(filePath)));

        // 男女背景色
        if (sex == 0)
        {
            convertView.setBackgroundColor(Color.parseColor("#CAE4F3"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#f3bec4"));
        }
    }
}
