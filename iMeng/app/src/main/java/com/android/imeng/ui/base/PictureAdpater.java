package com.android.imeng.ui.base;

import android.content.Context;
import android.text.TextUtils;

import com.android.imeng.logic.PictureInfo;
import com.android.imeng.ui.base.BasePictureAdapter;

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

    @Override
    public PictureInfo.State getState(int position) {
        if (hasDownload(position))
        {
            return PictureInfo.State.SUCCESS;
        }
        else
        {
            PictureInfo pictureInfo = getItem(position);
            return pictureInfo.getmState();
        }
    }
}
