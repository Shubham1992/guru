package com.example.helperapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helperapp.AppDetail;
import com.example.helperapp.R;
import com.example.helperapp.models.AppModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class FeaturedAppListAdapter extends RecyclerView.Adapter<FeaturedAppListAdapter.MyViewHolder> {

    private ArrayList<AppModel> appModels;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            imageView = view.findViewById(R.id.icon);
        }
    }


    public FeaturedAppListAdapter(ArrayList<AppModel> moviesList, Context context) {
        this.appModels = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_app_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AppModel appModel = appModels.get(position);
        holder.title.setText(appModel.getName());
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