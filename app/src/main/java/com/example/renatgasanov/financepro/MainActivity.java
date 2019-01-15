package com.example.renatgasanov.financepro;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.renatgasanov.financepro.db.Post;

public class MainActivity extends AppCompatActivity implements PostsAdapter.OnDeleteButtonClickListener {

    private PostsAdapter postsAdapter;
    private PostViewModel postViewModel;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOTALSPENT = "totalspent";
    public static final String APP_PREFERENCES_TOTALEARNED = "totalearned";
    private SharedPreferences mSettings;
    public Integer totalSpent;
    public Integer totalEarned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_green));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_green));

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        postsAdapter = new PostsAdapter(this, this);

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        postViewModel.getAllPosts().observe(this, posts -> postsAdapter.setData(posts));

        RecyclerView recyclerView = findViewById(R.id.rvPostsLis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postsAdapter);

        totalSpent = 0;
        totalEarned = 0;
        updateTextView(Integer.toString(totalSpent));
        updateTotalEarned(Integer.toString(totalEarned));
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addPost) {
            //postViewModel.savePost(new Post("This is a post title", "This is a post content", "666 rub"));
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityForResult(intent, 1);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String title = data.getStringExtra("TITLE");
        String content = data.getStringExtra("CONTENT");
        String amount = String.valueOf(data.getStringExtra("AMOUNT"));
        String type = data.getStringExtra("TYPE");
        postViewModel.savePost(new Post(title, content, amount, type));

        if (type.equals("Расход")) {totalSpent += Integer.parseInt(amount); updateTextView(Integer.toString(totalSpent)); } else {totalEarned +=Integer.parseInt(amount); updateTotalEarned(Integer.toString(totalEarned));}
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_TOTALSPENT, totalSpent);
        editor.putInt(APP_PREFERENCES_TOTALEARNED, totalEarned);
        editor.apply();
    }

    @Override
    public void onDeleteButtonClicked(Post post) {
        postViewModel.deletePost(post);
        if (post.getType().equals("Расход")) {totalSpent -= Integer.parseInt(post.getAmount()); updateTextView(Integer.toString(totalSpent)); } else {totalEarned -=Integer.parseInt(post.getAmount()); updateTotalEarned(Integer.toString(totalEarned));}

    }

    public void updateTextView(String toThis) {
        View includedLayout = findViewById(R.id.include);
        TextView insideTheIncludedLayout = includedLayout.findViewById(R.id.textSpentAmount);
        insideTheIncludedLayout.setText(toThis);
    }
    public void updateTotalEarned(String toThis){
        View includedLayout = findViewById(R.id.include);
        TextView TTT = includedLayout.findViewById(R.id.textEarnedAmount);
        TTT.setText(toThis);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_TOTALSPENT, totalSpent); editor.putInt(APP_PREFERENCES_TOTALEARNED, totalEarned);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateTextView(Integer.toString(totalSpent));

        if (mSettings.contains(APP_PREFERENCES_TOTALSPENT)) {
            totalSpent = mSettings.getInt(APP_PREFERENCES_TOTALSPENT, 0);
            updateTextView(Integer.toString(totalSpent));
        }
        if (mSettings.contains(APP_PREFERENCES_TOTALEARNED)) {
            totalEarned = mSettings.getInt(APP_PREFERENCES_TOTALEARNED, 0);
            updateTotalEarned(Integer.toString(totalEarned));
        }
    }
}