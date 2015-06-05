package com.android.imeng.ui;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.HairInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * 头发显示适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/05 12:48]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class HairAdpater extends BasicAdapter<HairInfo> {
    private int columnWidth; // 列宽
    private int columnHeight; // 列高
    private int totalSize; // 图片总共数量

    public HairAdpater(Context context, List<HairInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId);
        this.totalSize = totalSize;
    }

    public void setSize(int columnWidth, int columnHeight)
    {
        this.columnWidth = columnWidth;
        this.columnHeight = columnHeight;
    }

    @Override
    protected void getView(int position, View convertView) {
        // 调整宽高
        convertView.setLayoutParams(new AbsListView.LayoutParams(columnWidth, columnHeight));
        SimpleDraweeView picView = findViewById(convertView, R.id.pic_view);
        picView.setAspectRatio((columnWidth * 1.0f) / columnHeight);
        if (position == mData.size()) // 加载更多图标
        {
            ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.more).build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request).build();
            picView.setController(controller);
        }
        else // 缩略图
        {
            HairInfo hairInfo = getItem(position);

            picView.setImageURI(Uri.parse(hairInfo.getThumbnailInfo().getThumbnailUrl()));
        }
    }

    @Override
    public int getCount() {
        if (mData != null && mData.size() < totalSize)
        {
            return mData.size() + 1;
        }
        return super.getCount();
    }
}
