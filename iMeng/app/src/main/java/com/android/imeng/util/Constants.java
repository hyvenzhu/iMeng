package com.android.imeng.util;

/**
 * 常量定义
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-01 22:17]
 */
public class Constants {
    public static final String DOWNLOAD_DIR = "download";
    public static final String TEMP_DIR = "temp";
    public static final String IMAGE_DIR = "image"; // 形象存储文件夹
    public static final String GALLERY_COVER = "cover"; // 相册封面图片文件名
    public static final String DAILY_DIR = "daily"; // 每日形象文件夹
    public static final int IMAGE_WIDTH_HEIGHT = 500; // 形象宽高
    public static final int IMAGE_WIDTH_HEIGHT_WITHOUT_LEFT_EREA = 270; // 形象宽高(不包含左边透明区域)
    public static final int PIC_THUMBNAIL_WIDTH = 160; // 修饰小图宽度
    public static final int PIC_THUMBNAIL_HEIGHT = 212; // 修饰小图高度
    public static final int DEFAULT_PAGE_SIZE = 10; // 默认每页数据大小

    // face++
    public static final String API_KEY = "88c06ced7ef99158cda408abee1adc45";
    public static final String API_SECRET = "IFY9pE1KXazxgTMHv58msCDWgtdlffCI";
    public static final String DETECT_URL = "http://apicn.faceplusplus.com/v2/detection/detect";

    // sharesdk
    public static final String ShareAppKey = "6e2f7bd20747";
    public static final String ShareWxAppId = "wx775169d54b05c8d9";
    public static final String ShareWxAppSecret = "2c291ffe154e1a340a2921977c878ea6";
    public static final String ShareQQAppId = "1104563534";
    public static final String ShareQQSecret = "FFgjXqVejuDmpQWO";

    public static final String IP_PORT = "http://121.41.114.48";
    public static final String CLOTHES_URL = IP_PORT + "/mm/clothes"; // 衣服列表
    public static final String HAIRS_URL = IP_PORT + "/mm/hairs"; // 头发列表
    public static final String DECORATIONS_URL = IP_PORT + "/mm/decorations"; // 装饰列表
    public static final String EYES_URL = IP_PORT + "/mm/eyes"; // 眼睛列表
    public static final String FACESHAPES_URL = IP_PORT + "/mm/faceShapes"; // 脸型列表
    public static final String EYEBROWS_URL = IP_PORT + "/mm/eyebrows"; // 眉毛列表
    public static final String MOUTHS_URL = IP_PORT + "/mm/mouths"; // 嘴列表

    public static final String FACE_URL = IP_PORT + "/mm/face"; // 查询单个脸信息
    public static final String CLOTHES_AND_EXPRESSION_URL = IP_PORT + "/mm/clothesAndExpression"; // 查询表情和衣服列表
    public static final String BIG_CLOTHES_URL = IP_PORT + "/mm/bigClothes"; // 根据性别、类别ID查询一套衣服的所有大图列表
}
