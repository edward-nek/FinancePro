package com.example.renatgasanov.financepro;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.renatgasanov.financepro.Adaptor.CoinAdaptor;
import com.example.renatgasanov.financepro.Interface.ILoadMore;
import com.example.renatgasanov.financepro.Model.CoinModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.karan.churi.PermissionManager.PermissionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
    Класс Tracker отвечает за отображение данных в трекере криптовалют,
    запрос этих данных на сервере CoinMarketCup, а также отображение и обработку
    нажатий на элементы Activity
*/

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
        setContentView(R.layout.activity_tracker);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.header));
        getWindow().setStatusBarColor(getResources().getColor(R.color.header));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textHeader));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.header));
        toolbar.setTitle("Трекер криптовалют");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        permissionManager=new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        swipeRefreshLayout=findViewById(R.id.rootLayout);
        swipeRefreshLayout.setRefreshing(false);
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

        request= new Request.Builder().url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=e477882e-2d4e-42f7-bd02-6d07ceb272b0&start=1&limit=10").build();
        Log.d("Requestl", request.toString());

        swipeRefreshLayout.setRefreshing(false);
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

                        Log.d("bodyl", body);

                        Gson googleJson = new Gson();
                        JsonObject data = googleJson.fromJson(body, JsonObject.class);

                        Log.d("bodyl_data", data.toString());

                        JsonArray jsonArr = data.getAsJsonArray("data");
                        ArrayList jsonObjList = googleJson.fromJson(jsonArr, ArrayList.class);

                        final List<CoinModel> newitems = new ArrayList<CoinModel>();
                        for(int i=0; i< jsonObjList.size(); i++) {

                            CoinModel item = new CoinModel();

                            item.setId(jsonArr.get(i).getAsJsonObject().get("id").toString());
                            item.setName(jsonArr.get(i).getAsJsonObject().get("name").toString());
                            item.setSymbol(jsonArr.get(i).getAsJsonObject().get("symbol").toString());


                            JsonObject quote = googleJson.fromJson(jsonArr.get(i).toString(), JsonObject.class);
                            JsonElement jsonArrQuote = quote.getAsJsonObject().get("quote");

                            JsonObject usd = googleJson.fromJson(jsonArrQuote.toString(), JsonObject.class);
                            JsonElement jsonArrUsd = usd.getAsJsonObject("USD");


                            item.setPercent_change_1h(jsonArrUsd.getAsJsonObject().get("percent_change_1h").toString());
                            item.setPercent_change_24h(jsonArrUsd.getAsJsonObject().get("percent_change_24h").toString());
                            item.setPercent_change_7d(jsonArrUsd.getAsJsonObject().get("percent_change_7d").toString());
                            item.setPrice_usd(jsonArrUsd.getAsJsonObject().get("price").toString());

                            newitems.add(item);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adaptor.updateData(newitems);
                            }
                        });
                    }
                });


    }

    private void loadFirst10Coins(int index) {
        client=new OkHttpClient();
        request=new Request.Builder().url(String.format("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=e477882e-2d4e-42f7-bd02-6d07ceb272b0&start=1&limit=10",index))
                .build();

        Log.d("Requestl", request.toString());

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

                        Log.d("bodyl", body);

                        Gson googleJson = new Gson();
                        JsonObject data = googleJson.fromJson(body, JsonObject.class);

                        Log.d("bodyl_data", data.toString());

                        JsonArray jsonArr = data.getAsJsonArray("data");
                        ArrayList jsonObjList = googleJson.fromJson(jsonArr, ArrayList.class);

                        final List<CoinModel> newitems = new ArrayList<CoinModel>();
                        for(int i=0; i< jsonObjList.size(); i++) {

                            CoinModel item = new CoinModel();

                            item.setId(jsonArr.get(i).getAsJsonObject().get("id").toString());
                            item.setName(jsonArr.get(i).getAsJsonObject().get("name").toString());
                            item.setSymbol(jsonArr.get(i).getAsJsonObject().get("symbol").toString());


                            JsonObject quote = googleJson.fromJson(jsonArr.get(i).toString(), JsonObject.class);
                            JsonElement jsonArrQuote = quote.getAsJsonObject().get("quote");

                            JsonObject usd = googleJson.fromJson(jsonArrQuote.toString(), JsonObject.class);
                            JsonElement jsonArrUsd = usd.getAsJsonObject("USD");


                            item.setPercent_change_1h(jsonArrUsd.getAsJsonObject().get("percent_change_1h").toString());
                            item.setPercent_change_24h(jsonArrUsd.getAsJsonObject().get("percent_change_24h").toString());
                            item.setPercent_change_7d(jsonArrUsd.getAsJsonObject().get("percent_change_7d").toString());
                            item.setPrice_usd(jsonArrUsd.getAsJsonObject().get("price").toString());

                            newitems.add(item);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adaptor.updateData(newitems);
                            }
                        });
                    }
                });
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        return true;
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
