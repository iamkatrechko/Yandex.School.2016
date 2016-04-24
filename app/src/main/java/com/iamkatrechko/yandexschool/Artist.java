package com.iamkatrechko.yandexschool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Muxa on 07.04.2016.
 */
public class Artist {

    private int mID;
    private String mName;
    private String[] mGenres;
    private int mTracks;
    private int mAlbums;
    private String mLink;
    private String mDescription;
    private String mCoverSmall;
    private String mCoverBig;

    public Artist(JSONObject json) throws JSONException {
        mID = json.getInt("id");
        mName = json.getString("name");

        JSONArray array = json.getJSONArray("genres");
        mGenres = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            mGenres[i] = array.getString(i);
        }

        mTracks = json.getInt("tracks");
        mAlbums = json.getInt("albums");
        mLink = json.optString("link");
        mDescription = json.getString("description");

        JSONObject covers = json.getJSONObject("cover");
        mCoverSmall = covers.getString("small");
        mCoverBig = covers.getString("big");
    }


    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String[] getGenres() {
        return mGenres;
    }

    public int getTracks() {
        return mTracks;
    }

    public int getAlbums() {
        return mAlbums;
    }

    public String getLink() {
        return mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCoverSmall() {
        return mCoverSmall;
    }

    public String getCoverBig() {
        return mCoverBig;
    }
}
