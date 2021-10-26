package com.nezha.gamelib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by CH
 * on 2021/9/2 09:09
 * desc
 */
    public abstract class CommonAdapter<T> extends BaseAdapter {

        private final Context mContext;
        private final int mLayoutId;
        private final List<T> data;

        public CommonAdapter(Context mContext, int mLayoutId, List<T> mDatas) {
            this.mContext = mContext;
            this.mLayoutId = mLayoutId;
            this.data = mDatas;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public T getItem(int position) {
            return data.get(position);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(view, mContext, mLayoutId, viewGroup);
            if (data != null && data.size() > 0) {
                convert(viewHolder, getItem(position), position);
            }
            return viewHolder.getConvertView();
        }

        public abstract void convert(CommonViewHolder holder, T itemData, int position);

    }
