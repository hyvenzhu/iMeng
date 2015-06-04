package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.FaceInfo;

/**
 * 单个脸解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/04 16:27]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class FaceInfoParser extends JsonParser {
    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess())
        {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            FaceInfo faceInfo = JSON.parseObject(dataObj.toJSONString(), FaceInfo.class);
            infoResult.setExtraObj(faceInfo);
        }
    }
}
