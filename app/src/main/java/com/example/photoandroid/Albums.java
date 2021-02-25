package com.example.photoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

class Album{
    String name;
    ArrayList<Photo> gallery;

    public Album(String name){
        this.name = name;
        gallery = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Photo> getGallery() {
        return gallery;
    }

    @Override
    public String toString() {
        return name;
    }
}
public class Albums extends AppCompatActivity {
    public static ArrayList<Album> albums;
    public static ListView namelist;
    private static final int ADD_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
        albums = new ArrayList<Album>();

        //加载相册名称
        File file = new File(Albums.this.getFilesDir(), "text");
        if(!file.exists()){
            file.mkdir();
        }
        File gpxfile = new File(file, "namelist");
        try {
        if (!gpxfile.exists()) {
            file.createNewFile();
            File directory = new File(Albums.this.getFilesDir(), "stock");

            if (!directory.exists()) {
                directory.mkdirs();
            }


                //albums.add(new Album("stock"));
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("stock\n");
                writer.flush();
                writer.close();
            }
        } catch (IOException e) { }





        InputStream ip = getResources().openRawResource(R.raw.namelist);



        //根据相册名称加载ListView
        try{

            //File gpxfile = new File(file, "namelist");
            Scanner myReader = new Scanner(gpxfile);
            BufferedReader brTest = new BufferedReader(new FileReader(gpxfile));
            String currentLine;
            while ((currentLine=brTest.readLine())!=null) {
                    //String data = brTest.readLine();
                albums.add(new Album(currentLine));
                System.out.println("Item: "+currentLine+"\n");
            }
        namelist = (ListView)findViewById(R.id.namelist);
        namelist.setAdapter(new ArrayAdapter<Album>(this,R.layout.album,albums));
        }
        catch (IOException e){

        }

        namelist.setOnItemClickListener((p, V, pos, id) ->
                openAlbum(pos));
    }


    //点击➕添加相册
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addAlbum();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void openAlbum(int pos) {
        Bundle bundle = new Bundle();
        Album album = albums.get(pos);
        bundle.putString(albumDisplay.ALBUM_NAME, album.name);
        bundle.putInt(albumDisplay.ALBUM_INDEX,pos);
        Intent intent = new Intent(this, albumDisplay.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
    }
    private void addAlbum() {
        Bundle bundle = new Bundle();
        StringBuilder albumList = null;
        for(Album a:albums){
            System.out.println("current name loading:"+a.name);
            if(albumList == null){
                albumList = new StringBuilder(a.name + "\n");
            }
            else {
                albumList.append(a.name).append("\n");
            }
        }
        if(albumList == null){
            Intent intent = new Intent(this, AddAlbum.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, ADD_ALBUM);
        }
        else {
            bundle.putString(AddAlbum.ALBUM_NAME, albumList.toString());
            Intent intent = new Intent(this, AddAlbum.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, ADD_ALBUM);
        }
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            return;
        }


        if(resultCode == albumDisplay.ALBUM_DELETE){
            Bundle bundle = intent.getExtras();
            String name = bundle.getString(albumDisplay.ALBUM_NAME);
            albums.removeIf(n->n.name.equals(name));
            //namelist = (ListView)findViewById(R.id.namelist);
            namelist.setAdapter(new ArrayAdapter<Album>(this,R.layout.album,albums));
            File inputFile = new File(Albums.this.getFilesDir(), "text");

            File input = new File(inputFile, "namelist");
            File output = new File(inputFile, "temp");
            try {
            BufferedReader reader = null;

                reader = new BufferedReader(new FileReader(input));

            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            String lineToRemove = name;
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean successful = output.renameTo(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(resultCode == RESULT_OK){
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            // gather all info passed back by launched activity
            String name = bundle.getString(AddAlbum.ALBUM_NAME);



            albums.add(new Album(name));

            File directory = new File(Albums.this.getFilesDir(), name);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            //write new album in namelist
            FileOutputStream fileout= null;
            File file = new File(Albums.this.getFilesDir(), "text");


            try {
                File gpxfile = new File(file, "namelist");
                FileWriter writer = new FileWriter(gpxfile,true);
                writer.append(name+"\n");
                writer.flush();
                writer.close();
            } catch (Exception e) { }


            // redo the adapter to reflect change^K
            namelist.setAdapter(
                    new ArrayAdapter<Album>(this, R.layout.album, albums));
        }
        }

}