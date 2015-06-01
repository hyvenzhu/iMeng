package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.PictureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片解析器（适用于小图与大图组合的类型）
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-01 22:39]
 */
public class PictureInfoParser extends JsonParser {
    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess())
        {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            if (dataObj != null)
            {
                JSONArray datasArray = dataObj.getJSONArray("datas");
                List<PictureInfo> pictureInfos = new ArrayList<PictureInfo>();
                for(int i = 0; i < datasArray.size(); i++)
                {
                    JSONObject object = datasArray.getJSONObject(i);
                    JSONObject smallObj = object.getJSONObject("0");
                    JSONObject bigObj = object.getJSONObject("1");

                    PictureInfo pictureInfo = new PictureInfo();
                    pictureInfo.setThumbnailUrl(smallObj.getString("url"));
                    pictureInfo.setOriginalUrl(bigObj.getString("url"));
                    pictureInfos.add(pictureInfo);
                }
                infoResult.setExtraObj(pictureInfos);

                int total = dataObj.getIntValue("total");
                infoResult.setOtherObj(total);
            }
        }
    }
}
