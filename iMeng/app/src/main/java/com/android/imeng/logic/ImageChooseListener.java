package com.android.imeng.logic;

/**
 * 选择形象监听器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-07 16:54]
 */
public interface ImageChooseListener {
    boolean isChoosed(ImageInfo imageInfo);
    void choose(ImageInfo imageInfo);
}
