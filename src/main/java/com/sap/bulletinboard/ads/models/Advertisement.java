package com.sap.bulletinboard.ads.models;

public class Advertisement {
    public String title; 
    public Advertisement (String title) {
        this.title = title;
    }
    public Advertisement () {
       
    }
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

}
