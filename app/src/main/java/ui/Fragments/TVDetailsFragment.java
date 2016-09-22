package ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.showbox.showbox.R;

import java.util.ArrayList;
import java.util.List;

import adapter.GalleryViewPagerAdapter;
import adapter.SimilarRecyclerViewAdapter;
import interfaces.ApiConstants;
import interfaces.TVApi;

import model.Movie.GenreDTO;
import model.TV.ResponseTVDTO;
import model.TV.ResponseTVGenresDTO;
import model.TV.ResponseTVImagesDTO;
import model.TV.TVDTO;
import model.TV.TVImageDTO;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import util.AppPreference;

/**
 * Created by Vlade Ilievski on 9/16/2016.
 */
public class TVDetailsFragment extends Fragment {
    public static final String TAG = TVDetailsFragment.class.getName();
    //VideoView video;
    public TVDTO tvShow = null;
    public TVDTO tvShowDetails = null;
    private List<TVDTO> simularTvShows = null;
    private List<TVDTO> items;
    TextView tvTitle;
    TextView genres;
    TextView episodeRunTime;
    TextView overviewtv;
    TextView rating_score;
    TextView seasonsnumber;
    TextView episodenumber;
    TextView onAirFirstTime;
    TVApi api;
    ProgressBar progressBar;
    GalleryViewPagerAdapter adapter;
    ViewPager viewpager;
    List<String> images = new ArrayList<>();
    ImageView rating;
    Context context = null;
    AppPreference preference;
    private RecyclerView recycler_view;
    ProgressBar progress_bar;
    private SimilarRecyclerViewAdapter similarRecyclerViewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b.containsKey("tv_dto")) {
            tvShow = (TVDTO) b.getSerializable("tv_dto");
        }
        preference = new AppPreference(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tv_details_activity, container, false);
        initVariables(v);

        return v;
    }

    private void initVariables(View v) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApiConstants.THEMOVIIEDB_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = restAdapter.create(TVApi.class);

        //video = (VideoView) v.findViewById(R.id.video);
        viewpager = (ViewPager) v.findViewById(R.id.viewpager);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        genres = (TextView) v.findViewById(R.id.genres);
        overviewtv = (TextView) v.findViewById(R.id.overviewtv);
        rating_score = (TextView) v.findViewById(R.id.rating_score);
        seasonsnumber = (TextView) v.findViewById(R.id.seasonsnumber);
        episodenumber = (TextView) v.findViewById(R.id.episodenumber);
        onAirFirstTime = (TextView) v.findViewById(R.id.onAirFirstTime);
        episodeRunTime = (TextView) v.findViewById(R.id.episodeRunTime);
        progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        adapter = new GalleryViewPagerAdapter(getContext(), images);
        recycler_view = (RecyclerView) v.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
//        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView myList = (RecyclerView) v.findViewById(R.id.recycler_view);
        myList.setLayoutManager(layoutManager);

        progress_bar.setVisibility(View.VISIBLE);
        viewpager.setAdapter(adapter);
        getTVGenres();
        getTVDetails(tvShow.getId());
        getTVImages(tvShow.getId());
        getTVSimular(tvShow.getId());
        //    Picasso.with(getContext()).load(ApiConstants.IMAGE_BASE_URL + tvShow.get(position).getPoster_path()).into(tvimages);

//        Uri videoPath = Uri.parse("");
//        video.setVideoURI(videoPath);
//        video.start();

        tvTitle.setText((tvShow.getName()));

        //  episodeRunTime.setText((tvShow.getEpisode_run_time())+"");
        overviewtv.setText((tvShow.getOverview()));
        rating_score.setText((tvShow.getVote_average() + ""));

        onAirFirstTime.setText(tvShow.getFirstAirDate());


    }

    private void initListeners() {
        recycler_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }


    private void initTvDetails(TVDTO details) {
        seasonsnumber.setText(details.getNumber_of_seasons() + "");
        episodenumber.setText(details.getNumber_of_episodes() + "");
        episodeRunTime.setText(details.getEpisode_run_time().toString());

        genres.setText(getGenres(details.getGenre()));

    }

    private String getGenres(GenreDTO[] genres) {
        StringBuilder builder = new StringBuilder();
        for (GenreDTO genre : genres) {
            builder.append(genre.getName() + ", ");
        }
        return builder.toString();
    }

    private void initSimilar(List<TVDTO> similar) {
        similarRecyclerViewAdapter = new SimilarRecyclerViewAdapter<TVDTO>(getContext(), R.layout.library_layout_item, similar, null);
        recycler_view.setAdapter(similarRecyclerViewAdapter);
    }

    private void getTVSimular(int tvId) {
        api.getSimilarTVShow(ApiConstants.API_KEY, tvId, new Callback<ResponseTVDTO>() {
            @Override
            public void success(ResponseTVDTO responseTVDTO, Response response) {
                Log.d(TAG, "success getting details from server " + responseTVDTO);
                simularTvShows = responseTVDTO.getTvshow();
                initSimilar(responseTVDTO.getTvshow());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "success getting details from server " + error);
            }
        });
    }


    private void getTVDetails(int tvId) {
        api.getTvDetails(ApiConstants.API_KEY, tvId, new Callback<TVDTO>() {
            @Override
            public void success(TVDTO responseTVDTO, Response response) {
                Log.d(TAG, "success getting details from server " + responseTVDTO);
                tvShowDetails = responseTVDTO;

                initTvDetails(responseTVDTO);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "success getting details from server " + error);
            }
        });
    }

    private void getTVImages(int tvId) {
        api.getTVImages(ApiConstants.API_KEY, tvId, new Callback<ResponseTVImagesDTO>() {
            @Override
            public void success(ResponseTVImagesDTO responseTVImagesDTO, Response response) {
                Log.d(TAG, "success getting images from server " + responseTVImagesDTO);
                getImagePaths(responseTVImagesDTO);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure getting images from server " + error);
            }
        });
    }

    private void getTVGenres() {
        api.getTvGenres(ApiConstants.API_KEY, new Callback<ResponseTVGenresDTO>() {
            @Override
            public void success(ResponseTVGenresDTO responseTVGenresDTO, Response response) {
                Log.d(TAG, "success getting genres from server " + responseTVGenresDTO);
                preference.saveTVGenres(responseTVGenresDTO.getGenres());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "success getting genres from server ");
            }
        });
    }

    private void getImagePaths(ResponseTVImagesDTO res) {
        for (TVImageDTO imgs : res.getImages()) {
            images.add(imgs.getFile_path());
        }
        adapter.notifyDataSetChanged();
    }


}

