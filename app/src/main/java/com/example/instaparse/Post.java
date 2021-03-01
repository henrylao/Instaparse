package com.example.instaparse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String newDescription){
        put(KEY_DESCRIPTION, newDescription);
    }
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile newParseFile){
        put(KEY_IMAGE, newParseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser newParseUser){
        put(KEY_USER, newParseUser);
    }
}
