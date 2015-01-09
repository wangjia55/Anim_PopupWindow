package com.jacob.anim.popup;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jacob.anim.R;
import com.jacob.anim.UIUtils;

/**
 * Package : com.jacob.anim.popup
 * Author : jacob
 * Date : 15-1-8
 * Description : 这个类是用来xxx
 */
public class ShowBottomPopup extends PopupWindow {
    private ObjectAnimator animShow;
    private ObjectAnimator animDismiss;
    private View rootView;
    private Context context;
    private boolean needDismiss = false;
    private int rootViewHeight;
    private ListView mListView;
    //    String[] res = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "H", "I", "H", "I"};
    private String[] res = new String[]{"A", "B", "C", "D", "E", "F"};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            needDismiss = true;
            dismiss();
        }
    };

    public ShowBottomPopup(Context context) {
        super(context);
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_popup_show_bottom, null);
        setContentView(rootView);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, res);
        mListView.setAdapter(adapter);
        rootViewHeight = UIUtils.setListViewHeightBasedOnChildren(mListView)+50;
    }


    public void show(View archor, int screentHeight) {
        int height = archor.getMeasuredHeight();
        int with = archor.getMeasuredWidth();

        int[] location = new int[2];
        archor.getLocationOnScreen(location);

        int h = screentHeight - location[1] - height;
        if (rootViewHeight > h) {
            rootViewHeight = h;
        }
        setHeight(rootViewHeight);
        animShow = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, height - rootViewHeight, 20, -5, 0).setDuration(300);
        animDismiss = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, 0, -rootViewHeight).setDuration(200);

        showAtLocation(archor, Gravity.NO_GRAVITY, location[0], location[1] + height);
        animShow.start();
    }


    /**
     * 这里是重点：两次调用dismiss，如果直接使用super方法是没有办法显示动画的，
     * 所以这里的做法是，通过一个boolean变量进行控制，第一次的dismiss的时候先显示动画，
     * 动画结束后，再调用自身的dismiss方法，将整个window消失掉
     */
    @Override
    public void dismiss() {
        if (!needDismiss) {
            animDismiss.start();
            handler.sendEmptyMessageDelayed(10, 350);
        }
        if (needDismiss) {
            handler.removeMessages(10);
            needDismiss = false;
            super.dismiss();
        }
    }
}
