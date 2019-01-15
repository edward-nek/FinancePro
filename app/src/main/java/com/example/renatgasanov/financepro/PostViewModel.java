package com.example.renatgasanov.financepro;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.renatgasanov.financepro.db.Post;
import com.example.renatgasanov.financepro.db.PostDao;
import com.example.renatgasanov.financepro.db.PostsDatabase;

public class PostViewModel extends AndroidViewModel {

    private PostDao postDao;
    private ExecutorService executorService;

    public PostViewModel(@NonNull Application application) {
        super(application);
        postDao = PostsDatabase.getInstance(application).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    LiveData<List<Post>> getAllPosts() {
        return postDao.findAll();
    }

    Post getPostById(int index){
        return postDao.findById(index);
    }

    void savePost(Post post) {
        executorService.execute(() -> postDao.save(post));
    }

    void deletePost(Post post) {
        executorService.execute(() -> postDao.delete(post));
    }
}
