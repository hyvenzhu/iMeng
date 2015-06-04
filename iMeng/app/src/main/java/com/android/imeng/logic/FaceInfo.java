package com.android.imeng.logic;

/**
 * 人脸特征点信息
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-03 22:59]
 */
public class FaceInfo
{
    private int eye; // 眼睛
    private int mouth; // 嘴
    private int shape; // 脸型
    private int eyebrows; // 眉毛
    private String url; // 脸地址

    public int getEye() {
        return eye;
    }

    public void setEye(int eye) {
        this.eye = eye;
    }

    public int getMouth() {
        return mouth;
    }

    public void setMouth(int mouth) {
        this.mouth = mouth;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public int getEyebrows() {
        return eyebrows;
    }

    public void setEyebrows(int eyebrows) {
        this.eyebrows = eyebrows;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
