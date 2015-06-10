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
import com.android.imeng.ui.base.PictureAdpater;
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
        setContentView(R.layout.activity_decorate);
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
        imageView.setImageDrawable(overlay());
    }

    /**
     * 图层叠加
     * @return
     */
    private Drawable overlay()
    {
        Drawable hairBackgroundDrawable = drawableMap.get(0);
        Drawable clothesDrawable = drawableMap.get(1);
        Drawable faceDrawable = drawableMap.get(2);
        Drawable hairFontDrawable = drawableMap.get(3);
        Drawable eyeDrawable = drawableMap.get(4);
        Drawable eyeBrowDrawable = drawableMap.get(5);
        Drawable mouthDrawable = drawableMap.get(6);
        Drawable drawable = null;
        // 背后的头发
        if (hairBackgroundDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairBackgroundDrawable);
        }

        // 衣服
        if (drawable != null && clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, clothesDrawable);
        }
        else if (clothesDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(clothesDrawable);
        }

        // 脸
        if (drawable != null && faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, faceDrawable);
        }
        else if (faceDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(faceDrawable);
        }

        // 前面的头发
        if (drawable != null && hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, hairFontDrawable);
        }
        else if (hairFontDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(hairFontDrawable);
        }

        // 眼睛
        if (drawable != null && eyeDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, eyeDrawable);
        }
        else if (eyeDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(eyeDrawable);
        }

        // 眉毛
        if (drawable != null && eyeBrowDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, eyeBrowDrawable);
        }
        else if (eyeBrowDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(eyeBrowDrawable);
        }

        // 嘴巴
        if (drawable != null && mouthDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(drawable, mouthDrawable);
        }
        else if (mouthDrawable != null)
        {
            drawable = BitmapHelper.overlayDrawable(mouthDrawable);
        }
        return drawable;
    }

    @OnClick({R.id.hair_lay, R.id.face_btn, R.id.clothes_lay, R.id.clothes_btn, R.id.decoration_lay, R.id.decoration_btn,
              R.id.title_right_btn, R.id.title_left_btn})
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

                finish();
                break;
        }
    }

    private int choosedClothesCategroyId; // 选择的衣服类别Id
    private String choosedHairBackground; // 选择的背后头发
    private String choosedHairFont; // 选择的前面头发
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
                     choosedClothesCategroyId = pictureInfo.getCategoryId();
                     drawableMap.put(1, new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath()));
                     imageView.setImageDrawable(overlay());
                 }
             }
         }
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what)
        {
            case R.id.downloadFace:
                if (checkResponse(msg))
                {
                    PictureInfo pictureInfo = (PictureInfo)(((InfoResult)msg.obj).getExtraObj());
                    Drawable faceDrawable = new BitmapDrawable(getResources(), pictureInfo.getOriginalLocalPath());
                    drawableMap.put(2, faceDrawable);
                    imageView.setImageDrawable(overlay());
                }
                break;
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
                        int hoizontalSpacing = clothesGrid.getHorizontalSpacing();
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
