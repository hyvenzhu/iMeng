package com.android.imeng.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.ui.home.HomeActivity;

/**
 * 闪屏页面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-05-30 23:12]
 */
public class SplashActivity extends BasicActivity{
    @ViewInject(R.id.splash_view)
    View splashView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 延迟3秒, 跳转到首页
        splashView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
                // 淡出淡入效果
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 3000);
    }
}
