package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;

/**
 * 人脸识别检测解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/03 17:57]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class DetectParser extends JsonParser {

    @Override
    public InfoResult doParse(String response) throws JSONException {

        return null;
    }

    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {

    }
}
