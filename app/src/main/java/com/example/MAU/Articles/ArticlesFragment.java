package com.example.MAU.Articles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.Notes.AddNoteActivity;
import com.example.MAU.Notes.EditNoteActivity;
import com.example.MAU.Notes.NoteManager;
import com.example.MAU.R;
import com.example.MAU.models.Articles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArticlesFragment extends Fragment implements ArticleAdapter.OnArticleClickListener {

    ImageButton addArticleButton;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewArticles;
    private ArticleAdapter articleAdapter;
    private ProgressBar progressBarArticles;
    private ArticlesManager articleManager;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleAdapter.setOnArticleClickListener(this);
    }

    @Override
    public void onArticleClick(int position) {
    }

    @Override
    public void onArticleLongClick(int position) {
        View itemView = recyclerViewArticles.findViewHolderForAdapterPosition(position).itemView;
        showPopupMenu(itemView, position);
    }
    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.context_menu_update_delete);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                Articles currentArticleEdit = articleAdapter.getArticles().get(position);

                Intent intent = new Intent(getActivity(), EditArticleActivity.class);

                intent.putExtra("article_id", currentArticleEdit.getArticle_id());
                intent.putExtra("title", currentArticleEdit.getTitle());
                intent.putExtra("description", currentArticleEdit.getDescription());
                intent.putExtra("articleText", currentArticleEdit.getArticleText());
                intent.putExtra("photo_URL", currentArticleEdit.getPhoto_URL());

                startActivity(intent);

                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                Articles currentArticleDelete = articleAdapter.getArticles().get(position);
                articleManager.deleteArticle(currentArticleDelete.getArticle_id());
                loadArticles();
                return true;
            } else {
                return false;
            }
        });

        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);


        db = FirebaseFirestore.getInstance();

        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles);
        addArticleButton = view.findViewById(R.id.addArticleButton);
        recyclerViewArticles.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBarArticles = view.findViewById(R.id.progressBarArticles);

        articleAdapter = new ArticleAdapter(getContext(), new ArrayList<>());
        recyclerViewArticles.setAdapter(articleAdapter);

        articleManager = new ArticlesManager();

        loadArticles();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(Objects.equals(currentUser.getDisplayName(), "Administrator")){
            addArticleButton.setVisibility(View.VISIBLE);
            addArticleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AddArticleActivity.class));
                    getActivity().finish();
                }
            });
        } else addArticleButton.setVisibility(View.GONE);

        return view;
    }
    private void loadArticles() {

        db.collection("articles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Articles> articlesList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            progressBarArticles.setVisibility(View.VISIBLE);
                            Articles article = document.toObject(Articles.class);
                            if (article != null) {
                                article.setArticle_id(document.getId());
                                articlesList.add(article);
                                progressBarArticles.setVisibility(View.VISIBLE);
                            }
                        }
                        progressBarArticles.setVisibility(View.GONE);
                        articleAdapter.setArticles(articlesList);
                    } else {
                        progressBarArticles.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Ошибка загрузки статей", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadArticles();
    }

}