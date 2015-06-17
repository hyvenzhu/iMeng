package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.model.PictureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 衣服大图解析
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-16 22:12]
 */
public class BigClothesParser extends JsonParser {
    private int sex;
    public BigClothesParser(int sex)
    {
        this.sex = sex;
    }

    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess())
        {
            JSONArray dataArray = jsonObject.getJSONArray("data");
            if (dataArray != null)
            {
                List<PictureInfo> pictureInfos = new ArrayList<PictureInfo>();
                for(int i = 0; i < dataArray.size(); i++)
                {
                    PictureInfo pictureInfo = new PictureInfo();
                    JSONObject clothesObj = dataArray.getJSONObject(i);
                    pictureInfo.setOriginalUrl(clothesObj.getString("url"));
                    String actionIdentifier = null;
                    switch (sex)
                    {
                        case 0:
                            actionIdentifier = "action_boy_";
                            break;
                        case 1:
                            actionIdentifier = "action_girl_";
                            break;
                    }
                    actionIdentifier += (i + 1);
                    pictureInfo.setThumbnailUrl(actionIdentifier);
                    pictureInfos.add(pictureInfo);
                }
                infoResult.setExtraObj(pictureInfos);
            }
        }
    }
}
