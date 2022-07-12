package com.example.sensory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private ArrayList<AdapterModel> adapterModels;
    private OnItemClickListener mListener;

    public SensorAdapter(ArrayList<AdapterModel> adapterModels) {
        this.adapterModels = adapterModels;
    }

    public void setModels(ArrayList<AdapterModel> newAdapterModels) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallBack(newAdapterModels, adapterModels));
        adapterModels = newAdapterModels;
        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtViewTitle;
        private final TextView txtViewValue;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener mListener) {
            super(itemView);
            this.txtViewTitle = itemView.findViewById(R.id.title);
            this.txtViewValue = itemView.findViewById(R.id.value);
            this.imageView = itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(view -> {
                if(mListener!=null){
                    int position = getBindingAdapterPosition();
                    System.out.println("Tapped position: "+ position);
                    if(position!=RecyclerView.NO_POSITION){
                        mListener.onItemClick(position);
                    }
                }
            });
        }
        private void bind(AdapterModel adapterModel){
            txtViewTitle.setText(adapterModel.getTitle());
            txtViewValue.setText(adapterModel.getValue());
            imageView.setImageResource(adapterModel.getImg());
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(adapterModels.get(position));
    }

    @Override
    public int getItemCount() {
        return adapterModels.size();
    }
}
