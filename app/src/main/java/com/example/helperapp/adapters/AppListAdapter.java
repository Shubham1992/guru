package com.example.helperapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helperapp.AppDetail;
import com.example.helperapp.R;
import com.example.helperapp.VideoPlayer;
import com.example.helperapp.models.AppModel;

import java.util.ArrayList;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {

    private ArrayList<AppModel> appModels;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            imageView = view.findViewById(R.id.imgIcon);
        }
    }


    public AppListAdapter(ArrayList<AppModel> moviesList, Context context) {
        this.appModels = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_icon_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AppModel appModel = appModels.get(position);
        holder.name.setText(appModel.getName());
        if (appModel.getPackageName() != null) {
            try {
                holder.imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(appModel.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AppDetail.class);
                intent.putExtra("appModel", appModel);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }
}