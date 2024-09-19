package com.yannis.ledcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.bean.SpeedAndModelBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * $DESC$
 *
 * @author yannis
 * Created on 2016/9/13 17:22
 * Email:923080261@qq.com
 */
public class SpeedAndModelAdapter extends BaseAdapter {

    private List<SpeedAndModelBean> speedAndModelBeans;
    private Context context;
    private LayoutInflater inflater;

    public SpeedAndModelAdapter(List<SpeedAndModelBean> speedAndModelBeans, Context context) {
        this.speedAndModelBeans = speedAndModelBeans;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return speedAndModelBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return speedAndModelBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_speed_model, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SpeedAndModelBean speedAndModelBean = speedAndModelBeans.get(position);
        viewHolder.tvTitle.setText(speedAndModelBean.title);
        viewHolder.ivSelect.setVisibility(speedAndModelBean.isSelect ? View.VISIBLE : View.INVISIBLE);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int i = 0; i < speedAndModelBeans.size(); i++) {
//                    speedAndModelBeans.get(i).isSelect = i == position;
//                    notifyDataSetChanged();
//                }
//            }
//        });
        return view;
    }

    public int getSelectIndex() {
        for (int i = 0; i < speedAndModelBeans.size(); i++) {
            if (speedAndModelBeans.get(i).isSelect) {
                return i;
            }
        }
        return 0;
    }

    static class ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivSelect)
        ImageView ivSelect;

        View itemView;

        public ViewHolder(View view) {
            this.itemView = view;
            ButterKnife.bind(this, view);
        }
    }
}
