package com.iamkatrechko.yandexschool;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ArtistInfoFragment extends Fragment {
    private int ID;

    public static ArtistInfoFragment newInstance(int ID){
        ArtistInfoFragment fragment = new ArtistInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ID", ID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        ID = getArguments().getInt("ID");                                                           //Получаем ID артиста из передаваемых данных
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artist_info, parent, false);

        Artist artist = ArtistLab.get(getActivity()).getArtistByID(ID);                             //Получаем экземпляр артиста
        ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar)).            //Устанавливаем название активности
                setTitle(artist.getName());

        ImageView artistImage = (ImageView) getActivity().findViewById(R.id.ivCover);               //Находим виджеты на экране по их id
        TextView tvGenres = (TextView) v.findViewById(R.id.tvGenres);
        TextView tvAlbums = (TextView) v.findViewById(R.id.tvAlbums);
        TextView tvTracks = (TextView) v.findViewById(R.id.tvTracks);
        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);

        String genres = "";                                                                         //Связываем список жанров в 1 строку
        for (int i = 0; i < artist.getGenres().length; i++){                                        //
            genres += artist.getGenres()[i];                                                        //
            if (i != artist.getGenres().length - 1) genres += ", ";                                 //
        }                                                                                           //
        tvGenres.setText(genres);                                                                   //Вывод информации о жанрах

        String albumsText = "";                                                                     //Склоняем падеж для вывода количества альбомов
        int remainder_10 = artist.getAlbums() % 10;                                                 //путем сравнения остатков от деления на 10 и 100
        int remainder_100 = artist.getAlbums() % 100;
        if (remainder_100 >= 11 && remainder_100 <= 14) albumsText = getString(R.string.text_albums_1, artist.getAlbums());
        else if (remainder_10 == 1) albumsText = getString(R.string.text_albums_2, artist.getAlbums());
        else if (remainder_10 >= 2 && remainder_10 <= 4) albumsText = getString(R.string.text_albums_3, artist.getAlbums());
        else albumsText = getString(R.string.text_albums_1, artist.getAlbums());

        tvAlbums.setText(albumsText);                                                               //Вывод количества альбомов

        String tracksText = "";
        int remainder_t_10 = artist.getTracks() % 10;
        int remainder_t_100 = artist.getTracks() % 100;
        if (remainder_t_100 >= 11 && remainder_t_100 <= 14) tracksText = getString(R.string.text_tracks_1, artist.getTracks());
        else if (remainder_t_10 == 1) tracksText = getString(R.string.text_tracks_2, artist.getTracks());
        else if (remainder_t_10 >= 2 && remainder_t_10 <= 4) tracksText = getString(R.string.text_tracks_3, artist.getTracks());
        else tracksText = getString(R.string.text_tracks_1, artist.getTracks());

        tvTracks.setText(tracksText);                                                               //Вывод количества треков

        tvDescription.setText(artist.getDescription());                                             //Вывод биографии

        Picasso.with(getActivity())                                                                 //Используем библиотеку Picasso
                .load(artist.getCoverBig())                                                         //для асинхронной загрузки изображения артиста,
                .into(artistImage);                                                                 //которое отобразится по окончанию загрузки

        return v;
    }
}
