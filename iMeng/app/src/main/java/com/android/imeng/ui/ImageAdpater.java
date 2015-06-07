package com.android.imeng.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人形象适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 23:13]
 */
public class ImageAdpater extends BasicAdapter<ImageInfo> {
    private int size; // 宽高
    private List<ImageInfo> choosedImageInfos = new ArrayList<ImageInfo>(); // 选择的形象
    public ImageAdpater(Context context, List<ImageInfo> data, int resourceId) {
        super(context, data, resourceId);
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public List<ImageInfo> getChoosedImageInfos()
    {
        return choosedImageInfos;
    }

    /**
     * 选中状态切换
     * @param position
     */
    public void toggleState(int position)
    {
        ImageInfo imageInfo = getItem(position);
        if (choosedImageInfos.contains(imageInfo))
        {
            choosedImageInfos.remove(imageInfo);
        }
        else
        {
            choosedImageInfos.add(imageInfo);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void getView(int position, View convertView) {
        // 调整宽高
        convertView.setLayoutParams(new AbsListView.LayoutParams(size, size));
        ImageView imagView = findViewById(convertView, R.id.image_view);
        RadioButton chooseBtn = findViewById(convertView, R.id.image_choose);

        final ImageInfo imageInfo = getItem(position);
        imagView.setImageDrawable(imageInfo.getOverlayDrawable(mContext.getResources()));

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
        if (choosedImageInfos.contains(imageInfo))
        {
            chooseBtn.setChecked(true);
        }
        else
        {
            chooseBtn.setChecked(false);
        }
    }
}
