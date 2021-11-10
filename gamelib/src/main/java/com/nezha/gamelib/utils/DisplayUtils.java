package com.nezha.gamelib.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {

    /**
     * 得到的屏幕的宽度
     */
    public static int getWidthPx(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();// 初始化一个结构
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaysMetrics);// 对该结构赋值
        return displaysMetrics.widthPixels;
    }
    /**
     * 得到的屏幕的高度
     */
    public static int getHeightPx(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        WindowManager wm = activity.getWindowManager();
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 将720的像素值转换成其他屏幕的像素值
     *
     * @param size
     * @param activity
     * @return
     */
    public static int dealWithSize(int size, Activity activity) {
        float widthPx = 720;
        // 判断屏幕放向
        String orientation = DeviceUtil.getOrientation(activity);
        if (orientation == "") {
        } else if ("landscape".equals(orientation)) {
            widthPx = getHeightPx(activity);
        } else if ("portrait".equals(orientation)) {
            widthPx = getWidthPx(activity);
        }
        if (widthPx == 720) {
            return size;
        }
        float bili = 720 / widthPx;
        int resize = (int) ((size / bili) + 0.5);
        return resize;
    }

}
