package com.yannis.ledcard.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.SpeedAndModelAdapter;
import com.yannis.ledcard.base.BaseActivity;
import com.yannis.ledcard.bean.SpeedAndModelBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SpeedAndModelSelectActivity extends BaseActivity {
    public static final String SETTING_TYPE = "_SETTING_TYPE_";
    public static final String DEFAULT_SELECT_INDEX = "_DEFAULT_SELECT_INDEX_";

    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.lvSpeedModel)
    public ListView lvSpeedModel;

    private int type;
    private int defaultSelectIndex;

    List<SpeedAndModelBean> data = new ArrayList<>();

    SpeedAndModelAdapter adapter = null;

    @Override
    protected void init() {
        type = getIntent().getIntExtra(SETTING_TYPE, 0);
        defaultSelectIndex = getIntent().getIntExtra(DEFAULT_SELECT_INDEX, 0);
        tvContext.setText(type == 0 ? R.string.speed : R.string.mode);
    }

    @Override
    protected void initData() {
        initSettingTypeList(type, defaultSelectIndex);
        adapter = new SpeedAndModelAdapter(data, this);
        lvSpeedModel.setAdapter(adapter);
        lvSpeedModel.setOnItemClickListener((parent, view, position, id) -> {

            for (int i = 0; i < data.size(); i++) {
                data.get(i).isSelect = i == position;
                adapter.notifyDataSetChanged();
            }


            Intent intent = new Intent();
            intent.putExtra(SETTING_TYPE, type);
            intent.putExtra(DEFAULT_SELECT_INDEX, adapter.getSelectIndex());
            setResult(0, intent);
            finish();
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_speed_model_select;
    }

    @Override
    public void onClick(View v) {

    }

    @OnClick(R.id.iv_back)
    public void onBack() {
        Intent intent = new Intent();
        intent.putExtra(SETTING_TYPE, type);
        intent.putExtra(DEFAULT_SELECT_INDEX, adapter.getSelectIndex());
        setResult(0, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(SETTING_TYPE, type);
        intent.putExtra(DEFAULT_SELECT_INDEX, adapter.getSelectIndex());
        setResult(0, intent);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(SETTING_TYPE, type);
            intent.putExtra(DEFAULT_SELECT_INDEX, adapter.getSelectIndex());
            setResult(0, intent);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initSettingTypeList(int type, int defaultIndex) {
        data.clear();
        if (type == 0) { // speed
            for (int i = 0; i < 8; i++) {
                data.add(new SpeedAndModelBean(i, (i + 1) + "", i == defaultIndex));
            }
        } else {
            String[] modelArray = getResources().getStringArray(R.array.modestr);
            for (int i = 0; i < modelArray.length; i++) {
                data.add(new SpeedAndModelBean(i, modelArray[i], i == defaultIndex));
            }
        }
    }
}
