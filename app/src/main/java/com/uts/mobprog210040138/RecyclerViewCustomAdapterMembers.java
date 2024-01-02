package com.uts.mobprog210040138;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uts.mobprog210040138.models.ModelMember;


public class RecyclerViewCustomAdapterMembers extends RecyclerView.Adapter<RecyclerViewCustomAdapterMembers.ViewHolder> {
    Context ctx;

    private static ClickListener clickListener;

    List<ModelMember> data;

    public RecyclerViewCustomAdapterMembers(Context context, List<ModelMember> dataMember) {
        ctx = context;
        data = dataMember;
    }

    public void setOnItemCLickListener(ClickListener clickListener){
        RecyclerViewCustomAdapterMembers.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtUsername, txtFullName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) { clickListener.onItemClick(getAdapterPosition(), view); }
    }

    @NonNull
    @Override
    public RecyclerViewCustomAdapterMembers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_data_members, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCustomAdapterMembers.ViewHolder holder, int position) {
        ModelMember member = data.get(position);
        holder.txtUsername.setText(member.getUsername());
        holder.txtFullName.setText(member.getFullName());
    }
    @Override
    public int getItemCount() { return data.size(); }

}