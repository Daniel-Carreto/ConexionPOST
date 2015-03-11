package com.systemboy.carreto.conexionphpost;

import org.json.JSONObject;

/**
 * Created by Daniel on 19/01/2015.
 */
public class Articles {
    private String title;
    private String desc;
    private String url;

    public Articles(String pTitle, String pDesc, String pUrl){
        this.title = pTitle;
        this.title = pDesc;
        this.url = pUrl;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(){
        this.title = title;
    }
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("tiitle",getTitle());
            jsonObject.put("desc",getDesc());
            jsonObject.put("url",getUrl());
            return jsonObject.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
