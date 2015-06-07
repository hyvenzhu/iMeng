package com.android.imeng.ui;

import android.os.Bundle;
import android.view.View;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;

/**
 * 表情相册
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-07 14:01]
 */
public class ImageGalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "表情相册", true);
        leftBtn.setText("返回");
        rightBtn.setText("编辑");
    }

    @OnClick({R.id.title_left_btn, R.id.title_right_btn})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn: // 编辑

                break;
        }
    }
}
