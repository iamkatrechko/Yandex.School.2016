package com.iamkatrechko.yandexschool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ParseJSON mParseJSON;

    ArrayList<Artist> mArtists = new ArrayList<>();
    RecyclerView recyclerView;
    ImageView ivDownload;
    LinearLayout linearDownload;
    public TasksAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParseJSON = new ParseJSON();
        mParseJSON.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ivDownload = (ImageView) v.findViewById(R.id.ivDownload);
        linearDownload = (LinearLayout) v.findViewById(R.id.linear_download);
        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        startAnimation();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mParseJSON.cancel(true);
    }

    public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>{
        public Context aContext;
        private ArrayList<Artist> mArtists = new ArrayList<>();

        public TasksAdapter(ArrayList<Artist> list, Context context) {
            mArtists = list;
            aContext = context;
        }

        @Override
        public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View recyclerView = inflater.inflate(R.layout.recycler_task_item, parent, false);

            return new ViewHolder(recyclerView);
        }

        @Override
        public void onBindViewHolder(TasksAdapter.ViewHolder vHolder, int position) {
            final Artist artist = mArtists.get(position);
            vHolder._id = artist.getID();

            vHolder.tvName.setText(artist.getName());

            String genres = "";
            for (int i = 0; i < artist.getGenres().length; i++){
                genres += artist.getGenres()[i];
                if (i != artist.getGenres().length - 1) genres += ", ";
            }

            vHolder.tvGenre.setText(genres);

            String albumsText = "";
            int remainder_10 = artist.getAlbums() % 10;
            int remainder_100 = artist.getAlbums() % 100;
            if (remainder_100 >= 11 && remainder_100 <= 14) albumsText = getString(R.string.text_albums_1, artist.getAlbums());
            else if (remainder_10 == 1) albumsText = getString(R.string.text_albums_2, artist.getAlbums());
            else if (remainder_10 >= 2 && remainder_10 <= 4) albumsText = getString(R.string.text_albums_3, artist.getAlbums());
            else albumsText = getString(R.string.text_albums_1, artist.getAlbums());
            vHolder.tvAlbums.setText(albumsText);

            String tracksText = "";
            int remainder_t_10 = artist.getTracks() % 10;
            int remainder_t_100 = artist.getTracks() % 100;
            if (remainder_t_100 >= 11 && remainder_t_100 <= 14) tracksText = getString(R.string.text_tracks_1, artist.getTracks());
            else if (remainder_t_10 == 1) tracksText = getString(R.string.text_tracks_2, artist.getTracks());
            else if (remainder_t_10 >= 2 && remainder_t_10 <= 4) tracksText = getString(R.string.text_tracks_3, artist.getTracks());
            else tracksText = getString(R.string.text_tracks_1, artist.getTracks());
            vHolder.tvTracks.setText(tracksText);

            Picasso.with(aContext)
                    .load(artist.getCoverSmall())
                    .placeholder(R.drawable.ic_profile)
                    .into(vHolder.imageView);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public int _id;
            public TextView tvName;                                                                 //TextView с именем исполнителя
            public TextView tvGenre;                                                                //TextView с жанрами
            public TextView tvAlbums;
            public TextView tvTracks;
            public ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.name);
                tvGenre = (TextView) itemView.findViewById(R.id.genre);
                tvAlbums = (TextView) itemView.findViewById(R.id.albums);
                tvTracks = (TextView) itemView.findViewById(R.id.tracks);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);

                itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(aContext, ArtistInfoActivity.class);
                        intent.putExtra("ID", _id);
                        aContext.startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.act_slide_down_in, R.anim.act_slide_down_out);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mArtists.size();
        }
    }

    private class ParseJSON extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://cache-default05h.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

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
            ArtistLab lab = ArtistLab.get(getActivity().getApplicationContext());
            mArtists = lab.parseJson(jsonString);

            adapter = new TasksAdapter(mArtists, getActivity());
            recyclerView.setAdapter(adapter);

            recyclerView.setVisibility(View.VISIBLE);
            linearDownload.setVisibility(View.GONE);
            ivDownload.clearAnimation();
        }
    }

    /***
     * Отображает анимацию загрузки
     */
    private void startAnimation(){
        RotateAnimation rotate = new RotateAnimation (-10, 10,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f); //2
        rotate.setDuration(1500); //3
        rotate.setRepeatMode(Animation.REVERSE); //4
        rotate.setRepeatCount(-1); //5

        ivDownload.startAnimation(rotate);
    }
}
