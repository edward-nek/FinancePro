package com.example.renatgasanov.financepro.Adaptor;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.renatgasanov.financepro.Interface.ILoadMore;
import com.example.renatgasanov.financepro.Model.CoinModel;
import com.example.renatgasanov.financepro.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoinAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ILoadMore iLoadMore;
    boolean isLoading;
    Activity activity;
    List<CoinModel> items;

    int visibleThreshold = 5, lastvisibleItem, totalItemCount;

    public CoinAdaptor(RecyclerView recyclerView, List<CoinModel> items,Activity activity) {
        this.activity = activity;
        this.items = items;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                totalItemCount = linearLayoutManager.getItemCount();
                lastvisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastvisibleItem + visibleThreshold)) {

                    if (iLoadMore != null)
                        iLoadMore.onLoadMore();
                    isLoading = true;
                }
            }
        });

    }

    public void setiLoadMore(ILoadMore iLoadMore) {
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity)
                .inflate(R.layout.coin_layout, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CoinModel item = items.get(position);
        CoinViewHolder holderitem = (CoinViewHolder) holder;

        holderitem.coin_name.setText(item.getName());
        holderitem.coin_symbol.setText(item.getSymbol());
        holderitem.coin_price.setText(item.getPrice_usd().substring(0, 7));
        holderitem.one_hour_change.setText(item.getPercent_change_1h() + "%");
        holderitem.twenty_hour_change.setText(item.getPercent_change_24h() + "%");
        holderitem.seven_days_change.setText(item.getPercent_change_7d() + "%");

        //Load image

//        Picasso.with(activity)
//                .load(new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/")
//                        .append(item.getSymbol().toLowerCase()).append(".png").toString())
//                .into(holderitem.coin_icon);


        Log.d("ImageIcon", item.getSymbol().substring(1, item.getSymbol().length()-1).toLowerCase());
        if (item.getSymbol().substring(1, item.getSymbol().length()-1).toString().toLowerCase().equals("bsv")){
            Picasso.with(activity)
                    .load((new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/btc.png") + "")).into(holderitem.coin_icon);

            Log.d("ImageIcon", "THIS IS BSV");

        }
        else{
            Picasso.with(activity)
                    .load((new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/") + item.getSymbol().substring(1, item.getSymbol().length()-1).toLowerCase() + ".png")).into(holderitem.coin_icon);

        }


        holderitem.one_hour_change.setTextColor(item.getPercent_change_24h().contains("-") ?
                Color.parseColor("#ff0000") : Color.parseColor("#32cd32"));
        holderitem.twenty_hour_change.setTextColor(item.getPercent_change_24h().contains("-") ?
                Color.parseColor("#ff0000") : Color.parseColor("#32cd32"));
        holderitem.seven_days_change.setTextColor(item.getPercent_change_24h().contains("-") ?
                Color.parseColor("#ff0000") : Color.parseColor("#32cd32"));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded(){isLoading=false;}

    public void updateData(List<CoinModel> coinModel){

        this.items=coinModel;
        notifyDataSetChanged();

    }
}
