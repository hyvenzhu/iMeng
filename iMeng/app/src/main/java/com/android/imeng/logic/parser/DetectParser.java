package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.model.FaceInfo;

/**
 * 人脸识别检测解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/03 17:57]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class DetectParser extends JsonParser {

    @Override
    public InfoResult doParse(String response) throws JSONException {
        InfoResult infoResult = new InfoResult.Builder().success(true).build();
        FaceInfo faceInfo = new FaceInfo();
        // 根据face++返回的人脸(Face)相应的面部轮廓，五官等关键点的位置计算出脸部信息

        infoResult.setExtraObj(faceInfo);
        return infoResult;
    }

    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {

    }
}
