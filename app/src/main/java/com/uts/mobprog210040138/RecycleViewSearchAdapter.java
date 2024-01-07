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

public class RecycleViewSearchAdapter extends RecyclerView.Adapter<RecycleViewSearchAdapter.ViewHolder> {
    Context ctx;

    public static RecycleViewSearchAdapter.ClickListener clickListener;

    List<ModelBook> data;

    public RecycleViewSearchAdapter(Context context, List<ModelBook> dataBook) {
        ctx = context;
        data = dataBook;
    }

    public void setOnItemCLickListener(RecycleViewSearchAdapter.ClickListener clickListener){
        RecycleViewSearchAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle4;
        public ImageView imageView7;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView7 = itemView.findViewById(R.id.imageView7);
            txtTitle4 = itemView.findViewById(R.id.txtTitle4);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) { clickListener.onItemClick(getAdapterPosition(), view); }
    }

    @NonNull
    @Override
    public RecycleViewSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_list2, parent, false);

        return new RecycleViewSearchAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecycleViewSearchAdapter.ViewHolder holder, int position) {
        ModelBook book = data.get(position);
        holder.txtTitle4.setText(book.getTitle());
        ImageView imageView = holder.imageView7;
        Glide.with(ctx)
                .load(book.getImageUrl())
                .placeholder(R.drawable.none)
                .into(imageView);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

//    public void setFilteredBooks(List<ModelBook> filteredBooks) {
//        this.data = filteredBooks;
//        notifyDataSetChanged();
//    }

}
