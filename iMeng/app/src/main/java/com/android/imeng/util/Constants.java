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
    public static final String FAVORITE_DIR = "favorite"; // 收藏文件夹
    public static final int IMAGE_WIDTH_HEIGHT = 500; // 形象宽高
    public static final int IMAGE_WIDTH_WITHOUT_LEFT_EREA = 270; // 形象宽高(不包含左边透明区域)
    public static final int PIC_THUMBNAIL_WIDTH = 160; // 修饰小图宽度
    public static final int PIC_THUMBNAIL_HEIGHT = 212; // 修饰小图高度
    public static final float SAY_CONTENT_HEIGHT = 420; // 文字高度
    public static final float SAY_CONTENT_WIDTH = 80; // 文字宽度
    public static final int SAY_EMPTY_HORIZONTAL_X = 95; // 空白文字背景内容区域中间线x坐标
    public static final int DEFAULT_PAGE_SIZE = 10; // 默认每页数据大小
    public static final float TRANSLATE_X_PERCENT = (Constants.IMAGE_WIDTH_HEIGHT -
            Constants.IMAGE_WIDTH_WITHOUT_LEFT_EREA +
            Constants.IMAGE_WIDTH_WITHOUT_LEFT_EREA / 2f -
            Constants.IMAGE_WIDTH_HEIGHT / 2f) / Constants.IMAGE_WIDTH_HEIGHT; // 图片居中, x轴横向偏移量占图片宽度的百分比
    public static final float SAY_INPUT_TRANSLATE_X_PERCENT = (IMAGE_WIDTH_HEIGHT / 2 - SAY_EMPTY_HORIZONTAL_X) /
            (IMAGE_WIDTH_HEIGHT * 1.0f); // 输入框平移百分比

    // face++
    public static final String API_KEY = "88c06ced7ef99158cda408abee1adc45";
    public static final String API_SECRET = "IFY9pE1KXazxgTMHv58msCDWgtdlffCI";

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
