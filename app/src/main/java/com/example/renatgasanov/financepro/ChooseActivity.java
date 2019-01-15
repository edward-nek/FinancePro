package com.example.renatgasanov.financepro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {
    private List<Choose> CL = new ArrayList<>();
    private ChooseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_green));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_green));

        RecyclerView choose_objs = findViewById(R.id.choose);
        adapter = new ChooseAdapter(CL);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        choose_objs.setLayoutManager(mLayoutManager);
        choose_objs.setItemAnimator(new DefaultItemAnimator());
        choose_objs.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        choose_objs.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), choose_objs, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Choose T = CL.get(position);
                switch (position){
                    case 0:
                        Intent transactions = new Intent(ChooseActivity.this, MainActivity.class);
                        startActivity(transactions);
                        break;
                    case 1:
                        Intent tracker = new Intent(ChooseActivity.this, Tracker.class);
                        startActivity(tracker);
                        break;
                    case 2:
                        Intent portfolio = new Intent(ChooseActivity.this, Portfolio.class);
                        startActivity(portfolio);
                        break;
                }
                //todo new screens
            }

            @Override
            public void onLongClick(View view, int position) { }

    }));
        choose_objs.setAdapter(adapter);
        setChoose_objs();
    }
    private void setChoose_objs(){
        Choose choose = new Choose("Список транзакций", "Просматривайте и изменяйте существующие транзакции");
        CL.add(choose);
        adapter.notifyDataSetChanged();
        choose = new Choose("Трекер криптовалют", "Просматривайте актуальные цены на самые известные криптовалюты");
        CL.add(choose);
        adapter.notifyDataSetChanged();
        choose = new Choose("Портфель криптовалют", "Просматривайте и добавляйте свои криптовалютные сбережения");
        CL.add(choose);
        adapter.notifyDataSetChanged();
        choose = new Choose("Настройки", "Настройки программы");
        CL.add(choose);
        adapter.notifyDataSetChanged();
    }
}

