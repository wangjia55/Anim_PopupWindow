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

/**
 * Package : com.jacob.anim.popup
 * Author : jacob
 * Date : 15-1-8
 * Description : 这个类是用来xxx
 */
public class ShowLeftPopup extends PopupWindow {
    private ObjectAnimator animShow;
    private ObjectAnimator animDismiss;
    private View rootView;
    private Context context;
    private boolean needDismiss = false;
    private int  rootViewWidth;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            needDismiss = true;
            dismiss();
        }
    };

    public ShowLeftPopup(Context context) {
        super(context);
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.layout_popup_show_left, null);

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

        setHeight(height);

        int[] location = new int[2];
        archor.getLocationOnScreen(location);
        rootViewWidth = dip2px(context,180);
        setWidth(rootViewWidth);

        animShow = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_X, -100, 20, 10, 0).setDuration(300);
        animDismiss = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_X, 0, -rootViewWidth).setDuration(200);

        showAtLocation(archor, Gravity.NO_GRAVITY, location[0] + with, location[1]);
        animShow.start();
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     * 这里是重点：两次调用dismiss，如果直接使用super方法是没有办法显示动画的，
     * 所以这里的做法是，通过一个boolean变量进行控制，第一次的dismiss的时候先显示动画，
     * 动画结束后，再调用自身的dismiss方法，将整个window消失掉
     */
    @Override
    public void dismiss() {
        if (!needDismiss){
            animDismiss.start();
            handler.sendEmptyMessageDelayed(10, 350);
        }
        if (needDismiss){
            handler.removeMessages(10);
            needDismiss = false;
            super.dismiss();
        }
    }
}
