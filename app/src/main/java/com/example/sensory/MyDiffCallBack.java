package com.example.sensory;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class MyDiffCallBack extends DiffUtil.Callback{
    ArrayList<AdapterModel> newAdapterModels;
    ArrayList<AdapterModel> oldAdapterModels;

    public MyDiffCallBack(ArrayList<AdapterModel> newAdapterModels, ArrayList<AdapterModel> oldAdapterModels) {
        this.newAdapterModels = newAdapterModels;
        this.oldAdapterModels = oldAdapterModels;
    }

    @Override
    public int getOldListSize() {
        return oldAdapterModels.size();
    }

    @Override
    public int getNewListSize() {
        return newAdapterModels.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //return (oldModels.get(oldItemPosition).getTitle() == newModels.get(newItemPosition).getTitle());
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return (oldAdapterModels.get(oldItemPosition).getValue().equals(newAdapterModels.get(newItemPosition).getValue()));
    }

}
