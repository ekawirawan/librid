package com.uts.mobprog210040138;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uts.mobprog210040138.models.ModelBook;


public class RecyclerViewCustomeAdapterBooks extends RecyclerView.Adapter<RecyclerViewCustomeAdapterBooks.ViewHolder> {
    Context ctx;

    public static ClickListener clickListener;

    List<ModelBook> data;

    public RecyclerViewCustomeAdapterBooks(Context context, List<ModelBook> dataBook) {
        ctx = context;
        data = dataBook;
    }

    public void setOnItemCLickListener(ClickListener clickListener){
        RecyclerViewCustomeAdapterBooks.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle3, txtAuthor3, txtStock3;
        public ImageView imageView4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView4 = itemView.findViewById(R.id.imageView4);
            txtTitle3 = itemView.findViewById(R.id.txtTitle3);
            txtAuthor3 = itemView.findViewById(R.id.txtAuthor3);
            txtStock3 = itemView.findViewById(R.id.txtStock3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) { clickListener.onItemClick(getAdapterPosition(), view); }
    }

    @NonNull
    @Override
    public RecyclerViewCustomeAdapterBooks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_data_buku2, parent, false);

        return new ViewHolder(v);
    }

    //belummmm
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCustomeAdapterBooks.ViewHolder holder, int position) {
        ModelBook book = data.get(position);
        holder.txtTitle3.setText(book.getTitle());
        holder.txtAuthor3.setText(book.getAuthor());
        holder.txtStock3.setText("Stock: " + book.getStock().toString());
        ImageView imageView = holder.imageView4;
        if (ctx != null && !((AppCompatActivity) ctx).isFinishing()) {
            Glide.with(ctx)
                    .load(R.drawable.none)
                    .into(holder.imageView4);
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

}


