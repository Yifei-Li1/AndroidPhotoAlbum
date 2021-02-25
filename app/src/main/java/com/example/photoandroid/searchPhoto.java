package com.example.photoandroid;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class searchPhoto extends AppCompatActivity {

    private EditText locationE;
    private EditText personE;
    private CheckBox andBox;
    private CheckBox orBox;
    private String nameList;
    private String location;
    private String person;
    private Boolean and;
    private Boolean or;
    private String searchInfo; //searchInfo -> 一个判断指令符号 and 还是 or 还是单一搜索 并且将指令符号传递到上层

    public static final String ALBUM_NAME = "albumname";
    public static final String SEARCH_PHOTO_Person = "searchByPerson";
    public static final String SEARCH_PHOTO_Location = "searchBylocation";
    public static final String SEARCH_PHOTO_Info = "searchByInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationE = findViewById(R.id.search_location_input);
        personE = findViewById(R.id.search_person_input);
        andBox = findViewById(R.id.andBox);
        orBox = findViewById(R.id.orBox);


    }
    public void create(View view){

        //make a bundle
        Bundle bundle = new Bundle();
        location = locationE.getText().toString();
        person = personE.getText().toString();
        and = andBox.isChecked();
        or = orBox.isChecked();



        if(person.length()==0 && location.length()==0){
            Toast.makeText(searchPhoto.this, "You need to search at least one tag",
                    Toast.LENGTH_SHORT).show();
            return;
        }else if(person.length()==0 || location.length()==0){
            if(and || or){
                Toast.makeText(searchPhoto.this, "Have to enter 2 tag for AND/OR searching",
                        Toast.LENGTH_SHORT).show();
                return;
            }else{
                searchInfo = "single";
            }

        }else if(location.length()!=0 && person.length()!=0 ){
            if(!and && !or){
                Toast.makeText(searchPhoto.this, "Have to choose a checkbox",
                        Toast.LENGTH_SHORT).show();
                return;
            }else if(and && or){
                Toast.makeText(searchPhoto.this, "Can only choose 1 checkbox",
                        Toast.LENGTH_SHORT).show();
                return;
            }else if(and && !or ) {
                searchInfo = "and";
            }else if(!and && or){
                searchInfo = "or";
            }

        }else{
            //return error dialogue;
            Toast.makeText(searchPhoto.this, "Searching error",
                    Toast.LENGTH_SHORT).show();
            return;
        }











        bundle.putString(SEARCH_PHOTO_Person, person);
        bundle.putString(SEARCH_PHOTO_Location, location);
        bundle.putString(SEARCH_PHOTO_Info, searchInfo);

        // send back to caller
        Intent intent = new Intent(this, searchResult.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
        //finish(); // pops activity from the call stack, returns to parent

    }
    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }
}