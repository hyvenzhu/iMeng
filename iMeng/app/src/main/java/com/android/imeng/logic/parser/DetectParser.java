package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.model.FaceInfo;
import java.util.HashMap;

/**
 * 人脸识别检测解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/03 17:57]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class DetectParser extends JsonParser {
    private double smiling;
    private String glassValue; // None/Dark/Normal
    public DetectParser(double smiling, String glassValue)
    {
        this.smiling = smiling;
        this.glassValue = glassValue;
    }

    @Override
    public InfoResult doParse(String response) throws JSONException {
        InfoResult infoResult = new InfoResult.Builder().success(true).build();
        FaceInfo faceInfo = new FaceInfo();
        // 根据face++返回的人脸(Face)相应的面部轮廓，五官等关键点的位置计算出脸部信息
        JSONObject landmarkObject = JSON.parseObject(response);
        faceInfo.setEye(eyeAnalyse(landmarkObject));
        faceInfo.setEyebrows(eyebrowAnalyse(landmarkObject));
        faceInfo.setMouth(mouthAnalyse(landmarkObject));
        faceInfo.setShape(contourAnalyse(landmarkObject));
        faceInfo.setGlassValue(glassValue);
        infoResult.setExtraObj(faceInfo);
        return infoResult;
    }

    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {

    }

    /**
     * 分析眼睛
     * 长小上                    0
     * 长小下                    1
     * 长大上                    2
     * 长大下                    3
     * 短小上                    4
     * 短小下                    5
     * 短大上                    6
     * 短大下                    7
     * @param landmarkObject
     * @return
     */
    private int eyeAnalyse(JSONObject landmarkObject)
    {
        JSONObject left_eye_left_corner =  landmarkObject.getJSONObject("left_eye_left_corner");
        JSONObject left_eye_right_corner =  landmarkObject.getJSONObject("left_eye_right_corner");
        // 眼睛宽度
        double eyeWidth = calculateLength(left_eye_left_corner.getDouble("x"), left_eye_left_corner.getDouble("y"),
                left_eye_right_corner.getDouble("x"), left_eye_right_corner.getDouble("y"));

        JSONObject left_eye_top = landmarkObject.getJSONObject("left_eye_top");
        JSONObject left_eye_bottom = landmarkObject.getJSONObject("left_eye_bottom");
        // 眼睛高度
        double eyeHeight = calculateLength(left_eye_top.getDouble("x"), left_eye_top.getDouble("y"),
                left_eye_bottom.getDouble("x"), left_eye_bottom.getDouble("y"));

        JSONObject right_eye_left_corner =  landmarkObject.getJSONObject("right_eye_left_corner");
        JSONObject right_eye_right_corner =  landmarkObject.getJSONObject("right_eye_right_corner");
        // 眼睛上下
        double directionSize = (left_eye_left_corner.getDouble("y") - left_eye_right_corner.getDouble("y")) +
                (right_eye_left_corner.getDouble("y") - right_eye_right_corner.getDouble("y"));

        JSONObject contour_left1 =  landmarkObject.getJSONObject("contour_left1");
        JSONObject contour_right1 =  landmarkObject.getJSONObject("contour_right1");
        // 脸长度
        double faceWidth = calculateLength(contour_left1.getDouble("x"), contour_left1.getDouble("y"),
                contour_right1.getDouble("x"), contour_right1.getDouble("y"));

        double widthRatio = eyeWidth / faceWidth;
        double heightRatio = eyeHeight / faceWidth;

        HashMap<String, Integer> map = new HashMap<>();
        map.put("101", 0); // 长小上
        map.put("100", 1); // 长小下
        map.put("111", 2); // 长大上
        map.put("110", 3); // 长大下
        map.put("001", 4); // 短小上
        map.put("000", 5); // 短小下
        map.put("011", 6); // 短大上
        map.put("010", 7); // 短大下

        if (widthRatio <= 0.21) // 短
        {
            widthRatio = 0;
        }
        else
        {
            widthRatio = 1;
        }
        if (heightRatio <= 0.054) // 小
        {
            heightRatio = 0;
        }
        else
        {
            heightRatio = 1;
        }
        if (directionSize > 0) // 下
        {
            directionSize = 0;
        }
        else
        {
            directionSize = 1;
        }
        return map.get(String.valueOf((int)widthRatio) + String.valueOf((int)heightRatio) + String.valueOf((int)directionSize));
    }

    /**
     * 分析眉毛
     * 粗长上                     0
     * 粗长下                     1
     * 粗短上                     2
     * 粗短下                     3
     * 细长上                     4
     * 细长下                     5
     * 细短上                     6
     * 细短下                     7
     * @param landmarkObject
     * @return
     */
    private int eyebrowAnalyse(JSONObject landmarkObject)
    {
        JSONObject left_eyebrow_left_corner =  landmarkObject.getJSONObject("left_eyebrow_left_corner");
        JSONObject left_eyebrow_right_corner =  landmarkObject.getJSONObject("left_eyebrow_right_corner");
        // 眉毛宽度
        double eyebrowWidth = calculateLength(left_eyebrow_left_corner.getDouble("x"), left_eyebrow_left_corner.getDouble("y"),
                left_eyebrow_right_corner.getDouble("x"), left_eyebrow_right_corner.getDouble("y"));

        JSONObject left_eyebrow_lower_left_quarter = landmarkObject.getJSONObject("left_eyebrow_lower_left_quarter");
        JSONObject left_eyebrow_upper_left_quarter = landmarkObject.getJSONObject("left_eyebrow_upper_left_quarter");
        JSONObject left_eyebrow_lower_middle = landmarkObject.getJSONObject("left_eyebrow_lower_middle");
        JSONObject left_eyebrow_upper_middle = landmarkObject.getJSONObject("left_eyebrow_upper_middle");
        JSONObject left_eyebrow_lower_right_quarter = landmarkObject.getJSONObject("left_eyebrow_lower_right_quarter");
        JSONObject left_eyebrow_upper_right_quarter = landmarkObject.getJSONObject("left_eyebrow_upper_right_quarter");
        double eyebrowHeight1 = calculateLength(left_eyebrow_lower_left_quarter.getDouble("x"), left_eyebrow_lower_left_quarter.getDouble("y"),
                left_eyebrow_upper_left_quarter.getDouble("x"), left_eyebrow_upper_left_quarter.getDouble("y"));
        double eyebrowHeight2 = calculateLength(left_eyebrow_lower_middle.getDouble("x"), left_eyebrow_lower_middle.getDouble("y"),
                left_eyebrow_upper_middle.getDouble("x"), left_eyebrow_upper_middle.getDouble("y"));
        double eyebrowHeight3 = calculateLength(left_eyebrow_lower_right_quarter.getDouble("x"), left_eyebrow_lower_right_quarter.getDouble("y"),
                left_eyebrow_upper_right_quarter.getDouble("x"), left_eyebrow_upper_right_quarter.getDouble("y"));
        double eyebrowHeight =  Math.max(Math.max(eyebrowHeight1, eyebrowHeight2), eyebrowHeight3);

        JSONObject right_eyebrow_left_corner =  landmarkObject.getJSONObject("right_eyebrow_left_corner");
        JSONObject right_eyebrow_right_corner =  landmarkObject.getJSONObject("right_eyebrow_right_corner");
        // 眉毛上下
        double directionSize = (left_eyebrow_left_corner.getDouble("y") - left_eyebrow_right_corner.getDouble("y")) +
                (right_eyebrow_left_corner.getDouble("y") - right_eyebrow_right_corner.getDouble("y"));

        JSONObject contour_left1 =  landmarkObject.getJSONObject("contour_left1");
        JSONObject contour_right1 =  landmarkObject.getJSONObject("contour_right1");
        // 脸长度
        double faceWidth = calculateLength(contour_left1.getDouble("x"), contour_left1.getDouble("y"),
                contour_right1.getDouble("x"), contour_right1.getDouble("y"));

        double widthRatio = eyebrowWidth / faceWidth;
        double heightRatio = eyebrowHeight / eyebrowWidth;

        HashMap<String, Integer> map = new HashMap<>();
        map.put("111", 0); // 粗长上
        map.put("110", 1); // 粗长下
        map.put("101", 2); // 粗短上
        map.put("100", 3); // 粗短下
        map.put("011", 4); // 细长上
        map.put("010", 5); // 细长下
        map.put("001", 6); // 细短上
        map.put("000", 7); // 细短下

        if (widthRatio <= 0.32) // 短
        {
            widthRatio = 0;
        }
        else
        {
            widthRatio = 1;
        }
        if (heightRatio < 0.15) // 细
        {
            heightRatio = 0;
        }
        else
        {
            heightRatio = 1;
        }
        if (directionSize > 0) // 下
        {
            directionSize = 0;
        }
        else
        {
            directionSize = 1;
        }
        return map.get(String.valueOf((int)heightRatio) + String.valueOf((int)widthRatio) + String.valueOf((int)directionSize));
    }

    /**
     * 嘴巴分析
     * 小嘴不笑 0
     * 小嘴微笑 1
     * 小嘴大笑 2
     * 大嘴不笑 3
     * 大嘴微笑 4
     * 大嘴大笑 5
     * @param landmarkObject
     * @return
     */
    private int mouthAnalyse(JSONObject landmarkObject)
    {
        JSONObject mouth_left_corner = landmarkObject.getJSONObject("mouth_left_corner");
        JSONObject mouth_right_corner = landmarkObject.getJSONObject("mouth_right_corner");
        // 嘴巴长度
        double mouthWidth = calculateLength(mouth_left_corner.getDouble("x"), mouth_left_corner.getDouble("y"),
                mouth_right_corner.getDouble("x"), mouth_right_corner.getDouble("y"));

        JSONObject contour_left1 =  landmarkObject.getJSONObject("contour_left1");
        JSONObject contour_right1 =  landmarkObject.getJSONObject("contour_right1");
        // 脸长度
        double faceWidth = calculateLength(contour_left1.getDouble("x"), contour_left1.getDouble("y"),
                contour_right1.getDouble("x"), contour_right1.getDouble("y"));

        HashMap<String, Integer> map = new HashMap<>();
        map.put("00", 0); // 小嘴不笑
        map.put("01", 1); // 小嘴微笑
        map.put("02", 2); // 小嘴大笑
        map.put("10", 3); // 大嘴不笑
        map.put("11", 4); // 大嘴微笑
        map.put("12", 5); // 大嘴大笑

        double widthRatio = mouthWidth / faceWidth;
        if (widthRatio <= 0.36)
        {
            widthRatio = 0;
        }
        else
        {
            widthRatio = 1;
        }
        double smilingRatio = 0 ;
        if (smiling <= 20)
        {
            smilingRatio = 0;
        }
        else if (smiling > 20 && smiling <= 80)
        {
            smilingRatio = 1;
        }
        else
        {
            smilingRatio = 2;
        }
        return map.get(String.valueOf((int)widthRatio) + String.valueOf((int)smilingRatio));
    }

    /**
     * 轮廓分析
     * 瓜 0
     * 圆 1
     * 方 2
     * 胖 3
     * @param landmarkObject
     * @return
     */
    public int contourAnalyse(JSONObject landmarkObject)
    {
        JSONObject contour_left3 = landmarkObject.getJSONObject("contour_left3");
        JSONObject contour_right3 = landmarkObject.getJSONObject("contour_right3");
        JSONObject contour_left4 = landmarkObject.getJSONObject("contour_left4");
        JSONObject contour_right4 = landmarkObject.getJSONObject("contour_right4");
        // 第3个点宽度
        double contour3Width = calculateLength(contour_left3.getDouble("x"), contour_left3.getDouble("y"),
                contour_right3.getDouble("x"), contour_right3.getDouble("y"));
        // 第4个点宽度
        double contour4Width = calculateLength(contour_left4.getDouble("x"), contour_left4.getDouble("y"),
                contour_right4.getDouble("x"), contour_right4.getDouble("y"));
        if (contour4Width > contour3Width) // 胖脸
        {
            return 3;
        }

        JSONObject contour_left1 = landmarkObject.getJSONObject("contour_left1");
        JSONObject contour_right1 = landmarkObject.getJSONObject("contour_right1");
        JSONObject contour_left8 = landmarkObject.getJSONObject("contour_left8");
        JSONObject contour_right8 = landmarkObject.getJSONObject("contour_right8");
        // 第1个点宽度
        double contour1Width = calculateLength(contour_left1.getDouble("x"), contour_left1.getDouble("y"),
                contour_right1.getDouble("x"), contour_right1.getDouble("y"));
        // 第8个点宽度
        double contour8Width = calculateLength(contour_left8.getDouble("x"), contour_left8.getDouble("y"),
                contour_right8.getDouble("x"), contour_right8.getDouble("y"));

        double ratio = contour8Width / contour1Width;
        if (ratio <= 0.46)
        {
            return 0;
        }
        else if (ratio > 0.7)
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }

    /**
     * 算两点直直线距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double calculateLength(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
