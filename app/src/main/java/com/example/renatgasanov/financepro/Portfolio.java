package com.example.renatgasanov.financepro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renatgasanov.financepro.Model.CoinModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        tv = findViewById(R.id.textView);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tt = findViewById(R.id.editAmount);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

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
            }
        });
    }

    private void loadNext10Coin(int index) {
        client = new OkHttpClient();
        request = new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10", index))
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(Portfolio.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body = response.body().string();
                        Gson gson = new Gson();
                        final List<CoinModel> newitems = gson.fromJson(body, new TypeToken<List<CoinModel>>() {
                        }.getType());
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
}