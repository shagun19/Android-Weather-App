package com.example.myapplication;

public class NewsCard {
    private String newsId;
    private String imgUrl;
    private String headline;
    private String time;
    private String date;
    private String newsSource;

    public  void setImgUrl(String imgUrl){
        this.imgUrl=imgUrl;
    }
    public  void setHeadline(String headline){
        this.headline=headline;
    }
    public  void setTime(String time){
        this.time=time;
    }
    public  void setNewsSource(String newsSource){
        this.newsSource=newsSource;
    }
    public void setNewsId(String newsId) {this.newsId=newsId;}
    public void setDate(String date) {this.date=date;}
    public  String getImgUrl(){ return imgUrl; }
    public  String getHeadline(){
        return headline;
    }
    public  String getTime(){ return time; }
    public  String getNewsSource(){
        return newsSource;
    }
    public  String getNewsId() {return newsId;}
    public String getDate() {return date;}

}
