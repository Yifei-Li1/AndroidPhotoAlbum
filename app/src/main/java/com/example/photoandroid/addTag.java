package com.example.photoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class addTag extends AppCompatActivity {
    EditText personField;
    EditText locationField;
    public static final String PHOTO_INDEX = "photoindex";
    public static final String ALBUM_INDEX = "albumindex";

    private int photoIndex;
    private int albumIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        Bundle bundle = getIntent().getExtras();
        personField = (EditText)findViewById(R.id.personTag);
        locationField = (EditText)findViewById(R.id.locationTag);
        if (bundle != null) {
            photoIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }


    }
    public void addTagAction(View view) throws IOException {
        //add tag to photo object
        //add tag to info txt

        String person = personField.getText().toString();
        String location = locationField.getText().toString();
        System.out.println("personTag: "+person);
        System.out.println("locationTag: "+location);
        //check duplicate


        String deletePath =  Albums.albums.get(albumIndex).gallery.get(photoIndex).getImageFile().getPath();
        File txtFile = new File(deletePath+".txt");
        System.out.println("photo info txt: "+txtFile.toPath());
        FileWriter fw = new FileWriter(txtFile,true);
        BufferedWriter out = new BufferedWriter(fw);

        if(person!=null&&person.length()!=0){
            if(Albums.albums.get(albumIndex).gallery.get(photoIndex).getPersonTag().contains(person)){
                //duplicated
                Toast.makeText(addTag.this, "duplicated tag",
                        Toast.LENGTH_SHORT).show();
            }
            else {

                Albums.albums.get(albumIndex).gallery.get(photoIndex).addPersonTag(person);
                out.write("PERSON" + " " + person + "\n");
            }
        }
        if(location!=null&&location.length()!=0){
            if(Albums.albums.get(albumIndex).gallery.get(photoIndex).getLocationTag().contains(location)){
                //duplicated
                Toast.makeText(addTag.this, "duplicated tag",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Albums.albums.get(albumIndex).gallery.get(photoIndex).addLocationTag(location);
                out.write("LOCATION" + " " + location + "\n");
            }
        }
        out.close();
        setResult(RESULT_OK);
        finish();



    }
    public void cancelTag(View view){
        setResult(RESULT_CANCELED);
        finish();

    }
}