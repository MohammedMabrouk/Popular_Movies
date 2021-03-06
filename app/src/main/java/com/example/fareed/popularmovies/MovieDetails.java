package com.example.fareed.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.popularmovies.Models.ReviewsBean;
import com.example.fareed.popularmovies.Models.TrailersBean;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MovieDetails extends AppCompatActivity  {
    String TAG = "MOVIEDETAILSLOG", baseUrl = "http://api.themoviedb.org/3/movie/", APIKEY = "api_key="+BuildConfig.MOVIE_API_KEY;
    TextView textView_title, textView_rating, textView_overView, textView_date;
    ImageView image_poster;
    RatingBar ratingBar;
    int movieId;
    LinearLayout dynamicTrailersView, dynamicReviewsView;
    TrailersBean trailersBean;
    ReviewsBean mreviewBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getIntent().getStringExtra("title"));

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details, new MovieDetailsFragment())
                    .commit();
        }
//
//        Intent intent = getIntent();
//        String title = intent.getStringExtra("title");
//        actionBar.setTitle(title);
//        String date = intent.getStringExtra("date");
//        String poster = intent.getStringExtra("poster");
//        double rating = intent.getDoubleExtra("rating", 1.5);
//        Log.d(TAG, "onCreate: "+rating);
//        String overView = intent.getStringExtra("overView");
//        movieId = intent.getIntExtra("movieId", 1);
//        Log.d(TAG, "onCreate: movieId" + movieId);

//        //initialize views
//        image_poster = (ImageView) findViewById(R.id.image_poster);
//        Picasso.with(this).load(poster).into(image_poster);
//        textView_title = (TextView) findViewById(R.id.textView_title);
//        textView_title.setText(title);
//        textView_rating = (TextView) findViewById(R.id.textView_Rating);
//        textView_rating.setText(rating + " / 10.0");
//        textView_overView = (TextView) findViewById(R.id.textView_overView);
//        textView_overView.setText(overView);
//        textView_date = (TextView) findViewById(R.id.textView_date);
//        textView_date.setText(date);
//        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
//        ratingBar.setRating((float) rating);
////
//        findViewById(R.id.button_favorite).setOnClickListener(this);
//
//        dynamicReviewsView = (LinearLayout) findViewById(R.id.linearLayout_reviews);
//        dynamicTrailersView = (LinearLayout) findViewById(R.id.linearLayout_trailers);
//
//        findViewById(R.id.button_reviews).setOnClickListener(this);
//        findViewById(R.id.button_trailers).setOnClickListener(this);
    }

    private void getReviews() {
        final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, baseUrl + movieId + "/reviews?" + APIKEY, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                mreviewBean = gson.fromJson(new String(responseBody), ReviewsBean.class);
                final List<ReviewsBean.ResultsBean> results = mreviewBean.getResults();
                if (results.isEmpty()){
                    TextView textView = new TextView(MovieDetails.this);
                    textView.setText("No reviews yet!");
                    dynamicReviewsView.addView(textView);
                }
                for (int i = 0; i < results.size(); i++) {
                    TextView textViewAuthor = new TextView(MovieDetails.this);
                    textViewAuthor.setText(results.get(i).getAuthor());
                    textViewAuthor.setLayoutParams(lp);
                    textViewAuthor.setTextAppearance(MovieDetails.this, android.R.style.TextAppearance_Large);
                    dynamicReviewsView.addView(textViewAuthor);

                    TextView textViewContent = new TextView(MovieDetails.this);
                    textViewContent.setText(results.get(i).getContent());
                    textViewContent.setLayoutParams(lp);
                    textViewContent.setPadding(16, 8, 8,8);
                    textViewContent.setTextAppearance(MovieDetails.this, android.R.style.TextAppearance_Material_Body1);
                    dynamicReviewsView.addView(textViewContent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getTrailers() {
        final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, baseUrl + movieId + "/videos?" + APIKEY, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                trailersBean = gson.fromJson(new String(responseBody), TrailersBean.class);
                final List<TrailersBean.ResultsBean> results = trailersBean.getResults();
                for (int i = 0; i < results.size(); i++) {
                    Button btn = new Button(MovieDetails.this);
                    btn.setId(i + 1);
                    btn.setText(results.get(i).getName());
                    Log.d(TAG, "onSuccess: " + results.get(i).getName());
                    btn.setLayoutParams(lp);
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MovieDetails.this, view.getId() + "", Toast.LENGTH_SHORT).show();
                            results.get(finalI).getKey();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" +
                                        results.get(finalI).getKey())));
                            } catch (ActivityNotFoundException ex) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +
                                        results.get(finalI).getKey())));
                            }
                        }
                    });
                    dynamicTrailersView.addView(btn);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.button_favorite) {
//            addMovieToFav();
//        }
//        else if (view.getId() == R.id.button_trailers){
//            if (!trailersFlag) {
//                getTrailers();
//                trailersFlag = true;
//            }
//        }
//        else if (view.getId() == R.id.button_reviews){
//            if (!reviewsFlag) {
//                getReviews();
//                reviewsFlag = true;
//            }
//        }
//    }
//
//    private void addMovieToFav() {
//        Realm realm = Realm.getDefaultInstance();
//        try {
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    MovieDb movieDb = realm.createObject(MovieDb.class, movieId);
//                    movieDb.name= title;
//                    movieDb.poster = poster;
//                    movieDb.movieId = movieId;
//                    movieDb.rating = rating;
//                    movieDb.overView = overView;
//                    movieDb.date = date;
//                }
//            });
//            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
//        }
//        catch (RealmPrimaryKeyConstraintException exception){
//            Log.d(TAG, "saveMovieToDb: "+exception.toString());
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    RealmResults<MovieDb> result = realm.where(MovieDb.class).equalTo("movieId", movieId).findAll();
//                    result.deleteAllFromRealm();
//                }
//            });
//            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
//
//        }
//        realm.close();
//    }
}
