package com.iamkatrechko.yandexschool;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

public class MainActivityFragment extends Fragment {

    /*** Класс {@link ParseJSON}, который является асинхронной задачей AsyncTask */
    private ParseJSON mParseJSON;

    /*** Список артистов */
    private ArrayList<Artist> mArtists = new ArrayList<>();
    /*** Виджет для отображения списка артистов */
    private RecyclerView recyclerView;
    /*** Картинка загрузки */
    private ImageView ivDownload;
    /*** Текст загрузки */
    private TextView tvDownload;
    /*** Область с картинкой и текстом загрузки */
    private LinearLayout linearDownload;
    /*** Адаптер для отображения списка артистов */
    private ArtistsAdapter adapter;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParseJSON = new ParseJSON();
        mParseJSON.execute();                                                                       //Запускаем асинхронную задачу (скачку JSON файла)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ivDownload = (ImageView) v.findViewById(R.id.ivDownload);                                   //Находим виджеты на экране по их id
        tvDownload = (TextView) v.findViewById(R.id.tvDownload);                                    //
        linearDownload = (LinearLayout) v.findViewById(R.id.linear_download);                       //
        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);                            //

        recyclerView.setHasFixedSize(true);                                                         //Делаем размер виджета списка фиксированным
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        startAnimation();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mParseJSON.cancel(true);                                                                    //Отменяем загрузку при скрытии экрана
    }

    public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder>{
        public Context aContext;
        /*** Список артистов */
        private ArrayList<Artist> mArtists = new ArrayList<>();

        public ArtistsAdapter(ArrayList<Artist> list, Context context) {
            mArtists = list;
            aContext = context;
        }

        @Override
        public ArtistsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View recyclerView = inflater.inflate(R.layout.recycler_task_item, parent, false);

            return new ViewHolder(recyclerView);
        }

        @Override
        public void onBindViewHolder(ArtistsAdapter.ViewHolder vHolder, int position) {
            final Artist artist = mArtists.get(position);                                           //Получаем экземпляр артиста
            vHolder._id = artist.getID();                                                           //Записываем его ID в viewHolder
            vHolder.tvName.setText(artist.getName());                                               //Выводим имя артиста

            String genres = "";                                                                     //Связываем список жанров в 1 строку
            for (int i = 0; i < artist.getGenres().length; i++){                                    //
                genres += artist.getGenres()[i];                                                    //
                if (i != artist.getGenres().length - 1) genres += ", ";                             //
            }

            vHolder.tvGenre.setText(genres);                                                        //Выводим список жанров

            String albumsText = "";                                                                 //Склоняем падеж для вывода количества альбомов
            int remainder_10 = artist.getAlbums() % 10;                                             //путем сравнения остатков от деления на 10 и 100
            int remainder_100 = artist.getAlbums() % 100;
            if (remainder_100 >= 11 && remainder_100 <= 14) albumsText = getString(R.string.text_albums_1, artist.getAlbums());
            else if (remainder_10 == 1) albumsText = getString(R.string.text_albums_2, artist.getAlbums());
            else if (remainder_10 >= 2 && remainder_10 <= 4) albumsText = getString(R.string.text_albums_3, artist.getAlbums());
            else albumsText = getString(R.string.text_albums_1, artist.getAlbums());

            vHolder.tvAlbums.setText(albumsText);                                                   //Выводим количество альбомов

            String tracksText = "";
            int remainder_t_10 = artist.getTracks() % 10;
            int remainder_t_100 = artist.getTracks() % 100;
            if (remainder_t_100 >= 11 && remainder_t_100 <= 14) tracksText = getString(R.string.text_tracks_1, artist.getTracks());
            else if (remainder_t_10 == 1) tracksText = getString(R.string.text_tracks_2, artist.getTracks());
            else if (remainder_t_10 >= 2 && remainder_t_10 <= 4) tracksText = getString(R.string.text_tracks_3, artist.getTracks());
            else tracksText = getString(R.string.text_tracks_1, artist.getTracks());

            vHolder.tvTracks.setText(tracksText);                                                   //Выводим количество треков

            Picasso.with(aContext)                                                                  //Используем библиотеку Picasso
                    .load(artist.getCoverSmall())                                                   //для асинхронной загрузки изображения артиста,
                    .placeholder(R.drawable.ic_profile)                                             //которое отобразится по окончанию загрузки
                    .into(vHolder.imageView);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public int _id;                                                                         //ID для каждого экземпляра ViewHolder
            public TextView tvName;                                                                 //TextView с именем исполнителя
            public TextView tvGenre;                                                                //TextView с жанрами
            public TextView tvAlbums;                                                               //TextView с количеством альбомов
            public TextView tvTracks;                                                               //TextView с количеством треков
            public ImageView imageView;                                                             //Изображение исполнителя

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.name);                               //Находим виджеты на элементах списка
                tvGenre = (TextView) itemView.findViewById(R.id.genre);                             //по их id
                tvAlbums = (TextView) itemView.findViewById(R.id.albums);
                tvTracks = (TextView) itemView.findViewById(R.id.tracks);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);

                //Назначаем слушателя на нажатие по карточке исполнителя
                itemView.findViewById(R.id.card_view_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(aContext, ArtistInfoActivity.class);             //Создаем intent с активностью информации об исполнителе
                        intent.putExtra("ID", _id);                                                 //Передаем ID исполнителя
                        aContext.startActivity(intent);                                             //Запускаем активность
                        //Применяем анимацию перехода
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
        //Для соединения с веб-серверами Android предлагает несколько способов взаимодействия.
        //В новых проектах для современных устройств рекомендуется использовать класс HttpURLConnection
        HttpURLConnection urlConnection = null;

        //Класс BufferedReader увеличивает производительность за счёт буферизации ввода.
        /*** Буферизированный входной символьный поток */
        BufferedReader reader = null;
        /*** Строка с конечным результатом запроса */
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            //Получаем данные с внешнего ресурса
            try {
                URL url = new URL("http://cache-default05h.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

                urlConnection = (HttpURLConnection) url.openConnection();                           //Получаем объект HttpURLConnection
                urlConnection.setRequestMethod("GET");                                              //Устанавливаем тип запроса
                urlConnection.connect();                                                            //Подключаемся (делаем запрос)

                InputStream inputStream = urlConnection.getInputStream();                           //Записываем данные из потока
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();                                                     //Переводим конечный результат в строку
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;                                                                      //Возвращаем строку методу onPostExecute
        }

        /***
         * Вызывается при завершении работы потока AsyncTask
         * @param jsonString JSON строка с конечным результатом
         */
        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            if (jsonString.length() != 0) {                                                         //Если полученная JSON строка не пуста
                ArtistLab lab = ArtistLab.get(getActivity().getApplicationContext());               //Создаем экземпляр контроллера ProjectLab
                mArtists = lab.parseJson(jsonString);                                               //Отправляем строку JSON на обработку и получаем список исполнителей

                adapter = new ArtistsAdapter(mArtists, getActivity());                              //После получения артистов создаем экземпляр адаптера списка
                recyclerView.setAdapter(adapter);                                                   //Привязываем адаптер к виджету списка RecyclerView

                recyclerView.setVisibility(View.VISIBLE);                                           //Отображаем изначально скрытый виджет списка
                linearDownload.setVisibility(View.GONE);                                            //Скрываем виджеты с информацией о загрузке
                ivDownload.clearAnimation();                                                        //Останавливаем анимацию загрузки
            }else{
                tvDownload.setText(getContext().getResources().getText(R.string.download_error_text));                                              //Выводим информацию о неудачной попытке загрузки
            }
        }
    }

    /***
     * Запускает анимацию загрузки
     */
    private void startAnimation(){
        RotateAnimation rotate = new RotateAnimation (-10, 10,                                      //Создание анимации (поворот)
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f); //2
        rotate.setDuration(1500);                                                                   //Время полного проигрыша
        rotate.setRepeatMode(Animation.REVERSE);                                                    //Обратная анимация после основной
        rotate.setRepeatCount(-1);                                                                  //Бесконечный повтор

        ivDownload.startAnimation(rotate);
    }
}
