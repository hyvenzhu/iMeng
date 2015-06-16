package com.android.imeng.ui.decorate.cartoon.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.adapter.BasePictureAdapter;

import java.util.List;

/**
 * 大衣服显示适配器（最大区别：没有缩略图）
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/16 23:04]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class BigClothesAdpater extends BasePictureAdapter<PictureInfo> {
    public BigClothesAdpater(Context context, List<PictureInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId, totalSize);
    }

    @Override
    public String getThumbnailUrl(int position) {
        PictureInfo pictureInfo = getItem(position);
        return pictureInfo.getOriginalUrl();
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
