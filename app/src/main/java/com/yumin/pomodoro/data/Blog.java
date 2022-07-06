package com.yumin.pomodoro.data;

import java.io.Serializable;
import java.lang.String;

public class Blog implements Serializable {
  private String blog_url;

  private String img_url;

  private String description;

  private String from;

  private String title;

  private String published_at;

  public String getBlog_url() {
    return this.blog_url;
  }

  public void setBlog_url(String blog_url) {
    this.blog_url = blog_url;
  }

  public String getImg_url() {
    return this.img_url;
  }

  public void setImg_url(String img_url) {
    this.img_url = img_url;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPublished_at() {
    return this.published_at;
  }

  public void setPublished_at(String published_at) {
    this.published_at = published_at;
  }
}
