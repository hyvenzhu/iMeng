package com.android.imeng.ui.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.imeng.R;
import com.android.imeng.util.APKUtil;

/**
 * 分享卡片布局
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015-06-13 10:03]
 */
public class ShareCardView extends LinearLayout{
    private Paint mPaint; // 背景色画笔

    private int r = 2; // 圆角矩形半径
    private int arc = 3; // 1/4扇形半径

    private int direction; // 方向 0：下   1：上
    private int color; // 颜色

    public ShareCardView(Context context) {
        this(context, null);
    }

    public ShareCardView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ShareCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardView);
        direction = a.getInt(R.styleable.CardView_direction, 0);
        color = a.getColor(R.styleable.CardView_color, R.color.male_color);

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        r = APKUtil.dip2px(context, r);
        arc = APKUtil.dip2px(context, arc);
    }

    public void setColor(int color)
    {
        this.color = color;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        switch (direction)
        {
            case 0:
                mPaint.setColor(color);
                // 外轮廓
                mPaint.setXfermode(null);
                int sc = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
                canvas.drawRoundRect(new RectF(0, 0, width, height), r, r, mPaint);
                // 再画一个长方形与1/4圆交集
                canvas.drawRect(new RectF(0, r, width, height), mPaint);
                // 1/4 圆
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR)); // 去除交集部分
                canvas.drawCircle(0, height, arc, mPaint);
                canvas.drawCircle(width, height, arc, mPaint);
                canvas.restoreToCount(sc);
                break;
            case 1:
                mPaint.setColor(Color.WHITE);
                // 外轮廓
                int sc2 = canvas.saveLayer(0 ,0, width, height, null, Canvas.ALL_SAVE_FLAG);
                mPaint.setXfermode(null);
                canvas.drawRoundRect(new RectF(0, 0, width, height), r, r, mPaint);
                // 再画一个长方形与1/4圆交集
                canvas.drawRect(new RectF(0, 0, width, height - r), mPaint);
                // 1/4 圆
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR)); // 去除交集部分
                canvas.drawCircle(0, 0, arc, mPaint);
                canvas.drawCircle(width, 0, arc, mPaint);
                canvas.restoreToCount(sc2);
                break;
        }
        super.dispatchDraw(canvas);
    }
}
