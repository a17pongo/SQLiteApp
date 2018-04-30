package org.brohede.marcus.sqliteapp;

/**
 * Created by marcus on 2018-04-25.
 */

public class Mountain {

    // You need to create proper member variables, methods, and constructors

    // These member variables should be used
    private String name;
    private String location;
    private int height;
    private String img_url;
    private String info_url;

    public Mountain(String inName,String inLocation, int inHeight, String inImg_url, String inInfo_url){
        name = inName;
        location = inLocation;
        height = inHeight;
        img_url=inImg_url;
        info_url=inInfo_url;
    }

    @Override
    public String toString() {
        return name;
    }


    public String info(){
        String str = "Name: " + name;
        str += "\n" + "Height: " + height;
        str += "\n" + "Location: " + location;
        //str += "\n" + "Image: " + img_url;
        //str += "\n" + "Info: " + info_url;
        return str;
    }
}
