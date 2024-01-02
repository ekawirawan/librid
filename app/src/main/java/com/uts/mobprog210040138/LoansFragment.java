package com.uts.mobprog210040138;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.uts.mobprog210040138.helpers.DateFormatterHelpers;
import com.uts.mobprog210040138.helpers.TextViewStyle;
import com.uts.mobprog210040138.models.ModelAPIResLoans;
import com.uts.mobprog210040138.models.ModelAPIResSingleLoans;
import com.uts.mobprog210040138.models.ModelLoans;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import com.uts.mobprog210040138.RecyclerViewCustomeAdapterLoans.OnUpdateStatusButtonClickListener;


public class LoansFragment extends Fragment  {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //start

    enum ReturnStatus {
        NOT_YET_RETURNED,
        RETURNED,
        RETURNED_LATE
    }

    Context ctx;
    APIInterfaceLoans apiService = APIClient.getClient().create(APIInterfaceLoans.class);
    ModelAPIResLoans result;
    ModelAPIResSingleLoans resultLoanSingle;
    List<ModelLoans> dataLoan, dataResSearch;

    ModelLoans dataLoanSingle;

    TextView txtTotalLoans;
    SearchView searchViewLoan;

    public RecyclerView recyclerView1;

    RecyclerViewCustomeAdapterLoans adapterLoans;

    private View view;

    public LoansFragment() {
        // Required empty public constructor
    }

    public static LoansFragment newInstance(String param1, String param2) {
        LoansFragment fragment = new LoansFragment();
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
        //tess kalii
        view = inflater.inflate(R.layout.fragment_loans, container, false);
        recyclerView1 = view.findViewById(R.id.recyclerViewLoans);

        searchViewLoan = view.findViewById(R.id.searchViewLoan);

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setHasFixedSize(true);

        loadDataLoan();
        searchLoan();

        return view;
    }



