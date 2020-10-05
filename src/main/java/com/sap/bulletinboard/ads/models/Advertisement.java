package com.sap.bulletinboard.ads.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;



@Entity 
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
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
