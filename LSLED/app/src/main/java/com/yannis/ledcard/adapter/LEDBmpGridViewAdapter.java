package com.yannis.ledcard.adapter;

import static org.litepal.LitePalApplication.getContext;

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
            if (isEditMode)
                viewHolder.ivDelete.setVisibility(View.VISIBLE);
            else
                viewHolder.ivDelete.setVisibility(View.INVISIBLE);
            viewHolder.ivAdd.setVisibility(View.INVISIBLE);
            viewHolder.ledBmp.setVisibility(View.VISIBLE);
            String filePath = ledBmp.getFilePath();
            Drawable drawable;
            if (filePath != null) {
                drawable = Drawable.createFromPath(filePath);
            } else {
                drawable = context.getResources().getDrawable(getImageResourceIdById(ledBmp.getId()));
            }
            if (drawable != null) {
                viewHolder.ledBmp.setImageDrawable(drawable);
            }
        }
        return view;
    }


    public static int getImageResourceId(String resourceName) {
        // 获取包名
        String packageName = getContext().getPackageName();
        // 根据资源名和类型获取资源ID
        return getContext().getResources().getIdentifier(resourceName, "drawable", packageName);
    }

    public static int getImageResourceIdById(int id) {
        if (id > 0 && id <= 48) {
            int realId = id % 16;
            //1-16   17-32 33-48
            realId -= 1;
            switch (realId) {
                case 0:
                    return R.drawable.img5;
                case 1:
                    return R.drawable.img6;
                case 2:
                    return R.drawable.img7;
                case 3:
                    return R.drawable.img8;
                case 4:
                    return R.drawable.img9;
                case 5:
                    return R.drawable.img10;
                case 6:
                    return R.drawable.img11;
                case 7:
                    return R.drawable.img12;
                case 8:
                    return R.drawable.img13;
                case 9:
                    return R.drawable.img14;
                case 10:
                    return R.drawable.img15;
                case 11:
                    return R.drawable.img16;
                case 12:
                    return R.drawable.img17;
                case 13:
                    return R.drawable.img18;
                case 14:
                    return R.drawable.img19;
                case -1:
                    return R.drawable.img20;
                default:
                    return R.drawable.img5;
            }
        }
        return R.drawable.img5;
    }

    class ViewHolder {
        ImageView ledBmp;
        ImageView ivAdd;
        ImageView ivDelete;
    }
}
