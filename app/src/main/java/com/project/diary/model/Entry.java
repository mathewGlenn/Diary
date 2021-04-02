package com.project.diary.model;

public class Entry {

    private String title, content, date, time;

    public Entry(){}

    public Entry(String nTitle, String nContent, String nDate, String nTime){
        this.title = nTitle;
        this.content = nContent;
        this.date = nDate;
        this.time = nTime;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
