package com.example.photoandroid;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;

public class AddAlbum extends AppCompatActivity {
    private EditText albumname;
    private String nameList;
    public static final String ALBUM_NAME = "albumname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        albumname = findViewById(R.id.album_name);
        //get the album name list to check if same name appear
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nameList = bundle.getString(ALBUM_NAME);

        }
    }
    public void create(View view){
        String name = albumname.getText().toString();
        if(name==null||name.length()==0){
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name is required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }
        System.out.println(nameList);
        if(nameList!=null) {
            String[] split = nameList.split("\n");
        //check duplicate

            for(String a:split) {
                if (a.equals(name)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                            "Album already exists");
                    DialogFragment newFragment = new AlbumDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                    return;
                }
            }
        }


        //make a bundle
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME, name);


        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to parent

    }
    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}