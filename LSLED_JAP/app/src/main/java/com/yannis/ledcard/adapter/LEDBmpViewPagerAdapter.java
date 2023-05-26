package com.yannis.ledcard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/23 17:12
 * @email : 923080261@qq.com
 * @description : TODO
 */
public class LEDBmpViewPagerAdapter extends PagerAdapter {
    private final Context context;
    private final ArrayList<GridView> itemViews;

    public LEDBmpViewPagerAdapter(Context context, ArrayList<GridView> itemViews) {
        this.context=context;
        this.itemViews=itemViews;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = itemViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return itemViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
