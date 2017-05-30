package com.example.android.newsfeedapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    ArrayList<News> mNews;
    private static OnItemClickListener mListener;
    MainActivity mContext;

    public interface OnItemClickListener {
        void onItemClick(News news);
    }

    public NewsAdapter(MainActivity context, ArrayList<News> news, OnItemClickListener listener){
        mContext = context;
        mNews = news;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        News news = mNews.get(position);

        holder.newsTitleTextView.setText(news.getTitle());
        holder.newsAuthorTextView.setText(news.getAuthor());
        holder.newsDateTextView.setText(news.getDate());
        holder.newsSectionTextView.setText(news.getSection());

        //Picasso Library to convert the url from JSONObject imageLinks to a image(@thumbnail)
        Picasso.with(mContext).load(news.getImage()).placeholder(R.mipmap.ic_launcher).into(holder.newsImageView);

        holder.bind(mNews.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newsImageView;
        TextView newsTitleTextView;
        TextView newsAuthorTextView;
        TextView newsDateTextView;
        TextView newsSectionTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            newsImageView = (ImageView) itemView.findViewById(R.id.news_img);
            newsTitleTextView = (TextView) itemView.findViewById(R.id.news_title);
            newsAuthorTextView = (TextView) itemView.findViewById(R.id.news_author);
            newsDateTextView = (TextView) itemView.findViewById(R.id.news_date);
            newsSectionTextView = (TextView) itemView.findViewById(R.id.news_section);
        }

        public void bind(final News news, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(news);
                }
            });
        }
    }

    public void clear(){
        mNews.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<News> news){
        mNews.addAll(news);
        notifyDataSetChanged();
    }
}
