package com.android.imeng.logic;

import com.android.imeng.AppDroid;
import com.android.imeng.R;
import com.android.imeng.framework.asyncquery.TaskExecutor;
import com.android.imeng.framework.logic.BaseLogic;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.logic.parser.InputStreamParser;
import com.android.imeng.framework.volley.InfoResultMultiPartRequest;
import com.android.imeng.framework.volley.InfoResultRequest;
import com.android.imeng.logic.parser.DetectParser;
import com.android.imeng.logic.parser.PictureInfoParser;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;
import com.android.volley.Request;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求相关
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-01 21:42]
 */
public class NetLogic extends BaseLogic {

    public NetLogic(Object subscriber) {
        super(subscriber);
    }

    /**
     * 下载图片
     * @param imageInfo
     */
    public void downloadImage(final PictureInfo imageInfo)
    {
        InfoResultRequest request = new InfoResultRequest(R.id.download, imageInfo.getOriginalUrl(), new InputStreamParser()
        {
            public InfoResult doParse(final InputStream response)
            {
                InfoResult infoResult = null;
                try
                {
                    // 保存路径
                    String localPath = APKUtil.stringToMD5(imageInfo.getOriginalUrl());
                    File dir = APKUtil.getDiskCacheDir(AppDroid.getInstance().getApplicationContext(), Constants.DOWNLOAD_DIR);
                    File file = new File(dir, localPath);

                    // 保存到文件
                    APKUtil.save2File(response, file.getAbsolutePath());

                    // 下载成功
                    infoResult = new InfoResult.Builder().success(true).extraObj(imageInfo).build();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    // 下载失败
                    infoResult = new InfoResult.Builder().success(false).extraObj(imageInfo).build();
                }
                return infoResult;
            }
        }, this);
        request.setNeedStream(true);
        sendRequest(request, R.id.download);
    }

    /**
     * 查询衣服列表
     * @param sex 性别，0：男  1：女
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void clothes(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.clothes, Constants.CLOTHES_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.clothes);
    }

    /**
     * 查询头发列表
     * @param sex 性别，0：男  1：女
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void hairs(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.hairs, Constants.HAIRS_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.hairs);
    }

    /**
     * 查询装饰列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void decorations(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.decorations, Constants.DECORATIONS_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.decorations);
    }

    /**
     * 查询眼睛列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void eyes(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.eyes, Constants.EYES_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.eyes);
    }

    /**
     * 查询脸型列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void faceShapes(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.faceShapes, Constants.FACESHAPES_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.faceShapes);
    }

    /**
     * 查询眉毛列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void eyebrows(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.eyebrows, Constants.EYEBROWS_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.eyebrows);
    }

    /**
     * 查询嘴列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param index 分页下标，不传，默认为0
     * @param length 返回数据长度，不传，默认为10
     */
    public void mouths(int sex, int index, int length)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("index", index);
        params.put("length", length);
        InfoResultRequest request = new InfoResultRequest(R.id.mouths, Constants.MOUTHS_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.mouths);
    }

    /**
     * 人脸识别
     * @param photoPtah
     */
    public void faceDetect(String photoPtah)
    {
        TaskExecutor.getInstance().execute(new DetectTask(R.id.detect, this, photoPtah));
    }

    /**
     * 查询单个脸信息
     * @param sex 性别，0：男  1：女
     * @param eye 眼睛
     * @param mouth 嘴
     * @param shape 脸型
     * @param eyebrows 眉毛
     */
    public void face(int sex, float eye, float mouth, float shape, float eyebrows)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sex", sex);
        params.put("eye", eye);
        params.put("mouth", mouth);
        params.put("shape", shape);
        params.put("eyebrows", eyebrows);
        InfoResultRequest request = new InfoResultRequest(R.id.face, Constants.FACE_URL, params,
                new PictureInfoParser(), this);
        sendRequest(request, R.id.face);
    }

    /**
     * 查询表情和衣服列表
     * @param sex 性别，0：男  1：女
     * @param categoryId 衣服类别
     * @param glasses 眼镜
     */
    public void clothesAndExpression(int sex, int categoryId, int glasses)
    {

    }

    /**
     * 根据性别、类别ID查询一套衣服的所有大图列表
     * @param sex 性别，0：男  1：女  2：男女通用
     * @param categoryId 衣服类别
     */
    public void bigClothes(int sex, int categoryId)
    {

    }
}
