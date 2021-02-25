package com.example.photoandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Photo {
    private String title;
    private File imageFile;
    transient Bitmap image;
    private ArrayList<String> personTag;
    private ArrayList<String> locationTag;

    public Photo(String title){
        this.title = title;
    }
    public Photo(File imageFile){
        this.imageFile = imageFile;
        this.title = imageFile.getName();
        this.image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        this.personTag = new ArrayList<>();
        this.locationTag = new ArrayList<>();

    }

    public File getImageFile() {
        return imageFile;
    }
    public void setImageFile(String albumName){
        String oldpath = this.getImageFile().getPath();
        String[] frag = oldpath.split("/");
//        for(int i = 0; i < frag.length;i++){
//
//            System.out.println(frag[i]);
//        }
        frag[frag.length-2] = albumName;
        StringBuilder newPath = new StringBuilder();
        for(int i = 0;i < frag.length;i++){
            newPath.append("/");
            newPath.append(frag[i]);
        }
        this.imageFile = new File(newPath.toString());
    }
    public String getTitle() {
        return title;
    }
    public Bitmap getBitmap(){
        return image;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public void addPersonTag(String person){
        personTag.add(person);
    }
    public void addLocationTag(String location){
        locationTag.add(location);
    }

    public ArrayList<String> getLocationTag() {
        return locationTag;
    }

    public ArrayList<String> getPersonTag() {
        return personTag;
    }
    public void removeLocation(String location){
        this.locationTag.remove(location);
    }
    public void removePerson(String person){
        this.personTag.remove(person);
    }
}
