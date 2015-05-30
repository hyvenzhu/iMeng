package com.android.imeng.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;

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

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 跳转到首页
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashView.startAnimation(alphaAnimation);
    }
}
