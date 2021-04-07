package com.project.diary.model;

import java.util.Arrays;

public class Entry {

    private String title, content, date, feeling;

    public Entry(){}

    public Entry(String nTitle, String nContent, String nDate, String nFeeling){
        this.title = nTitle;
        this.content = nContent;
        this.date = nDate;
        this.feeling = nFeeling;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

}
