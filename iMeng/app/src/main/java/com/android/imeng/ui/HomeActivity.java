package com.android.imeng.ui;

import android.os.Bundle;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;

/**
 * 首页
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-05-30 23:58]
 */
public class HomeActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
