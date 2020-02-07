package com.example.helperapp.models;

public class WorkflowSuggestionModel {

    public WorkflowSuggestionModel(String name){
        this.workflowName = name;
    }

    private String workflowName = "";

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
}
