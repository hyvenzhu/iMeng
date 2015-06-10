package com.android.imeng.logic.model;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.android.imeng.logic.BitmapHelper;

/**
 * 个人形象
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 23:16]
 */
public class ImageInfo {
    private int sex; // 性别 0：男  1：女
    private String hairBackground; // 背后头发
    private String clothes; // 衣服
    private String face; // 脸
    private String hairFont; // 前面头发
    private String decoration; // 装饰
    private int sayDrawableId = -1; // 文字资源

    private int index; // 下标
    private String localPath; // 组装好的本地图片路径

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHairBackground() {
        return hairBackground;
    }

    public void setHairBackground(String hairBackground) {
        this.hairBackground = hairBackground;
    }

    public String getClothes() {
        return clothes;
    }

    public void setClothes(String clothes) {
        this.clothes = clothes;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getHairFont() {
        return hairFont;
    }

    public void setHairFont(String hairFont) {
        this.hairFont = hairFont;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSayDrawableId() {
        return sayDrawableId;
    }

    public void setSayDrawableId(int sayDrawableId) {
        this.sayDrawableId = sayDrawableId;
    }

    private Drawable overlayDrawable; // 不包含文字
    private Drawable overlayDrawableIncludeSay; // 包含文字

    /**
     * 清除引用，释放内存
     */
    public void clearDrawable()
    {
        overlayDrawable = null;
        overlayDrawableIncludeSay = null;
    }

    public Drawable getOverlayDrawable(Resources res) {
        return getOverlayDrawable(res, true);
    }

    public Drawable getOverlayDrawable(Resources res, boolean includeSay) {
        if (overlayDrawable == null)
        {
            overlayDrawable = overlay(res);
        }
        if (includeSay)
        {
            if (overlayDrawableIncludeSay == null)
            {
                // 文字
                Drawable sayDrawable = null;
                if (sayDrawableId != -1)
                {
                    sayDrawable = res.getDrawable(sayDrawableId);
                }
                if (overlayDrawable != null && sayDrawable != null)
                {
                    overlayDrawableIncludeSay = BitmapHelper.overlayDrawable(overlayDrawable, sayDrawable);
                }
                else if (sayDrawable != null)
                {
                    overlayDrawableIncludeSay = BitmapHelper.overlayDrawable(sayDrawable);
                }
            }
            return overlayDrawableIncludeSay != null? overlayDrawableIncludeSay : overlayDrawable;
        }
        else
        {
            return overlayDrawable;
        }
    }

    /**
     * 图层叠加
     * @return
     */
    private Drawable overlay(Resources res)
    {
        Drawable hairBackgroundDrawable = null;
        if (!TextUtils.isEmpty(hairBackground))
        {
            hairBackgroundDrawable = new BitmapDrawable(res, hairBackground);
        }

        Drawable clothesDrawable = null;
        String clothesLocalPath = BitmapHelper.getLocalPath(clothes);
        if (!TextUtils.isEmpty(clothesLocalPath))
        {
            clothesDrawable = new BitmapDrawable(res, clothesLocalPath);
        }

        Drawable faceDrawable = null;
        String faceLocalPath = BitmapHelper.getLocalPath(face);
        if (!TextUtils.isEmpty(faceLocalPath))
        {
            faceDrawable = new BitmapDrawable(res, faceLocalPath);
        }

        Drawable hairFontDrawable = null;
        if (!TextUtils.isEmpty(hairFont))
        {
            hairFontDrawable = new BitmapDrawable(res, hairFont);
        }

        Drawable decorationDrawable = null;
        if (!TextUtils.isEmpty(decoration))
        {
            decorationDrawable = new BitmapDrawable(res, decoration);
        }

        Drawable drawable = null;
        // 背后的头发
        if (hairBackgroundDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairBackgroundDrawable);
        }

        // 衣服
        if (drawable != null && clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, clothesDrawable);
        }
        else if (clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(clothesDrawable);
        }

        // 脸
        if (drawable != null && faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, faceDrawable);
        }
        else if (faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(faceDrawable);
        }

        // 前面的头发
        if (drawable != null && hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, hairFontDrawable);
        }
        else if (hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairFontDrawable);
        }

        // 装饰
        if (drawable != null && decorationDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, decorationDrawable);
        }
        else if (decorationDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(decorationDrawable);
        }

        return drawable;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "sex=" + sex +
                ", hairBackground='" + hairBackground + '\'' +
                ", clothes='" + clothes + '\'' +
                ", face='" + face + '\'' +
                ", hairFont='" + hairFont + '\'' +
                ", decoration='" + decoration + '\'' +
                ", sayDrawableId=" + sayDrawableId +
                '}';
    }
}
