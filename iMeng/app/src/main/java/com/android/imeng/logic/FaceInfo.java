package com.android.imeng.logic;

/**
 * 人脸特征点信息
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-03 22:59]
 */
public class FaceInfo
{
    private float eye; // 眼睛
    private float mouth; // 嘴
    private float shape; // 脸型
    private float eyebrows; // 眉毛
    private String url; // 脸地址

    public float getEye() {
        return eye;
    }

    public void setEye(float eye) {
        this.eye = eye;
    }

    public float getMouth() {
        return mouth;
    }

    public void setMouth(float mouth) {
        this.mouth = mouth;
    }

    public float getShape() {
        return shape;
    }

    public void setShape(float shape) {
        this.shape = shape;
    }

    public float getEyebrows() {
        return eyebrows;
    }

    public void setEyebrows(float eyebrows) {
        this.eyebrows = eyebrows;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
