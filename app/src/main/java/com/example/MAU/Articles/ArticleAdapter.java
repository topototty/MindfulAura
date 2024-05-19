package com.example.MAU.Articles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.R;
import com.example.MAU.models.Articles;
import com.example.MAU.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<Articles> articles;
    public interface OnArticleClickListener {
        void onArticleClick(int position);
        void onArticleLongClick(int position);
    }
    private OnArticleClickListener mListener;

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        mListener = listener;
    }
    public ArticleAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    public List<Articles> getNotes() {
        return articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Articles currentArticle = articles.get(position);

        holder.textViewTitle.setText(currentArticle.getTitle());
        holder.textViewDescription.setText(currentArticle.getDescription());

        Picasso.get().load(currentArticle.getPhoto_URL()).into(holder.imageViewPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onArticleClick(position);

                    Intent intent = new Intent(context, ArticleDetailsActivity.class);

                    intent.putExtra("title", currentArticle.getTitle());
                    intent.putExtra("description", currentArticle.getDescription());
                    intent.putExtra("articleText", currentArticle.getArticleText());
                    intent.putExtra("photo_URL", currentArticle.getPhoto_URL());

                    context.startActivity(intent);
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(Objects.equals(currentUser.getDisplayName(), "Administrator")){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.onArticleLongClick(position);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewPhoto;
        TextView textViewTitle;
        TextView textViewDescription;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        }
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public List<Articles> getArticles() {
        return articles;
    }

}
