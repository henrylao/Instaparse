package com.example.instaparse.adapters;

//import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instaparse.R;
import com.example.instaparse.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // view elements
        private TextView tvItemUsername;
        private TextView tvItemDescription;
        private ImageView ivItemPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemUsername = itemView.findViewById(R.id.tvItemUsername);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            ivItemPostImage = itemView.findViewById(R.id.ivItemPostImage);
        }

        public void bind(Post post){
            // Bind data of post to the view element
            tvItemUsername.setText(post.getUser().getUsername());
            tvItemDescription.setText(post.getDescription());
            ParseFile image = post.getImage();
            if (image != null)
                Glide.with(context).load(image.getUrl()).into(ivItemPostImage);
//            ivItemPostImage.
        }
    }

}
