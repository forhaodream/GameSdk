package com.nezha.gamelib.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CH
 * on 2021/9/2 09:09
 * desc
 */
public class CommonViewHolder {

    private final SparseArray<View> views;
    private final View itemView;

    private CommonViewHolder(Context context, int layoutId, ViewGroup viewGroup) {
        this.views = new SparseArray<>();
        this.itemView = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        this.itemView.setTag(this);
    }

    public static CommonViewHolder getViewHolder(View convertView, Context context, int layoutId, ViewGroup viewGroup) {
        if (convertView == null) {
            return new CommonViewHolder(context, layoutId, viewGroup);
        }
        return (CommonViewHolder) convertView.getTag();
    }


    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return itemView;
    }

}
