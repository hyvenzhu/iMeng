package com.android.imeng.logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.imeng.framework.asyncquery.Task;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 保存到表情相册
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-07 20:08]
 */
public class Save2GalleryTask extends Task
{
    private List<ImageInfo> imageInfos;
    private String destDir;
    public Save2GalleryTask(int taskId, Object subscriber, List<ImageInfo> imageInfos, String destDir) {
        super(taskId, subscriber);
        this.imageInfos = imageInfos;
        this.destDir = destDir;
    }

    @Override
    public Object doInBackground() {
        int minIndex = imageInfos.get(0).getIndex();
        int coverPosition = 0;
        for(int i = 0; i < imageInfos.size(); i++)
        {
            ImageInfo imageInfo = imageInfos.get(i);
            String filePtah = imageInfo.getLocalPath();
            File sFile = new File(filePtah);
            BitmapHelper.copyFile(sFile, new File(destDir, sFile.getName()));

            if (imageInfo.getIndex() < minIndex)
            {
                minIndex = imageInfo.getIndex();
                coverPosition = i;
            }
        }

        ImageInfo imageInfo = imageInfos.get(coverPosition);
        Bitmap sBitmap = BitmapFactory.decodeFile(imageInfo.getLocalPath());
        Bitmap dBitmap = Bitmap.createBitmap(sBitmap, Constants.IMAGE_WIDTH_HEIGHT - Constants.IMAGE_WIDTH_HEIGHT_WITHOUT_LEFT_EREA, 0,
                Constants.IMAGE_WIDTH_HEIGHT_WITHOUT_LEFT_EREA, Constants.IMAGE_WIDTH_HEIGHT);
        try {
            BitmapHelper.bitmap2File(dBitmap, new File(destDir, Constants.GALLERY_COVER).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sBitmap != null && !sBitmap.isRecycled())
            {
                sBitmap.recycle();
            }
            if (dBitmap != null && !dBitmap.isRecycled())
            {
                dBitmap.recycle();
            }
        }

        return new InfoResult.Builder().success(true).build();
    }
}
