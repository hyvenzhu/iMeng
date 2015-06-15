package com.android.imeng.ui.decorate.cartoon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.imeng.R;
import com.android.imeng.framework.logic.InfoResult;
import com.android.imeng.framework.ui.BasicActivity;
import com.android.imeng.framework.ui.base.annotations.ViewInject;
import com.android.imeng.framework.ui.base.annotations.event.OnClick;
import com.android.imeng.logic.BitmapHelper;
import com.android.imeng.logic.NetLogic;
import com.android.imeng.logic.model.PictureInfo;
import com.android.imeng.ui.base.adapter.PictureAdpater;
import com.android.imeng.util.APKUtil;
import com.android.imeng.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择衣服界面
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/10 21:09]
 */
public class SelectClothesActivity extends BasicActivity implements AdapterView.OnItemClickListener{

    /**
     * 跳转
     * @param sex 性别 0：女 1：男
     * @param context 上下文
     */
    public static void actionStart(int sex, Context context)
    {
        Intent intent = new Intent(context, SelectClothesActivity.class);
        intent.putExtra("sex", sex);
        context.startActivity(intent);
    }

    @ViewInject(R.id.image_wall)
    private View imageWall; // 背景墙
    @ViewInject(R.id.image_view)
    private ImageView imageView; // 形象展示View
    @ViewInject(R.id.gridView)
    private GridView clothesGrid;
    private int sex; // 性别
    private NetLogic netLogic;
    // key 0：后面的头发  1：衣服   2：脸   3：前面的头发    4：眼睛    5：眉毛    6：嘴
    private Map<Integer, Drawable> drawableMap = new HashMap<Integer, Drawable>();

    private PictureAdpater clothesAdapter; // 衣服
    private int clothesIndex;

    private float scale = Constants.PIC_THUMBNAIL_WIDTH / (Constants.PIC_THUMBNAIL_HEIGHT * 1.0f); // GridView item宽高比
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clothes);
    }

    @Override
    protected void init() {
        super.init();
        setTitleBar(true, "换装", true);
        leftBtn.setText("返回");
        netLogic = new NetLogic(this);
        clothesGrid.setOnItemClickListener(this);

        sex = getIntent().getIntExtra("sex", 0);

        // 调整宽高
        adjustView();
        // 背景墙
        adjustWall();
        // 加载默认
        loadDefault();

        netLogic.clothes(sex, clothesIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterAll(netLogic);
    }

    /**
     * 调整宽高
     */
    private void adjustView()
    {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                // 1、调整背景墙, 如果宽度>500且高度<500，则高度调节为500
                int wallWidth = imageWall.getMeasuredWidth();
                int wallHeight = imageWall.getMeasuredHeight();
                if (wallWidth > Constants.IMAGE_WIDTH_HEIGHT && wallHeight < Constants.IMAGE_WIDTH_HEIGHT)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)imageWall.getLayoutParams();
                    layoutParams.height = Constants.IMAGE_WIDTH_HEIGHT;
                    layoutParams.weight= 0;
                    imageWall.setLayoutParams(layoutParams);
                    imageWall.requestLayout();
                }

                // 2、调整形象，取宽与高的较小值，如果较小值仍大于500，则设置宽高都为500，否则设置宽和高为较小值
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                int viewWidth = imageView.getMeasuredWidth();
                int viewHeight = imageView.getMeasuredHeight();

                int minSize = Math.min(viewWidth, viewHeight);
                if (minSize > Constants.IMAGE_WIDTH_HEIGHT)
                {
                    minSize = Constants.IMAGE_WIDTH_HEIGHT;
                }
                layoutParams.width = minSize;
                layoutParams.height = minSize;
                imageView.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * 性别调节背景墙
     */
    private void adjustWall()
    {
        if (sex == 0)
        {
            imageWall.setBackgroundColor(Color.parseColor("#CAE4F3"));
        }
        else
        {
            imageWall.setBackgroundColor(Color.parseColor("#f3bec4"));
        }
    }

    /**
     * 加载默认图片
     */
    private void loadDefault()
    {
        drawableMap.put(2, getResources().getDrawable(R.drawable.default_face));
        drawableMap.put(6, getResources().getDrawable(R.drawable.default_mouth));
        switch (sex)
        {
            case 0:
                drawableMap.put(3, getResources().getDrawable(R.drawable.default_boy_hair));
                drawableMap.put(4, getResources().getDrawable(R.drawable.default_boy_eye));
                drawableMap.put(5, getResources().getDrawable(R.drawable.default_boy_eyebrow));
                break;
            case 1:
                drawableMap.put(0, getResources().getDrawable(R.drawable.default_girl_hair_background));
                drawableMap.put(3, getResources().getDrawable(R.drawable.default_girl_hair_font));
                drawableMap.put(4, getResources().getDrawable(R.drawable.default_girl_eye));
                drawableMap.put(5, getResources().getDrawable(R.drawable.default_girl_eyebrow));
                break;
        }
        imageView.setImageDrawable(BitmapHelper.overlay(drawableMap));
    }

    @OnClick({R.id.title_right_btn, R.id.title_left_btn})
    public void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn:
                if (!drawableMap.containsKey(1))
                {
                    showToast("请选择衣服");
                    return;
                }
                // 选择装饰界面
                Intent intent = new Intent(this, DecorateActivity.class);
                intent.putExtra("sex", sex);
                intent.putExtra("clothesPath", clothesPath);
                startActivity(intent);
                finish();
                break;
        }
    }

    private String clothesPath; // 选择的衣服
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
         if (adapter == clothesAdapter) // 衣服
         {
             if (clothesAdapter.isMore(position)) // More
             {
                 netLogic.clothes(sex, clothesIndex * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);
             }
             else
             {
                 PictureInfo pictureInfo = clothesAdapter.getItem(position);
                 if (!clothesAdapter.hasDownload(position)) // 未下载
                 {
                     netLogic.download(pictureInfo);
                     clothesAdapter.notifyDataSetChanged();
                 }
                 else
                 {
                     clothesPath = pictureInfo.getOriginalLocalPath();
                     drawableMap.put(1, new BitmapDrawable(getResources(), clothesPath));
                     imageView.setImageDrawable(BitmapHelper.overlay(drawableMap));
                 }
             }
         }
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.downloadOriginal:
                if (clothesAdapter != null)
                {
                    clothesAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.clothes: // 衣服
                if (checkResponse(msg))
                {
                    InfoResult infoResult = (InfoResult)msg.obj;
                    List<PictureInfo> pictureInfos = (List<PictureInfo>)infoResult.getExtraObj();
                    clothesIndex++;

                    if (clothesAdapter == null)
                    {
                        int clothesCount = Integer.parseInt(infoResult.getOtherObj().toString());
                        clothesAdapter = new PictureAdpater(this, pictureInfos, R.layout.layout_item_picture, clothesCount);

                        final int viewWidth = clothesGrid.getWidth();
                        int numColumns = clothesGrid.getNumColumns();
                        int hoizontalSpacing = APKUtil.dip2px(this, 2);
                        int columnWidth = (int)((viewWidth - (numColumns - 1) * hoizontalSpacing) / (1.0f * numColumns));
                        int columnHeight = (int)(columnWidth * scale);
                        clothesAdapter.setSize(columnWidth, columnHeight);
                        clothesGrid.setAdapter(clothesAdapter);
                    }
                    else
                    {
                        clothesAdapter.getDataSource().addAll(pictureInfos);
                    }
                    clothesAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
