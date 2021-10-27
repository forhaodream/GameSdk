package com.nezha.gamelib.bean;

public class JPushBean {
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
        private String username;
        private String passwd;
        private String token;
        private int uid;
        private int is_bind_idcard;
        private int age;
        private String mobile;
        private int indulge;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getIs_bind_idcard() {
            return is_bind_idcard;
        }

        public void setIs_bind_idcard(int is_bind_idcard) {
            this.is_bind_idcard = is_bind_idcard;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getIndulge() {
            return indulge;
        }

        public void setIndulge(int indulge) {
            this.indulge = indulge;
        }
    }
}
