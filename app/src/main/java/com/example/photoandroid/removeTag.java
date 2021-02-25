package com.example.photoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class removeTag extends AppCompatActivity {
    EditText personField;
    EditText locationField;
    public static final String PHOTO_INDEX = "photoindex";
    public static final String ALBUM_INDEX = "albumindex";

    private int photoIndex;
    private int albumIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_tag);
        Bundle bundle = getIntent().getExtras();
        personField = (EditText)findViewById(R.id.personTag);
        locationField = (EditText)findViewById(R.id.locationTag);
        if (bundle != null) {
            photoIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }
    }


    public void removeTagAction(View view) throws IOException {
        String person = personField.getText().toString();
        String location = locationField.getText().toString();
        System.out.println("personTag: "+person);
        System.out.println("locationTag: "+location);
        String deletePath =  Albums.albums.get(albumIndex).gallery.get(photoIndex).getImageFile().getPath();
        File txtFile = new File(deletePath+".txt");
        System.out.println("photo info txt: "+txtFile.toPath());
        FileWriter fw = new FileWriter(txtFile,true);
        BufferedWriter out = new BufferedWriter(fw);

        //remove txt file tag
        //remove object tag
        if(person!=null&&person.length()!=0){
            String stringTobeDelete = "PERSON "+person;

            File tempFile = new File(deletePath+"temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(txtFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(stringTobeDelete)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(txtFile);

            Albums.albums.get(albumIndex).gallery.get(photoIndex).removePerson(person);


        }
        if(location!=null&&location.length()!=0){
            String stringTobeDelete = "LOCATION "+location;

            File tempFile = new File(deletePath+"temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(txtFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(stringTobeDelete)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(txtFile);

            Albums.albums.get(albumIndex).gallery.get(photoIndex).removeLocation(location);
        }
        setResult(RESULT_OK);
        finish();
    }
    public void cancelTag(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}