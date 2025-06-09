package com.example.musicstreamproject2.models;

import java.util.List;

public class CategoryModel {
    String name=null;
    String coverURL=null;
    List<Integer> songs=null;
    public CategoryModel() {
    }

    public CategoryModel(String name, String coverURL, List<Integer> songs)
    {
        this.name=name;
        this.coverURL=coverURL;
        this.songs=songs;
    }

    public String getName()
    {
        return name;
    }
    public String getCoverURL()
    {
        return coverURL;
    }
    public List<Integer> getSongs()
    {
        return songs;
    }
}
