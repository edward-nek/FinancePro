package com.example.renatgasanov.financepro;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

/*
    Класс MainActivity отвечает за отображение списка транзакций, удаления транзакций,
    обработку элементов списка и отображение баланса пользователя.
*/

public class MainActivity extends AppCompatActivity implements PostsAdapter.OnDeleteButtonClickListener {

    private PostsAdapter postsAdapter;
    private PostViewModel postViewModel;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOTALSPENT = "totalspent";
    public static final String APP_PREFERENCES_TOTALEARNED = "totalearned";
    private SharedPreferences mSettings;
    public Float totalSpent;
    public Float totalEarned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //скрыть панель навигации начало

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        //скрыть панель навигации конец

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textHeader));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.header));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.header));
        getWindow().setStatusBarColor(getResources().getColor(R.color.header));

        postsAdapter = new PostsAdapter(this, this);

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        postViewModel.getAllPosts().observe(this, posts -> postsAdapter.setData(posts));

        RecyclerView recyclerView = findViewById(R.id.rvPostsLis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postsAdapter);

        totalSpent = (float) 0;
        totalEarned = (float) 0;
        updateTextView(Float.toString(totalSpent));
        updateTotalEarned(Float.toString(totalEarned));

        updateSumAmount(Float.toString(totalEarned - totalSpent));

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        return true;
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
            overridePendingTransition(0, 0);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        String title = data.getStringExtra("TITLE");
        String content = data.getStringExtra("CONTENT");
        String amount = String.valueOf(data.getStringExtra("AMOUNT"));
        String type = data.getStringExtra("TYPE");

        postViewModel.savePost(new Post(title, content, amount, type));

        if (type.equals("Расход")) {
            totalSpent += Float.parseFloat(amount);
            updateTextView(Float.toString(totalSpent));
            updateSumAmount(Float.toString(totalEarned - totalSpent));
        } else
            {
                totalEarned +=Float.parseFloat(amount);
                updateTotalEarned(Float.toString(totalEarned));
                updateSumAmount(Float.toString(totalEarned - totalSpent));
            }

        SharedPreferences.Editor editor = mSettings.edit();

        editor.putFloat(APP_PREFERENCES_TOTALSPENT, totalSpent);
        editor.putFloat(APP_PREFERENCES_TOTALEARNED, totalEarned);
        editor.apply();
    }

    @Override
    public void onDeleteButtonClicked(Post post) {
        postViewModel.deletePost(post);
        if (post.getType().equals("Расход")) {
            totalSpent -= Float.parseFloat(post.getAmount());
            updateTextView(Float.toString(totalSpent));
            updateSumAmount(Float.toString(totalEarned - totalSpent));
        } else {
            totalEarned -=Float.parseFloat(post.getAmount());
            updateTotalEarned(Float.toString(totalEarned));
            updateSumAmount(Float.toString(totalEarned - totalSpent));
        }

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
    public void updateSumAmount(String toThis) {

        View includedLayout = findViewById(R.id.include);
        TextView TTT = includedLayout.findViewById(R.id.textSumAmount);
        TextView TR = includedLayout.findViewById(R.id.r1);
        if (toThis.length() > 0){
            double check = Double.valueOf(toThis);
            if (check >= 0){
                TTT.setTextColor(getResources().getColor(R.color.colorAccent_green));
                TR.setTextColor(getResources().getColor(R.color.colorAccent_green));
            }
            else{
                TTT.setTextColor(getResources().getColor(R.color.colorAccent));
                TR.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
        TTT.setText(toThis);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putFloat(APP_PREFERENCES_TOTALSPENT, totalSpent);
        editor.putFloat(APP_PREFERENCES_TOTALEARNED, totalEarned);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateTextView(Double.toString(totalSpent));

        if (mSettings.contains(APP_PREFERENCES_TOTALSPENT)) {
            totalSpent = mSettings.getFloat(APP_PREFERENCES_TOTALSPENT, 0);
            updateTextView(Float.toString(totalSpent));

            updateSumAmount(Float.toString(totalEarned - totalSpent));
        }
        if (mSettings.contains(APP_PREFERENCES_TOTALEARNED)) {
            totalEarned = mSettings.getFloat(APP_PREFERENCES_TOTALEARNED, 0);
            updateTotalEarned(Float.toString(totalEarned));

            updateSumAmount(Float.toString(totalEarned - totalSpent));
        }
    }

    //отслеживание нажатий на экран

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}