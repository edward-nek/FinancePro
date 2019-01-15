package com.example.renatgasanov.financepro.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String amount;
    private String type;

    public Post() {
    }

    @Ignore
    public Post(String title, String content, String amount, String type) {
        this.title = title;
        this.content = content;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAmount(){
        return amount;
    }

    public void setAmount(String amount){
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }
}