    public void loadDataLoan () {
        Call<ModelAPIResLoans> getAllLoan = apiService.getAllLoan();
        getAllLoan.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataLoan = result.getData();

                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
//                        adapterLoans.setOnUpdateStatusButtonClickListener(new OnUpdateStatusButtonClickListener() {
//                            @Override
//                            public void onUpdateStatusButtonClick(int position) {
//                                updateReturnStatusLoans(position);
//                            }
//                        });

                        adapterLoans.setOnItemCLickListener(new RecyclerViewCustomeAdapterLoans.ClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                sendIdToDetailLoan(dataLoan.get(position).getLoanId());
                            }
                        });

                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataLoan);


                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {

            }
        });
    }

    public void sendIdToDetailLoan(String loanId) {
        Bundle bundle = new Bundle();
        bundle.putString("loanId", loanId);

        DetailLoanFragment fragment = new DetailLoanFragment();
        fragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    public void searchLoan() {
        searchViewLoan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.trim().toLowerCase();

                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    resetSearch();
                }

                return true;
            }
        });

    }

    public void performSearch(String query) {
        Log.d("Query", query);
        Call<ModelAPIResLoans> getAllLoanByUsername = apiService.getAllLoanByUsername(query.trim().toLowerCase());
        getAllLoanByUsername.enqueue(new Callback<ModelAPIResLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResLoans> call, Response<ModelAPIResLoans> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        result = response.body();
                        dataResSearch = result.getData();
                        Log.d("Search Results", dataResSearch.toString());
                        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataResSearch);
                        recyclerView1.setAdapter(adapterLoans);
                        setTotalLoan(dataResSearch);

                    }
                }
            }

            @Override
            public void onFailure(Call<ModelAPIResLoans> call, Throwable t) {

            }
        });
    }

    private void resetSearch() {
        // Kembalikan ke data awal atau tampilkan semua data
        adapterLoans = new RecyclerViewCustomeAdapterLoans(ctx, dataLoan);
        recyclerView1.setAdapter(adapterLoans);
        setTotalLoan(dataLoan);
    }

    public void setTotalLoan(List<ModelLoans> data) {
        String wordLoan = "Loan";
        txtTotalLoans = view.findViewById(R.id.txtTotalLoans);
        Integer totalDataReturn = data.size();
        if(totalDataReturn > 1) { wordLoan = "Loans"; }
        txtTotalLoans.setText(totalDataReturn.toString() + " " + wordLoan);
    }

    public void updateReturnStatusLoans (int position) {
        Call<ModelAPIResSingleLoans> updateReturnStatus = apiService.updateReturnStatusLoan(dataLoan.get(position).getLoanId());
        updateReturnStatus.enqueue(new Callback<ModelAPIResSingleLoans>() {
            @Override
            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
                if (response.code() != 200) {

                } else {
                    if (response.body() == null) {

                    } else {
                        loadDataLoan();
                        adapterLoans.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {

            }
        });
    }

//    public void showDetailLoans (int position) {
//        Call<ModelAPIResSingleLoans> getLoanById = apiService.getLoanById(dataLoan.get(position).getLoanId());
//        getLoanById.enqueue(new Callback<ModelAPIResSingleLoans>() {
//            @Override
//            public void onResponse(Call<ModelAPIResSingleLoans> call, Response<ModelAPIResSingleLoans> response) {
//                if (response.code() != 200) {
//
//                } else {
//                    if(response.body() == null) {
//
//                    } else {
//                        resultLoanSingle = response.body();
//                        dataLoanSingle = resultLoanSingle.getData();
//
//                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
//                        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.detail_data_loans, viewGroup, false);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                        builder.setTitle("Informasi");
//                        builder.setIcon(android.R.drawable.ic_dialog_info);
//                        builder.setCancelable(false);
//
//                        TextView txtTitleBookLoan = dialogView.findViewById(R.id.txtTitleBookLoan);
//                        TextView txtUsernameLoans = dialogView.findViewById(R.id.txtUsernameLoans);
//                        TextView txtStatusLoans = dialogView.findViewById(R.id.txtStatusLoans);
//                        TextView txtBorrowedAtLoans = dialogView.findViewById(R.id.txtBorrowedAtLoans);
//                        TextView txtDueDateLoans = dialogView.findViewById(R.id.txtDueDateLoans);
//                        TextView txtReturnedAtLoans = dialogView.findViewById(R.id.txtReturnedAtLoans);
//
//                        builder.setView(dialogView);
//
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//
//                        txtTitleBookLoan.setText(dataLoanSingle.getBook().getTitle());
//                        txtUsernameLoans.setText(dataLoanSingle.getBorrower().getUsername());
//
//                        if (ReturnStatus.NOT_YET_RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("NOT YET RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.WARNING, ctx);
//                        } else if (ReturnStatus.RETURNED.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("RETURNED", txtStatusLoans, TextViewStyle.TypeStyle.SUCCESS, ctx);
//                        } else if (ReturnStatus.RETURNED_LATE.name().equals(dataLoanSingle.getReturnStatus())) {
//                            TextViewStyle.textStatusReturnedStyle("RETURNED LATE", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
//                        } else {
//                            TextViewStyle.textStatusReturnedStyle("INVALID", txtStatusLoans, TextViewStyle.TypeStyle.DANGER, ctx);
//                        }
//
//
//                        txtBorrowedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getBorrowedAt()));
//                        txtDueDateLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getDueDate()));
//                        if (dataLoanSingle.getReturnedAt() == null)
//                            txtReturnedAtLoans.setText("-");
//                        else {
//                            txtReturnedAtLoans.setText(DateFormatterHelpers.formatLongDate(dataLoanSingle.getReturnedAt()));
//                        }
//
//                        AlertDialog alertDialog = builder.create();
//                        alertDialog.show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ModelAPIResSingleLoans> call, Throwable t) {
//
//            }
//        });
//    }
    
}