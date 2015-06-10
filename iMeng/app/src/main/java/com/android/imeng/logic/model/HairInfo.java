package com.android.imeng.logic.model;

import java.util.List;

/**
 * 头发信息
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/05 15:14]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class HairInfo {
    private PictureInfo thumbnailInfo; // 小图片信息
    private List<PictureInfo> originalInfos; // 大图信息

    public PictureInfo getThumbnailInfo() {
        return thumbnailInfo;
    }

    public void setThumbnailInfo(PictureInfo thumbnailInfo) {
        this.thumbnailInfo = thumbnailInfo;
    }

    public List<PictureInfo> getOriginalInfos() {
        return originalInfos;
    }

    public void setOriginalInfos(List<PictureInfo> originalInfos) {
        this.originalInfos = originalInfos;
    }
}
