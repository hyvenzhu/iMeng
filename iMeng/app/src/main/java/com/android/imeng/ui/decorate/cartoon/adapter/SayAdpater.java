package com.android.imeng.ui.decorate.cartoon.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.adapter.BasePictureAdapter;

import java.util.List;

/**
 * 文字适配器, 最大区别：
 * 1、全部是本地图片
 * 2、第一张图表示取消装饰
 * 3、第二张表示输入文字
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/20 00:08]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class SayAdpater extends BasePictureAdapter<PictureInfo> {
    public SayAdpater(Context context, List<PictureInfo> data, int resourceId, int totalSize) {
        super(context, data, resourceId, totalSize);
    }

    @Override
    public String getThumbnailUrl(int position) {
        if (position == 0)
        {
            return "decoration_none";
        }
        else if (position == 1)
        {
            return "say_write";
        }
        PictureInfo pictureInfo = getItem(position - 2);
        return pictureInfo.getThumbnailUrl();
    }

    @Override
    public int getCount() {
        return super.getCount() + 2;
    }

    @Override
    public boolean hasDownload(int position) {
        return true;
    }

    @Override
    public PictureInfo.State getState(int position) {
        return PictureInfo.State.SUCCESS;
    }
}
