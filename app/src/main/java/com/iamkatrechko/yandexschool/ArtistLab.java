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
    }

    public static ArtistLab get(Context c) {
        if (sArtistLab == null) {
            sArtistLab = new ArtistLab(c.getApplicationContext());
        }
        return sArtistLab;
    }

    /***
     * Обрабатывает загруженную строку JSON и наполняет список артистов.
     * @param jsonString JSON строка
     * @return Список артистов
     */
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

    /***
     * Возвращает артиста {@link Artist} по его ID
     * @param ID ID искомого артиста
     * @return Артист {@link Artist}
     */
    public Artist getArtistByID(int ID){
        for (Artist artist : mArtists){
            if (artist.getID() == ID){
                return artist;
            }
        }

        return null;
    }
}
