package com.nezha.gamelib.bean;

import java.io.Serializable;
import java.util.List;

public class AccountListBean implements Serializable {
    private List<AutoBean> list;

    public List<AutoBean> getList() {
        return list;
    }

    public void setList(List<AutoBean> list) {
        this.list = list;
    }
}
