package com.sap.bulletinboard.ads.models;

import org.hibernate.validator.constraints.NotBlank;

public class Advertisement {
    @NotBlank
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
