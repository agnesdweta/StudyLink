package com.example.studylink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAllAdapter extends RecyclerView.Adapter<SearchAllAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SearchResult item);
    }

    private List<SearchResult> list;
    private OnItemClickListener listener;

    public SearchAllAdapter(List<SearchResult> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult item = list.get(position);
        holder.txtTitle.setText(item.itemTitle);
        holder.txtDesc.setText(item.itemDesc);
        holder.txtType.setText(item.type);

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtType;

        ViewHolder(View v) {
            super(v);
            txtTitle = v.findViewById(R.id.txtTitle);
            txtDesc = v.findViewById(R.id.txtDesc);
            txtType = v.findViewById(R.id.txtType);
        }
    }
}
