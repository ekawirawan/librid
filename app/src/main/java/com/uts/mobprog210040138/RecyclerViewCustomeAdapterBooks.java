package com.uts.mobprog210040138;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uts.mobprog210040138.models.ModelBook;


public class RecyclerViewCustomeAdapterBooks extends RecyclerView.Adapter<RecyclerViewCustomeAdapterBooks.ViewHolder> {
    Context ctx;
    Boolean withBottomSheet;
    public interface OnMoreButtonClickListener{
        void onMoreButtonClick(int position);
    }

    public static ClickListener clickListener;
    private RecyclerViewCustomeAdapterBooks.OnMoreButtonClickListener onMoreButtonClickListener;

    List<ModelBook> data;

    public RecyclerViewCustomeAdapterBooks(Context context, List<ModelBook> dataBook, Boolean withBottomSheetP) {
        ctx = context;
        data = dataBook;
        withBottomSheet = withBottomSheetP;
    }

    public void setOnItemCLickListener(ClickListener clickListener){
        RecyclerViewCustomeAdapterBooks.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public void setOnMoreButtonClickListener(RecyclerViewCustomeAdapterBooks.OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle3, txtAuthor3, txtStock3;
        public ImageView imageView4;
        ImageButton btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnMoreAction3);
            imageView4 = itemView.findViewById(R.id.imageView4);
            txtTitle3 = itemView.findViewById(R.id.txtTitle3);
            txtAuthor3 = itemView.findViewById(R.id.txtAuthor3);
            txtStock3 = itemView.findViewById(R.id.txtStock3);
            if (!withBottomSheet){btnMore.setVisibility(View.GONE);}

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
        Glide.with(ctx)
                .load(book.getImageUrl())
                .placeholder(R.drawable.none)
                .into(imageView);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilteredBooksAdpters(List<ModelBook> filteredBooks) {
        this.data = filteredBooks;
        notifyDataSetChanged();
    }



}


