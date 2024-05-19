package com.example.MAU.Articles;

import android.util.Log;

import com.example.MAU.models.Articles;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
public class ArticlesManager {


    private final String COLLECTION_NAME = "articles";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference articlesCollection = db.collection(COLLECTION_NAME);

    // Получение всех статей
    public void getAllArticles(final OnDataRetrievedListener listener) {
        List<Articles> articlesList = new ArrayList<>();
        articlesCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Articles article = snapshot.toObject(Articles.class);
                        article.setArticle_id(snapshot.getId());
                        articlesList.add(article);
                    }
                    listener.onArticlesRetrieved(articlesList);
                })
                .addOnFailureListener(e -> {
                    Log.e("ArticlesManager", "Error getting documents: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    // Добавление новой статьи
    public void addArticle(Articles article) {
        articlesCollection.document()
                .set(article)
                .addOnSuccessListener(aVoid -> Log.d("ArticlesManager", "Article added successfully"))
                .addOnFailureListener(e -> Log.e("ArticlesManager", "Error adding article: " + e.getMessage()));
    }

    // Обновление существующей статьи
    public void updateArticle(Articles article) {
        articlesCollection.document(article.getArticle_id())
                .set(article)
                .addOnSuccessListener(aVoid -> Log.d("ArticlesManager", "Article updated successfully"))
                .addOnFailureListener(e -> Log.e("ArticlesManager", "Error updating article: " + e.getMessage()));
    }

    // Удаление статьи
    public void deleteArticle(String articleId) {
        articlesCollection.document(articleId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("ArticlesManager", "Article deleted successfully"))
                .addOnFailureListener(e -> Log.e("ArticlesManager", "Error deleting article: " + e.getMessage()));
    }

    public interface OnDataRetrievedListener {
        void onArticlesRetrieved(List<Articles> articles);
        void onError(String errorMessage);
    }

    public void getCurrentArticle(String articleId, final OnArticleRetrievedListener listener) {
        articlesCollection.document(articleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Articles article = documentSnapshot.toObject(Articles.class);
                    if (article != null) {
                        article.setArticle_id(documentSnapshot.getId());
                        listener.onArticleRetrieved(article);
                    } else {
                        listener.onError("Article not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ArticlesManager", "Error getting current article: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    public interface OnArticleRetrievedListener {
        void onArticleRetrieved(Articles article);
        void onError(String errorMessage);
    }
}
