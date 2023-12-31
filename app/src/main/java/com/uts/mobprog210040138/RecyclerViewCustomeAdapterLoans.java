package com.uts.mobprog210040138;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.uts.mobprog210040138.models.ModelLoans;
import com.uts.mobprog210040138.helpers.DateFormatterHelpers;
import com.uts.mobprog210040138.LoansFragment.ReturnStatus;
import com.uts.mobprog210040138.helpers.TextViewStyle;

import retrofit2.Call;


public class RecyclerViewCustomeAdapterLoans extends RecyclerView.Adapter<RecyclerViewCustomeAdapterLoans.ViewHolder> {
    Context ctx;
    private static ClickListener clickListener;
    //private OnUpdateStatusButtonClickListener onUpdateStatusButtonClickListener;

    List<ModelLoans> data;

    public RecyclerViewCustomeAdapterLoans(Context context, List<ModelLoans> dataLoans) {
        ctx = context;
        data = dataLoans;
    }

    public void setOnItemCLickListener(ClickListener clickListener){
        RecyclerViewCustomeAdapterLoans.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

//    public interface OnUpdateStatusButtonClickListener {
//        void onUpdateStatusButtonClick(int position);
//    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle, txtUsername, txtBorrowerAt, txtStatusReturned;
        public Button btnReturn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitleBook);
            txtUsername = itemView.findViewById(R.id.txtUsernameBorrower);
            txtBorrowerAt = itemView.findViewById(R.id.txtBorrowedAt);
            txtStatusReturned = itemView.findViewById(R.id.txtStatusReturned);
            //btnReturn = itemView.findViewById(R.id.btnReturn);


//            btnReturn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("ViewHolder", "Button clicked");
//                    if (onUpdateStatusButtonClickListener != null) {
//                        onUpdateStatusButtonClickListener.onUpdateStatusButtonClick(getAdapterPosition());
//                    }
//                }
//            });

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) { clickListener.onItemClick(getAdapterPosition(), view); }
    }

    @NonNull
    @Override
    public RecyclerViewCustomeAdapterLoans.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_data_loans, parent, false);

        return new ViewHolder(v);
    }

//    public void setOnUpdateStatusButtonClickListener(OnUpdateStatusButtonClickListener onUpdateStatusButtonClickListener) {
//        this.onUpdateStatusButtonClickListener = onUpdateStatusButtonClickListener;
//    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCustomeAdapterLoans.ViewHolder holder, int position) {
        ModelLoans loans = data.get(position);
        holder.txtTitle.setText(loans.getBook().getTitle());
        holder.txtUsername.setText(loans.getBorrower().getUsername());

        holder.txtBorrowerAt.setText(DateFormatterHelpers.formatShortDate(loans.getBorrowedAt()));

        if (ReturnStatus.NOT_YET_RETURNED.name().equals(loans.getReturnStatus())) {
            TextViewStyle.textStatusReturnedStyle("NOT YET RETURNED", holder.txtStatusReturned, TextViewStyle.TypeStyle.WARNING, ctx);
        } else if (ReturnStatus.RETURNED.name().equals(loans.getReturnStatus())) {
            TextViewStyle.textStatusReturnedStyle("RETURNED", holder.txtStatusReturned, TextViewStyle.TypeStyle.SUCCESS, ctx);
        } else if (ReturnStatus.RETURNED_LATE.name().equals(loans.getReturnStatus())) {
            TextViewStyle.textStatusReturnedStyle("RETURNED LATE", holder.txtStatusReturned, TextViewStyle.TypeStyle.DANGER, ctx);
        } else {
            TextViewStyle.textStatusReturnedStyle("INVALID", holder.txtStatusReturned, TextViewStyle.TypeStyle.DANGER, ctx);
        }
    }


//    public void hiddenButtonReturn(@NonNull RecyclerViewCustomeAdapterLoans.ViewHolder holder) {
//        holder.txtStatusReturned.setVisibility(View.VISIBLE);
//        holder.btnReturn.setVisibility(View.INVISIBLE);
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.btnReturn.getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        holder.btnReturn.setLayoutParams(params);
//        holder.btnReturn.setEnabled(false);
//    }

    @Override
    public int getItemCount() { return data.size(); }

}
