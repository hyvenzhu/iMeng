package com.android.imeng.ui.decorate.photo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RadioButton;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.ImageChooseListener;
import com.android.imeng.logic.ImageInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * 个人形象适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 23:13]
 */
public class ImageAdpater extends BasicAdapter<ImageInfo> {
    private int size; // 宽高
    ImageChooseListener chooseListener;
    private int pageIndex;
    public ImageAdpater(Context context, List<ImageInfo> data, int resourceId, ImageChooseListener chooseListener, int pageIndex) {
        super(context, data, resourceId);
        this.chooseListener = chooseListener;
        this.pageIndex = pageIndex;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * 选中状态切换
     * @param position
     */
    public void toggleState(int position)
    {
        ImageInfo imageInfo = getItem(position);
        chooseListener.choose(imageInfo);
        notifyDataSetChanged();
    }

    @Override
    protected void getView(int position, View convertView) {
        // 调整宽高
        if (convertView.getTag() == null) // convertView刚创建
        {
            convertView.setLayoutParams(new AbsListView.LayoutParams(size, size));
        }
        SimpleDraweeView imagView = findViewById(convertView, R.id.image_view);
        RadioButton chooseBtn = findViewById(convertView, R.id.image_choose);

        final ImageInfo imageInfo = getItem(position);
        imagView.setImageURI(Uri.fromFile(new File(imageInfo.getLocalPath())));

        // 男女背景色
        if (imageInfo.getSex() == 0)
        {
            convertView.setBackgroundColor(Color.parseColor("#CAE4F3"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#f3bec4"));
        }

        // 是否选中
        if (chooseListener.isChoosed(imageInfo))
        {
            chooseBtn.setChecked(true);
        }
        else
        {
            chooseBtn.setChecked(false);
        }
    }
}
