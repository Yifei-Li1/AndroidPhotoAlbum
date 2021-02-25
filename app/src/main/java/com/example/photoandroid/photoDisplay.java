package com.example.photoandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class photoDisplay extends AppCompatActivity {
    public static final String DISPLAY_PHOTO = "displayphoto";
    public static final String PHOTO_INDEX = "photoindex";
    public static final String CURRENT_ALBUM = "currentalbum";
    public static final int ADD_TAG = 11;
    public static final int REMOVE_TAG = 12;
    public static final int MOVE_PHOTO = 13;
    private String imagePath;
    private int imageIndex;
    private int albumIndex;
    private ImageView imageView;
    private TextView personView;
    private TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StringBuilder persons = new StringBuilder();
        StringBuilder locations = new StringBuilder();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            imagePath = bundle.getString(DISPLAY_PHOTO);
            imageIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(CURRENT_ALBUM);

            System.out.println("photo displayed: "+imagePath);
        }
        File file = new File(imagePath);
        System.out.println("imagePath: "+imagePath);
        imageView = findViewById(R.id.image);
        personView = findViewById(R.id.locationTagDisplay);
        locationView = findViewById(R.id.personTagDisplay);

        if(file.exists()){
            //load imageView
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            //load tags
            for(int i = 0;i < Albums.albums.get(albumIndex).gallery.size();i++){
                if(Albums.albums.get(albumIndex).gallery.get(i).getImageFile().equals(file)){
                    for(String s:Albums.albums.get(albumIndex).gallery.get(i).getPersonTag()){
                        persons.append(s);
                        persons.append(", ");
                    }
                    for(String s:Albums.albums.get(albumIndex).gallery.get(i).getLocationTag()){
                        locations.append(s);
                        locations.append(", ");
                    }
                }
            }



        }
        System.out.println("imageIndex: "+ imageIndex);
        setTitle(Albums.albums.get(albumIndex).gallery.get(imageIndex).getTitle());
        personView.setText(null);
        locationView.setText(null);
        System.out.println("personTags:"+ persons.toString());
        System.out.println("locationTags:"+ locations.toString());
        personView.setText(persons.toString());

        locationView.setText(locations.toString());






    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photo_delete:
                deletePhoto();
                return true;
            case R.id.action_addTag:
                addTag();
                return true;
            case R.id.action_removeTag:
                removeTag();
                return true;
            case R.id.action_movePhoto:
                movePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void movePhoto() {
        Bundle bundle = new Bundle();
        bundle.putInt(movePhoto.IMAGE_INDEX,imageIndex);
        bundle.putInt(movePhoto.ALBUM_INDEX,albumIndex);
        Intent intent = new Intent(this, movePhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, MOVE_PHOTO);
    }

    public void nextPhoto(View view){
        int totalSize = Albums.albums.get(albumIndex).gallery.size();
        if(imageIndex+1>=totalSize){
            imageIndex = 0;
        }
        else {
            imageIndex += 1;
        }
            imagePath = Albums.albums.get(albumIndex).gallery.get(imageIndex).getImageFile().getPath();
            File file = new File(imagePath);
            StringBuilder persons = new StringBuilder();
            StringBuilder locations = new StringBuilder();
            if (file.exists()) {
                //load imageView
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                //load tags
                File infoTxt = new File(imagePath + ".txt");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(infoTxt));
                    String currentLine;
                    while ((currentLine = reader.readLine()) != null) {
                        //System.out.println(currentLine);
                        if (currentLine.substring(0, currentLine.indexOf(" ")).equals("TAGS:")) {
                            //System.out.println("I'm here");
                            while ((currentLine = reader.readLine()) != null) {
                                String name, value;

                                name = currentLine.substring(0, currentLine.indexOf(" "));
                                value = currentLine.substring(currentLine.indexOf(" ") + 1);
                                System.out.println("Value: " + value);
                                if (name.equals("PERSON")) {
                                    Albums.albums.get(albumIndex).gallery.get(imageIndex).addPersonTag(value);
                                    persons.append(value);
                                    persons.append(", ");
                                } else if (name.equals("LOCATION")) {
                                    Albums.albums.get(albumIndex).gallery.get(imageIndex).addLocationTag(value);
                                    locations.append(value);
                                    locations.append(", ");
                                }
                            }
                            break;
                        }

                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            setTitle(Albums.albums.get(albumIndex).gallery.get(imageIndex).getTitle());

            personView.setText(persons.toString());
            locationView.setText(locations.toString());


    }
    public void previousPhoto(View view){
        //int totalSize = Albums.albums.get(albumIndex).gallery.size();
        if(imageIndex-1<0){
            imageIndex = Albums.albums.get(albumIndex).gallery.size()-1;
        }
        else {
            imageIndex = imageIndex - 1;
        }
            imagePath = Albums.albums.get(albumIndex).gallery.get(imageIndex).getImageFile().getPath();
            File file = new File(imagePath);
            StringBuilder persons = new StringBuilder();
            StringBuilder locations = new StringBuilder();
            if (file.exists()) {
                //load imageView
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                //load tags
                File infoTxt = new File(imagePath + ".txt");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(infoTxt));
                    String currentLine;
                    while ((currentLine = reader.readLine()) != null) {
                        //System.out.println(currentLine);
                        if (currentLine.substring(0, currentLine.indexOf(" ")).equals("TAGS:")) {
                            //System.out.println("I'm here");
                            while ((currentLine = reader.readLine()) != null) {
                                String name, value;

                                name = currentLine.substring(0, currentLine.indexOf(" "));
                                value = currentLine.substring(currentLine.indexOf(" ") + 1);
                                System.out.println("Value: " + value);
                                if (name.equals("PERSON")) {
                                    Albums.albums.get(albumIndex).gallery.get(imageIndex).addPersonTag(value);
                                    persons.append(value);
                                    persons.append(", ");
                                } else if (name.equals("LOCATION")) {
                                    Albums.albums.get(albumIndex).gallery.get(imageIndex).addLocationTag(value);
                                    locations.append(value);
                                    locations.append(", ");
                                }
                            }
                            break;
                        }

                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            setTitle(Albums.albums.get(albumIndex).gallery.get(imageIndex).getTitle());

            personView.setText(persons.toString());
            locationView.setText(locations.toString());

    }
    public void deletePhoto(){
        Bundle bundle = new Bundle();
        bundle.putInt(albumDisplay.DELETE_PHOTOINDEX,imageIndex);
        bundle.putInt(albumDisplay.DELETE_ALBUMINDEX,albumIndex);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(albumDisplay.DELETE_PHOTO,intent);
        finish();
    }
    public void addTag(){
        Bundle bundle = new Bundle();
        bundle.putInt(addTag.PHOTO_INDEX,imageIndex);
        bundle.putInt(addTag.ALBUM_INDEX,albumIndex);
        Intent intent = new Intent(this, addTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_TAG);
    }
    public void removeTag(){
        Bundle bundle = new Bundle();
        bundle.putInt(removeTag.PHOTO_INDEX,imageIndex);
        bundle.putInt(removeTag.ALBUM_INDEX,albumIndex);
        Intent intent = new Intent(this, removeTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REMOVE_TAG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            StringBuilder persons = new StringBuilder();
            StringBuilder locations = new StringBuilder();
            Photo currentPhoto = Albums.albums.get(albumIndex).gallery.get(imageIndex);
            for(String s:currentPhoto.getPersonTag()){

                persons.append(s);
                persons.append(", ");
            }
            for(String s:currentPhoto.getLocationTag()){
                locations.append(s);
                locations.append(", ");
            }
            locationView.setText(null);
            personView.setText(null);
            locationView.setText(locations.toString());
            personView.setText(persons.toString());
        }
        if(resultCode == RESULT_CANCELED){
            return;
        }
        if(resultCode == MOVE_PHOTO){
            finish();

        }


    }
}