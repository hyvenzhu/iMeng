package com.android.imeng.logic.model;

/**
 * 衣服和表情
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 22:43]
 */
public class ClothesAndExpression {
    private PictureInfo clothesInfo;
    private PictureInfo expressionInfo;

    public PictureInfo getExpressionInfo() {
        return expressionInfo;
    }

    public void setExpressionInfo(PictureInfo expressionInfo) {
        this.expressionInfo = expressionInfo;
    }

    public PictureInfo getClothesInfo() {
        return clothesInfo;
    }

    public void setClothesInfo(PictureInfo clothesInfo) {
        this.clothesInfo = clothesInfo;
    }
}
