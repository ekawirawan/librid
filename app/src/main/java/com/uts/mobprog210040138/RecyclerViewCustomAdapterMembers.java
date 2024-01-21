package com.uts.mobprog210040138;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uts.mobprog210040138.models.ModelMember;


public class RecyclerViewCustomAdapterMembers extends RecyclerView.Adapter<RecyclerViewCustomAdapterMembers.ViewHolder> {
    Context ctx;
    Boolean withBottomSheet;
    public interface OnMoreButtonClickListener {
        void onMoreButtonClick(int position);
    }
    private static ClickListener clickListener;
    private RecyclerViewCustomAdapterMembers.OnMoreButtonClickListener onMoreButtonClickListener;
    List<ModelMember> data;

    public RecyclerViewCustomAdapterMembers(Context context, List<ModelMember> dataMember, Boolean withBottomSheetP) {
        ctx = context;
        data = dataMember;
        withBottomSheet = withBottomSheetP;
    }

    public void setOnItemCLickListener(ClickListener clickListener){
        RecyclerViewCustomAdapterMembers.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public void setOnMoreButtonClickListener(RecyclerViewCustomAdapterMembers.OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtUsername, txtFullName;
        public ImageButton btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnMoreAction);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            if (!withBottomSheet){
                btnMore.setVisibility(View.GONE);
            }

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ViewHolder", "Button clicked");
                    if (onMoreButtonClickListener != null) {
                        onMoreButtonClickListener.onMoreButtonClick(getAdapterPosition());
                    }
                }
            });
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