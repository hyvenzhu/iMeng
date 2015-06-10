package com.android.imeng.logic;

import android.text.TextUtils;

import com.android.imeng.AppDroid;
import com.android.imeng.framework.asyncquery.Task;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.logic.model.ClothesAndExpression;
import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 批量下载衣服和表情图片
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-06 23:31]
 */
public class ClothesAndExpressionDownloadTask extends Task {
    private List<ClothesAndExpression> clothesAndExpressions;
    public ClothesAndExpressionDownloadTask(int taskId, Object subscriber, List<ClothesAndExpression> clothesAndExpressions) {
        super(taskId, subscriber);
        this.clothesAndExpressions = clothesAndExpressions;
    }

    @Override
    public Object doInBackground() {
        InfoResult infoResult = null;
        for(ClothesAndExpression clothesAndExpression : clothesAndExpressions)
        {
            InputStream is = null;
            try {
                PictureInfo clothesInfo = clothesAndExpression.getClothesInfo();
                PictureInfo expressionInfo= clothesAndExpression.getExpressionInfo();
                PictureInfo[] pictureInfos = new PictureInfo[]{clothesInfo, expressionInfo};
                for (int i = 0; i < pictureInfos.length; i++)
                {
                    PictureInfo pictureInfo = pictureInfos[i];
                    if (TextUtils.isEmpty(pictureInfo.getOriginalLocalPath()) && !TextUtils.isEmpty(pictureInfo.getOriginalUrl()))
                    {
                        HttpURLConnection urlConnection = (HttpURLConnection)new URL(pictureInfo.getOriginalUrl()).openConnection();
                        is = urlConnection.getInputStream();

                        // 保存路径
                        String localPath = APKUtil.stringToMD5(pictureInfo.getOriginalUrl());
                        File dir = APKUtil.getDiskCacheDir(AppDroid.getInstance().getApplicationContext(), Constants.DOWNLOAD_DIR);
                        File file = new File(dir, localPath);

                        // 保存到文件
                        APKUtil.save2File(is, file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 下载失败则停止
                infoResult = new InfoResult.Builder().success(false).build();
                return infoResult;
            } finally {
                if (is != null)
                {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        infoResult = new InfoResult.Builder().success(true).build();
        return infoResult;
    }
}
