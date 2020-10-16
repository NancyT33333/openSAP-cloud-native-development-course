package com.sap.bulletinboard.ads.models;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotBlank;



@Entity 
@Table(name = "advertisements")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @NotBlank
    public String title; 
    
    @Override
    public String toString() {
        return "Advertisement [id=" + id + ", title=" + title + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", version=" + version + "]";
    }

    public Timestamp createdAt;
    public Timestamp updatedAt;
    
    @Version
    public Long version;
    public Advertisement (String title) {
        this.title = title;
    }
    @PrePersist 
    public void CreateTimestamp() {
        this.createdAt = now();
    }
    
    @PreUpdate
    public void CreateUpdTimestamp() {
        this.updatedAt = now();
    }
    
    public Advertisement () {
       
    }
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    protected Timestamp now() {                       
        return new Timestamp((new Date()).getTime()); 
    }
    public Timestamp getCreatedAt() {        
        return this.createdAt;
    } 
    
    public Timestamp getUpdatedAt() {        
        return this.updatedAt;
    }
    public Long getVersion() {
        
        return this.version;
    } 

}
