package com.android.imeng.ui;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.HairInfo;
import com.android.imeng.logic.PictureInfo;
import com.android.imeng.util.Constants;
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
public class HairAdpater extends BasePictureAdapter<HairInfo> {

    public HairAdpater(Context context, List<HairInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId, totalSize);
    }

    @Override
    public String getThumbnailUrl(int position) {
        HairInfo hairInfo = getItem(position);
        return hairInfo.getThumbnailInfo().getThumbnailUrl();
    }

    @Override
    public boolean hasDownload(int position) {
        HairInfo hairInfo = getItem(position);
        List<PictureInfo> originalHairs = hairInfo.getOriginalInfos();
        for(PictureInfo pictureInfo : originalHairs)
        {
            if (TextUtils.isEmpty(pictureInfo.getOriginalLocalPath()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public PictureInfo.State getState(int position) {
        if (hasDownload(position))
        {
            return PictureInfo.State.SUCCESS;
        }
        else
        {
            HairInfo hairInfo = getItem(position);
            List<PictureInfo> originalHairs = hairInfo.getOriginalInfos();
            boolean hasDownloading = false;
            boolean hasError = false;
            for(PictureInfo pictureInfo : originalHairs)
            {
                if (pictureInfo.getmState() == PictureInfo.State.DOWNLOADING)
                {
                    hasDownloading = true;
                }
                else if (pictureInfo.getmState() == PictureInfo.State.ERROR)
                {
                    hasError = true;
                }
            }
            if (hasDownloading) // 有一个在下载
            {
                return PictureInfo.State.DOWNLOADING;
            }
            else if (hasError) // 没有在下载，但有至少一个下载错误
            {
                return PictureInfo.State.ERROR;
            }
            else
            {
                return PictureInfo.State.INIT;
            }
        }
    }
}
