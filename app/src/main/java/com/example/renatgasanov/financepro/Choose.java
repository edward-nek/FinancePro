package com.example.renatgasanov.financepro;

public class Choose {
    private String title, description;

    public Choose() {
    }
    //todo add picture to class and choose_row

    public Choose(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}