package com.android.imeng.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.util.APKUtil;

/**
 * 性别选择界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-05-31 21:40]
 */
public class SelectSexActivity extends BasicActivity{
    @ViewInject(R.id.male_view)
    private View maleView;
    @ViewInject(R.id.female_view)
    private View femaleView;

    private int screenWidth;
    private int screenHeight;
    private final int endMarginBetween = 40; // 按钮之间的间距
    private float maleOriginalX;
    private float maleOriginalY;
    private float maleDestX;
    private float maleDestY;
    private float femaleDestX;
    private float femaleDestY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sex);

        // 获得屏蔽宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        maleView.post(new Runnable() {
            @Override
            public void run() {
                expandSelector();
            }
        });
    }

    @OnClick({R.id.male_btn, R.id.female_btn, R.id.view_lay})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.male_btn: // 男

                break;
            case R.id.female_btn: // 女

                break;
            case R.id.view_lay: // 整个布局
                collapseSelector();
                break;
        }
    }

    /**
     * 展开按钮
     */
    private void expandSelector()
    {
        int viewWidth = maleView.getWidth();
        int viewHeight = maleView.getHeight();

        // 间距
        int pixBetween = APKUtil.dip2px(getApplicationContext(), endMarginBetween);
        maleOriginalX = maleView.getX();
        maleOriginalY = maleView.getY();

        // 男最终坐标
        maleDestX = screenWidth / 2f - pixBetween / 2f - viewWidth;
        maleDestY = screenHeight / 2f - viewHeight;

        // 女最终坐标
        femaleDestX = screenWidth / 2f + pixBetween / 2f;
        femaleDestY = screenHeight / 2f - viewHeight;

        ObjectAnimator maleXAnimator = ObjectAnimator.ofFloat(maleView, "x", maleDestX);
        ObjectAnimator maleYAnimator = ObjectAnimator.ofFloat(maleView, "y", maleDestY);
        ObjectAnimator maleAlphaAnimator = ObjectAnimator.ofFloat(maleView, "alpha", 0.0f, 1.0f);
        AnimatorSet maleAnimSet = new AnimatorSet();
        maleAnimSet.play(maleXAnimator).with(maleYAnimator).with(maleAlphaAnimator);
        maleAnimSet.setDuration(400);
        maleAnimSet.start();

        ObjectAnimator femaleXAnimator = ObjectAnimator.ofFloat(femaleView, "x", femaleDestX);
        ObjectAnimator femaleYAnimator = ObjectAnimator.ofFloat(femaleView, "y", femaleDestY);
        ObjectAnimator femaleAlphaAnimator = ObjectAnimator.ofFloat(femaleView, "alpha", 0.0f, 1.0f);
        AnimatorSet femaleAnimSet = new AnimatorSet();
        femaleAnimSet.play(femaleXAnimator).with(femaleYAnimator).with(femaleAlphaAnimator);
        femaleAnimSet.setDuration(400);
        femaleAnimSet.start();
    }

    /**
     * 合并按钮
     */
    private void collapseSelector()
    {
        ObjectAnimator maleXAnimator = ObjectAnimator.ofFloat(maleView, "x", maleOriginalX);
        ObjectAnimator maleYAnimator = ObjectAnimator.ofFloat(maleView, "y", maleOriginalY);
        ObjectAnimator maleAlphaAnimator = ObjectAnimator.ofFloat(maleView, "alpha", 1.0f, 0.0f);
        AnimatorSet maleAnimSet = new AnimatorSet();
        maleAnimSet.play(maleXAnimator).with(maleYAnimator).with(maleAlphaAnimator);
        maleAnimSet.setDuration(400);
        maleAnimSet.start();

        ObjectAnimator femaleXAnimator = ObjectAnimator.ofFloat(femaleView, "x",  maleOriginalX);
        ObjectAnimator femaleYAnimator = ObjectAnimator.ofFloat(femaleView, "y", maleOriginalY);
        ObjectAnimator femaleAlphaAnimator = ObjectAnimator.ofFloat(femaleView, "alpha", 1.0f, 0.0f);
        AnimatorSet femaleAnimSet = new AnimatorSet();
        femaleAnimSet.play(femaleXAnimator).with(femaleYAnimator).with(femaleAlphaAnimator);
        femaleAnimSet.setDuration(400);
        femaleAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
        femaleAnimSet.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_int, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        collapseSelector();
    }
}
