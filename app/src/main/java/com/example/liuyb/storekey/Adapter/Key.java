package com.example.liuyb.storekey.Adapter;

import java.io.Serializable;

/**
 * Created by liuyb on 2017/8/15.
 */

public class Key implements Serializable{

    private String id; //编号
    private String app; //app名
    private String key;

    public Key(String id, String app, String key){
        this.id = id;
        this.app = app;
        this.key = key;
    }

    public String getId(){return id;}

    public void setId(String id){this.id = id;}

    public String getApp(){return app;}

    public void setApp(String app){this.app = app;}

    public String getKey(){return key;}

    public void setKey(String username){this.key = key;}

    @Override
    public String toString() {
        return "Key{" +
                "id=" + id +
                ",app='" + app + '\'' +
                ",key='" + key  + '\'' +
                '}';
    }
}
