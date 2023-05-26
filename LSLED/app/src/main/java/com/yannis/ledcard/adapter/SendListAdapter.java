package com.yannis.ledcard.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.bean.SendContent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yannis.ledcard.activity.LedSettingsActivity.parseLEDBmp;

/**
 * $DESC$
 *
 * @author yannis
 *         Created on 2016/9/13 17:22
 *         Email:923080261@qq.com
 */
public class SendListAdapter extends BaseAdapter {

    private List<SendContent> sendContentList;
    private Context context;
    private LayoutInflater inflater;

    public SendListAdapter(List<SendContent> sendContentList, Context context) {
        this.sendContentList = sendContentList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sendContentList.size();
    }

    @Override
    public Object getItem(int i) {
        return sendContentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_send_content, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SendContent sendContent = sendContentList.get(position);
        viewHolder.tvIndex.setText((position + 1) + ".");
        viewHolder.tvContext.setText(TextUtils.isEmpty(sendContent.getMessage()) ? "" : sendContent.getMessage());
        parseLEDBmp(LedBleApplication.instance,TextUtils.isEmpty(sendContent.getMessage()) ? "" : sendContent.getMessage(), viewHolder.tvContext);
        viewHolder.checkBox.setChecked(sendContent.isSelect());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContent.setSelect(!sendContent.isSelect());
                sendContent.update(sendContent.getId());
            }
        });

        return view;
    }

    class ViewHolder {
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_context)
        TextView tvContext;
        @BindView(R.id.cb_isSelect)
        CheckBox checkBox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
