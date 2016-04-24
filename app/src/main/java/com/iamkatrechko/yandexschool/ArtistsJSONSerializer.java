package com.iamkatrechko.yandexschool;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Muxa on 07.04.2016.
 */
public class ArtistsJSONSerializer {
    private static ArtistsJSONSerializer sArtistsJSON;
    private Context mContext;
    private ArrayList<Artist> mArtists = new ArrayList<>();

    public ArtistsJSONSerializer(Context c) {
        mContext = c;
    }

    public static ArtistsJSONSerializer get(Context c) {
        if (sArtistsJSON == null) {
            sArtistsJSON = new ArtistsJSONSerializer(c.getApplicationContext());
        }
        return sArtistsJSON;
    }

    public ArrayList<Artist> loadArtistsList(){
        ParseTask parser = new ParseTask();
        parser.execute();
        return null;
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                //URL url = new URL("http://androiddocs.ru/api/friends.json");
                URL url = new URL("http://cache-default05h.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            // выводим целиком полученную json-строку
            Log.d("Строка: ", jsonString);

            JSONObject dataJsonObj = null;
            String secondName = "";

            try {
                /*dataJsonObj = new JSONObject(jsonString);
                JSONArray friends = dataJsonObj.getJSONArray("friends");

                // 1. достаем инфо о втором друге - индекс 1
                JSONObject secondFriend = friends.getJSONObject(1);
                secondName = secondFriend.getString("name");
                Log.d("фыв", "Второе имя: " + secondName);

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject friend = friends.getJSONObject(i);

                    JSONObject contacts = friend.getJSONObject("contacts");

                    String phone = contacts.getString("mobile");
                    String email = contacts.getString("email");
                    String skype = contacts.getString("skype");

                    Log.d("1", "phone: " + phone);
                    Log.d("1", "email: " + email);
                    Log.d("1", "skype: " + skype);
                }*/
                JSONArray array = (JSONArray) new JSONTokener(jsonString)
                        .nextValue();

                for (int i = 0; i < array.length(); i++) {
                    mArtists.add(new Artist(array.getJSONObject(i)));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
