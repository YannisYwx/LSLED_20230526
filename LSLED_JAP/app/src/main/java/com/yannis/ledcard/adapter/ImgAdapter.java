package com.yannis.ledcard.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.bean.LedImg;

import java.util.List;

/**
 * $DESC$
 *
 * @author yannis
 *         Created on 2016/9/14 11:18
 *         Email:923080261@qq.com
 */
public class ImgAdapter extends BaseAdapter {
    private List<LedImg> ledImgList;
    private Context context;
    private LayoutInflater inflater;

    public ImgAdapter(Context context, List<LedImg> ledImgList) {
        this.ledImgList = ledImgList;
        this.context = context;
        this.inflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ledImgList.size();
    }

    @Override
    public Object getItem(int i) {
        return ledImgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view=inflater.inflate(R.layout.item_img,null);
            viewHolder=new ViewHolder();
            viewHolder.img= (ImageView) view.findViewById(R.id.led_img);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),ledImgList.get(i).getImgRes()));

        return view;
    }

    class ViewHolder{
        ImageView img;
    }
}
