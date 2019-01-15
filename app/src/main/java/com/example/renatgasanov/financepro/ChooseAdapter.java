package com.example.renatgasanov.financepro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.MyViewHolder> {

    private List<Choose> chooseList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
        }
    }


    public ChooseAdapter(List<Choose> chooseL) {
        this.chooseList = chooseL;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
         .inflate(R.layout.choose_row, parent, false);

         return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Choose choose = chooseList.get(position);
        holder.title.setText(choose.getTitle());
        holder.description.setText(choose.getDescription());
    }

    @Override
    public int getItemCount() {
        return chooseList.size();
    }
}