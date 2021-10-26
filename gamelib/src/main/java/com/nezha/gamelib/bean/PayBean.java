package com.nezha.gamelib.bean;

public class PayBean {



    private int code;
    private String msg;


    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String trade_id;
        private String pay_url;

        public String getTrade_id() {
            return trade_id;
        }

        public void setTrade_id(String trade_id) {
            this.trade_id = trade_id;
        }

        public String getPay_url() {
            return pay_url;
        }

        public void setPay_url(String pay_url) {
            this.pay_url = pay_url;
        }
    }
}
