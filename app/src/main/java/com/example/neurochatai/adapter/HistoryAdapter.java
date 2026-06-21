package com.example.neurochatai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.neurochatai.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<String> historyList;
    OnItemClick listener;
    int selectedPosition = -1;

    public interface OnItemClick {
        void onClick(int position);
    }

    public HistoryAdapter(List<String> historyList, OnItemClick listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.historyText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //  Icon + text
        holder.text.setText("💬 " + historyList.get(position));

        //  Highlight selected
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(0xFF1E293B);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }

        // Click handling
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}