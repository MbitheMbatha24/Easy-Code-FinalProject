package com.example.scratch;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.view.View;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<ListData> listData;

    public MyAdapter(List<ListData> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_action,parent,false);
        return new MyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListData ld=listData.get(position);
        String action=ld.getAction();
        switch (action) {
            case "u":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                break;
            case "r":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
                break;
            case "l":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_arrow_back_24);

                break;
            case "d":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                break;
            case "t":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_message_24);
                break;
            case "s":
                holder.imgbt.setImageResource(R.drawable.ic_baseline_surround_sound_24);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageButton imgbt;
        public ViewHolder(android.view.View view) {
            super(view);
            imgbt=(ImageButton)view.findViewById(R.id.imgbt);

        }
    }
}
