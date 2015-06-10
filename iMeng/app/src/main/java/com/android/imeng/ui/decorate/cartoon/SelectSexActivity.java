package com.android.imeng.ui.decorate.cartoon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
    @ViewInject(R.id.male_zoom)
    private ImageView maleZoomView;
    @ViewInject(R.id.female_zoom)
    private ImageView femaleZoomView;
    @ViewInject(R.id.male_btn)
    private Button maleBtn;
    @ViewInject(R.id.female_btn)
    private Button femaleBtn;

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
                maleZoomView.setVisibility(View.VISIBLE);
                ObjectAnimator maleZoomXAnimator = ObjectAnimator.ofFloat(maleZoomView, "scaleX", 0, 5);
                ObjectAnimator maleZoomYAnimator = ObjectAnimator.ofFloat(maleZoomView, "scaleY", 0, 5);
                ObjectAnimator maleAlphaYAnimator = ObjectAnimator.ofFloat(maleZoomView, "alpha", 1, 0);
                AnimatorSet maleZoomAnimSet = new AnimatorSet();
                maleZoomAnimSet.play(maleZoomXAnimator).with(maleZoomYAnimator).with(maleAlphaYAnimator);
                maleZoomAnimSet.setDuration(1000);
                maleZoomAnimSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        maleBtn.setEnabled(false);
                        femaleBtn.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finish();
                        // 跳转到自定义表情界面
                        SelectClothesActivity.actionStart(0, SelectSexActivity.this);
                    }
                });
                maleZoomAnimSet.start();
                break;
            case R.id.female_btn: // 女
                femaleZoomView.setVisibility(View.VISIBLE);
                ObjectAnimator femaleZoomXAnimator = ObjectAnimator.ofFloat(femaleZoomView, "scaleX", 0, 5);
                ObjectAnimator femaleZoomYAnimator = ObjectAnimator.ofFloat(femaleZoomView, "scaleY", 0, 5);
                ObjectAnimator femaleAlphaYAnimator = ObjectAnimator.ofFloat(femaleZoomView, "alpha", 1, 0);
                AnimatorSet femaleZoomAnimSet = new AnimatorSet();
                femaleZoomAnimSet.play(femaleZoomXAnimator).with(femaleZoomYAnimator).with(femaleAlphaYAnimator);
                femaleZoomAnimSet.setDuration(1000);
                femaleZoomAnimSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        maleBtn.setEnabled(false);
                        femaleBtn.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finish();
                        // 跳转到自定义表情界面
                        SelectClothesActivity.actionStart(1, SelectSexActivity.this);
                    }
                });
                femaleZoomAnimSet.start();
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
        maleAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                maleBtn.setEnabled(true);
                femaleBtn.setEnabled(true);
            }
        });
        maleAnimSet.start();

        // 移动缩放布局
        ObjectAnimator maleZoomXAnimator = ObjectAnimator.ofFloat(maleZoomView, "x", maleDestX);
        ObjectAnimator maleZoomYAnimator = ObjectAnimator.ofFloat(maleZoomView, "y", maleDestY);
        AnimatorSet maleZoomAnimSet = new AnimatorSet();
        maleZoomAnimSet.play(maleZoomXAnimator).with(maleZoomYAnimator);
        maleZoomAnimSet.setDuration(400);
        maleZoomAnimSet.start();

        ObjectAnimator femaleXAnimator = ObjectAnimator.ofFloat(femaleView, "x", femaleDestX);
        ObjectAnimator femaleYAnimator = ObjectAnimator.ofFloat(femaleView, "y", femaleDestY);
        ObjectAnimator femaleAlphaAnimator = ObjectAnimator.ofFloat(femaleView, "alpha", 0.0f, 1.0f);
        AnimatorSet femaleAnimSet = new AnimatorSet();
        femaleAnimSet.play(femaleXAnimator).with(femaleYAnimator).with(femaleAlphaAnimator);
        femaleAnimSet.setDuration(400);
        femaleAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                maleBtn.setEnabled(true);
                femaleBtn.setEnabled(true);
            }
        });
        femaleAnimSet.start();

        // 移动缩放布局
        ObjectAnimator femaleZoomXAnimator = ObjectAnimator.ofFloat(femaleZoomView, "x", femaleDestX);
        ObjectAnimator femaleZoomYAnimator = ObjectAnimator.ofFloat(femaleZoomView, "y", femaleDestY);
        AnimatorSet femaleZoomAnimSet = new AnimatorSet();
        femaleZoomAnimSet.play(femaleZoomXAnimator).with(femaleZoomYAnimator);
        femaleZoomAnimSet.setDuration(400);
        femaleZoomAnimSet.start();
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
        maleAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                maleBtn.setEnabled(false);
                femaleBtn.setEnabled(false);
            }
        });
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

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                maleBtn.setEnabled(false);
                femaleBtn.setEnabled(false);
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
