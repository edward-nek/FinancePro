package com.example.renatgasanov.financepro;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.renatgasanov.financepro.Adaptor.CoinAdaptor;
import com.example.renatgasanov.financepro.Interface.ILoadMore;
import com.example.renatgasanov.financepro.Model.CoinModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Tracker extends AppCompatActivity {

    List<CoinModel> items = new ArrayList<>();
    CoinAdaptor adaptor;
    RecyclerView recyclerView;

    OkHttpClient client;
    Request request;
    SwipeRefreshLayout swipeRefreshLayout;
    PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        permissionManager=new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        swipeRefreshLayout=findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                loadFirst10Coins(0);

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                loadFirst10Coins(0);
                setUpAdaptor();
            }
        });

        recyclerView=findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpAdaptor();

    }

    private void setUpAdaptor() {

        adaptor=new CoinAdaptor(recyclerView,items,Tracker.this);
        recyclerView.setAdapter(adaptor);
        adaptor.setiLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

                if (items.size()<=1000){
                    loadNext10Coin(items.size());
                }
                else {
                    Toast.makeText(Tracker.this, "Maximum limit is 1000", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void loadNext10Coin(int index) {
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",index))
                .build();

        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(Tracker.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body=response.body().string();
                        Gson gson=new Gson();
                        final List<CoinModel> newitems=gson.fromJson(body,new TypeToken<List<CoinModel>>(){}.getType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                items.addAll(newitems);
                                adaptor.setLoaded();
                                adaptor.updateData(items);
                                swipeRefreshLayout.setRefreshing(false);


                            }
                        });
                    }
                });


    }

    private void loadFirst10Coins(int index) {
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",index))
                .build();

        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(Tracker.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body=response.body().string();
                        Gson gson=new Gson();
                        final List<CoinModel> newitems=gson.fromJson(body,new TypeToken<List<CoinModel>>(){}.getType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adaptor.updateData(newitems);
                            }
                        });
                    }
                });

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);


    }
}
