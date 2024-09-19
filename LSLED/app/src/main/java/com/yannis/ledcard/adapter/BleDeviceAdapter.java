package com.yannis.ledcard.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.ble.ScanDevice;

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
public class BleDeviceAdapter extends BaseAdapter {

    private List<ScanDevice> scanDevices;
    private Context context;
    private LayoutInflater inflater;

    public BleDeviceAdapter(List<ScanDevice> scanDevices, Context context) {
        this.scanDevices = scanDevices;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scanDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return scanDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_ble_device, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final ScanDevice device = scanDevices.get(position);
        viewHolder.tvDeviceName.setText(device.getName()+"-" + device.getAddress());
        viewHolder.tvRssi.setText(device.getRssi()+"");
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tvDeviceName)
        TextView tvDeviceName;
        @BindView(R.id.tvRssi)
        TextView tvRssi;

        View itemView;

        public ViewHolder(View view) {
            this.itemView = view;
            ButterKnife.bind(this, view);
        }
    }
}
