package com.yannis.ledcard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;

import com.yannis.ledcard.R;
import com.yannis.ledcard.widget.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 92308 on 2016/12/11.
 */

public class ColorChoiceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<LedColor> ledColorList;
    private int pos;
    private Map<Integer,RadioButton> rmaps=new HashMap<>();
    public ColorChoiceAdapter(Context context){
        this.context=context;
        inflater= LayoutInflater.from(context);
        ledColorList=getLedColorList();
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_color,null);
            holder=new ViewHolder();
            holder.ivColor= (CircleImageView) convertView.findViewById(R.id.iv_color);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        LedColor ledColor=ledColorList.get(position);
//        holder.ivColor.setBackgroundColor(ledColor.color);
//        holder.ivColor.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),getResBitmap(position)));
        holder.ivColor.setImageDrawable(context.getDrawable(getResBitmap(position)));
        return convertView;
    }

    public class LedColor{
        public int pos;
        public int color;

        public LedColor(int pos, int color) {
            this.pos = pos;
            this.color = color;
        }
    }

    public class ViewHolder{
        CircleImageView ivColor;
    }

    public int getResBitmap(int pos){
        int res=R.drawable.bg_color_1;
        switch (pos){
            case 0:
                res=R.drawable.bg_color_1;
                break;
            case 1:
                res=R.drawable.bg_color_2;
                break;
            case 2:
                res=R.drawable.bg_color_3;
                break;
            case 3:
                res=R.drawable.bg_color_4;
                break;
            case 4:
                res=R.drawable.bg_color_5;
                break;
            case 5:
                res=R.drawable.bg_color_6;
                break;
            case 6:
                res=R.drawable.bg_color_7;
                break;
        }
        return res;
    }

    public List<LedColor> getLedColorList(){
        List<LedColor> ledColorList=new ArrayList<>();
        LedColor ldc1=new LedColor(0, Color.parseColor("#FF0000"));
        LedColor ldc2=new LedColor(1, Color.parseColor("#00FF00"));
        LedColor ldc3=new LedColor(2, Color.parseColor("#0000FF"));
        LedColor ldc4=new LedColor(3, Color.parseColor("#FEFE04"));
        LedColor ldc5=new LedColor(4, Color.parseColor("#FF00FF"));
        LedColor ldc6=new LedColor(5, Color.parseColor("#00FFFF"));
        LedColor ldc7=new LedColor(6, Color.parseColor("#FFFFFF"));
        ledColorList.add(ldc1);
        ledColorList.add(ldc2);
        ledColorList.add(ldc3);
        ledColorList.add(ldc4);
        ledColorList.add(ldc5);
        ledColorList.add(ldc6);
        ledColorList.add(ldc7);
        return ledColorList;
    }

    public LedColor getLedColor(){
        return ledColorList.get(pos);
    }
}
