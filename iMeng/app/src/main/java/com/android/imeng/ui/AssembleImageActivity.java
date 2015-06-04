package com.android.imeng.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;

/**
 * 组装个人形象界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/04 17:09]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class AssembleImageActivity extends BasicActivity{

    /**
     * 跳转
     * @param faceUrl 脸地址
     * @param sex 性别 0：女 1：男
     * @param context 上下文
     */
    public static void actionStart(String faceUrl, int sex, Context context)
    {
        Intent intent = new Intent(context, AssembleImageActivity.class);
        intent.putExtra("faceUrl", faceUrl);
        intent.putExtra("sex", sex);
        context.startActivity(intent);
    }

    private String faceUrl; // 脸地址
    private int sex; // 性别
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemble_image);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "捏表情", true);
        leftBtn.setText("返回");
        rightBtn.setVisibility(View.VISIBLE);

        faceUrl = getIntent().getStringExtra("faceUrl");
        sex = getIntent().getIntExtra("sex", 0);
    }
}
