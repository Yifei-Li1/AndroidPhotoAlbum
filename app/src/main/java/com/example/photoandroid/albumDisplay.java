package com.example.photoandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class albumDisplay extends AppCompatActivity {
    private ThumbnailAdapter gridViewAdapter;
    public static final String ALBUM_NAME = "albumname";
    public static final String ALBUM_INDEX = "albumindex";
    public static final String FEEDBACK = "feedback";
    public static final String PHOTO_PATH = "photopath";
    public static final String DELETE_PHOTOINDEX = "deltephotoindex";
    public static final String DELETE_ALBUMINDEX = "deltealbumindex";
    public static final int DELETE_PHOTO = 5;
    public static final int ADD_PHOTO = 1;
    public static final int ALBUM_DELETE = 2;
    public static final int DISPLAY_PHOTO = 3;
    public static final int RENAME_ALBUM = 4;
    public static final int SEARCH_PHOTO = 5;

    ScrollView scrollView;
    GridView gridView;
    private String albumName;
    private int albumIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_display);
        //scrollView = findViewById(R.id.image_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        gridView = findViewById(R.id.gridView);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumName = bundle.getString(ALBUM_NAME);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }
        setTitle(albumName);
        Album currentAlbum =  Albums.albums.get(albumIndex);
        //preload all photos in the album

        if(currentAlbum.getGallery().size()==0) {


            File file = new File(albumDisplay.this.getFilesDir(), albumName);
            File[] fs = file.listFiles();
            if (fs != null) {
                for (File f : fs) {

                    if (!f.isDirectory()) {//扫面文件夹

                        if (isPicture(f)) { // 如果是照片 添加到preview中
                            String pathName = f.getPath();

                            System.out.println("photo path name: " + pathName);

                            currentAlbum.getGallery().add(new Photo(f));
                        }
                        else{
                            //image info txt
                            String photoName = "";
                            StringBuilder persons = new StringBuilder();
                            StringBuilder locations = new StringBuilder();
                            int index = 0;
//                            String photoObjectPath = f.getPath().substring(0,f.getPath().length()-4);
//                            File photoOb = new File(photoObjectPath);
//                            Photo tempPhoto = new Photo(photoOb);
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(f));
                                String currentLine;
                                while ((currentLine = reader.readLine()) != null) {
                                    //System.out.println(currentLine);
                                    if(currentLine.substring(0,currentLine.indexOf(" ")).equals("NAME:")){
                                        photoName = currentLine.substring(6);
                                        System.out.println("current scanned name: "+photoName);
                                        for(int i=0; i<Albums.albums.get(albumIndex).gallery.size(); i++) {
                                            if (Albums.albums.get(albumIndex).gallery.get(i).getTitle().equals(photoName)) {
                                                index = i;
                                            }
                                        }

                                    }
                                    if (currentLine.substring(0, currentLine.indexOf(" ")).equals("TAGS:")) {
                                        //System.out.println("I'm here");
                                        while ((currentLine = reader.readLine()) != null) {
                                            String name, value;

                                            name = currentLine.substring(0, currentLine.indexOf(" "));
                                            value = currentLine.substring(currentLine.indexOf(" ") + 1);
                                            System.out.println("Value: " + value);
                                            //System.out.println("Name: "+name);
                                            if (name.equals("PERSON")) {
                                                Albums.albums.get(albumIndex).gallery.get(index).addPersonTag(value);

                                            } else if (name.equals("LOCATION")) {
                                                Albums.albums.get(albumIndex).gallery.get(index).addLocationTag(value);
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
                    }
                }
            }

        }
        ThumbnailAdapter thumbnailAdapter =
                new ThumbnailAdapter(this, Albums.albums.get(albumIndex).getGallery());
        gridView.setAdapter(thumbnailAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayPhoto(position);
            }
        });

    }
    public void displayPhoto(int position){
        System.out.println(albumIndex);
        File file = Albums.albums.get(albumIndex).gallery.get(position).getImageFile();
        String filePath = file.getPath();
        Bundle bundle = new Bundle();
        bundle.putInt(photoDisplay.CURRENT_ALBUM,albumIndex);
        bundle.putInt(photoDisplay.PHOTO_INDEX,position);
        bundle.putString(photoDisplay.DISPLAY_PHOTO,filePath);
        Intent intent = new Intent (this, photoDisplay.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, DISPLAY_PHOTO);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteAlbum();
                return true;
            case R.id.action_rename:
                renameAlbum();
                return true;
            case R.id.action_addPhoto:
                addPhoto();
                return true;
            case R.id.action_searchPhoto:
                searchPhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void deleteAlbum(){

        File directory = new File(albumDisplay.this.getFilesDir(), albumName);
        if(directory.exists()){
            boolean hold = deleteDirectory(directory);
            System.out.println("if delete: "+hold);
        }
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME,albumName);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(ALBUM_DELETE,intent);
        finish();
    }
    public void renameAlbum(){

        Bundle bundle = new Bundle();
        System.out.println("current albumName: "+albumName);
        bundle.putString(renameAlbum.ALBUM_NAME,albumName);
        Intent intent = new Intent(this, renameAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RENAME_ALBUM);



    }
    public void searchPhoto(){
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME, albumName);
        bundle.putInt(ALBUM_INDEX, albumIndex);
        Intent intent = new Intent(this, searchPhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, SEARCH_PHOTO);
    }
    //通过file chooser添加PHOTO，
    public void addPhoto(){
        Album albumSeleted = Albums.albums.get(albumIndex);
        // pass existing photo name for dupulicate check

        Bundle bundle = new Bundle();
        StringBuilder titleList = null;
        for(Photo a:albumSeleted.gallery){
            System.out.println("current name loading:"+a.getTitle());
            if(titleList == null){
                titleList = new StringBuilder(a.getTitle() + "\n");
            }
            else {
                titleList.append(a.getTitle()).append("\n");
            }
        }
        //pass album name

        if(titleList == null){

        }
        else {
            bundle.putString(AddPhoto.PHOTO_NAME, titleList.toString());

        }
        bundle.putString(AddPhoto.CURRENT_ALBUM,albumSeleted.name);
        Intent intent = new Intent(this, AddPhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == AddPhoto.PHOTO_ADD) {
            Bundle bundle = intent.getExtras();
            String photoPath = bundle.getString(albumDisplay.PHOTO_PATH);
            File file = new File(photoPath);
            //System.out.println(file.exists());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            ImageView myImage = (ImageView) findViewById(R.id.image);

            //myImage.setImageBitmap(myBitmap);
            Albums.albums.get(albumIndex).gallery.add(new Photo(file));
            ThumbnailAdapter thumbnailAdapter =
                    new ThumbnailAdapter(this,Albums.albums.get(albumIndex).getGallery());
            gridView.setAdapter(thumbnailAdapter);
            System.out.println(photoPath);
        }
        if(resultCode == RESULT_OK){
            // 这里进行更改文件
            Bundle bundle = intent.getExtras();

            // gather all info passed back by launched activity
            String name = bundle.getString(renameAlbum.ALBUM_NAME);  // new name

            System.out.println("Old Album Name: "+albumName);
            System.out.println("New Album Name: "+name);
            //修改文件
            File temp = new File(albumDisplay.this.getFilesDir(), albumName);
            File dest = new File(albumDisplay.this.getFilesDir(), name);
            temp.renameTo(dest);
            //修改namelist.txt
            File inputFile = new File(albumDisplay.this.getFilesDir(), "text");
            File input = new File(inputFile, "namelist");  // input = nameList.txt
            File output = new File(inputFile, "temp");
            try {

                BufferedReader reader = new BufferedReader(new FileReader(input));

                BufferedWriter writer = new BufferedWriter(new FileWriter(output));

                String lineToRename = albumName;
                String currentLine;

                while((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if(trimmedLine.equals(lineToRename)) {
                        writer.write(name + System.getProperty("line.separator"));
                        continue;
                    };
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
                writer.close();
                reader.close();
                boolean successful = output.renameTo(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //修改当前显示  setTitle(new name)
            setTitle(name);
            //更新display list 设置albums层的adaptor/namelist
            for(int i=0; i<Albums.albums.size(); i++) {
                if (Albums.albums.get(i).getName().equals(albumName)) {
                    Albums.albums.get(i).setName(name);
                }
            }
            Albums.namelist.setAdapter(new ArrayAdapter<Album>(this,R.layout.album,Albums.albums));
            for(Photo p: Albums.albums.get(albumIndex).gallery){
                p.setImageFile(name);
            }


            return;

        }

        if(resultCode == DELETE_PHOTO){
            Bundle bundle = intent.getExtras();
            int photoIndex = bundle.getInt(DELETE_PHOTOINDEX);
            int albunIndex = bundle.getInt(DELETE_ALBUMINDEX);
            String deletePath =  Albums.albums.get(albunIndex).gallery.get(photoIndex).getImageFile().getPath();
            File fileToDelte = new File(deletePath);
            File txtFile = new File(deletePath+".txt");
            if(fileToDelte.exists()){
                fileToDelte.delete();
                txtFile.delete();
            }

            Albums.albums.get(albunIndex).gallery.remove(photoIndex);
            ThumbnailAdapter thumbnailAdapter =
                    new ThumbnailAdapter(this, Albums.albums.get(albumIndex).getGallery());
            gridView.setAdapter(thumbnailAdapter);
            return;
        }
        if(resultCode == photoDisplay.MOVE_PHOTO){

        }



    }
    public boolean isPicture(File temp) {

        String fileName = temp.getName();
        if(fileName.toLowerCase().endsWith(".jpg")||
                fileName.toLowerCase().endsWith(".jpeg")||
                fileName.toLowerCase().endsWith(".png")||
                fileName.toLowerCase().endsWith(".bmp")||
                fileName.toLowerCase().endsWith(".gif")	) {
            return true;
        }

        return false;
    }
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        return directoryToBeDeleted.delete();
    }
}