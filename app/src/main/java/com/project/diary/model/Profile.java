package com.project.diary.model;

import java.util.List;

public class Profile {
    private String profile_name, profile_img_link, fav_quote;

    public Profile(){}

    public Profile( String pName, String pImgLink, String pFavQuote){
        this.profile_img_link = pImgLink;
        this.profile_name = pName;
        this.fav_quote = pFavQuote;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_img_link() {
        return profile_img_link;
    }

    public void setProfile_img_link(String profile_img_link) {
        this.profile_img_link = profile_img_link;
    }

    public String getFav_quote() {
        return fav_quote;
    }

    public void setFav_quote(String fav_quote) {
        this.fav_quote = fav_quote;
    }
}
