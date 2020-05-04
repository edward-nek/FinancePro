package com.example.renatgasanov.financepro;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renatgasanov.financepro.Model.CoinModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
    Класс Portfolio отвечает за отображение данных в портфеле криптовалют,
    запрос этих данных на сервере CoinMarketCup, а также отображение и обработку
    нажатий на элементы Activity
*/


public class Portfolio extends AppCompatActivity {
    OkHttpClient client;
    Request request;
    List<CoinModel> items = new ArrayList<>();
    String[] data = {"", "", "", "", "", "", "", "", "" ,""};
    float[] ii = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Spinner spinner;
    TextView tv;
    String tvData;
    TextView tt;

    boolean test = false;

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
        setContentView(R.layout.activity_portfolio);

        tv = findViewById(R.id.textView);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tt = findViewById(R.id.editAmount);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.header));
        getWindow().setStatusBarColor(getResources().getColor(R.color.header));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textHeader));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.header));
        toolbar.setTitle("Портфель криптовалют");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadNext10Coin(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //for(int i = 0; i <=9; i++) {tvData = tvData + data[i] + ": " + "\n";}
        //tv.setText(tvData);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        //spinner.setSelection(4);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (!test && !data[1].equals("")) {
                    tvData = "";
                    for(int i = 0; i <=9; i++) {tvData = tvData + data[i] + ": " + "\n";}
                    tv.setText(tvData);
                    //spinner.setSelection(1);
                    test = true;
                }
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        Button b = findViewById(R.id.buttonDone);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ii[spinner.getSelectedItemPosition()]+=Float.parseFloat(tt.getText().toString());
                updateText(ii);
                tt.setText("");
            }
        });
    }

    private void loadNext10Coin(int index) {
        client = new OkHttpClient();
        request = new Request.Builder().url(String.format("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=e477882e-2d4e-42f7-bd02-6d07ceb272b0&start=1&limit=10", index))
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(Portfolio.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body=response.body().string();
                        Gson gson=new Gson();

                        Log.d("bodyl", body);

                        Gson googleJson = new Gson();
                        JsonObject datal = googleJson.fromJson(body, JsonObject.class);

                        Log.d("bodyl_data", datal.toString());

                        JsonArray jsonArr = datal.getAsJsonArray("data");
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
                                items.addAll(newitems);
                                for (int i=0; i<=9; i++){ CoinModel c = newitems.get(i); data[i] = c.getName(); }
                                //????
                            }
                        });
                    }
                });
    }
    void updateText(float ii[]){
        String res = "";
        for(int i = 0; i <=9; i++) {res = res + data[i] + ": " + ii[i] +  "\n";}
        tv.setText(res);
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