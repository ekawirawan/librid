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
import com.uts.mobprog210040138.helpers.TextViewStyle;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoans;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailLoanFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //my
    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);

    Context ctx;
    ModelAPIResSingleLoans resultLoanSingle;
    ModelLoans dataLoanSingle;
    private View view;


    public DetailLoanFragment() {
        // Required empty public constructor
    }

    public static DetailLoanFragment newInstance(String param1, String param2) {
        DetailLoanFragment fragment = new DetailLoanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ctx = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_loan, container, false);
        Bundle bundle = this.getArguments();
        String loanId = bundle.getString("loanId");
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
//        buttonOpenSheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showBottomSheetLoan();
//            }
//        });

        return view;
    }

//    public void showBottomSheetLoan() {
//        final Dialog dialog = new Dialog(ctx);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.fragment_bottom_action);
//
//        LinearLayout returnLayout = dialog.findViewById(R.id.layoutReturn);
//        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
//        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
//        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
//
//        returnLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                ConfirmMessage confirmMessage = new ConfirmMessage(ctx);
//                confirmMessage.setMessage("Are you sure to return this loans?");
//                confirmMessage.setTextButtonYes("Yes");
//                confirmMessage.setTextButtonCancle("Cancel");
//                confirmMessage.show();
//
//                confirmMessage.setConfirmationCallback(new ConfirmMessage.ConfirmationCallback() {
//                    @Override
//                    public void onConfirmation(boolean isConfirmed) {
//                        if (isConfirmed) {
//                            Log.d("onConfirm", "Dikonfirmasi");
//                        } else {
//                            Log.d("onConfirm", "Dicancle");
//                        }
//                    }
//                });
//
//            }
//        });
//
//        editLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//                Toast.makeText(ctx,"Create a short is Clicked",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        deleteLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//                Toast.makeText(ctx,"Go live is Clicked",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//    }

    public void showDetailLoans (String idLoan) {
        Call<ModelAPIResSingleLoans> getLoanById = apiService.getLoanById(idLoan);
        getLoanById.enqueue(new Callback<ModelAPIResSingleLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                if (response.code() != 200) {

                } else {
                    if(response.body() == null) {

                    } else {
                        try {
                            resultLoanSingle = response.body();
                            dataLoanSingle = resultLoanSingle.getData();

                            TextView txtTitleBookLoan = view.findViewById(R.id.txtTitleBookLoan);
                            TextView txtUsernameLoans = view.findViewById(R.id.txtUsernameLoans);
                            TextView txtStatusLoans = view.findViewById(R.id.txtStatusLoans);
                            TextView txtBorrowedAtLoans = view.findViewById(R.id.txtBorrowedAtLoans);
                            TextView txtDueDateLoans = view.findViewById(R.id.txtDueDateLoans);
                            TextView txtReturnedAtLoans = view.findViewById(R.id.txtReturnedAtLoans);

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
                        } catch (Exception err) {
                            Log.e("error nihhh bos", err.getMessage());
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {

            }
        });
    }


}