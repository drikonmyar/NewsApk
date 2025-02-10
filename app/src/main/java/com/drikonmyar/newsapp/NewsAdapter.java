package com.drikonmyar.newsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private String[] newsTexts;
    private String[] imageUrls;

    public NewsAdapter(String[] newsTexts, String[] imageUrls) {
        this.newsTexts = newsTexts;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Load image from URL using Glide
        Glide.with(holder.newsImage.getContext())
                .load(imageUrls[position])
                .placeholder(R.drawable.botcoder)  // Optional: Placeholder image while loading
                .error(R.drawable.botcoder)        // Optional: Error image
                .into(holder.newsImage);

        holder.newsText.setText(newsTexts[position]);
    }

    @Override
    public int getItemCount() {
        return newsTexts.length;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView newsText;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsText = itemView.findViewById(R.id.newsText);
        }
    }
}