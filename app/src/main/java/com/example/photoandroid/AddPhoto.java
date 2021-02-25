package com.example.photoandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class AddPhoto extends AppCompatActivity {
    public static final String CURRENT_ALBUM = "currentalbum";
    public static final String PHOTO_NAME = "albumname";
    private int STORAGE_PERMISSION_CODE = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 10;
    public static final int PHOTO_ADD = 4;
    TextView pathString;
    Uri uri;
    private String current_album;
    private String exist_photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Add Photo");
        pathString = (TextView)findViewById(R.id.photo_path);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            exist_photos = bundle.getString(PHOTO_NAME);
            current_album = bundle.getString(CURRENT_ALBUM);
        }
        System.out.println("\ncurrent album I'm in: "+ current_album);




    }
    public void browsePhoto(View view) {
        //open file chooser and let user select photo
        Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("*/*");
        startActivityForResult(myFileIntent,10);

    }
    public void addPhoto(View view){
        String path = pathString.getText().toString();
        String state = Environment.getExternalStorageState();
        System.out.println("\n state:"+ state);
        if (ContextCompat.checkSelfPermission(AddPhoto.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(AddPhoto.this, "You have already granted this permission!",
            //       Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }

        if(path==null||path.length()==0){
            //path not specified
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "no file selected");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;


        }
        else{
            //check file type
            ContentResolver cR = this.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(uri));
            System.out.println("\nfile type: "+type);
            if(isPicture(type)){
                //file is an image, check duplicate
                //1.copy file to current directory
                //2.create .txt to store image data
                //3.add add photo instance to current album
//
                String uriString = uri.getPath();
                String filePath = "";
                try {


                    String wholeID = DocumentsContract.getDocumentId(uri);

// Split at colon, use second item in the array
                    String id = wholeID.split(":")[1];

                    String[] column = {MediaStore.Images.Media.DATA};

// where id is equal to
                    String sel = MediaStore.Images.Media._ID + "=?";

                    Cursor cursor = getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    column, sel, new String[]{id}, null);



                    int columnIndex = cursor.getColumnIndex(column[0]);

                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex);
                    }

                    cursor.close();
                }catch (IllegalArgumentException e){
                    Toast.makeText(AddPhoto.this, "file not found",
                            Toast.LENGTH_SHORT).show();
                }
                if(filePath.length()==0){
                    if(uriString.startsWith("/document/raw")){
                        uriString = uriString.substring(uriString.lastIndexOf(":")+1);
                    }
                    else if(uriString.startsWith("/document/primary")){

                        uriString = uriString.substring(uriString.lastIndexOf(":")+1);
                        uriString = "/sdcard/"+uriString;
                    }
                    filePath = uriString;
                }

                System.out.println("source path: "+filePath);

                File source = new File(filePath);
                System.out.println("file exists: "+ source.exists());
                File dest = new File(AddPhoto.this.getFilesDir(), current_album );
                File destination = new File(dest, source.getName() );
                String fileName = source.getName();
                System.out.println("fileName: "+ fileName);

                try {
//
                    Files.copy(source.toPath(), destination.toPath());

                    File info = new File(dest,fileName+".txt");
                    info.createNewFile();
                    FileWriter fw = new FileWriter(info,true);
                    BufferedWriter out = new BufferedWriter(fw);
                    out.write("NAME: "+fileName+"\n");
                    out.write("TAGS: \n");
                    out.close();
                    Bundle bundle = new Bundle();
                    bundle.putString(albumDisplay.PHOTO_PATH,destination.toPath().toString());
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(PHOTO_ADD,intent);
                    finish();
                }catch(NoSuchFileException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                        Toast.makeText(AddPhoto.this, "file not found",
                                Toast.LENGTH_SHORT).show();


                }catch (FileAlreadyExistsException e){
                    e.printStackTrace();
                    Toast.makeText(AddPhoto.this, "file already exists",
                            Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
            else{
                Bundle bundle = new Bundle();
                bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                        "please choose an image file");
                DialogFragment newFragment = new AlbumDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return;
            }


        }
    }
//
    public void cancelAddPhoto(View view){
        setResult(RESULT_CANCELED);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10){
            if(resultCode==RESULT_OK){
                String path = data.getData().getPath();
                System.out.println("file path:"+path);
                uri = data.getData();
                pathString.setText(path);
            }
        }
    }
    public boolean isPicture(String type){

        if(type.toLowerCase().endsWith("jpg")||
                type.toLowerCase().endsWith("jpeg")||
                type.toLowerCase().endsWith("png")||
                type.toLowerCase().endsWith("bmp")||
                type.toLowerCase().endsWith("gif")	) {
            return true;
        }

        return false;
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddPhoto.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}