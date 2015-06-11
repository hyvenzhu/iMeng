package com.android.imeng.ui.base;

import android.os.Bundle;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;

/**
 * 分享
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-11 22:19]
 */
public class ShareActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }
}
