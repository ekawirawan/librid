package com.uts.mobprog210040138;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uts.mobprog210040138.helpers.DateFormatterHelpers;
import com.uts.mobprog210040138.helpers.NotificationHelpers;
import com.uts.mobprog210040138.helpers.TextViewStyle;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoans;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailLoanFragment extends Fragment {
    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);

    Context ctx;
    ModelAPIResSingleLoans resultLoanSingle;
    ModelLoans dataLoanSingle;

    String loanId;
    private View view;


    public DetailLoanFragment() {

    }

    public static DetailLoanFragment newInstance(String loanIdP) {
        DetailLoanFragment fragment = new DetailLoanFragment();
        Bundle args = new Bundle();
        args.putString("loanId", loanIdP);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loanId = getArguments().getString("loanId");
        }
        ctx = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_loan, container, false);
        if (loanId != null) {
            showDetailLoans(loanId);
        }

        ImageButton buttonBack = view.findViewById(R.id.btnBackAdd);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        ImageButton buttonOpenSheet = view.findViewById(R.id.btnMoreAction);


        return view;
    }


    public void showDetailLoans (String idLoan) {
        Call<ModelAPIResSingleLoans> getLoanById = apiService.getLoanById(idLoan);
        getLoanById.enqueue(new Callback<ModelAPIResSingleLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                if (response.code() != 200) {
                    NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load detail loans", NotificationHelpers.Status.DANGER);
                    notification.show();
                } else {
                    if(response.body() == null) {
                        NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load detail loans", NotificationHelpers.Status.DANGER);
                        notification.show();
                    } else {
                        resultLoanSingle = response.body();
                        dataLoanSingle = resultLoanSingle.getData();

                        TextView txtIdLoan = view.findViewById(R.id.txtIdLoans);
                        TextView txtTitleBookLoan = view.findViewById(R.id.txtTitleBookLoan);
                        TextView txtUsernameLoans = view.findViewById(R.id.txtUsernameLoans);
                        TextView txtStatusLoans = view.findViewById(R.id.txtStatusLoans);
                        TextView txtBorrowedAtLoans = view.findViewById(R.id.txtBorrowedAtLoans);
                        TextView txtDueDateLoans = view.findViewById(R.id.txtDueDateLoans);
                        TextView txtReturnedAtLoans = view.findViewById(R.id.txtReturnedAtLoans);

                        txtIdLoan.setText(dataLoanSingle.getLoanId());
                        txtTitleBookLoan.setText(dataLoanSingle.getBook().getTitle());
                        txtUsernameLoans.setText(dataLoanSingle.getBorrower().getUsername());

                        if (LoansFragment.ReturnStatus.NOT_YET_RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
                            TextViewStyle.textStatusReturnedStyle("NOT YET RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.WARNING, ctx);
                        } else if (LoansFragment.ReturnStatus.RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
                            TextViewStyle.textStatusReturnedStyle("RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.SUCCESS, ctx);
                        } else if (LoansFragment.ReturnStatus.RETURNED_LATE.name().equals(dataLoanSingle.getReturnStatus())) {
                            TextViewStyle.textStatusReturnedStyle("RETURNED LATE", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
                        } else {
                            TextViewStyle.textStatusReturnedStyle("INVALID", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
                        }


                        txtBorrowedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getBorrowedAt()));
                        txtDueDateLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getDueDate()));
                        if (dataLoanSingle.getReturnedAt() == null)
                            txtReturnedAtLoans.setText("-");
                        else {
                            txtReturnedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getReturnedAt()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
                NotificationHelpers notification = new NotificationHelpers(ctx, "Opss..Failed to load detail loans", NotificationHelpers.Status.DANGER);
                notification.show();
            }
        });
    }


}