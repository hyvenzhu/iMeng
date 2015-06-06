package com.android.imeng.ui;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.PictureInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 缩略图适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 10:18]
 */
public abstract class BasePictureAdapter<T> extends BasicAdapter<T> {
    private int columnWidth; // 列宽
    private int columnHeight; // 列高
    private int totalSize; // 图片总共数量

    public BasePictureAdapter(Context context, List<T> data, int resourceId, int totalSize) {
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
        final SimpleDraweeView picView = (SimpleDraweeView)findViewById(convertView, R.id.pic_view);
        picView.setAspectRatio((columnWidth * 1.0f) / columnHeight);

        // 调整是否已下载宽高
        View downloadView = findViewById(convertView, R.id.download_lay);
        int size = (int)(Math.min(columnWidth, columnHeight) * 0.8);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        downloadView.setLayoutParams(layoutParams);

        if (position == mData.size()) // 加载更多图标
        {
            downloadView.setVisibility(View.GONE);

            ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.more).build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request).build();
            picView.setController(controller);
        }
        else
        {
            // 缩略图
            picView.setImageURI(Uri.parse(getThumbnailUrl(position)));

            // 是否已下载
            if (hasDownload(position))
            {
                downloadView.setVisibility(View.GONE);
            }
            else
            {
                downloadView.setVisibility(View.VISIBLE);

                // 状态
                TextView stateTxt = findViewById(convertView, R.id.state_txt);
                PictureInfo.State mState = getState(position);
                if (mState == PictureInfo.State.INIT)
                {
                    stateTxt.setText("未下载");
                }
                else if (mState == PictureInfo.State.DOWNLOADING)
                {
                    stateTxt.setText("下载中...");
                }
                else if (mState == PictureInfo.State.ERROR)
                {
                    stateTxt.setText("下载失败");
                }
            }
        }
    }

    /**
     * 缩略图地址
     * @param position
     * @return
     */
    public abstract String getThumbnailUrl(int position);

    /**
     * 大图是否已下载
     * @param position
     * @return
     */
    public abstract boolean hasDownload(int position);

    /**
     * 下载状态
     * @param position
     * @return
     */
    public abstract PictureInfo.State getState(int position);

    @Override
    public int getCount() {
        if (mData != null && mData.size() < totalSize)
        {
            return mData.size() + 1;
        }
        return super.getCount();
    }

    /**
     * 是否是More按钮
     * @param position
     * @return
     */
    public boolean isMore(int position)
    {
        if (mData != null && mData.size() < totalSize && position == getCount() - 1)
        {
            return true;
        }
        return false;
    }
}
