package com.example.applanta;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.List;

public class RecordRecyclerAdapter extends RecyclerView.Adapter<RecordRecyclerAdapter.ViewHolder> {
    private List<Record> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    RecordRecyclerAdapter(Context context, List<Record> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Record record = mData.get(position);
        holder.etName.setText(record.getName());
        holder.tvPlantName.setText(record.getPlantName());
        holder.vBin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RecordsActivity.idToRemove = record.getId();
                RecordsActivity.recordToRemove.setText(record.getName() + "?");

                ObjectAnimator animationShow = ObjectAnimator.ofFloat(RecordsActivity.deletePopup, "translationY", 0f);
                animationShow.setDuration(900);
                animationShow.start();
            }
        });
        holder.vPencil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RecordsActivity act = new RecordsActivity();
                act.editRecord(holder.etName.getText().toString(), record.getId());
            }
        });
        holder.vPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RecordsActivity act = new RecordsActivity();
                try {
                    act.playRecord(record.getCode(), record.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText etName;
        TextView tvPlantName;
        View vBin;
        View vPencil;
        View vPlay;


        ViewHolder(View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.record_title);
            tvPlantName = itemView.findViewById(R.id.plant_name);
            vBin = itemView.findViewById(R.id.record_bin);
            vPencil = itemView.findViewById(R.id.record_pencil);
            vPlay = itemView.findViewById(R.id.play_button);
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

    String getCode(int id) {
        return mData.get(id).getCode();
    }

    String getPlantName(int id) {return mData.get(id).getPlantName();}

    int getId(int id) {return mData.get(id).getId();}

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
