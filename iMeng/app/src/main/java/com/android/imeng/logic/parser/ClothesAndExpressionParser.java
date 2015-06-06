package com.android.imeng.logic.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.JsonParser;
import com.android.imeng.logic.ClothesAndExpression;
import com.android.imeng.logic.PictureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 衣服和表情
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 22:44]
 */
public class ClothesAndExpressionParser extends JsonParser {
    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess())
        {
            JSONArray dataArray = jsonObject.getJSONArray("data");
            if (dataArray != null)
            {
                List<ClothesAndExpression> clothesAndExpressions = new ArrayList<ClothesAndExpression>();

                for(int i = 0; i < dataArray.size(); i++)
                {
                    JSONObject dataObj = dataArray.getJSONObject(i);

                    JSONObject expressionObj = dataObj.getJSONObject("expressionInfo");
                    JSONObject clothesObj = dataObj.getJSONObject("clothesInfo");

                    ClothesAndExpression clothesAndExpression = new ClothesAndExpression();
                    if (expressionObj != null)
                    {
                        PictureInfo expressionInfo = new PictureInfo();
                        expressionInfo.setOriginalUrl(expressionObj.getString("url"));
                        clothesAndExpression.setExpressionInfo(expressionInfo);
                    }

                    if (clothesObj != null)
                    {
                        PictureInfo clothesInfo = new PictureInfo();
                        clothesInfo.setOriginalUrl(clothesObj.getString("url"));
                        clothesAndExpression.setClothesInfo(clothesInfo);
                    }
                    clothesAndExpressions.add(clothesAndExpression);
                }

                infoResult.setExtraObj(clothesAndExpressions);
            }
        }
    }
}
