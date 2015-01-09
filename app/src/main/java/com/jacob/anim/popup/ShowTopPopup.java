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
import android.widget.PopupWindow;

import com.jacob.anim.R;
import com.jacob.anim.UIUtils;

/**
 * Package : com.jacob.anim.popup
 * Author : jacob
 * Date : 15-1-8
 * Description : 这个类是用来xxx
 */
public class ShowTopPopup extends PopupWindow {
    private ObjectAnimator animShow;
    private ObjectAnimator animDismiss;
    private View rootView;
    private Context context;
    private boolean needDismiss = false;
    private int rootViewHeight;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            needDismiss = true;
            dismiss();
        }
    };

    public ShowTopPopup(Context context) {
        super(context);
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.layout_popup_show_top, null);

        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(rootView);
    }


    public void show(View archor) {
        int height = archor.getMeasuredHeight();
        int with = archor.getMeasuredWidth();

        setWidth(with);
        int[] location = new int[2];
        archor.getLocationOnScreen(location);
        rootViewHeight =  UIUtils.dip2px(context, 180);
        setHeight(rootViewHeight);

        animShow = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, rootViewHeight, -20, 5, 0).setDuration(300);
        animDismiss = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, 0, rootViewHeight).setDuration(200);

        showAtLocation(archor, Gravity.NO_GRAVITY, location[0], location[1] - rootViewHeight);
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
