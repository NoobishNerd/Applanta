package com.example.applanta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantRecyclerAdapter extends RecyclerView.Adapter<PlantRecyclerAdapter.ViewHolder> {
    private List<Plant> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    PlantRecyclerAdapter(Context context, List<Plant> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_plant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Plant plant = mData.get(position);
        holder.tvName.setText(plant.getName());
        holder.tvNature.setText(plant.getSpecies() + " | " + plant.getNature());
        holder.tvDesc.setText(plant.getDesc());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvNature;
        TextView tvDesc;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.plant_name);
            tvNature = itemView.findViewById(R.id.plant_nature);
            tvDesc = itemView.findViewById(R.id.plant_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getName(int id) {
        return mData.get(id).getName();
    }

    String getNature(int id) {
        return mData.get(id).getNature();
    }

    String getSpecies(int id) {
        return mData.get(id).getSpecies();
    }

    String getDesc(int id) { return mData.get(id).getDesc(); }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
