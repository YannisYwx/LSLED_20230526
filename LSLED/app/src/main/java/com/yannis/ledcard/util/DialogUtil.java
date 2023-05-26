package com.yannis.ledcard.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.ColorChoiceAdapter;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.widget.LEDView;
import com.yannis.ledcard.widget.WheelView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * $DESC$
 *
 * @author yannis
 * Created on 2016/9/14 15:13
 * Email:923080261@qq.com
 */
public class DialogUtil {

    public class DialogMode {
        public static final int SPEED = 1;
        public static final int MODE = 2;
        public static final int PIX = 3;
    }

    public static void showWheelViewDialog(Context context, final int mode, final int defaultIndex, String label, final OnWheelViewSelectListener listener) {
        final Dialog dialog = new Dialog(context, R.style.YDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_wheelview);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.dialogAnim);
        dialog.getWindow().getAttributes().width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        final WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);
        TextView txLabel = (TextView) dialog.findViewById(R.id.wheelView_label);
        if (!TextUtils.isEmpty(label)) {
            txLabel.setText(label);
        }
        initDataByDialogMode(wheelView, mode, defaultIndex);
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.OnWheelViewSelect(wheelView.getSelectedText(), wheelView.getSelected());
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void initDataByDialogMode(WheelView wheelView, final int mode, final int defaultIndex) {
        ArrayList<String> data = new ArrayList<>();
        String txLabEL = "";
        switch (mode) {
            case DialogMode.SPEED:
                for (int i = 0; i < 8; i++) {
                    data.add((i + 1) + "");
                }
                break;
            case DialogMode.MODE:
                data.addAll(Arrays.asList(wheelView.getContext().getResources().getStringArray(R.array.modestr)));
//                data.add("左移");
//                data.add("右移");
//                data.add("上移");
//                data.add("下移");
//                data.add("固定");
//                data.add("雪花");
//                data.add("画卷");
//                data.add("激光");
                break;
            case DialogMode.PIX:
                data.add("11");
                data.add("12");
                data.add("16");
                break;
            default:
                break;

        }
        wheelView.setData(data);
        wheelView.setDefault(defaultIndex);
    }

    public interface OnWheelViewSelectListener {
        void OnWheelViewSelect(Object obj, int index);
    }

    public static Dialog getChoiceColorDialog(Context context, final OnColorSelectListener listener) {
        final Dialog dialog = new Dialog(context, R.style.YDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_colors);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.dialogAnim);
        dialog.getWindow().getAttributes().width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        GridView listView = (GridView) dialog.findViewById(R.id.lv_color);
        final ColorChoiceAdapter adapter = new ColorChoiceAdapter(context);
        listView.setAdapter(adapter);
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.OnColorSelect(position);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        return dialog;
    }

    public interface OnColorSelectListener {
        void OnColorSelect(int pos);
    }

    public static Dialog getMsgDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.YDialog);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_msg);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.dialogAnim);
        return dialog;
    }

    public static Dialog showEidtLedViewDialog(final Context context, final LEDBmp ledBmp, final OnLEDBmpViewOptListener listener) {
        final Dialog dialog = new Dialog(context, R.style.YDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_led_view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
//        dialog.getWindow().setWindowAnimations(R.style.dialogAnim);
        final LEDView ledView = (LEDView) dialog.findViewById(R.id.ledBmpView);
        final String content = ledBmp.getContent();
        ledView.setLEDData(ledBmp.getContent());
        ledView.setIsCanTouch(true);
        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //修改
                String fileName = BitmapUtils.generateFileName();
                String dir = BitmapUtils.getBmpDir();
                BitmapUtils.deleteFile(ledBmp.getFilePath());
                ledBmp.setContent(ledView.getLedData());
                ContentValues contentValues = new ContentValues();
                contentValues.put("content", ledBmp.getContent());
                contentValues.put("filePath", dir + fileName);
                int id = DataSupport.update(LEDBmp.class, contentValues, ledBmp.getId());
                BitmapUtils.saveBitmapToBmpFile(BitmapUtils.loadBitmapFromView(ledView, ViewUtils.dp2px(context, 33), ViewUtils.dp2px(context, 33), false), fileName);
                if (id > 0 && listener != null) {
                    listener.onLEDBmpModify(ledBmp, id);
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapUtils.deleteFile(ledBmp.getFilePath());
                int id = ledBmp.delete();
                if (id > 0 && listener != null) {
                    listener.onLEDBmpDelete(ledBmp, id);
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.iv_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ledView.setLEDData(content);
            }
        });

        dialog.findViewById(R.id.iv_reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ledView.reverse();
            }
        });

        dialog.findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ledView.clear();
            }
        });
        dialog.show();
        return dialog;
    }

    public interface OnLEDBmpViewOptListener {
        void onLEDBmpModify(LEDBmp ledBmp, int id);

        void onLEDBmpDelete(LEDBmp ledBmp, int id);
    }
}
