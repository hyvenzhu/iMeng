package com.android.imeng.ui.decorate.cartoon.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.adapter.BasePictureAdapter;

import java.util.List;

/**
 * 装饰适配器（最大区别：第一张图是本地的，表示取消装饰）
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/17 22:20]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class DecorationAdpater extends BasePictureAdapter<PictureInfo> {
    public DecorationAdpater(Context context, List<PictureInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId, totalSize);
    }

    @Override
    public String getThumbnailUrl(int position) {
        if (position == 0)
        {
            return "decoration_none";
        }
        PictureInfo pictureInfo = getItem(position - 1);
        return pictureInfo.getThumbnailUrl();
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public boolean hasDownload(int position) {
        if (position == 0) // 第一张本地默认
        {
            return true;
        }
        PictureInfo pictureInfo = getItem(position - 1);
        return !TextUtils.isEmpty(pictureInfo.getOriginalLocalPath());
    }

    @Override
    public PictureInfo.State getState(int position) {
        if (hasDownload(position) || position == 0)
        {
            return PictureInfo.State.SUCCESS;
        }
        else
        {
            PictureInfo pictureInfo = getItem(position - 1);
            return pictureInfo.getmState();
        }
    }
}
