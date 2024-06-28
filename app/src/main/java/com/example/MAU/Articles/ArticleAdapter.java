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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<Articles> articles;
    private FirebaseUser currentUser;
    private boolean isAdmin;

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
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.isAdmin = "Administrator".equals(currentUser.getDisplayName());
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

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onArticleClick(position);
            }
            launchArticleDetails(currentArticle);
        });

        if (isAdmin) {
            holder.itemView.setOnLongClickListener(v -> {
                if (mListener != null) {
                    mListener.onArticleLongClick(position);
                    return true;
                }
                return false;
            });
        }
    }

    private void launchArticleDetails(Articles article) {
        Intent intent = new Intent(context, ArticleDetailsActivity.class);
        intent.putExtra("title", article.getTitle());
        intent.putExtra("description", article.getDescription());
        intent.putExtra("articleText", article.getArticleText());
        intent.putExtra("photo_URL", article.getPhoto_URL());
        context.startActivity(intent);
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
