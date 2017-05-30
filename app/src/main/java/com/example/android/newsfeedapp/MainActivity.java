package com.example.android.newsfeedapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    /**
     * URL for book data from the GUARDIAN NEWS dataset
     */
    private final String URL = "http://content.guardianapis.com/search?q=android";
    private final String URL_CONTENT = "&show-fields=thumbnail&show-references=author";
    private final String URL_KEY = "&api-key=test";

    private static int NEWS_LOADER_ID = 1;

    private NewsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout swipe;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(this);

        // Find a reference to the {@link RecyclerView} in the layout
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        // First, hide loading indicator so error message will be visible
        mLoadingIndicator.setVisibility(View.GONE);


        // Create a new adapter that takes an empty list of book as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(News news) {
                String url = news.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        mRecyclerView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            android.app.LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mLoadingIndicator.setVisibility(View.GONE);

            mRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(GONE);

        String query = URL + URL_CONTENT + URL_KEY;

        Uri baseUri = Uri.parse(query);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        return new NewsLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }
}
