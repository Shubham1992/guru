package com.example.helperapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.helperapp.R;
import com.example.helperapp.models.WorkflowSuggestionModel;
import com.example.helperapp.service.ChatHeadService;

import java.util.ArrayList;

public class WorkflowSuggestionAdapter extends RecyclerView.Adapter<WorkflowSuggestionAdapter.MyViewHolder> {
 
    private ArrayList<WorkflowSuggestionModel> workflowSuggestionModels;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
 
 
    public WorkflowSuggestionAdapter(ArrayList<WorkflowSuggestionModel> moviesList, Context context) {
        this.workflowSuggestionModels = moviesList;
        this.mContext = context;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_suggestion_layout_item, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        WorkflowSuggestionModel movie = workflowSuggestionModels.get(position);
        holder.title.setText(movie.getWorkflowName());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatHeadService)mContext).startWorkflow(workflowSuggestionModels.get(position));
            }
        });

    }
 
    @Override
    public int getItemCount() {
        return workflowSuggestionModels.size();
    }
}