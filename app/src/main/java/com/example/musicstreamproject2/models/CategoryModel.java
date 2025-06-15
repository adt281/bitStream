// Used to both category as well as for sections [sections- all the dynamic lists we are displaying below categories]


package com.example.musicstreamproject2.models;

import java.util.List;
public class CategoryModel {
    String name=null;
    String coverUrl =null;
    List<Integer> songs=null;
    public CategoryModel() {
    }

    public CategoryModel(String name, String coverUrl, List<Integer> songs)
    {
        this.name=name;
        this.coverUrl = coverUrl;
        this.songs=songs;
    }

    public String getName()
    {
        return name;
    }
    public String getCoverUrl()
    {
        return coverUrl;
    }
    public List<Integer> getSongs()
    {
        return songs;
    }
}
