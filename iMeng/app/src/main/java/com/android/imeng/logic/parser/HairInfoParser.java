package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.HairInfo;
import com.android.imeng.logic.PictureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 头发信息解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/05 15:19]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class HairInfoParser extends JsonParser {
    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess())
        {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            if (dataObj != null)
            {
                JSONArray datasArray = dataObj.getJSONArray("datas");
                List<HairInfo> hairInfos = new ArrayList<HairInfo>();
                for(int i = 0; i < datasArray.size(); i++)
                {
                    HairInfo hairInfo = new HairInfo();

                    JSONObject object = datasArray.getJSONObject(i);
                    JSONArray smallArray = object.getJSONArray("0");
                    JSONArray bigArray = object.getJSONArray("1");

                    // 小图只有一张
                    JSONObject smallObj = smallArray.getJSONObject(0);
                    PictureInfo smallPictureInfo = new PictureInfo();
                    smallPictureInfo.setThumbnailUrl(smallObj.getString("url"));

                    hairInfo.setThumbnailInfo(smallPictureInfo);

                    List<PictureInfo> originalInfos = new ArrayList<PictureInfo>();
                    for(int j = 0; j < bigArray.size(); j++)
                    {
                        JSONObject bigObj = bigArray.getJSONObject(j);
                        PictureInfo bigPictureInfo = new PictureInfo();
                        bigPictureInfo.setOriginalUrl(bigObj.getString("url"));
                        originalInfos.add(bigPictureInfo);
                    }

                    hairInfo.setOriginalInfos(originalInfos);

                    hairInfos.add(hairInfo);
                }
                infoResult.setExtraObj(hairInfos);

                int total = dataObj.getIntValue("total");
                infoResult.setOtherObj(total);
            }
        }
    }
}
