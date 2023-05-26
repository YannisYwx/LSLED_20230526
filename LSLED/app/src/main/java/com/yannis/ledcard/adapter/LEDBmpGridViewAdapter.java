package com.yannis.ledcard.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.bean.LEDBmp;

import java.util.List;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/23 17:06
 * @email : 923080261@qq.com
 * @description : TODO
 */
public class LEDBmpGridViewAdapter extends BaseAdapter {
    private final Context context;
    private LayoutInflater mInflater;
    private final List<LEDBmp> mBmpList;
    private final boolean isEditMode;

    public LEDBmpGridViewAdapter(Context context, List<LEDBmp> ledBmps, boolean isEditMode) {
        this.context = context;
        this.mBmpList = ledBmps;
        this.mInflater = LayoutInflater.from(this.context);
        this.isEditMode = isEditMode;
    }

    @Override
    public int getCount() {
        return mBmpList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBmpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        LEDBmp ledBmp = mBmpList.get(i);
        if (view == null) {
            view = mInflater.inflate(R.layout.item_led_bmp, null);
            viewHolder = new ViewHolder();
            viewHolder.ledBmp = (ImageView) view.findViewById(R.id.led_img);
            viewHolder.ivAdd = (ImageView) view.findViewById(R.id.iv_add);
            viewHolder.ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i == mBmpList.size() - 1 && ledBmp.getId() == -1) {
            viewHolder.ledBmp.setVisibility(View.INVISIBLE);
            viewHolder.ivAdd.setVisibility(View.VISIBLE);
        } else {
            if(isEditMode)
                viewHolder.ivDelete.setVisibility(View.VISIBLE);
            else
                viewHolder.ivDelete.setVisibility(View.INVISIBLE);
            viewHolder.ivAdd.setVisibility(View.INVISIBLE);
            viewHolder.ledBmp.setVisibility(View.VISIBLE);
            String filePath = ledBmp.getFilePath();
            Drawable drawable;
            if (filePath != null) {
                drawable = Drawable.createFromPath(filePath);
            }
            else {
                drawable = context.getResources().getDrawable(ledBmp.getResourceID());
            }
            if (drawable != null) {
                viewHolder.ledBmp.setImageDrawable(drawable);
            }
        }
        return view;
    }

    class ViewHolder {
        ImageView ledBmp;
        ImageView ivAdd;
        ImageView ivDelete;
    }
}
