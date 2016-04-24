package com.iamkatrechko.yandexschool;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Muxa on 07.04.2016.
 */
public class ArtistLab {
    private static ArtistLab sArtistLab;
    private Context mAppContext;
    private ArrayList<Artist> mArtists = new ArrayList<>();

    private ArtistLab(Context appContext) {
        mAppContext = appContext;
        mArtists = new ArrayList<>();
        //ArtistsJSONSerializer serializer = ArtistsJSONSerializer.get(appContext);
        //mArtists = serializer.loadArtistsList();
        //projectsJSONSerializer = ProjectsJSONSerializer.get(appContext);

        //loadProjectsFromJSON();
        //generateTestData();
    }

    public static ArtistLab get(Context c) {
        if (sArtistLab == null) {
            sArtistLab = new ArtistLab(c.getApplicationContext());
        }
        return sArtistLab;
    }

    public ArrayList<Artist> parseJson(String jsonString){
        try {
            JSONArray array = (JSONArray) new JSONTokener(jsonString)
                    .nextValue();

            for (int i = 0; i < array.length(); i++) {
                mArtists.add(new Artist(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getArtists();
    }

    public ArrayList<Artist> getArtists(){
        return mArtists;
    }

    public Artist getArtistByID(int ID){
        for (Artist artist : mArtists){
            if (artist.getID() == ID){
                return artist;
            }
        }

        return null;
    }
}
