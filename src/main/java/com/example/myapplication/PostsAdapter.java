package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postsList;

    public PostsAdapter(Context context, List<Post> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postsList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewContent.setText(post.getContent());

        if (post.getImageUrl() != null) {
            holder.imageViewPost.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getImageUrl()).into(holder.imageViewPost);
        } else {
            holder.imageViewPost.setVisibility(View.GONE);
        }

        if (post.getAddress() != null && post.getCity() != null && post.getState() != null && post.getCountry() != null) {
            String city =  "Location: " + post.getCity();
            holder.textViewLocation.setText(city);
        } else {
            String locationText = "Location: " + post.getLatitude() + ", " + post.getLongitude();
            holder.textViewLocation.setText(locationText);
        }
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        TextView textViewLocation;
        ImageView imageViewPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
        }
    }
}
