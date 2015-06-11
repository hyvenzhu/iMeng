package com.android.imeng.logic.parser;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享帮助类
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-11 22:31]
 */
public class ShareHelper {
    /**
     * 分享
     * @param platForm 平台名称
     * @param imagePath 图片路径
     * @param actionListener 分享结果回调
     */
    public void share(String platForm, String imagePath, PlatformActionListener actionListener)
    {
        Platform.ShareParams shareParams = null;
        if (Wechat.NAME.equals(platForm))
        {
            Wechat.ShareParams wechatParams = new Wechat.ShareParams();
            wechatParams.setTitle("脸萌");
            wechatParams.setComment("脸萌");
            wechatParams.setImagePath(imagePath);
            shareParams = wechatParams;
        }
        else if (WechatMoments.NAME.equals(platForm))
        {
            WechatMoments.ShareParams wechatMomentsParams = new WechatMoments.ShareParams();
            wechatMomentsParams.setTitle("脸萌");
            wechatMomentsParams.setComment("脸萌");
            wechatMomentsParams.setImagePath(imagePath);
            shareParams = wechatMomentsParams;
        }
        else if (QQ.NAME.equals(platForm))
        {
            QQ.ShareParams qqParams = new QQ.ShareParams();
            qqParams.setTitle("脸萌");
            qqParams.setComment("脸萌");
            qqParams.setImagePath(imagePath);
            shareParams = qqParams;
        }
        if (shareParams != null)
        {
            Platform platform = ShareSDK.getPlatform(platForm);
            platform.setPlatformActionListener(actionListener);
            platform.share(shareParams);
        }
    }
}
