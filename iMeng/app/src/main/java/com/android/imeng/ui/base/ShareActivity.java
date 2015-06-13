package com.android.imeng.ui.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.imeng.R;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.ShareHelper;
import com.android.imeng.ui.base.view.ShareCardView;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-11 22:19]
 */
public class ShareActivity extends BasicActivity {
    @ViewInject(R.id.card_view)
    private ShareCardView cardView; // 背景
    @ViewInject(R.id.share_img)
    private ImageView shareImg; // 形象

    public static void actionStart(Context activity, String path, int sex)
    {
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("sex", sex);
        activity.startActivity(intent);
    }

    private String path; // 图片路径
    private int sex; // 性别
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }

    @Override
    protected void init() {
        super.init();
        path = getIntent().getStringExtra("path");
        sex = getIntent().getIntExtra("sex", 0);
        shareImg.setImageURI(Uri.fromFile(new File(path)));

        // 调整卡片高度
        cardView.post(new Runnable() {
            @Override
            public void run() {
                int width = cardView.getMeasuredWidth();
                int height = (int)(width / 1.5f);
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                params.height = height;
                cardView.setLayoutParams(params);
            }
        });

        switch (sex)
        {
            case 0:
                cardView.setColor(getResources().getColor(R.color.male_color));
                break;
            case 1:
                cardView.setColor(getResources().getColor(R.color.female_color));
                break;
        }
    }

    @OnClick({R.id.btn_favorite, R.id.btn_qq, R.id.btn_wechatmoments, R.id.btn_wechat,
    R.id.btn_close, R.id.btn_download})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_favorite: // 收藏

                break;
            case R.id.btn_qq: // qq分享
                ShareHelper.share(QQ.NAME, path, new ShareActionListener());
                break;
            case R.id.btn_wechatmoments: // 微信朋友圈分享
                ShareHelper.share(WechatMoments.NAME, path, new ShareActionListener());
                break;
            case R.id.btn_wechat: // 微信分享
                ShareHelper.share(Wechat.NAME, path, new ShareActionListener());
                break;
            case R.id.btn_close: // 关闭
                finish();
                break;
            case R.id.btn_download: // 下载

                break;
        }
    }

    private class ShareActionListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
            showToast("分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            showToast("分享失败：" + i);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            showToast("已取消分享");
        }
    }
}
