package com.android.imeng.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * 人脸识别界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-02 20:37]
 */
public class FaceDetectiveActivity extends BasicActivity {
    @ViewInject(value = R.id.photo_view)
    private SimpleDraweeView photoView;
    @ViewInject(value = R.id.detective_view)
    private ImageView detectiveView;
    @ViewInject(value = R.id.male_btn)
    private Button maleBtn;
    @ViewInject(value = R.id.female_btn)
    private Button femaleBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detective);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "选择性别", false);
        leftBtn.setText("重拍");

        Uri uri = (Uri)getIntent().getParcelableExtra("photoUri");
        // 设置自动旋转
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).build();
        photoView.setController(controller);
    }

    @OnClick({R.id.title_left_btn, R.id.next_btn, R.id.male_btn, R.id.female_btn})
    public void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.male_btn:
                maleBtn.setEnabled(false);
                femaleBtn.setEnabled(true);
                break;
            case R.id.female_btn:
                maleBtn.setEnabled(true);
                femaleBtn.setEnabled(false);
                break;
            case R.id.next_btn:
                detectiveView.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable)detectiveView.getDrawable();
                animationDrawable.start();
                break;
        }
    }
}
