package com.example.ide.GitDB.Recycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ide.R;

import java.util.List;

public class GitLinksRecycleAdapter extends RecyclerView.Adapter<GitLinksRecycleAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<GitLinksRecycleViewShow> list;
    private final OnRecyclerViewItemListener onRecyclerViewItemListener;

    public GitLinksRecycleAdapter(LayoutInflater inflater, List<GitLinksRecycleViewShow> list, OnRecyclerViewItemListener onRecyclerViewItemListener) {
        this.inflater = inflater;
        this.list = list;
        this.onRecyclerViewItemListener = onRecyclerViewItemListener;
    }

    @NonNull
    @Override
    public GitLinksRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_repos, parent, false);
        return new ViewHolder(view, onRecyclerViewItemListener);
    }

    @Override
    public void onBindViewHolder(GitLinksRecycleAdapter.ViewHolder holder, int position) {
        GitLinksRecycleViewShow item = list.get(position);
        holder.repo_name.setText(item.getRepo_name());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView repo_name;
        OnRecyclerViewItemListener onRecyclerViewItemListener;
        ViewHolder(View view, OnRecyclerViewItemListener onRecyclerViewItemListener){
            super(view);
            repo_name = (TextView) view.findViewById(R.id.repo_name);

            this.onRecyclerViewItemListener = onRecyclerViewItemListener;
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onRecyclerViewItemListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface OnRecyclerViewItemListener{
        void onItemClick(int position);
    }
}

