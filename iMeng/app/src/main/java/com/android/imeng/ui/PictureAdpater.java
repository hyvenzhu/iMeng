package com.android.imeng.ui;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicAdapter;
import com.android.imeng.logic.HairInfo;
import com.android.imeng.logic.PictureInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * 大小图显示适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/05 12:48]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class PictureAdpater extends BasePictureAdapter<PictureInfo> {
    public PictureAdpater(Context context, List<PictureInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId, totalSize);
    }

    @Override
    public String getThumbnailUrl(int position) {
        PictureInfo pictureInfo = getItem(position);
        return pictureInfo.getThumbnailUrl();
    }

    @Override
    public boolean hasDownload(int position) {
        PictureInfo pictureInfo = getItem(position);
        return !TextUtils.isEmpty(pictureInfo.getOriginalLocalPath());
    }
}
