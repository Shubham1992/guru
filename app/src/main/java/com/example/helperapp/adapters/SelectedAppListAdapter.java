package com.example.helperapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helperapp.R;
import com.example.helperapp.models.AppModel;
import com.example.helperapp.onboarding.AccountInfoAfterQuiz;
import com.example.helperapp.onboarding.ThankYouActivity;

import java.util.ArrayList;


public class SelectedAppListAdapter extends RecyclerView.Adapter<SelectedAppListAdapter.MyViewHolder> {

    private ArrayList<AppModel> appModels;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            imageView = view.findViewById(R.id.image);
        }
    }


    public SelectedAppListAdapter(ArrayList<AppModel> moviesList, Context context) {
        this.appModels = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_apps_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AppModel appModel = appModels.get(position);
        Typeface faceBold = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/mPLUSRounded1cExtraBold.ttf");
        holder.name.setTypeface(faceBold);

        holder.name.setText(appModel.getName());
        if (appModel.getIcon() != null) {
            Glide.with(mContext).load(appModel.getIcon()).into(holder.imageView);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ThankYouActivity.class);
                mContext.startActivity(intent);
                ((AccountInfoAfterQuiz) mContext).finish();
            }
        });


    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }
}