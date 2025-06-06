package com.example.musicstreamproject2.models;

public class CategoryModel {
    String name=null;
    String coverURL=null;
    public CategoryModel(String name, String coverURL)
    {
        this.name=name;
        this.coverURL=coverURL;
    }

    public String getCategoryName()
    {
        return name;
    }
    public String getCategoryCoverURL()
    {
        return coverURL;
    }
}
