package com.example.photoandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

public class searchResult extends AppCompatActivity {

    public static final String SEARCH_PHOTO_Person = "searchByPerson";
    public static final String SEARCH_PHOTO_Location = "searchBylocation";
    public static final String SEARCH_PHOTO_Info = "searchByInfo";

    GridView gridView;
    String person;
    String location;
    String searchInfo;
    Album temp = new Album("temp");
    public static final String ALBUM_INDEX = "albumindex";
    private int albumIndex;
    private Album currentAlbum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        gridView = findViewById(R.id.gridView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            person = bundle.getString(SEARCH_PHOTO_Person);
            location = bundle.getString(SEARCH_PHOTO_Location);
            searchInfo= bundle.getString(SEARCH_PHOTO_Info);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }
        setTitle("Search By Tag");
        currentAlbum = Albums.albums.get(albumIndex);

        //进行挑拣工作
        for(int i=0; i<currentAlbum.getGallery().size(); i++){
            Photo currentPhoto = currentAlbum.getGallery().get(i);
            //    System.out.println("help: "+currentPhoto.getPersonTag().toString());
            if(searchInfo.equals("single")){
                if(person.length()!=0){
                    //search only by person
                    for(int j=0; j<currentPhoto.getPersonTag().size(); j++){
                        String currentTagValue = currentPhoto.getPersonTag().get(j);
                        //System.out.println("currentTagValue: "+currentTagValue);
                        if(currentTagValue.contains(person)){

                            if(!isDuplicated(currentPhoto, temp)){
                                temp.getGallery().add(currentPhoto);
                                System.out.println("add one photo into temp");
                            }

                        }

                    }
                }else if(location.length()!=0){
                    //search only by location
                    for(int j=0; j<currentPhoto.getLocationTag().size(); j++){
                        String currentTagValue = currentPhoto.getLocationTag().get(j);
                        //System.out.println("currentTagValue: "+currentTagValue);
                        if(currentTagValue.contains(location)){
                            if(!isDuplicated(currentPhoto, temp)) {
                                temp.getGallery().add(currentPhoto);
                                System.out.println("add one photo into temp");
                            }
                        }
                    }
                }

            }else if(searchInfo.equals("and")){   // search by and/or must have two non-null value
                //search by location and person
                for(int j=0; j<currentPhoto.getPersonTag().size(); j++){
                    String currentTagValue = currentPhoto.getPersonTag().get(j);
                    //System.out.println("currentTagValue: "+currentTagValue);
                    if(currentTagValue.contains(person)){

                        //search by location only
                        for(int k=0; k<currentPhoto.getLocationTag().size(); k++){
                            String currentPhotoLocationTagValue = currentPhoto.getLocationTag().get(k);
                            if(currentPhotoLocationTagValue.contains(location)){
                                if(!isDuplicated(currentPhoto, temp)) {
                                    temp.getGallery().add(currentPhoto);
                                    System.out.println("add one photo into temp");
                                }
                            }
                        }


                    }

                }



            }else if(searchInfo.equals("or")){
                //search by location or person
                for(int j=0; j<currentPhoto.getPersonTag().size(); j++){
                    String currentTagValue = currentPhoto.getPersonTag().get(j);
                    //System.out.println("currentTagValue: "+currentTagValue);
                    if(currentTagValue.contains(person)){

                        if(!isDuplicated(currentPhoto, temp)){
                            temp.getGallery().add(currentPhoto);
                            System.out.println("add one photo into temp");
                        }

                    }

                }

                for(int j=0; j<currentPhoto.getLocationTag().size(); j++){
                    String currentTagValue = currentPhoto.getLocationTag().get(j);
                    //    System.out.println("currentTagValue: "+currentTagValue);
                    if(currentTagValue.contains(location)){
                        if(!isDuplicated(currentPhoto, temp)) {
                            temp.getGallery().add(currentPhoto);
                            System.out.println("add one photo into temp");
                        }
                    }
                }
            }

        }


        //展示挑拣完的照片
        ThumbnailAdapter thumbnailAdapter =
                new ThumbnailAdapter(this,temp.getGallery());
        gridView.setAdapter(thumbnailAdapter);






        helper();
    }


    public void helper(){

        if (person == null){
            System.out.println("person is null");
        }else{
            System.out.println("person: "+person);
        }


        if(location == null){
            System.out.println("location is null");
        }else{
            System.out.println("location: "+location);
        }


        System.out.println("searchInfo: "+searchInfo);
        System.out.println("currentAlbum: "+currentAlbum.getName());
        System.out.println("currentNumberOfPhoto: "+currentAlbum.getGallery().size());
        System.out.println("currentAlbum: "+temp.getName());
        System.out.println("currentNumberOfPhoto: "+temp.getGallery().size());

    }

    public boolean isDuplicated(Photo a, Album b){
        for(int i=0; i<b.getGallery().size(); i++){
            Photo currentPhoto = b.getGallery().get(i);

            if(a.getTitle().compareTo(currentPhoto.getTitle())==0){
                return true;
            }
        }

        return false;
    }


}