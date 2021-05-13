package com.project.diary.model;

public class Feelings {
    private int happy, crazy, sad, love, sick, angry;

    public Feelings(){}

    public Feelings(int fHappy, int fCrazy, int fSad, int fLove, int fSick, int fAngry){
       this.happy = fHappy;
       this.crazy = fCrazy;
       this.sad = fSad;
       this.love = fLove;
       this.sick = fSick;
       this.angry = fAngry;
    }

    public int getHappy() {
        return happy;
    }

    public void setHappy(int happy) {
        this.happy = happy;
    }

    public int getCrazy() {
        return crazy;
    }

    public void setCrazy(int crazy) {
        this.crazy = crazy;
    }

    public int getSad() {
        return sad;
    }

    public void setSad(int sad) {
        this.sad = sad;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public int getSick() {
        return sick;
    }

    public void setSick(int sick) {
        this.sick = sick;
    }

    public int getAngry() {
        return angry;
    }

    public void setAngry(int angry) {
        this.angry = angry;
    }
}
