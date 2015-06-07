package com.android.imeng.ui;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 图片展示适配器
 * @author hiphonezhu@gmail.com
 * @version [iMeng, 2015/06/05 11:48]
 * @copyright Copyright 2010 RD information technology Co.,ltd.. All Rights Reserved.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private GridView[] gridViews;

    public ViewPagerAdapter(GridView... gridViews)
    {
        this.gridViews = gridViews;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GridView gridView = gridViews[position];
        container.addView(gridView);
        return gridView;
    }

    public GridView[] getViews()
    {
        return gridViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(gridViews[position]);
    }

    @Override
    public int getCount() {
        return gridViews.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
