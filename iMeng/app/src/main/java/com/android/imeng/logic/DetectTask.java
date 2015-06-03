package com.android.imeng.logic;

import com.android.imeng.framework.asyncquery.Task;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.logic.parser.DetectParser;
import com.android.imeng.util.Constants;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

/**
 * 照片识别
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-03 21:37]
 */
public class DetectTask extends Task {
    private String photoPath;
    public DetectTask(int taskId, Object subscriber, String photoPath) {
        super(taskId, subscriber);
        this.photoPath = photoPath;
    }

    @Override
    public Object doInBackground() {
        HttpRequests httpRequests = new HttpRequests(Constants.API_KEY, Constants.API_SECRET, true, false);
        try
        {
            // 1、找出最大脸的取出face_id
            JSONObject result = httpRequests.detectionDetect(new PostParameters()
                    .setImg(new File(photoPath)).setMode("oneface"));
            JSONArray faceArray = result.optJSONArray("face");
            if (faceArray != null && faceArray.length() > 0)
            {
                // 取出face_id
                String faceId = faceArray.getJSONObject(0).optString("face_id");

                // 2、检测给定人脸(Face)相应的面部轮廓，五官等关键点的位置
                result = httpRequests.detectionLandmark(new PostParameters()
                        .setFaceId(faceId).setType("83p"));
                faceArray = result.optJSONArray("result");
                if (faceArray != null && faceArray.length() > 0)
                {
                    JSONObject faceObject = faceArray.getJSONObject(0);
                    JSONObject landmarkObject = faceObject.optJSONObject("landmark");
                    return new DetectParser().doParse(landmarkObject.toString());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new InfoResult.Builder().success(false).errorCode("500").desc("服务器内部错误, 请稍后尝试").build();
        }
        return new InfoResult.Builder().success(false).errorCode("404").desc("未检测到人脸信息").build();
    }
}
