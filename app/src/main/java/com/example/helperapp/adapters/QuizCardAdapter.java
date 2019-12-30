package com.example.helperapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.helperapp.R;
import com.example.helperapp.models.AppModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class QuizCardAdapter extends RecyclerView.Adapter<QuizCardAdapter.MyViewHolder> {

    private ArrayList<AppModel> appModels;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_name);
            imageView = view.findViewById(R.id.item_image);
        }
    }


    public QuizCardAdapter(ArrayList<AppModel> moviesList, Context context) {
        this.appModels = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_swipe_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AppModel appModel = appModels.get(position);
        holder.name.setText(appModel.getName());
        Glide.with(mContext).load(appModel.getIcon()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }
}