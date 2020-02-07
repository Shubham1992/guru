package com.example.helperapp.models;

import java.util.ArrayList;

public class WorkflowDB {

    public static ArrayList<WorkflowSuggestionModel> arrayListDB = new ArrayList();

    public WorkflowDB(){
        arrayListDB.add(new WorkflowSuggestionModel("Group creation workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("Group deletion workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("New chat workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("Video call workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("Create status workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("Start Uber promotions workflow"));
        arrayListDB.add(new WorkflowSuggestionModel("Start Quests promotions workflow"));


    }

    public ArrayList<WorkflowSuggestionModel> getWorkflow(String inputText){

        if (inputText.contains("नया ग्रपु बनाएं") || inputText.contains("न्यू ग्रुप बना") || inputText.contains("नया ग्रुप बना") || inputText.contains("व्हाट्सएप ग्रुप बनाएं")
        || inputText.contains("व्हाट्सएप ग्रुप बनाने")) {
            ArrayList<WorkflowSuggestionModel> arrayList = new ArrayList();
            arrayList.add(arrayListDB.get(0));
            return  arrayList;
        }else if (inputText.contains("प्रमोशंस") || inputText.contains("प्रमोशन")) {
            ArrayList<WorkflowSuggestionModel> arrayList = new ArrayList();
            arrayList.add(arrayListDB.get(5));
            arrayList.add(arrayListDB.get(6));
            return  arrayList;
        }
        return new ArrayList<WorkflowSuggestionModel>();
    }
}
