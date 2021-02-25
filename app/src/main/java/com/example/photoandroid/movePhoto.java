package com.example.photoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class movePhoto extends AppCompatActivity {
    public static final String IMAGE_INDEX = "imageindex";
    public static final String ALBUM_INDEX = "albumindex";

    private int imageIndex;
    private int albumIndex;
    EditText destName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_photo);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            imageIndex = bundle.getInt(IMAGE_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
            destName = findViewById(R.id.dest_album_name);
            System.out.println("source index: " + imageIndex);

        }

    }

    public void move(View view){
        //find dest path
        //copy image and txt info
        //delete image and txt info
        //update object
        String albumtoMove = destName.getText().toString();
        if(albumtoMove==null||albumtoMove.length()==0){
            Toast.makeText(movePhoto.this, "no destination entered",
                    Toast.LENGTH_SHORT).show();
        }
        else if(albumtoMove.equals(Albums.albums.get(albumIndex).name)){
            Toast.makeText(movePhoto.this, "can not move to current album",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            File source = Albums.albums.get(albumIndex).gallery.get(imageIndex).getImageFile();
            File destRoot = new File(movePhoto.this.getFilesDir(),albumtoMove);
            if(!destRoot.isDirectory()){
                Toast.makeText(movePhoto.this, "destination not found",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                File dest = new File(destRoot, source.getName());


                System.out.println("source path: " + source.getPath());
                try {
                    Files.copy(source.toPath(), dest.toPath());
                    String temp = source.toPath()+".txt";
                    String temp2 = dest.toPath()+".txt";
                    Path sourcePath = Paths.get(temp);
                    Path destPath = Paths.get(temp2);
                    Files.copy(sourcePath,destPath);
                    File sourceTxt = new File(temp);
                    //delete original file
                    Files.deleteIfExists(source.toPath());
                    Files.deleteIfExists(sourceTxt.toPath());
                    Albums.albums.get(albumIndex).gallery.removeIf(n->n.getImageFile()==source);

                    setResult(photoDisplay.MOVE_PHOTO);
                    finish();
                } catch (FileAlreadyExistsException e) {
                    Toast.makeText(movePhoto.this, "file already exists",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }
    }
    public void cancel_move(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}