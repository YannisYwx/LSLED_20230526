package com.yannis.ledcard.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.LEDBmpGridViewAdapter;
import com.yannis.ledcard.adapter.LEDBmpViewPagerAdapter;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.util.BitmapUtils;
import com.yannis.ledcard.util.PrefUtils;
import com.yannis.ledcard.util.ViewUtils;
import com.yannis.ledcard.widget.CirclePointIndicatorView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.yannis.ledcard.activity.MainActivity.PIX;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/23 16:08
 * @email : 923080261@qq.com
 * @description : TODO
 */
public class LEDBmpFragment extends Fragment {
    private CirclePointIndicatorView mCirclePointIndicatorView;
    private ViewPager mViewPager;
    private int mRows = 3;//行
    private int mColumns = 8;//列
    private int mLastPosition;
    private ArrayList<LEDBmp> mLEDBmps;
    ArrayList<GridView> gridViewLists = new ArrayList<>();
    Dialog dialog;
    private int currentItem = 0;
    private static boolean isEditMode;

    public static LEDBmpFragment newInstance() {
        return new LEDBmpFragment();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnLEDBmpClickListener) {
            this.listener = (OnLEDBmpClickListener) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emoji_bottom, null);
        mCirclePointIndicatorView = (CirclePointIndicatorView) view.findViewById(R.id.circle_point_indicator_view);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_emoji);
        Log.d("=======onCreateView", "false");
        refreshLEDBmpView(false);
        return view;
    }

    public void refreshLEDBmpView(boolean isEditMode) {
        mLEDBmps = new ArrayList<>();
        this.isEditMode = isEditMode;
        Log.d("=======refreshLED", "  " + isEditMode);
        final int ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 11);
        mLEDBmps.addAll(DataSupport.where("matrix = ?", ledSize + "").find(LEDBmp.class));
//        mLEDBmps.add(new LEDBmp(-1));
        initViewPager(mLEDBmps);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViewPager(ArrayList<LEDBmp> ledBmps) {
        gridViewLists.clear();
        int viewPagerCount = getViewPagerCount(ledBmps);
        mCirclePointIndicatorView.init(viewPagerCount);
        for (int i = 0; i < viewPagerCount; i++) {
            GridView gridView = getViewPagerItem(i, ledBmps);
            gridViewLists.add(gridView);
        }
        mViewPager.setAdapter(new LEDBmpViewPagerAdapter(getActivity(), gridViewLists));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCirclePointIndicatorView.playBy(mLastPosition, position);
                mLastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(currentItem);
    }

    private int getViewPagerCount(ArrayList<LEDBmp> ledBmps) {
        int count = ledBmps.size();
        return count % (mRows * mColumns - 1) == 0 ? count / (mRows * mColumns - 1) : count / (mRows * mColumns - 1) + 1;
    }

    private GridView getViewPagerItem(int position, ArrayList<LEDBmp> ledBmps) {
        GridView gridView = (GridView) View.inflate(getActivity(), R.layout.gridview_led_bmp, null);
        final List<LEDBmp> subLEDBmpLists = new ArrayList<>(ledBmps.subList(position * (mRows * mColumns - 1), (position + 1) * (mRows * mColumns - 1) > ledBmps.size() ? ledBmps.size() : (position + 1) * (mRows * mColumns - 1)));
//        for (int i = subLEDBmpLists.size(); i < (mColumns * mRows - 1); i++) {
//            subLEDBmpLists.add(new LEDBmp(-1));
//        }
        LEDBmp ledBmp = new LEDBmp(-1);
        subLEDBmpLists.add(ledBmp);
        for (LEDBmp subLEDBmpList : subLEDBmpLists) {
            Log.d("====getViewPagerItem", "  id = " + subLEDBmpList.getId() + " , filePath = " + subLEDBmpList.getFilePath() + " , resourceId = " + subLEDBmpList.getResourceID() + " , content = " + subLEDBmpList.getContent());
        }
        Log.d("====getViewPagerItem", "  " + isEditMode);
        gridView.setAdapter(new LEDBmpGridViewAdapter(getActivity(), subLEDBmpLists, isEditMode));
        gridView.setNumColumns(mColumns);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    if (position == subLEDBmpLists.size() - 1) {
                        listener.onLEDBmpAdd();
                    } else {
                        ImageView ledView = (ImageView) view.findViewById(R.id.led_img);
                        LEDBmp ledBmp1 = subLEDBmpLists.get(position);
                        if (!isEditMode) {
                            if (ledBmp1 != null && ledBmp1.getId() > 0) {
                                listener.onLEDBmpChoice(ledBmp1, BitmapUtils.loadBitmapFromView(ledView, ViewUtils.dp2px(getContext(), 33), ViewUtils.dp2px(getContext(), 33), false));
                            }
                        } else {
                            if (ledBmp1 != null && ledBmp1.getId() > 0) {
                                listener.onLEDBmpDelete(ledBmp1);
                                refreshLEDBmpView(isEditMode);
                                if (editText != null) {
                                    LedSettingsActivity.parseLEDBmp(getContext(), "", editText);
                                }
//                            }
                            }
                        }
                    }
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                LEDBmp ledBmp1 = subLEDBmpLists.get(i);
                if (ledBmp1 != null && ledBmp1.getId() > 0) {
                    showNormalDialog(ledBmp1);
                }
                return true;
            }
        });

        return gridView;
    }

    private void showNormalDialog(final LEDBmp ledBmp) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this.getContext());
        normalDialog.setIcon(R.drawable.icon_launcher);
        normalDialog.setTitle(getResources().getText(R.string.edit));
        normalDialog.setPositiveButton(getResources().getText(R.string.sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), SavePictureActivity.class);
                        intent.putExtra(SavePictureActivity.LEDBMP_UNDER_EDITING, ledBmp);
                        getContext().startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton(getResources().getText(R.string.exit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


    private OnLEDBmpClickListener listener;

    public void setOnLEDBmpClickListener(OnLEDBmpClickListener listener) {
        this.listener = listener;
    }

    private EditText editText;

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public interface OnLEDBmpClickListener {
        void onLEDBmpChoice(LEDBmp ledBmp, Bitmap bitmap);

        void onLEDBmpDelete(LEDBmp ledBmp);

        void onLEDBmpAdd();
    }
}
